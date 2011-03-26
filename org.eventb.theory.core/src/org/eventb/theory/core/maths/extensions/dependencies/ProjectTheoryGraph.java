/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.maths.extensions.dependencies;

import java.util.List;
import java.util.Set;

import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.core.ISCTheoryRoot;

/**
 * 
 * @author maamria
 *
 */
public class ProjectTheoryGraph {
	
	SCTheoriesGraph scGraph;
	DeployedTheoriesGraph deployedGraph;
	
	public ProjectTheoryGraph(){
		scGraph = new SCTheoriesGraph();
		deployedGraph = new DeployedTheoriesGraph();
	}
	
	/**
	 * Sets the deployed roots to be manipulated through this graph.
	 * @param roots the deployed roots
	 * @return whether the roots have been set properly
	 */
	public boolean setDeployedRoots(IDeployedTheoryRoot[] roots){
		try {
			return deployedGraph.setElements(roots);
		} catch (CycleException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Sets the deployed roots to be manipulated through this graph.
	 * @param roots the deployed roots
	 * @return whether the roots have been set properly
	 */
	public boolean setDeployedRoots(List<IDeployedTheoryRoot> roots){
		return setDeployedRoots(roots.toArray(new IDeployedTheoryRoot[roots.size()]));
	}
	
	/**
	 * Returns the deployed roots manipulated through this graph. The roots are sorted according to the defined
	 * order on deployed theories.
	 * @return the deployed roots
	 */
	public Set<IDeployedTheoryRoot> getDeployedRoots() {
		return deployedGraph.getElements();
	}
	
	/**
	 * Sets the sc theory roots to be manipulated through this graph.
	 * @param roots the checked roots
	 * @return whether the roots have been set properly
	 */
	public boolean setCheckedRoots(ISCTheoryRoot[] roots){
		try {
			return scGraph.setElements(roots);
		} catch (CycleException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Sets the sc theory roots to be manipulated through this graph.
	 * @param roots the sc theory roots
	 * @return whether the roots have been set properly
	 */
	public boolean setCheckedRoots(List<ISCTheoryRoot> roots){
		return setCheckedRoots(roots.toArray(new ISCTheoryRoot[roots.size()]));
	}
	
	/**
	 * Returns the sc theory roots manipulated through this graph. The roots are sorted according to the defined
	 * order on sc theory theories.
	 * @return the sc theory roots
	 */
	public Set<ISCTheoryRoot> getCheckedRoots() {
		return scGraph.getElements();
	}
	
	/**
	 * Returns the set of needed theories by the given SC theory root.
	 * @param root the SC theory root
	 * @return the set of needed sc roots
	 */
	public Set<ISCTheoryRoot> getNeededTheories(ISCTheoryRoot root){
		Set<ISCTheoryRoot> set = scGraph.getUpperSet(root);
		// this is needed because clients looking for this are most likely dealing with file dependent on the root 
		set.add(root);
		return set;
	}
	
	/**
	 * Returns the theories that are needed by the given deployed root.
	 * @param root the deployed root
	 * @return all needed theories
	 */
	public Set<IDeployedTheoryRoot> getNeededTheories(IDeployedTheoryRoot root){
		return deployedGraph.getUpperSet(root);
	}
	
	/**
	 * Returns the theories that depend on the given deployed theory root.
	 * @param root the deployed root
	 * @return all dependent theories
	 */
	public Set<IDeployedTheoryRoot> getDependentTheories(IDeployedTheoryRoot root){
		return deployedGraph.getLowerSet(root);
	}

}
