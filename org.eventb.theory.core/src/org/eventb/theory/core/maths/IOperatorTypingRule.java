/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.maths;

import java.util.List;

import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.extension.IExtendedFormula;
import org.eventb.core.ast.extension.IWDMediator;

/**
 * Common protocol for an operator typing rule.
 * <p> A typing rule provides information about arguments types, the arity of the operator,
 * 
 * @since 1.0
 * 
 * @param <F> the type of the operator extension (Expression or Predicate)
 * 
 * @author maamria
 *
 */
public interface IOperatorTypingRule<F extends Formula<F>> {
	
	/**
	 * Adds the given operator argument as an argument of operator corresponding to this typing rule.
	 * @param arg the operator argument
	 */
	public void addOperatorArgument(IOperatorArgument arg);
	
	/**
	 * Returns the arity of the operator associated with this typing rule.
	 * @return the arity
	 */
	public int getArity();
	
	/**
	 * Sets the types on which the operator of this typing rule is polymorphic.
	 * 
	 * @param types the polymorphic types
	 */
	public void addTypeParameters(List<GivenType> types);
	
	/**
	 * Returns the well-definedness predicate of the given formula whose formula extension must
	 * the same as the extension of this operator.
	 * @param formula the instance formula
	 * @param wdMediator the well-definedness mediator
	 * @return the instantiated well-definedness condition
	 */
	public Predicate getWDPredicate(IExtendedFormula formula, IWDMediator wdMediator);
	
	/**
	 * Expands the definition of this operator if the given formula is an 
	 * extended formula whose root is this operator.
	 * @param extension the formula extension
	 * @param extendedFormula the formula
	 * @param factory the formula factory
	 * @return the rewritten formula
	 */
	public F expandDefinition(IOperatorExtension<F> extension, F extendedFormula, FormulaFactory factory);
	
	/**
	 * Returns the list of operator arguments.
	 * @return operator arguments
	 */
	public List<IOperatorArgument> getOperatorArguments();
	
	/**
	 * Returns the pattern WD predicate of this operator.
	 * @return pattern WD predicate
	 */
	public Predicate getWDPredicate();

}
