/*******************************************************************************
 * Copyright (c) 2010-11 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.internal.core.maths.extensions.dependencies;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eventb.core.IEventBRoot;

/**
 * Common protocol for an Event-B roots dependencies graph.
 * 
 * @author maamria
 * 
 * @param E the type of the Event-B root
 *
 */
public interface IDependenciesGraph<E extends IEventBRoot> {

	/**
	 * Sets the elements contained in the nodes of this graph to the given elements.
	 * @param elements the elements to reference in the nodes of the graph
	 * @return whether the elements were set correctly
	 * @throws CycleException if a cycle exists
	 */
	public boolean setElements(E[] elements) throws CycleException;
	
	/**
	 * Sets the elements contained in the nodes of this graph to the given elements.
	 * @param elements the elements to reference in the nodes of the graph
	 * @return whether the elements were set correctly
	 * @throws CycleException if a cycle exists
	 */
	public boolean setElements(Collection<E> elements) throws CycleException;
	
	/**
	 * Returns the elements that are reachable from this element.
	 * @param element the element
	 * @return reachable elements
	 */
	public Set<E> getUpperSet(E element);
	
	/**
	 * Returns the elements that can reach this element.
	 * @param element the element
	 * @return elements that can reach <code>e</code>
	 */
	public Set<E> getLowerSet(E element);
	
	/**
	 * Returns the elements stored in  the nodes of this graph.
	 * 
	 * <p> The elements are ordered according to the partial order defined by this graph.
	 * @return all elements of the nodes
	 */
	public Set<E> getElements();
	
	/**
	 * Returns the set of elements resulting from removing the given element and its
	 * dependent elements from the graph.
	 * @param element the element to exclude
	 * @return the set of left elements
	 */
	public Set<E> exclude(E element);
	
	/**
	 * Returns whether the graph contains a node for the given element.
	 * @param element the element for which to check whether a node exists
	 * @return whether a node for the given element exists in the graph
	 */
	public boolean contains(E element);
	
	/**
	 * A basic implementation of a node that holds a reference to an Event-B root.
	 * @author maamria
	 *
	 * @param <E> the type of the Event-B root
	 */
	static final class Node<E>{
		E element;
		Set<Node<E>> connected;
		
		public Node(E element){
			this.element = element;
			this.connected = new HashSet<Node<E>>();
		}
		
		public String toString(){
			return element.toString();
		}
		
		@SuppressWarnings("unchecked")
		public boolean equals(Object o){
			if(o == this)
				return true;
			if(!(o instanceof Node))
				return false;
			Node<E> other = (Node<E>) o;
			return element.equals(other.element);
		}
		
		/**
		 * Adds the given node to the set of nodes reachable from this node.
		 * @param node the node to add
		 */
		public void addConnectedNode(Node<E> node){
			connected.add(node);
		}
		
		/**
		 * Returns the element referenced by this node.
		 * @return the node element
		 */
		public E getElement(){
			return element;
		}
	}
}
