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
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.extension.IExtendedFormula;
import org.eventb.core.ast.extension.IWDMediator;

/**
 * Common protocol for an operator typing rule. This should encapsulate the intuitive understanding of a typical type inference rule
 * where operator arguments types are provided as well as the resultant type if any.
 * <p> A typing rule provides information about arguments types, the arity of the operator and any resultant types,
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
	 * Returns the arity of the operator associated with this typing rule.
	 * @return the arity
	 */
	public int getArity();
	
	/**
	 * Returns the well-definedness predicate of the given formula whose formula extension must
	 * the same as the extension of this operator.
	 * @param formula the instance formula
	 * @param wdMediator the well-definedness mediator
	 * @return the instantiated well-definedness condition
	 */
	public Predicate getWDPredicate(IExtendedFormula formula, IWDMediator wdMediator);
	
	/**
	 * Returns the well-definedness predicate general pattern for instances of the concerned operator.
	 * @return the pattern well-definedness
	 */
	public Predicate getWDPredicate();
	
	/**
	 * Returns the list of operator arguments.
	 * @return operator arguments
	 */
	public List<IOperatorArgument> getOperatorArguments();
	
}
