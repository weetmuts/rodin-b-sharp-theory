/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.internal.ast.maths;

import java.util.List;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.maths.AstUtilities;

/**
 * Implementation of operator arguments.
 * <p> An operator argument has an index, a name and a type.
 * 
 * <p> A type of an operator argument may contain type parameters. These will be the types on which 
 * the concerned operator definition is polymorphic.
 * 
 * @since 1.0
 * @author maamria
 *
 */
public class OperatorArgument{

	private String argumentName;
	private Type argumentType;
	private int index;
	
	public OperatorArgument(int index, String argumentName, Type argumentType){
		this.index = index;
		this.argumentName = argumentName;
		this.argumentType = argumentType;
	}
	
	public boolean equals(Object o){
		if(o == this)
			return true;
		if(o == null || !(o instanceof OperatorArgument)){
			return false;
		}
		OperatorArgument other = (OperatorArgument) o;
		return index == other.index && argumentName.equals(other.argumentName) &&
			argumentType.equals(other.argumentType);
	}
	
	public int hashCode(){
		return index*argumentType.hashCode() + argumentName.hashCode();
	}
	
	public String toString(){
		return "("+index +") :"+argumentName +":"+argumentType.toString();
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

	public List<GivenType> getGivenTypes() {
		return AstUtilities.getGivenTypes(argumentType);
	}
}
