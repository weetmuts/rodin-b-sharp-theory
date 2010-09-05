/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.maths.extensions;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.InvalidExpressionException;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;

/**
 * Common protocol for typing polymorphic operators when they are instantiated with concrete arguments.
 * 
 * @author maamria
 *
 */
interface IPolymorphicTypeManipulation {
	
	/**
	 * Verify that the type of the expression proposed is a valid one i.e., synthesised.
	 * @param proposedType the proposed type
	 * @return whether <code>proposedType</code> is the correct type
	 */
	public boolean verifyType(Type proposedType) throws InvalidExpressionException;
	
	/**
	 * Returns the type of the expression that is an instance of this concerned operator based on the definition.
	 * @param definitionType
	 * @return the type
	 */
	public Type synthesiseType(Type definitionType) throws InvalidExpressionException;
	
	/**
	 * Returns the well-definedness predicate of this expression based on the pattern 
	 * well-definedness predicate <code>wdDefinitionPredicate</code>
	 * @param wdDefinitionPredicate
	 * @return the well-definedness predicate
	 */
	public Predicate getWDPredicate(Predicate wdDefinitionPredicate);
	
	/**
	 * Returns the final type of this expression
	 * @return the type
	 * @throws InvalidExpressionException
	 */
	public Type getFinalType() throws InvalidExpressionException;
	
	/**
	 * Unifies the argument type (of the operator) and the corresponding child expression in the instance expression/predicate.
	 * @param argumentType
	 * @param actualType
	 * @return whether the types are matchable
	 */
	public boolean unifyTypes(Type argumentType, Type actualType);
	
	/**
	 * Add an argument and its type to the list of arguments of this operator
	 * @param arg the argument
	 * @param exp the expression
	 * @return whether no clash is encountered
	 */
	public boolean addArgumentMapping(String arg, Expression exp);

}
