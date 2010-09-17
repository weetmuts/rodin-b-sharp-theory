/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.internal.core.maths;

import java.util.List;

import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.IExtendedFormula;
import org.eventb.core.ast.extension.IWDMediator;

/**
 * Common protocol for an operator typing rule.
 * 
 * @author maamria
 *
 */
public interface IOperatorTypingRule {
	
	/**
	 * Adds the given operator argument as an argument of operator corresponding to this typing rule.
	 * @param arg the operator argument
	 */
	public void addOperatorArgument(IOperatorArgument arg);
	
	/**
	 * Returns the arity of the operator associated with this typing rule.
	 * @return
	 */
	public int getArity();
	
	/**
	 * Sets the types on which the operator of this typing rule is polymorphic.
	 * 
	 * @param types the polymorphic types
	 */
	public void addTypeParameters(List<GivenType> types);
	
	/**
	 * Sets the well-definedness predicate of the operator to the given predicate.
	 * @param wdPredicate the well-definedness predicate
	 */
	public void setWDPredicate(Predicate wdPredicate);
	
	/**
	 * Returns the well-definedness predicate of the given formula whose formula extension must
	 * the same as the extension of this operator.
	 * @param formula the instance formula
	 * @param wdMediator the well-definedness mediator
	 * @return the instantiated well-definedness condition
	 */
	public Predicate getWDPredicate(IExtendedFormula formula, IWDMediator wdMediator);
	
	public List<GivenType> getTypeParameters();
	
	public List<IOperatorArgument> getOperatorArguments();
	
	public Type getResultantType();
	
	public Predicate getWDPredicate();

}
