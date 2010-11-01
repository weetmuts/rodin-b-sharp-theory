/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.reasoning.tree;

import java.util.Collection;

/**
 * Common interface for trees with no restrictions on elements.
 * 
 * @author maamria
 *
 */
interface ITreeNode<E> {
	
	/**
	 * Returns the value stored in this node.
	 * @return the value
	 */
	E getValue();
	
	/**
	 * Returns the parent of this node.
	 * @return the parent or <code>null</code> if this node is root
	 */
	TreeNode<E> getParent();
	
	/**
	 *	Adds the given values as children of this node.
	 *
	 * @param col the children, must not be <code>null</code>
	 */
	void setChildren(Collection<E> col);
	
	/**
	 * Returns the root of the tree to which this node belongs.
	 * @return the tree root
	 */
	TreeNode<E> getRoot();
	
	/**
	 * Returns the leafs of this tree node.
	 * @return the leafs or <code>null</code> if this node is a leaf itself
	 */
	Collection<TreeNode<E>> getLeafs();
	
	/**
	 * returns the leaf values of this tree node.
	 * @return the leaf values or <code>null</code> if this node is a leaf itself
	 */
	Collection<E> getLeafValues();
	
	/**
	 * Returns whether this node is the root of the tree.
	 * @return whether this node is the root of the tree
	 */
	boolean isRoot();

}
