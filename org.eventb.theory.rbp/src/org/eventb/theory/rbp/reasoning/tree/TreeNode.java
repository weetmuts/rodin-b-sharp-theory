/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.reasoning.tree;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author maamria
 *
 */
abstract class TreeNode<E> implements ITreeNode<E>{
	
	E value;
	TreeNode<E> parent;
	Map<E, TreeNode<E>> childrenMap;
	Set<TreeNode<E>> children;
	
	protected TreeNode(E value, TreeNode<E> parent){
		this.value = value;
		this.parent = parent;
	}
	
	public E getValue(){
		return value;
	}
	
	public TreeNode<E> getParent(){
		return parent;
	}
	
	protected void addChild(E child){
		TreeNode<E> newNode = getNode();
		childrenMap.put(child, newNode);
		children.add(newNode);
	}
	
	
	public void setChildren(Collection<E> col){
		this.childrenMap = new LinkedHashMap<E, TreeNode<E>>();
		this.children = new LinkedHashSet<TreeNode<E>>();
		for (E node : col){
			addChild(node);
		}
	}
	
	public TreeNode<E> getRoot(){
		TreeNode<E> root = this;
		while(root.parent!=null){
			root = root.parent;
		}
		return root;
	}
	
	public Collection<TreeNode<E>> getLeafs(){
		Set<TreeNode<E>> leafs = new LinkedHashSet<TreeNode<E>>();
		if(children == null){
			return null;
		}
		else {
			for (TreeNode<E> node : children){
				leafs.addAll(node.getLeafs());
			}
		}
		return leafs;
	}
	
	public Collection<E> getLeafValues(){
		Collection<TreeNode<E>> nodeLeafs = getLeafs();
		if(nodeLeafs == null){
			return null;
		}
		Set<E> leafs = new LinkedHashSet<E>();
		for (TreeNode<E> node : getLeafs()){
			leafs.add(node.getValue());
		}
		return leafs;
		
	}
	
	public boolean isRoot(){
		return this.equals(getRoot());
	}
	
	protected abstract TreeNode<E> getNode();
}
