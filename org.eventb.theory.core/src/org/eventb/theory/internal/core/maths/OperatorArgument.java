/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.internal.core.maths;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Type;

/**
 * @author maamria
 *
 */
public class OperatorArgument implements IOperatorArgument{

	private String argumentName;
	private Type argumentType;
	private int index;
	
	public OperatorArgument(int index, String argumentName, Type argumentType){
		this.index = index;
		this.argumentName = argumentName;
		this.argumentType = argumentType;
	}

	@Override
	public int compareTo(IOperatorArgument o) {
		if(index > o.getIndex()){
			return 1;
		}
		else if (index == o.getIndex()){
			return 0;
		}
		else
			return -1;
	}
	
	public int getIndex() {
		return index;
	}

	public String getArgumentName() {
		return argumentName;
	}

	public Type getArgumentType() {
		return argumentType;
	}
	
	public FreeIdentifier toFreeIdentifier(FormulaFactory factory){
		return factory.makeFreeIdentifier(argumentName, null, argumentType);
	}

	public FreeIdentifier makeSubstituter(String newName, FormulaFactory factory){
		return factory.makeFreeIdentifier(newName, null, argumentType);
	}

	@Override
	public List<GivenType> getGivenTypes(FormulaFactory factory,
			ITypeEnvironment typeEnvironment) {
		List<GivenType> result = new ArrayList<GivenType>();
		Set<GivenType> types = argumentType.toExpression(factory).getGivenTypes();
		for (GivenType type : types){
			result.add(type);
		}
		return result;
	}
}
