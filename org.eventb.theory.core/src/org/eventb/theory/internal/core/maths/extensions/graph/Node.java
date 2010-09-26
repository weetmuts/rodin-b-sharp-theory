/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.internal.core.maths.extensions.graph;

import java.util.SortedSet;

import org.eventb.theory.core.IFormulaExtensionsSource;

/**
 * A basic implementation of a node.
 * 
 * <p> A node has an extension source, and a set of nodes whose extension sources are depended on
 * by this node's extension source. 
 * 
 * @author maamria
 *
 * @param <T> the type of the formula extension source
 */
public abstract class Node<T extends 
		IFormulaExtensionsSource<T>> implements Comparable<Node<T>>{
	
	T node;
	SortedSet<Node<T>> connectedNodes;
	
	public Node(T node, SortedSet<Node<T>> connectedNodes){
		this.node = node;
		this.connectedNodes = connectedNodes;
	}

	/**
	 * Compares the two theories (extension sources). A theory <code>thisTheory</code> is said to be
	 * greater than <code>otherTheory</code> iff <code>otherTheory</code> depends on <code>thisTheory</code>.
	 * Two theories are said to be equal iffnoa relationship or reverse relationship exists between them.
	 * @param thisTheory 
	 * @param otherTheory
	 * @return comparison result
	 */
	protected abstract int compare(T thisTheory, T otherTheory);
	
	// TODO this may not be consistent with equals(Object)
	@Override
	public int compareTo(Node<T> otherNode) {
		// TODO Auto-generated method stub
		return compare(node, otherNode.node);
	}
	
	public String toString(){
		return node.toString() + connectedNodes.toString();
	}
	
}
