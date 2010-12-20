/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.internal.core.maths.extensions.graph;

import static java.util.Collections.unmodifiableSet;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;


/**
 * @author maamria
 *
 */
public abstract class DependenciesGraph<E> implements IDependenciesGraph<E>{

	protected HashMap<E, DependencyNode<E>> map;
	protected SortedSet<DependencyNode<E>> vertices;
	
	public DependenciesGraph(){
		vertices = new TreeSet<DependencyNode<E>>();
		map = new LinkedHashMap<E, DependencyNode<E>>();
	}
	
	/**
	 * Returns the set of elements who have a relationship with the given element.
	 * @param vertex the element
	 * @return all related elements
	 */
	protected abstract Set<E> getEdgesOut(E e);
	
	/**
	 * Returns the dependency node with the given element and its reachable nodes.
	 * @param element the element
	 * @param connected connected nodes
	 * @return the dependency node
	 */
	protected abstract DependencyNode<E> getNode(E element,
			SortedSet<DependencyNode<E>> connected);
	
	/**
	 * Returns the name of the given element.
	 * @param element the element
	 * @return the name
	 */
	protected abstract String getElementName(E element);
	
	@Override
	public DependencyNode<E> addVertex(E vertex) 
	throws CycleException{
		Set<E> out = getEdgesOut(vertex);
		SortedSet<DependencyNode<E>> connected = new TreeSet<DependencyNode<E>>();
		for (E e : out){
			DependencyNode<E> node = addVertex(e);
			connected.add(node);
		}
		DependencyNode<E> currentNode = getNode(vertex, connected);
		vertices.add(currentNode);
		map.put(vertex, currentNode);
		if(detectCycle()){
			throw new CycleException();
		}
		return currentNode;
	}
	
	@Override
	public Set<E> execlude(E element) {
		DependencyNode<E> node = map.get(element);
		if(node == null)
			return getElements();
		SortedSet<E> set = new TreeSet<E>();
		for (DependencyNode<E> dep : execlude(node)){
			set.add(dep.element);
		}
		return set;
	}
	
	protected Set<DependencyNode<E>> execlude(DependencyNode<E> node){
		SortedSet<DependencyNode<E>> set = new TreeSet<DependencyNode<E>>();
		set.addAll(vertices);
		set.remove(node);
		set.removeAll(getLowerSet(node));
		return set;
	}
	
	
	@Override
	public Set<E> getUpperSet(E element) {
		LinkedHashSet<E> set = new LinkedHashSet<E>();
		DependencyNode<E> correspNode = map.get(element);
		if(correspNode == null){
			throw new IllegalArgumentException("Method should only be invoked on an element that is included in the graph.");
		}
		Set<DependencyNode<E>> upperSet = getUpperSet(correspNode);
		for (DependencyNode<E> node : upperSet){
			set.add(node.element);
		}
		return set;
	}

	protected Set<DependencyNode<E>> getUpperSet(DependencyNode<E> node){
		SortedSet<DependencyNode<E>> upper = new TreeSet<DependencyNode<E>>();
		for (DependencyNode<E> n : node.connectedNodes){
			upper.add(n);
			upper.addAll(getUpperSet(n));
		}
		return unmodifiableSet(upper);
	}
	
	@Override
	public Set<String> getUpperSetNames(E element) {
		Set<E> elements = getUpperSet(element);
		LinkedHashSet<String> result = new LinkedHashSet<String>();
		for (E e : elements){
			result.add(getElementName(e));
		}
		return result;
	}

	@Override
	public Set<E> getLowerSet(E element) {
		LinkedHashSet<E> set = new LinkedHashSet<E>();
		DependencyNode<E> correspNode = map.get(element);
		if(correspNode == null){
			throw new IllegalArgumentException("Method should only be invoked on an element that is included in the graph.");
		}
		for (DependencyNode<E> node : getLowerSet(correspNode)){
			set.add(node.element);
		}
		return set;
	}
	
	public Set<DependencyNode<E>> getLowerSet(
			DependencyNode<E> node) {
		SortedSet<DependencyNode<E>> lower = new TreeSet<DependencyNode<E>>();
		for (DependencyNode<E> n : vertices){
			if(getUpperSet(n).contains(node)){
				lower.add(n);
			}
		}
		return unmodifiableSet(lower);
	}

	@Override
	public Set<String> getLowerSetNames(E element) {
		Set<E> elements = getLowerSet(element);
		LinkedHashSet<String> result = new LinkedHashSet<String>();
		for (E e : elements){
			result.add(getElementName(e));
		}
		return result;
	}

	@Override
	public Set<E> getElements() {
		LinkedHashSet<E> set = new LinkedHashSet<E>();
		for (DependencyNode<E> node : vertices){
			set.add(node.element);
		}
		return set;
	}

	@Override
	public Set<String> getNames() {
		LinkedHashSet<String> set = new LinkedHashSet<String>();
		for(DependencyNode<E> node : vertices){
			set.add(getElementName(node.element));
		}
		return set;
	}

	@Override
	public E getElement(String name) {
		for(E e : map.keySet()){
			if(getElementName(e).equals(name)){
				return e;
			}
		}
		return null;
	}
	/**
	 * Detects whether a cycle exists in this graph.
	 * @return whether a cycle exists
	 */
	protected boolean detectCycle() {
		for (DependencyNode<E> n1 : vertices){
			for(DependencyNode<E> n2 : vertices){
				if(n1 != n2 && getUpperSet(n1).contains(n2) && getUpperSet(n2).contains(n1)){
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public boolean containsNodeFor(E element) {
		return map.containsKey(element);
	}
	

}
