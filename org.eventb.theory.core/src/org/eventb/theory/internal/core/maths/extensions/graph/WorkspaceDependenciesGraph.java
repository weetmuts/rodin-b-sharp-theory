/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.internal.core.maths.extensions.graph;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.runtime.CoreException;
import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.ITheoryRoot;
import org.eventb.theory.core.DB_TCFacade;
import org.eventb.theory.internal.core.util.CoreUtilities;

/**
 * @author maamria
 *
 */
public class WorkspaceDependenciesGraph {

	TheoryDependenciesGraph depGraph;
	boolean isErroneous;
	
	public void setDeployedRoots(IDeployedTheoryRoot[] roots){
		isErroneous = false;
		depGraph = new TheoryDependenciesGraph();
		// no need to check for cycles at this point, gaurantee externally
		for (IDeployedTheoryRoot root : roots){
			try {
				if(root.exists())
					depGraph.addVertex(root);
			} catch (CycleException e) {
				CoreUtilities.log(e, "Cycle exists within DAG.");
				isErroneous = true;
				break;
			}
		}
	}

	public boolean isErroneous(){
		return isErroneous;
	}
	
	public void setDeployedRoots(List<IDeployedTheoryRoot> roots){
		setDeployedRoots(roots.toArray(new IDeployedTheoryRoot[roots.size()]));
	}
	
	public Set<IDeployedTheoryRoot> getDeployedRoots() {
		return depGraph.getElements();
	}
	
	public Set<IDeployedTheoryRoot> getNeededTheories(ISCTheoryRoot root){
		TreeSet<IDeployedTheoryRoot> set = 
			new TreeSet<IDeployedTheoryRoot>(new Comparator<IDeployedTheoryRoot>() {

				@Override
				public int compare(IDeployedTheoryRoot o1,
						IDeployedTheoryRoot o2) {
					try {
						if(DB_TCFacade.doesTheoryUseTheory(o1, o2)){
							return 1;
						}
						if(DB_TCFacade.doesTheoryUseTheory(o2, o1)){
							return -1;
						}
					} catch (CoreException e) {
						e.printStackTrace();
					}
					if(o1.getComponentName().equals(o2.getComponentName())){
						return 0;
					}
					return 1;
				}
			});
		try {
			for (IDeployedTheoryRoot dep :root.getRelatedSources()){
				if(!depGraph.containsNodeFor(dep)){
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
	
	public Set<IDeployedTheoryRoot> getNeededTheories(ITheoryRoot root){
		Set<IDeployedTheoryRoot> set = new LinkedHashSet<IDeployedTheoryRoot>();
		set.addAll(depGraph.execlude(root.getDeployedTheoryRoot()));
		return set;
	}
	
	public Set<IDeployedTheoryRoot> getNeededTheories(IDeployedTheoryRoot root){
		return depGraph.getUpperSet(root);
	}
	
	public Set<IDeployedTheoryRoot> getDependantTheories(IDeployedTheoryRoot root){
		return depGraph.getLowerSet(root);
	}
}
