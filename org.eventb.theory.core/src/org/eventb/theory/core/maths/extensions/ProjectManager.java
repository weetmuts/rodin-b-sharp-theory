/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.maths.extensions;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.theory.core.DatabaseUtilities;
import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.maths.extensions.dependencies.ProjectTheoryGraph;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinElementDelta;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;

/**
 * 
 * @author maamria
 * 
 */
public class ProjectManager {

	private IRodinProject project;
	private ProjectTheoryGraph graph;

	private boolean deployedChanged;
	private boolean scChanged;

	private Map<String, Set<IFormulaExtension>> scExtensionsMap;
	private Map<String, Set<IFormulaExtension>> deployedExtensionsMap;
	private Set<IFormulaExtension> allDeployedExtensions;

	public ProjectManager(IRodinProject project) {
		this.project = project;
		this.scExtensionsMap = new LinkedHashMap<String, Set<IFormulaExtension>>();
		this.deployedExtensionsMap = new LinkedHashMap<String, Set<IFormulaExtension>>();
		this.allDeployedExtensions = new LinkedHashSet<IFormulaExtension>();
		this.graph = new ProjectTheoryGraph();
	}
	
	// for the benefit of models and theories that are project-scoped
	public synchronized Set<IFormulaExtension> getAllDeployedExtensions(){
		return allDeployedExtensions;
	}
	
	public synchronized Set<IFormulaExtension> getNeededTheories(ISCTheoryRoot scRoot){
		Set<ISCTheoryRoot> needed = graph.getNeededTheories(scRoot);
		Set<IFormulaExtension> extensions = new LinkedHashSet<IFormulaExtension>();
		for (ISCTheoryRoot root : needed){
			extensions.addAll(scExtensionsMap.get(root.getComponentName()));
		}
		return extensions;
	}

	public synchronized void reloadDeployedExtensions(FormulaFactory seedFactory)
			throws CoreException {
		deployedExtensionsMap.clear();
		allDeployedExtensions.clear();
		IDeployedTheoryRoot[] deployedRoots = DatabaseUtilities
				.getDeployedTheories(project);
		graph.setDeployedRoots(deployedRoots);

		for (IDeployedTheoryRoot root : graph.getDeployedRoots()) {
			FormulaExtensionsLoader loader = new FormulaExtensionsLoader(root, seedFactory);
			Set<IFormulaExtension> extensions = loader.load();
			deployedExtensionsMap.put(root.getComponentName(), extensions);
			allDeployedExtensions.addAll(extensions);
		}
	}

	public synchronized void reloadDirtyExtensions(final FormulaFactory seedFactory)
			throws CoreException {
		scExtensionsMap.clear();
		ISCTheoryRoot[] scRoots = DatabaseUtilities.getSCTheoryRoots(project,
				new DatabaseUtilities.TheoriesFilter<ISCTheoryRoot>() {

					@Override
					public boolean filter(ISCTheoryRoot theory) {
						// TODO Auto-generated method stub
						return theory.exists();
					}
				});
		graph.setCheckedRoots(scRoots);
		
		for (ISCTheoryRoot root : graph.getCheckedRoots()) {
			FormulaExtensionsLoader loader = new FormulaExtensionsLoader(root, seedFactory);
			Set<IFormulaExtension> extensions = loader.load();
			scExtensionsMap.put(root.getComponentName(), extensions);
		}
	}

	public synchronized void populate(FormulaFactory seedFactory)
			throws CoreException {
		reloadDeployedExtensions(seedFactory);
		reloadDirtyExtensions(seedFactory);
	}

	public void processDelta(IRodinElementDelta delta) throws CoreException {
		IRodinElement element = delta.getElement();
		IRodinElementDelta[] affected = delta.getAffectedChildren();
		if (element instanceof IRodinProject) {
			for (IRodinElementDelta d : affected) {
				processDelta(d);
			}
		}
		if (element instanceof IRodinFile) {
			IRodinFile file = (IRodinFile) element;
			IInternalElement root = file.getRoot();
			if (root instanceof IDeployedTheoryRoot) {
				deployedChanged = true;
			} else if (root instanceof ISCTheoryRoot) {
				scChanged = true;
			}
		}
	}

	/**
	 * Returns whether this manager is managing the <code>MathExtensions</code>
	 * project.
	 * 
	 * @return whether this manager is managing the <code>MathExtensions</code>
	 *         project
	 */
	public boolean managingMathExtensionsProject() {
		return project.getElementName().equals(DatabaseUtilities.THEORIES_PROJECT);
	}

	public boolean hasDeployedChanged() {
		return deployedChanged;
	}

	public void setDeployedChanged(boolean deployedChanged) {
		this.deployedChanged = deployedChanged;
	}

	public boolean hasSCChanged() {
		return scChanged;
	}

	public void setSCChanged(boolean scChanged) {
		this.scChanged = scChanged;
	}

}
