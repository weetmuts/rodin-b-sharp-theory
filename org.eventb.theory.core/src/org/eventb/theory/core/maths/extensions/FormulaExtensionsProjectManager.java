/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.maths.extensions;

import static org.eventb.theory.internal.core.util.MathExtensionsUtilities.COND;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IEventBRoot;
import org.eventb.core.IPRRoot;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.core.IInternalTheory;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.TheoryCoreFacade;
import org.eventb.theory.internal.core.maths.extensions.TheoryTransformer;
import org.eventb.theory.internal.core.maths.extensions.graph.ProjectDependenciesGraph;
import org.eventb.theory.internal.core.util.DeployUtilities;
import org.eventb.theory.internal.core.util.MathExtensionsUtilities;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;

/**
 * @author maamria
 * 
 */
public abstract class FormulaExtensionsProjectManager implements
		IFormulaExtensionsProjectManager {

	protected IRodinProject project;
	protected ProjectDependenciesGraph projectGraph;

	protected Map<DeployedEntry, Set<IFormulaExtension>> deployedExtensionsMap;
	protected Set<IFormulaExtension> allDeployedExtensions;

	protected Set<DeployedEntry> deployedEntries;
	protected Set<DeployedEntry> changedEntries;

	protected boolean isVoid = true;

	public FormulaExtensionsProjectManager(IRodinProject project)
			throws CoreException {
		this.project = project;
		this.isVoid = false;

		this.deployedExtensionsMap = Collections
				.synchronizedMap(new LinkedHashMap<DeployedEntry, Set<IFormulaExtension>>());
		this.allDeployedExtensions = Collections
				.synchronizedSet(new LinkedHashSet<IFormulaExtension>());

		this.deployedEntries = new LinkedHashSet<FormulaExtensionsProjectManager.DeployedEntry>();
		this.changedEntries = new LinkedHashSet<FormulaExtensionsProjectManager.DeployedEntry>();

		this.projectGraph = new ProjectDependenciesGraph();
		setGraph(new ISCTheoryRoot[0]);

		populateDeployedEntries();
		populateDeployedExtensions();
		populateAllDeployedExtensions();
	}

	@Override
	public Set<IFormulaExtension> getDeployedExtensions() throws CoreException {
		for (DeployedEntry entry : deployedEntries){
			reloadIfNecessary(entry);
		}
		populateAllDeployedExtensions();
		return allDeployedExtensions;
	}

	@Override
	public boolean isVoid() {
		return isVoid;
	}

	@Override
	public Set<IFormulaExtension> getDeployedExtensions(
			ISCTheoryRoot execludedTheory) throws CoreException {
		Set<IFormulaExtension> set = new LinkedHashSet<IFormulaExtension>();
		Set<String> execluded = new LinkedHashSet<String>();
		execluded.add(execludedTheory.getComponentName());
		for (ISCTheoryRoot thy : projectGraph
				.getNeededTheories(execludedTheory)) {
			execluded.add(thy.getComponentName());
		}
		synchronized (deployedExtensionsMap) {
			for (DeployedEntry entry : deployedExtensionsMap.keySet()) {
				if (execluded.contains(entry.name)) {
					continue;
				}
				reloadIfNecessary(entry);
				set.addAll(deployedExtensionsMap.get(entry));
			}
		}
		return set;
	}

	public Set<IFormulaExtension> getDirtyExtensions(ISCTheoryRoot theory)
			throws CoreException {
		Set<IFormulaExtension> set = new LinkedHashSet<IFormulaExtension>();
		setGraph(new ISCTheoryRoot[]{theory});
		Set<IDeployedTheoryRoot> roots = projectGraph
				.getIncludedTheories(theory);
		Set<ISCTheoryRoot> scRoots = projectGraph.getNeededTheories(theory);
		for (IDeployedTheoryRoot root : roots) {
			DeployedEntry entry = new DeployedEntry(root);
			reloadIfNecessary(entry);
			set.addAll(deployedExtensionsMap.get(entry));
		}
		scRoots.add(theory);
		FormulaFactory factory = MathExtensionsUtilities.getFactoryWithCond();
		factory = factory.withExtensions(set);
		for (ISCTheoryRoot root : scRoots) {
			TheoryTransformer transformer = new TheoryTransformer();
			Set<IFormulaExtension> nested = transformer.transform(root,
					factory, factory.makeTypeEnvironment());
			factory = factory.withExtensions(nested);
			set.addAll(nested);

		}
		set.remove(COND);
		return set;
	}

	@Override
	public Set<IFormulaExtension> getProofFileExtensions(IPRRoot proofFile)
			throws CoreException {
		IInternalTheory theories[] = proofFile
				.getChildrenOfType(IInternalTheory.ELEMENT_TYPE);
		if (theories.length == 0) {
			// get the PS root extensions
			return proofFile.getPSRoot().getFormulaFactory().getExtensions();
		}
		Set<IFormulaExtension> extensions = new LinkedHashSet<IFormulaExtension>();
		FormulaFactory factory = MathExtensionsUtilities.getFactoryWithCond();
		for (IInternalTheory theory : theories) {
			TheoryTransformer transformer = new TheoryTransformer();
			Set<IFormulaExtension> set = transformer.transform(theory, factory,
					factory.makeTypeEnvironment());
			factory = factory.withExtensions(set);
		}
		extensions.addAll(factory.getExtensions());
		extensions.remove(COND);
		return extensions;
	}

	@Override
	public void setProofFileExtensions(IPRRoot proofFile) throws CoreException {
		if (TheoryCoreFacade.originatedFromTheory(proofFile.getRodinFile())) {
			ISCTheoryRoot scRoot = TheoryCoreFacade.getSCTheory(
					proofFile.getComponentName(), project);
			setGraph(new ISCTheoryRoot[]{scRoot});
			Set<ISCTheoryRoot> needed = projectGraph.getNeededTheories(scRoot);
			needed.add(scRoot);
			Set<IDeployedTheoryRoot> neededDeployed = projectGraph
					.getIncludedTheories(scRoot);
			for (IDeployedTheoryRoot dep : neededDeployed) {
				IInternalTheory internal = proofFile.getInternalElement(
						IInternalTheory.ELEMENT_TYPE, dep.getComponentName());
				internal.create(null, null);
				DeployUtilities.copyMathematicalExtensions(internal, dep, null);
			}
			for (ISCTheoryRoot sc : needed) {
				IInternalTheory internal = proofFile.getInternalElement(
						IInternalTheory.ELEMENT_TYPE, sc.getComponentName());
				internal.create(null, null);
				DeployUtilities.copyMathematicalExtensions(internal, sc, null);
			}
		} else {
			Set<IDeployedTheoryRoot> neededDeployed = projectGraph
					.getDeployedRoots();
			for (IDeployedTheoryRoot dep : neededDeployed) {
				IInternalTheory internal = proofFile.getInternalElement(
						IInternalTheory.ELEMENT_TYPE, dep.getComponentName());
				internal.create(null, null);
				DeployUtilities.copyMathematicalExtensions(internal, dep, null);
			}
		}

	}

	@Override
	public Set<IRodinFile> getCommonFiles(IEventBRoot root)
			throws CoreException {
		Set<IRodinFile> files = new LinkedHashSet<IRodinFile>();
		for (DeployedEntry dep : deployedEntries) {
			files.add(dep.deployedRoot.getRodinFile());
		}
		return files;
	}

	protected void setGraph(ISCTheoryRoot[] scRoots) throws CoreException {
		this.projectGraph.setDeployedRoots(TheoryCoreFacade
				.getDeployedTheories(project));
		this.projectGraph.setSCTheoryRoots(scRoots);
	}

	protected void reloadIfNecessary(DeployedEntry entry) throws CoreException{
		if(changedEntries.contains(entry)){
			reloadDeployedEntry(entry.deployedRoot);
			changedEntries.remove(entry);
		}
		
	}
	
	protected abstract void populateDeployedEntries() throws CoreException;

	protected abstract void reloadDeployedEntry(IDeployedTheoryRoot deployedRoot) throws CoreException;

	protected abstract void populateAllDeployedExtensions();

	protected abstract void populateDeployedExtensions() throws CoreException;

	protected static class DeployedEntry {
		IDeployedTheoryRoot deployedRoot;
		String name;

		public DeployedEntry(IDeployedTheoryRoot deployedRoot) {
			this.deployedRoot = deployedRoot;
			this.name = deployedRoot.getComponentName();
		}

		public boolean equals(Object o) {
			if (o instanceof DeployedEntry) {
				return name.equals(((DeployedEntry) o).name);
			}
			return false;
		}

		public int hashCode() {
			return name.hashCode();
		}

	}

}
