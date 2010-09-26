/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.internal.core.maths.extensions.graph;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.SortedSet;

import org.eclipse.core.runtime.CoreException;
import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.core.IFormulaExtensionsSource;
import org.eventb.theory.core.ISCTheoryRoot;

/**
 * Common protocol for a graph describing "relationships" between theories. We use the term
 * "relationship" between theories <code>TH0</code> and <code>TH1</code> to denote that
 * <code>TH1</code> depends on <code>TH0</code>. Likewise, We use the term
 * "reverse relationship" between theories <code>TH1</code> and <code>TH0</code> to denote that
 * <code>TH0</code> is depended on by <code>TH0</code>.
 * <code></code>
 * <p> A relationship between deployed theories <code>D1</code> and <code>D0</code> exists if
 * <code>D1</code> uses <code>D0</code>.
 * 
 * <p> A relationship between SC theories <code>T1</code> and <code>T0</code> exists if
 * <code>T1</code> imports <code>T0</code>.
 * 
 * <p> An inverse relationship between deployed theories <code>D0</code> and <code>D1</code> exists if
 * <code>D1</code> uses <code>D0</code>.
 * 
 * <p> An inverse relationship between SC theories <code>T0</code> and <code>T1</code> exists if
 * <code>T1</code> imports <code>T0</code>.
 * 
 * @param E the type of the formula extensions source
 * @see IDeployedTheoryRoot
 * @see ISCTheoryRoot
 * 
 * @author maamria
 *
 */
public interface ITheoryGraph<E extends IFormulaExtensionsSource<E>> {
	
	/**
	 * Adds a vertex to the graph. As a side effect, all theories that <code>vertex</code> depends on
	 * are also added as vertices.
	 * @param vertex the vertex
	 * @return the node corresponding to the vertex
	 * @throws CoreException
	 */
	public Node<E> addVertex(E vertex) throws CoreException;
	
	/**
	 * Returns all source whose node closure includes the given source's node.
	 * @param node the node
	 * @return all sources that relate to the given node
	 */
	public LinkedHashSet<E> antiClosure(E source);
	
	/**
	 * Returns all nodes whose closure includes the given node.
	 * @param node the node
	 * @return all pointers to the given node
	 */
	public SortedSet<Node<E>> antiClosure(Node<E> node);
	
	/**
	 * Returns all sources that are related to the given source.
	 * @param source the source
	 * @return related sources
	 */
	public LinkedHashSet<E> closure(E source);
	
	/**
	 * Returns the closure of the relationships of the given node.
	 * @param node the node
	 * @return all connected nodes to the given node
	 */
	public SortedSet<Node<E>> closure(Node<E> node);
	
	/**
	 * Detects whether a cycle exists in this graph.
	 * @return whether a cycle exists
	 */
	public boolean detectCycle();
	
	/**
	 * Returns the names of the sources in the anticlosure of the given source.
	 * @param source the source
	 * @return anticlosure names
	 */
	public LinkedHashSet<String> getAntiClosureNames(E source);
	
	/**
	 * Returns the names of the sources in the closure of the given source.
	 * @param source the source
	 * @return closure names
	 */
	public LinkedHashSet<String> getClosureNames(E source);
	
	/**
	 * Returns the elements stored in  the nodes of this graph.
	 * @return all elements of the nodes
	 */
	public LinkedHashSet<E> getElements();
	
	/**
	 * Returns the names of the elements stored in the nodes of this graph.
	 * @return the names of elements
	 */
	public LinkedHashSet<String> getNames();
	
	/**
	 * Returns all nodes that have a relationship and reverse-relationship with this node.
	 * @param node the node
	 * @return pointers and closure
	 */
	public SortedSet<Node<E>> related(Node<E> node);
	
	/**
	 * Removes the nodes corresponding to the given sources. As a side effect, all nodes related
	 * to the removed nodes are also removed.
	 * @param sources the extension sources to remove
	 */
	public void remove(List<E> sources);
	
}


