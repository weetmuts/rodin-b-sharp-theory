/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.maths;

import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.ast.extension.IOperatorProperties.Notation;

/**
 * Common protocol for an operator extension. Additional methods are added for convenience when
 * checking the operator properties.
 * 
 * @since 1.0
 * 
 * @param <F> the type of the formula
 * 
 * @author maamria
 *
 */

public interface IOperatorExtension<F extends Formula<F>> extends IFormulaExtension{

	/**
	 * Returns the condition that checks whether this operator is indeed commutative.
	 * <p> This method should only be called when generating proof obligations regarding this operator.
	 * @param factory the formula factory
	 * @param typeEnvironment the type environment
	 * @return the commutativity condition
	 */
	public Predicate getCommutativityChecker(FormulaFactory factory, ITypeEnvironment typeEnvironment);
	
	/**
	 * Returns the condition that checks that the well-definedness condition supplied by the user is indeed stronger
	 * than the well-definedness condition of the direct definition.
	 * <p> This method should only be called when generating proof obligations regarding this operator.
	 * @param factory the formula factory
	 * @param typeEnvironment the type environment
	 * @return the WD strength condition
	 */
	public Predicate getWellDefinednessChecker(FormulaFactory factory, ITypeEnvironment typeEnvironment);
	
	/**
	 * Returns the condition that checks whether this operator is indeed associative.
	 * <p> This method should only be called when generating proof obligations regarding this operator.
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
	 * Returns whether this operator is commutative.
	 * @return operator commutativity
	 */
	public boolean isCommutative();
	
	/**
	 * Returns the notation of this operator.
	 * @return the notation
	 */
	public Notation getNotation();
	
	/**
	 * Expands the definition of this operator if the given formula is an 
	 * extended formula whose root is this operator.
	 * @param extendedFormula the formula
	 * @param factory the formula factory
	 * @return the rewritten formula
	 */
	public F expandDefinition(F extendedFormula, FormulaFactory factory);
	
}
