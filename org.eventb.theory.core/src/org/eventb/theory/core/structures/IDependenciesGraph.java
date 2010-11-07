/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.structures;

import java.util.Set;


/**
 * Common protocol for a dependency graph specified as a Directed Acyclic Graph.
 * 
 * @author maamria
 *
 */
public interface IDependenciesGraph<E> {

	/**
	 * Adds a vertex to the graph. As a side effect, all elements that <code>vertex</code> depends on
	 * are also added as vertices.
	 * @param vertex the vertex
	 * @return the node corresponding to the vertex
	 * @throws CycleException
	 */
	public DependencyNode<E> addVertex(E vertex) throws CycleException;
	
	/**
	 * Returns the elements that are reachable from this element.
	 * @param element the element
	 * @return reachable elements
	 */
	public Set<E> getUpperSet(E element);
	
	/**
	 * Returns the names of the elements that are reachable from this element.
	 * @param element the element
	 * @return reachable elements names
	 */
	public Set<String> getUpperSetNames(E element);
	
	/**
	 * Returns the elements that can reach this element.
	 * @param element the element
	 * @return elements that can reach <code>e</code>
	 */
	public Set<E> getLowerSet(E element);
	
	/**
	 * Returns the names of the elements that can reach this element.
	 * @param element the element
	 * @return names of elements that can reach <code>e</code>
	 */
	public Set<String> getLowerSetNames(E element);
	
	/**
	 * Returns the elements stored in  the nodes of this graph.
	 * 
	 * <p> The elements are ordered according to the partial order defined by this graph.
	 * @return all elements of the nodes
	 */
	public Set<E> getElements();
	
	/**
	 * Returns the names of the elements stored in the nodes of this graph.
	 * @return the names of elements
	 */
	public Set<String> getNames();
	
	/**
	 * Returns the element whose name is the given name.
	 * @param name the name of the element
	 * @return the element
	 */
	public E getElement(String name);
	
	/**
	 * Returns the set of elements resulting from removing the given element and its
	 * dependant elements from the graph.
	 * @param element the element
	 * @return the set of left elements
	 */
	public Set<E> execlude(E element);
	
	/**
	 * Returns whether the graph contains a node for the given element.
	 * @param element the element for which to check whether a node exists
	 * @return whether a node for the given element exists in the graph
	 */
	public boolean containsNodeFor(E element);
	
}
