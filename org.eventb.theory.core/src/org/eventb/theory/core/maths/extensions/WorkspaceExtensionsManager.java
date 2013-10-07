/*******************************************************************************
 * Copyright (c) 2010, 2013 University of Southampton and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University of Southampton - initial API and implementation
 *     Systerel -  refactored after imports only concern deployed theories
 *******************************************************************************/
package org.eventb.theory.core.maths.extensions;

import static org.eventb.theory.core.DatabaseUtilities.getDeployedTheories;
import static org.eventb.theory.core.DatabaseUtilities.getNonTempSCTheoryPaths;
import static org.eventb.theory.core.TheoryHierarchyHelper.getImportedTheories;
import static org.eventb.theory.core.TheoryHierarchyHelper.getTheoryPathImports;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IEventBRoot;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.ast.extensions.maths.AstUtilities;
import org.eventb.theory.core.DatabaseUtilities;
import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.core.IFormulaExtensionsSource;
import org.eventb.theory.core.ISCTheoryPathRoot;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.ITheoryRoot;
import org.eventb.theory.core.maths.extensions.dependencies.DeployedTheoriesGraph;
import org.eventb.theory.internal.core.util.CoreUtilities;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IElementChangedListener;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinElementDelta;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;

/**
 * Manager for theory dependencies and formula extensions throughout the
 * workspace.
 * 
 * @author maamria
 * @author beauger
 */
public class WorkspaceExtensionsManager implements IElementChangedListener {

	private static final Set<IFormulaExtension> COND_EXTS = Collections
			.singleton(AstUtilities.COND);

	private static final WorkspaceExtensionsManager INSTANCE = new WorkspaceExtensionsManager();

	private final FormulaFactory basicFactory = FormulaFactory
			.getInstance(COND_EXTS);

	// cache for SC theory direct dependencies (not a closure, ask deployedGraph
	// for that)
	private final Map<ISCTheoryRoot, Set<IDeployedTheoryRoot>> scDependencies = new HashMap<ISCTheoryRoot, Set<IDeployedTheoryRoot>>();

	// cache for deployed theory dependency graph
	private final DeployedTheoriesGraph deployedGraph = new DeployedTheoriesGraph();

	// cache for extensions provided by theories (both SC and deployed)
	private final Map<IFormulaExtensionsSource, Set<IFormulaExtension>> extensions = new HashMap<IFormulaExtensionsSource, Set<IFormulaExtension>>();

	// TODO add TheoryPath cache

	private final Queue<IDeployedTheoryRoot> changedDeployed = new ConcurrentLinkedQueue<IDeployedTheoryRoot>();
	private final Queue<ISCTheoryRoot> changedSC = new ConcurrentLinkedQueue<ISCTheoryRoot>();

	private WorkspaceExtensionsManager() {
		RodinCore.addElementChangedListener(this);
		initDeployedGraph();
	}

	private void initDeployedGraph() {
		try {
			for (IRodinProject project : RodinCore.getRodinDB()
					.getRodinProjects()) {
				for (IDeployedTheoryRoot deployed : getDeployedTheories(project)) {
					deployedGraph.addElement(deployed);
				}
			}
		} catch (CoreException e) {
			CoreUtilities.log(e, "While initializing deployed theory graph");
		}
	}

	public static WorkspaceExtensionsManager getInstance() {
		return INSTANCE;
	}

	// unique access point for 'extensions' field
	private Set<IFormulaExtension> fetchExtensions(
			IFormulaExtensionsSource source,
			Set<IFormulaExtension> requiredExtns) throws CoreException {
		Set<IFormulaExtension> extns = extensions.get(source);
		if (extns == null) {
			final FormulaFactory factory = basicFactory
					.withExtensions(requiredExtns);
			final FormulaExtensionsLoader loader = new FormulaExtensionsLoader(
					source, factory);
			extns = loader.load();
			extensions.put(source, extns);
		}
		return extns;
	}

	// unique access point for 'scDependencies' field
	private Set<IDeployedTheoryRoot> fetchSCDependencies(ISCTheoryRoot scTheory)
			throws CoreException {
		for (ISCTheoryRoot changedRoot : changedSC) {
			scDependencies.put(changedRoot, getImportedTheories(changedRoot));
		}
		Set<IDeployedTheoryRoot> requiredTheories = scDependencies
				.get(scTheory);
		if (requiredTheories == null) {
			requiredTheories = getImportedTheories(scTheory);
			scDependencies.put(scTheory, requiredTheories);
		}
		return requiredTheories;
	}

	// unique access point for 'deployedGraph' field
	private List<IDeployedTheoryRoot> fetchDependencies(
			IDeployedTheoryRoot deployedRoot) throws CoreException {
		if (!deployedRoot.exists()) {
			return Collections.emptyList();
		}
		synchronized (deployedGraph) {
			while(!changedDeployed.isEmpty()) {
				final IDeployedTheoryRoot deployed = changedDeployed.remove();
				if (deployed.exists()) {
					deployedGraph.addElement(deployed);
				} else {
					deployedGraph.removeElement(deployed);
				}
			}
			return deployedGraph.getUpperSet(deployedRoot);
		}
	}

	private Set<IFormulaExtension> getDeployedExtensionClosure(
			IDeployedTheoryRoot deployedRoot) throws CoreException {
		final Set<IFormulaExtension> extns = new HashSet<IFormulaExtension>();
		for (IDeployedTheoryRoot neededTheory : fetchDependencies(deployedRoot)) {
			extns.addAll(fetchExtensions(neededTheory, extns));
		}
		extns.addAll(fetchExtensions(deployedRoot, extns));
		return extns;
	}

	private Set<IFormulaExtension> getExtensionClosure(
			Iterable<IDeployedTheoryRoot> deployedRoots) throws CoreException {
		final Set<IFormulaExtension> extns = new HashSet<IFormulaExtension>();
		for (IDeployedTheoryRoot deployedRoot : deployedRoots) {
			extns.addAll(getDeployedExtensionClosure(deployedRoot));
		}
		return extns;
	}

	private Set<IFormulaExtension> getSCExtensionClosure(ISCTheoryRoot scTheory)
			throws CoreException {
		final Set<IFormulaExtension> extns = new HashSet<IFormulaExtension>();
		final Set<IDeployedTheoryRoot> requiredTheories = fetchSCDependencies(scTheory);
		extns.addAll(getExtensionClosure(requiredTheories));
		extns.addAll(fetchExtensions(scTheory, extns));
		return extns;
	}

	/**
	 * Returns formula extensions needed to parse formulas within the given file
	 * root.
	 * 
	 * @param root
	 *            a file root
	 * @return a set of extensions
	 * @throws CoreException
	 *             if something bad happens
	 */
	public synchronized Set<IFormulaExtension> getFormulaExtensions(
			IEventBRoot root) throws CoreException {

		if (!DatabaseUtilities.originatedFromTheory(root.getRodinFile())) {
			final IRodinProject project = root.getRodinProject();
			final ISCTheoryPathRoot[] paths = getNonTempSCTheoryPaths(project);
			// theories cannot depend on theory path, so the presence of a
			// theory path should not change the input language of a theory
			if (paths.length == 1) {
				final List<IDeployedTheoryRoot> deployedRoots = getTheoryPathImports(paths[0]);
				return getExtensionClosure(deployedRoots);
			}
			return Collections.emptySet();
		}

		final Set<IFormulaExtension> extns = new LinkedHashSet<IFormulaExtension>(
				COND_EXTS);

		// case unchecked Theory: no imports resolved => basic extension set
		if (root instanceof ITheoryRoot) {
			return extns;
		}

		// case Deployed Theory: basic set + from needed theories + from given
		// theory
		if (root instanceof IDeployedTheoryRoot) {
			extns.addAll(getDeployedExtensionClosure((IDeployedTheoryRoot) root));
			return extns;
		}

		// case SC Theory or other theory file (bpo, bps, bpr):
		// basic set + from needed theories + from SC theory
		final ISCTheoryRoot scRoot = DatabaseUtilities.getSCTheory(
				root.getComponentName(), root.getRodinProject());
		if (!scRoot.exists()) {
			// TODO try tmp SC ?
			throw new IllegalStateException("Cannot compute extensions for "
					+ root.getPath() + " without an associated SC theory");
		}

		extns.addAll(getSCExtensionClosure(scRoot));
		return extns;
	}

	/**
	 * Returns the closure of deployed theories imported by the given SC theory.
	 * 
	 * @param scRoot
	 *            a SC root (must not be a deployed root)
	 * @return
	 * @throws CoreException
	 */
	public synchronized List<IDeployedTheoryRoot> getTheoryImportClosure(
			ISCTheoryRoot scRoot) throws CoreException {
		if (scRoot instanceof IDeployedTheoryRoot) {
			throw new IllegalArgumentException(
					"Must be called with a SC theory, but was "
							+ scRoot.getPath());
		}
		final Set<IDeployedTheoryRoot> imported = new LinkedHashSet<IDeployedTheoryRoot>();
		for (IDeployedTheoryRoot neededTheory : fetchSCDependencies(scRoot)) {
			imported.addAll(fetchDependencies(neededTheory));
			imported.add(neededTheory);
		}
		return new ArrayList<IDeployedTheoryRoot>(imported);
	}

	private void processDelta(IRodinElementDelta delta) {
		IRodinElement element = delta.getElement();
		IRodinElementDelta[] affected = delta.getAffectedChildren();

		if (element instanceof IRodinFile) {
			final IRodinFile file = (IRodinFile) element;
			if (file.getRootElementType() == IDeployedTheoryRoot.ELEMENT_TYPE) {
				changedDeployed.add((IDeployedTheoryRoot) file.getRoot());
			}
		} else {
			for (IRodinElementDelta d : affected) {
				processDelta(d);
			}
		}
	}

	@Override
	public void elementChanged(ElementChangedEvent event) {
		final IRodinElementDelta delta = event.getDelta();
		processDelta(delta);
	}

	/**
	 * Informs that the given SC theory has changed and reloads its extensions.
	 * 
	 * @param scTheory
	 *            the changed SC theory root
	 * @throws CoreException
	 *             if extensions could not be reloaded
	 */
	public void scTheoryChanged(ISCTheoryRoot scTheory) throws CoreException {
		changedSC.add(scTheory);
	}

}
