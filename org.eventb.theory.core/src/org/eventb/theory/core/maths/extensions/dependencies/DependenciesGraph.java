/*******************************************************************************
 * Copyright (c) 2010-11 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.maths.extensions.dependencies;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eventb.core.IEventBRoot;

/**
 * @author maamria
 *
 */
public abstract class DependenciesGraph<E extends IEventBRoot> implements IDependenciesGraph<E>{

	protected Map<E, DependencyNode<E>> verticesMap;
	protected SortedSet<DependencyNode<E>> vertices;
	
	public DependenciesGraph(){
		verticesMap = new LinkedHashMap<E, DependencyNode<E>>();
		vertices = new TreeSet<DependencyNode<E>>(getPartialOrder());
	}
	
	/**
	 * Returns the relation used to order the Event-B roots stored in this graph.
	 * @return the partial order
	 */
	protected abstract Comparator<DependencyNode<E>> getPartialOrder();
	
	/**
	 * Returns the elements which are reachable from the given elements.
	 * @param element the element
	 * @return all reachable elements
	 */
	protected abstract E[] getEdgesOut(E element);
	
	@Override
	public boolean setElements(E[] elements) throws CycleException {
		startOver();
		if(elements == null){
			return false;
		}
		Collection<E> col = Arrays.asList(elements);
		return setElements(col);
	}

	@Override
	public boolean setElements(Collection<E> elements) throws CycleException {
		startOver();
		// no elements to begin with
		if(elements == null){
			return false;
		}
		for (E element : elements){
			verticesMap.put(element, new DependencyNode<E>(element));
		}
		for (DependencyNode<E> vertex : verticesMap.values()){
			E element = vertex.element;
			E[] connectedElements = getEdgesOut(element);
			for (E e : connectedElements){
				// some stated but missing dependencies
				if(!verticesMap.containsKey(e)){
					return false;
				}
				vertex.addConnectedNode(verticesMap.get(e));
			}
			//  FIXED Bug: the set of vertices was not augmented with the new vertex
			vertices.add(vertex);
		}
		return true;
	}

	@Override
	public Set<E> getUpperSet(E element) {
		LinkedHashSet<E> set = new LinkedHashSet<E>();
		DependencyNode<E> correspNode = verticesMap.get(element);
		if(correspNode == null){
			throw new IllegalArgumentException(
					"Method should only be invoked on an element that is included in the graph.");
		}
		Set<DependencyNode<E>> upperSet = getUpperSet(correspNode);
		for (DependencyNode<E> node : upperSet){
			set.add(node.element);
		}
		return set;
	}
	
	private Set<DependencyNode<E>> getUpperSet(DependencyNode<E> node){
		SortedSet<DependencyNode<E>> upper = new TreeSet<DependencyNode<E>>(getPartialOrder());
		for (DependencyNode<E> n : node.connected){
			upper.add(n);
			upper.addAll(getUpperSet(n));
		}
		return upper;
	}

	@Override
	public Set<E> getLowerSet(E element) {
		Set<E> set = new LinkedHashSet<E>();
		DependencyNode<E> correspNode = verticesMap.get(element);
		if(correspNode == null){
			throw new IllegalArgumentException(
					"Method should only be invoked on an element that is included in the graph.");
		}
		for (DependencyNode<E> node : getLowerSet(correspNode)){
			set.add(node.element);
		}
		return set;
	}
	
	private Set<DependencyNode<E>> getLowerSet(DependencyNode<E> node){
		SortedSet<DependencyNode<E>> lower = new TreeSet<DependencyNode<E>>(getPartialOrder());
		for (DependencyNode<E> n : vertices){
			if(getUpperSet(n).contains(node)){
				lower.add(n);
			}
		}
		return lower;
	}

	@Override
	public Set<E> getElements() {
		Set<E> set = new LinkedHashSet<E>();
		for (DependencyNode<E> node : vertices){
			set.add(node.element);
		}
		return set;
	}

	@Override
	public Set<E> exclude(E element) {
		DependencyNode<E> node = verticesMap.get(element);
		if(node == null)
			throw new IllegalArgumentException(
				"Method should only be invoked on an element that is included in the graph.");
		Set<E> set = new LinkedHashSet<E>();
		for (DependencyNode<E> dep : exclude(node)){
			set.add(dep.element);
		}
		return set;
	}
	
	private Set<DependencyNode<E>> exclude(DependencyNode<E> node){
		SortedSet<DependencyNode<E>> set = new TreeSet<DependencyNode<E>>(getPartialOrder());
		set.addAll(vertices);
		set.remove(node);
		set.removeAll(getLowerSet(node));
		return set;
	}

	@Override
	public boolean contains(E element) {
		return verticesMap.containsKey(element);
	}

	private void startOver(){
		vertices.clear();
		verticesMap.clear();
	}
	
}
