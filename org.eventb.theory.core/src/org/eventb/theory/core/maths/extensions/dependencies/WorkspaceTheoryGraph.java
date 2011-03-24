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

/**
 * 
 * @author maamria
 *
 */
public class WorkspaceTheoryGraph {
	
	SCTheoriesGraph scGraph;
	DeployedTheoriesGraph deployedGraph;
	
	public WorkspaceTheoryGraph(){
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

}
