/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.internal.core.maths.extensions.graph;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.core.ISCTheoryRoot;

/**
 * @author maamria
 *
 */
public class ProjectDependenciesGraph {

	TheoryDependenciesGraph<ISCTheoryRoot> scGraph;
	TheoryDependenciesGraph<IDeployedTheoryRoot> depGraph;
	
	TheoryGraphFactory factory;
	
	public ProjectDependenciesGraph(){
		factory = TheoryGraphFactory.getFactory();
	}
	
	public void setDeployedRoots(IDeployedTheoryRoot[] roots) throws CoreException{
		depGraph = factory.getGraph(roots);
	}
	
	public void setSCTheoryRoots(ISCTheoryRoot[] roots) throws CoreException{
		scGraph = factory.getGraph(roots);
	}
	
	public void setDeployedRoots(List<IDeployedTheoryRoot> roots) throws CoreException{
		depGraph = factory.getGraph(roots);
	}
	
	public void setSCTheoryRoots(List<ISCTheoryRoot> roots) throws CoreException{
		scGraph = factory.getGraph(roots);
	}
	
	public Set<IDeployedTheoryRoot> getDeployedRoots() {
		return depGraph.getElements();
	}
	
	public Set<ISCTheoryRoot> getSCTheoryRoots() {
		return scGraph.getElements();
	}
	
	public Set<ISCTheoryRoot> getNeededTheories(ISCTheoryRoot root){
		return scGraph.getUpperSet(root);
	}
	
	public Set<IDeployedTheoryRoot> getNeededTheories(IDeployedTheoryRoot root){
		return depGraph.getUpperSet(root);
	}
	
	public Set<IDeployedTheoryRoot> getDependantTheories(IDeployedTheoryRoot root){
		return depGraph.getLowerSet(root);
	}
	
	public Set<IDeployedTheoryRoot> getExecludedTheories(ISCTheoryRoot root) 
	throws CoreException{
		Set<String> execludedNames = scGraph.getUpperSetNames(root);
		Set<IDeployedTheoryRoot> notAllowedRoots = new LinkedHashSet<IDeployedTheoryRoot>();
		for (IDeployedTheoryRoot deployed : depGraph.getElements()){
			if(execludedNames.contains(deployed.getComponentName())){
				notAllowedRoots.add(deployed);
				notAllowedRoots.addAll(depGraph.getLowerSet(deployed));
			}
		}
		return notAllowedRoots;
	}
	
	public Set<IDeployedTheoryRoot> getExecludedTheories(Set<ISCTheoryRoot> roots)
	throws CoreException{
		Set<IDeployedTheoryRoot> execlus = new LinkedHashSet<IDeployedTheoryRoot>();
		for (ISCTheoryRoot root : roots){
			execlus.addAll(getExecludedTheories(root));
		}
		return execlus;
	}
	
	public Set<IDeployedTheoryRoot> getIncludedTheories(ISCTheoryRoot root) 
	throws CoreException{
		Set<IDeployedTheoryRoot> set = depGraph.getElements();
		set.removeAll(getExecludedTheories(root));
		return set;
	}
	
	public Set<IDeployedTheoryRoot> getIncludedTheories(Set<ISCTheoryRoot> roots) 
	throws CoreException{
		Set<IDeployedTheoryRoot> set = depGraph.getElements();
		for (ISCTheoryRoot root : roots){
			set.removeAll(getExecludedTheories(root));
		}
		return set;
	}
}
