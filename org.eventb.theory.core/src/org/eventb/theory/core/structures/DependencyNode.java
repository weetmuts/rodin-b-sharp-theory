/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.structures;

import java.util.SortedSet;

/**
 * Common protocol for a dependency node. Each node stores an element, and has a reference to all
 * nodes to which it is connected.
 * 
 * <p> A partial order exists between nodes. A node <code>n1</code> is said to be greather than node <code>n2</code>
 * iff <code>n2</code> is connected to <code>n1</code>. In this case, <code>n2</code> is said to be less than 
 * <code>n1</code>. If <code>n1</code> is not greater nor less than <code>n2</code>, we say that the two nodes are
 * independent.
 * 
 * @author maamria
 * 
 * @since 0.5
 *
 */
public abstract class DependencyNode<E> implements Comparable<DependencyNode<E>>{

	E element;
	SortedSet<DependencyNode<E>> connectedNodes;
	
	public DependencyNode(E element, SortedSet<DependencyNode<E>> connectedNodes){
		this.element = element;
		this.connectedNodes = connectedNodes;
	}
	
	protected abstract int compare(E e1, E e2);
	
	protected abstract String toString(E e);
		

	@Override
	public int compareTo(DependencyNode<E> node) {
		return compare(element, node.element);
	}
	
	@Override
	public String toString(){
		return toString(element)+connectedNodes.toString();
	}

}
