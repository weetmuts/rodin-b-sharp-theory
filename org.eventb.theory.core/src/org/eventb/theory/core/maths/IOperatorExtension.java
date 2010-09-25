/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.maths;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.ast.extension.IOperatorProperties.FormulaType;
import org.eventb.core.ast.extension.IOperatorProperties.Notation;

/**
 * Common protocol for an operator extension. Additional methods are added for convenience when
 * checking the operator properties.
 * 
 * @author maamria
 *
 */
public interface IOperatorExtension extends IFormulaExtension{

	/**
	 * Returns the condition that checks whether this operator is indeed commutative.
	 * @param factory the formula factory
	 * @param typeEnvironment the type environment
	 * @return the commutativity condition
	 */
	public Predicate getCommutativityChecker(FormulaFactory factory, ITypeEnvironment typeEnvironment);
	
	/**
	 * Returns the condition that checks that the well-definedness condition supplied by the user is indeed stronger
	 * than the well-definedness condition of the direct definition.
	 * @param factory the formula factory
	 * @param typeEnvironment the type environment
	 * @return the WD strength condition
	 */
	public Predicate getWellDefinednessChecker(FormulaFactory factory, ITypeEnvironment typeEnvironment);
	
	/**
	 * Returns the condition that checks whether this operator is indeed associative.
	 * @param factory the formula factory
	 * @param typeEnvironment the type environment
	 * @return the associativity condition
	 */
	public Predicate getAssociativityChecker(FormulaFactory factory, ITypeEnvironment typeEnvironment);
	
	/**
	 * Returns whether this operator is associative.
	 * @return operator associativity
	 */
	public boolean isAssociative();
	
	/**
	 * Returns the formula type of this operator.
	 * @return the formula type
	 */
	public FormulaType getFormulaType();
	
	/**
	 * Returns the notation of this operator.
	 * @return the notation
	 */
	public Notation getNotation();
	
	/**
	 * Returns the typing rule of this operator.
	 * @return the typing rule
	 */
	public IOperatorTypingRule getTypingRule();
	
}
