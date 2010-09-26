/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *		University of Southampton - Initial API and implementation
 *******************************************************************************/
package org.eventb.theory.internal.core.maths.extensions.graph;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.core.runtime.CoreException;
import org.eventb.theory.core.IFormulaExtensionsSource;

/**
 * An implementation of a graph structure that specifies the relationship between
 * theories (USE and IMPORT).
 * 
 * @param <E>
 * 
 * @author maamria
 *
 */
public  abstract class TheoryGraph<E extends IFormulaExtensionsSource<E>>
implements ITheoryGraph<E>{

	protected HashMap<E, Node<E>> map;
	protected SortedSet<Node<E>> vertices;
	
	public TheoryGraph(){
		vertices = new TreeSet<Node<E>>();
		map = new LinkedHashMap<E, Node<E>>();
	}
	
	public Node<E> addVertex(E vertex) throws CoreException{
		Set<E> out = getEdgesOut(vertex);
		SortedSet<Node<E>> connected = new TreeSet<Node<E>>();
		for (E e : out){
			Node<E> node = addVertex(e);
			connected.add(node);
		}
		Node<E> currentNode = getNode(vertex, connected);
		vertices.add(currentNode);
		map.put(vertex, currentNode);
		return currentNode;
	}
	
	@Override
	public LinkedHashSet<E> antiClosure(E source) {
		LinkedHashSet<E> set = new LinkedHashSet<E>();
		Node<E> node = map.get(source);
		for (Node<E> n : antiClosure(node)){
			set.add(n.node);
		}
		return set;
	}
	
	@Override
	public SortedSet<Node<E>> antiClosure(
			Node<E> node) {
		SortedSet<Node<E>> pointers = new TreeSet<Node<E>>();
		for (Node<E> n : vertices){
			if(closure(n).contains(node)){
				pointers.add(n);
			}
		}
		return pointers;
	}
	
	@Override
	public LinkedHashSet<E> closure(E source) {
		LinkedHashSet<E> set = new LinkedHashSet<E>();
		for (Node<E> node : closure(map.get(source))){
			set.add(node.node);
		}
		return set;
	}
	
	public SortedSet<Node<E>> closure(Node<E> node){
		SortedSet<Node<E>> closure = new TreeSet<Node<E>>();
		for (Node<E> n : node.connectedNodes){
			closure.add(n);
			closure.addAll(closure(n));
		}
		return closure;
	}
	
	
	public boolean detectCycle(){
		for (Node<E> n1 : vertices){
			for(Node<E> n2 : vertices){
				if(n1 != n2 && closure(n1).contains(n2) && closure(n2).contains(n1)){
					return true;
				}
			}
		}
		
		return false;
	}

	@Override
	public LinkedHashSet<String> getAntiClosureNames(E source) {
		LinkedHashSet<E> sources = antiClosure(source);
		LinkedHashSet<String> result = new LinkedHashSet<String>();
		for (E e : sources){
			result.add(e.getElementName());
		}
		return result;
	}

	
	@Override
	public LinkedHashSet<String> getClosureNames(E source) {
		LinkedHashSet<E> sources = closure(source);
		LinkedHashSet<String> result = new LinkedHashSet<String>();
		for (E e : sources){
			result.add(e.getElementName());
		}
		return result;
	}

	/**
	 * Returns the set of elements who have a relationship with the given element.
	 * @param vertex the element
	 * @return all related elements
	 * @throws CoreException
	 */
	protected Set<E> getEdgesOut(
			E vertex) throws CoreException {
		E[] sources = vertex.getRelatedSources();
		Set<E> set = new LinkedHashSet<E>();
		set.addAll(Arrays.asList(sources));
		return set;
	}
	
	public LinkedHashSet<E> getElements(){
		LinkedHashSet<E> set = new LinkedHashSet<E>();
		for (Node<E> node : vertices){
			set.add(node.node);
		}
		return set;
	}

	protected String getName(E vertex) {
		return vertex.getElementName();
	}

	public LinkedHashSet<String> getNames(){
		LinkedHashSet<String> set = new LinkedHashSet<String>();
		for(Node<E> node : vertices){
			set.add(getName(node.node));
		}
		return set;
	}

	protected abstract Node<E> getNode(E source,
			SortedSet<Node<E>> connected);

	@Override
	public SortedSet<Node<E>> related(
			Node<E> node) {
		SortedSet<Node<E>> closure = closure(node);
		closure.addAll(antiClosure(node));
		return closure;
	}
	
	@Override
	public void remove(List<E> sources){
		Set<Node<E>> set = new LinkedHashSet<Node<E>>();
		for (E source : sources){
			Node<E> node = map.get(source);
			Set<Node<E>> related = related(node);
			set.addAll(related);
			set.add(node);
		}
		for(Node<E> node : set){
			vertices.remove(node);
			map.remove(node.node);
		}
	}

}
