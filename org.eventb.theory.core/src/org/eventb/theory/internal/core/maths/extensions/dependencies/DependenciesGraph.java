/*******************************************************************************
 * Copyright (c) 2010-11 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.internal.core.maths.extensions.dependencies;

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

	protected Map<E, Node<E>> verticesMap;
	protected SortedSet<Node<E>> vertices;
	
	public DependenciesGraph(){
		verticesMap = new LinkedHashMap<E, Node<E>>();
		vertices = new TreeSet<Node<E>>(getPartialOrder());
	}
	
	/**
	 * Returns the relation used to order the Event-B roots stored in this graph.
	 * @return the partial order
	 */
	protected abstract Comparator<Node<E>> getPartialOrder();
	
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
			verticesMap.put(element, new Node<E>(element));
		}
		for (Node<E> vertex : verticesMap.values()){
			E element = vertex.element;
			E[] connectedElements = getEdgesOut(element);
			for (E e : connectedElements){
				// some stated but missing dependencies
				if(!verticesMap.containsKey(e)){
					return false;
				}
				vertex.addConnectedNode(verticesMap.get(e));
			}
		}
		if(detectCycle()){
			throw new CycleException();
		}
		return true;
	}

	@Override
	public Set<E> getUpperSet(E element) {
		LinkedHashSet<E> set = new LinkedHashSet<E>();
		Node<E> correspNode = verticesMap.get(element);
		if(correspNode == null){
			throw new IllegalArgumentException(
					"Method should only be invoked on an element that is included in the graph.");
		}
		Set<Node<E>> upperSet = getUpperSet(correspNode);
		for (Node<E> node : upperSet){
			set.add(node.element);
		}
		return set;
	}
	
	private Set<Node<E>> getUpperSet(Node<E> node){
		SortedSet<Node<E>> upper = new TreeSet<Node<E>>(getPartialOrder());
		for (Node<E> n : node.connected){
			upper.add(n);
			upper.addAll(getUpperSet(n));
		}
		return upper;
	}

	@Override
	public Set<E> getLowerSet(E element) {
		Set<E> set = new LinkedHashSet<E>();
		Node<E> correspNode = verticesMap.get(element);
		if(correspNode == null){
			throw new IllegalArgumentException(
					"Method should only be invoked on an element that is included in the graph.");
		}
		for (Node<E> node : getLowerSet(correspNode)){
			set.add(node.element);
		}
		return set;
	}
	
	private Set<Node<E>> getLowerSet(Node<E> node){
		SortedSet<Node<E>> lower = new TreeSet<Node<E>>(getPartialOrder());
		for (Node<E> n : vertices){
			if(getUpperSet(n).contains(node)){
				lower.add(n);
			}
		}
		return lower;
	}

	@Override
	public Set<E> getElements() {
		Set<E> set = new LinkedHashSet<E>();
		for (Node<E> node : vertices){
			set.add(node.element);
		}
		return set;
	}

	@Override
	public Set<E> exclude(E element) {
		Node<E> node = verticesMap.get(element);
		if(node == null)
			throw new IllegalArgumentException(
				"Method should only be invoked on an element that is included in the graph.");
		Set<E> set = new LinkedHashSet<E>();
		for (Node<E> dep : exclude(node)){
			set.add(dep.element);
		}
		return set;
	}
	
	private Set<Node<E>> exclude(Node<E> node){
		SortedSet<Node<E>> set = new TreeSet<Node<E>>(getPartialOrder());
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
	
	/**
	 * Detects whether a cycle exists in this graph (O(N^2) complexity N number of nodes).
	 * @return whether a cycle exists
	 */
	private boolean detectCycle() {
		for (Node<E> n1 : vertices){
			for(Node<E> n2 : vertices){
				if(n1 != n2 && getUpperSet(n1).contains(n2) && getUpperSet(n2).contains(n1)){
					return true;
				}
			}
		}
		return false;
	}
}
