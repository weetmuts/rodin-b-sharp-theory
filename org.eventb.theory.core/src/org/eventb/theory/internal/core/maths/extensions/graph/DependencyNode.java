/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.internal.core.maths.extensions.graph;

import java.util.SortedSet;

/**
 * @author maamria
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
