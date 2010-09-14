/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.internal.core.maths;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.ITypeCheckMediator;
import org.eventb.core.ast.extension.ITypeMediator;

/**
 * Common protocol for an extended expression type checker.
 * 
 * <p> The type checker must be able to synthesise a type of an extended expression based on the types
 * of its children.
 * <p> The type checker must be able to verify if a proposed type is indeed an acceptable type for a given
 * expression defined by its children.
 * <p> The type checker must be able to type check an extended expression.
 * 
 * @author maamria
 *
 */
public interface IExpressionTypeChecker {

	/**
	 * Verifies whether the proposed type is indeed a valid type for the extended expression with given
	 * children.
	 * @param proposedType
	 * 		the proposed type
	 * @param childExprs
	 * 		the children expressions
	 * @param childPreds
	 * 		the children predicates
	 * @return
	 */
	public boolean verifyType(Type proposedType, Expression[] childExprs,
			Predicate[] childPreds);
	
	/**
	 * Type checks the extended expression.
	 * 
	 * @param expression the expression to type check
	 * @param tcMediator the type check mediator
	 * @return the type of the given expression
	 */
	public Type typeCheck(ExtendedExpression expression,
			ITypeCheckMediator tcMediator);
	
	/**
	 * Synthesise a type of an extended expression based on the types of its children.
	 * 
	 * @param childExprs
	 * 		the children expressions
	 * @param childPreds
	 * 		the children predicates
	 * @param mediator
	 * 		the type mediator
	 * @return
	 * 		the symbolic type of the given expression
	 */
	public abstract Type synthesizeType(Expression[] childExprs, Predicate[] childPreds,
			ITypeMediator mediator);
	
}
