/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.internal.core.maths.extensions.dependencies;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.runtime.CoreException;
import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.ITheoryRoot;

/**
 * @author maamria
 *
 */
public class WorkspaceDependenciesGraph extends TheoryDependenciesGraph{
	
	/**
	 * Sets the deployed roots to be manipulated through this graph.
	 * @param roots the deployed roots
	 * @return whether the roots have been set properly
	 */
	public boolean setDeployedRoots(IDeployedTheoryRoot[] roots){
		try {
			return setElements(roots);
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
		return getElements();
	}
	
	/**
	 * Returns the set of needed theories by the given SC theory root.
	 * @param root the SC theory root
	 * @return the set of needed deployed roots
	 */
	public Set<IDeployedTheoryRoot> getNeededTheories(ISCTheoryRoot root){
		TreeSet<IDeployedTheoryRoot> set = 
			new TreeSet<IDeployedTheoryRoot>(new Comparator<IDeployedTheoryRoot>() {

				@Override
				public int compare(IDeployedTheoryRoot o1,
						IDeployedTheoryRoot o2) {
					return WorkspaceDependenciesGraph.this.compare(o1, o2);
				}
			});
		try {
			for (IDeployedTheoryRoot dep : root.getRelatedSources()){
				if(!contains(dep)){
					continue;
				}
				set.addAll(getNeededTheories(dep));
				set.add(dep);
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return set;
	}
	
	/**
	 * Returns the set of needed theories by the given theory root. 
	 * @param root the theory root
	 * @return all needed deployed theories
	 */
	public Set<IDeployedTheoryRoot> getNeededTheories(ITheoryRoot root){
		Set<IDeployedTheoryRoot> set = new LinkedHashSet<IDeployedTheoryRoot>();
		IDeployedTheoryRoot exclu = root.getDeployedTheoryRoot();
		// FIXED when theory does not have a deployed counterpart just use all deployed theories
		if(exclu.exists())
			set.addAll(exclude(root.getDeployedTheoryRoot()));
		else 
			set.addAll(getDeployedRoots());
		return set;
	}
	
	/**
	 * Returns the theories that are needed by the given deployed root.
	 * @param root the deployed root
	 * @return all needed theories
	 */
	public Set<IDeployedTheoryRoot> getNeededTheories(IDeployedTheoryRoot root){
		return getUpperSet(root);
	}
	
	/**
	 * Returns the theories that depend on the given deployed theory root.
	 * @param root the deployed root
	 * @return all dependent theories
	 */
	public Set<IDeployedTheoryRoot> getDependantTheories(IDeployedTheoryRoot root){
		return getLowerSet(root);
	}
}
