/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.maths.extensions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.core.IUseTheory;
import org.eventb.theory.core.DB_TCFacade;
import org.eventb.theory.internal.core.maths.extensions.TheoryTransformer;
import org.eventb.theory.internal.core.util.CoreUtilities;
import org.eventb.theory.internal.core.util.MathExtensionsUtilities;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IRodinDB;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinElementDelta;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;

/**
 * @author maamria
 * 
 */
public class WorkspaceManager extends FormulaExtensionsWorkspaceManager {

	private static IFormulaExtensionsWorkspaceManager manager;
	private boolean wsChanged;

	private WorkspaceManager() {
		super();
		RodinCore.addElementChangedListener(this);
	}

	@Override
	public void elementChanged(ElementChangedEvent event) {
		IRodinElementDelta delta = event.getDelta();
		try {
			processDelta(delta);
			if (wsChanged) {
				populateDeployedEntries();
				populateDeployedExtensions();
				wsChanged = false;
			}
		} catch (CoreException e) {
			reset();
			CoreUtilities
					.log(e,
							"Failed processing changes in deployed theories workspace.");
		}

	}

	protected void populateDeployedEntries() throws CoreException {
		deployedEntries.clear();
		IRodinProject deployProject = DB_TCFacade
					.getDeploymentProject(null);
		if(deployProject == null || !deployProject.exists()){
			return;
		}
		IDeployedTheoryRoot[] roots = DB_TCFacade
				.getDeployedTheories(deployProject);
		if (roots != null) {
			List<IDeployedTheoryRoot> okTheories = new ArrayList<IDeployedTheoryRoot>();
			for (IDeployedTheoryRoot root : roots){
				IUseTheory[] useTheories = root.getUsedTheories();
				boolean ok = true;
				for(IUseTheory useTheory : useTheories){
					if(!useTheory.getUsedTheory().exists()){
						ok = false;
					}
				}
				if(ok)
					okTheories.add(root);
			}
			graph.setDeployedRoots(okTheories);
			if (!graph.isErroneous())
				for (IDeployedTheoryRoot root : graph.getDeployedRoots()) {
					deployedEntries.add(new DeployedEntry(root));
				}
			else {
				reset();
			}
		}
	}

	protected void populateAllDeployedExtensions() {
		synchronized (deployedExtensionsMap) {
			updateExtensionsMap();
			allDeployedExtensions.clear();
			for (Set<IFormulaExtension> exts : deployedExtensionsMap.values()) {
				allDeployedExtensions.addAll(exts);
			}
		}
	}

	protected void updateExtensionsMap() {
		Iterator<Entry<DeployedEntry, Set<IFormulaExtension>>> iter = deployedExtensionsMap
				.entrySet().iterator();
		while (iter.hasNext()) {
			if (!deployedEntries.contains(iter.next().getKey())) {
				iter.remove();
			}
		}

	}

	protected void reloadDeployedEntry(IDeployedTheoryRoot deployedRoot)
			throws CoreException {
		if (deployedRoot.exists()) {
			DeployedEntry entry = new DeployedEntry(deployedRoot);
			Set<IDeployedTheoryRoot> needed = graph
					.getNeededTheories(deployedRoot);
			Set<IFormulaExtension> neededExts = new LinkedHashSet<IFormulaExtension>();
			for (IDeployedTheoryRoot root : needed) {
				Set<IFormulaExtension> exts = deployedExtensionsMap.get(new DeployedEntry(root));
				if(exts != null)
					neededExts.addAll(deployedExtensionsMap.get(new DeployedEntry(
							root)));
			}
			FormulaFactory factory = MathExtensionsUtilities
					.getFactoryWithCond();
			factory = factory.withExtensions(neededExts);
			TheoryTransformer transformer = new TheoryTransformer();
			Set<IFormulaExtension> ext = transformer.transform(deployedRoot,
					factory, factory.makeTypeEnvironment());
			deployedExtensionsMap.put(entry, ext);
		}
	}

	protected void populateDeployedExtensions() throws CoreException {
		for (DeployedEntry entry : deployedEntries) {
			reloadIfNecessary(entry);
		}
	}

	protected void processDelta(IRodinElementDelta delta) throws CoreException {
		IRodinElement element = delta.getElement();
		IRodinElementDelta[] affected = delta.getAffectedChildren();
		if (element instanceof IRodinDB) {
			for (IRodinElementDelta d : affected) {
				processDelta(d);
			}
		}
		if (element instanceof IRodinProject) {
			IRodinProject proj = (IRodinProject) element;
			if (proj.getElementName().equals(
					DB_TCFacade.THEORIES_PROJECT)) {
				for (IRodinElementDelta d : affected) {
					processDelta(d);
				}
			}
		}
		if (element instanceof IRodinFile) {
			IRodinFile file = (IRodinFile) element;
			if (file.getRoot() instanceof IDeployedTheoryRoot) {
				IDeployedTheoryRoot root = (IDeployedTheoryRoot) file.getRoot();
				DeployedEntry key = new DeployedEntry(root);
				changedEntries.add(key);
				wsChanged = true;
			}
		}
	}

	public static IFormulaExtensionsWorkspaceManager getDefault() {
		if (manager == null) {
			manager = new WorkspaceManager();
		}
		return manager;
	}
}
