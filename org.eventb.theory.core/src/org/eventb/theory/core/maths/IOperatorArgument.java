/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.maths;

import java.util.List;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Type;

/**
 * Common protocol for operator arguments.
 * <p> Each argument has an index, a name and a type.
 * 
 * @since 1.0
 * @author maamria
 *
 */
public interface IOperatorArgument{

	/**
	 * Returns the index of this operator argument.
	 * 
	 * @return the index
	 */
	public int getIndex();

	/**
	 * Returns the name of this operator argument.
	 * 
	 * @return the argumentName
	 */
	public String getArgumentName();

	/**
	 * Returns the type of this operator argument.
	 * 
	 * @return the argumentType
	 */
	public Type getArgumentType();
	
	/**
	 * Returns a free identifier corresponding to this operator argument. The type of the free identifier is
	 * the type of this operator argument.
	 * 
	 * @param factory the formula factory
	 * @return the corresponding free identifier
	 */
	public FreeIdentifier toFreeIdentifier(FormulaFactory factory);
	
	/**
	 * Returns a free identifier who mirrors this operator argument with a different name. 
	 * @param newName the new name
	 * @param factory the formula factory
	 * @return the substitute free identifier
	 */
	public FreeIdentifier makeSubstituter(String newName, FormulaFactory factory);
	
	/**
	 * Returns the list of given types occurring in the type of this argument.
	 * 
	 * @param factory
	 * @param typeEnvironment
	 * @return the list of given types
	 */
	public List<GivenType> getGivenTypes(FormulaFactory factory, ITypeEnvironment typeEnvironment);
}