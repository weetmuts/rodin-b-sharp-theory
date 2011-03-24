/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.maths.extensions.dependencies;

import java.util.HashSet;
import java.util.Set;

import org.eventb.core.IEventBRoot;

/**
 * A basic implementation of a node that holds a reference to an Event-B root.
 * @author maamria
 *
 * @param <E> the type of the Event-B root
 */
final class DependencyNode<E extends IEventBRoot> {
	
	E element;
	Set<DependencyNode<E>> connected;
	
	public DependencyNode(E element){
		this.element = element;
		this.connected = new HashSet<DependencyNode<E>>();
	}
	
	public String toString(){
		return element.toString();
	}
	
	@SuppressWarnings("unchecked")
	public boolean equals(Object o){
		if(o == this)
			return true;
		if(!(o instanceof DependencyNode))
			return false;
		DependencyNode<E> other = (DependencyNode<E>) o;
		return element.equals(other.element);
	}
	
	/**
	 * Adds the given node to the set of nodes reachable from this node.
	 * @param node the node to add
	 */
	public void addConnectedNode(DependencyNode<E> node){
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
