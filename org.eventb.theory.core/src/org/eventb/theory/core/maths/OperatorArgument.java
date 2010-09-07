/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.maths;

import org.eventb.core.ast.Type;

/**
 * @author maamria
 *
 */
public class OperatorArgument implements Comparable<OperatorArgument>{

	private String argumentName;
	private Type argumentType;
	private int index;
	
	public OperatorArgument(int index, String argumentName, Type argumentType){
		this.index = index;
		this.argumentName = argumentName;
		this.argumentType = argumentType;
	}

	@Override
	public int compareTo(OperatorArgument o) {
		if(index > o.index){
			return 1;
		}
		else if (index == o.index){
			return 0;
		}
		else
			return -1;
	}
	
	/**
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * @return the argumentName
	 */
	public String getArgumentName() {
		return argumentName;
	}

	/**
	 * @return the argumentType
	 */
	public Type getArgumentType() {
		return argumentType;
	}
}
