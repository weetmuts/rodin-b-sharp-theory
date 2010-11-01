/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.internal.core.maths;

import org.eventb.core.ast.ExtendedPredicate;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.extension.ICompatibilityMediator;
import org.eventb.core.ast.extension.IExtendedFormula;
import org.eventb.core.ast.extension.IOperatorProperties.FormulaType;
import org.eventb.core.ast.extension.IOperatorProperties.Notation;
import org.eventb.core.ast.extension.IPredicateExtension;
import org.eventb.core.ast.extension.IPriorityMediator;
import org.eventb.core.ast.extension.ITypeCheckMediator;
import org.eventb.core.ast.extension.IWDMediator;
import org.eventb.theory.core.maths.AbstractOperatorExtension;
import org.eventb.theory.core.maths.IOperatorTypingRule;

/**
 * An implementation of a predicate operator extension.
 * @since 1.0
 * 
 * @author maamria
 * 
 */
public class PredicateOperatorExtension extends AbstractOperatorExtension<Predicate>
		implements IPredicateExtension {
	
	public PredicateOperatorExtension(String operatorID, String syntax,
			FormulaType formulaType, Notation notation, String groupID,
			boolean isCommutative, IOperatorTypingRule<Predicate> typingRule,
			Predicate directDefinition, Object source){
		
		super(operatorID, syntax, formulaType, notation, groupID, isCommutative, false, 
				typingRule, directDefinition, source);
	}

	@Override
	public void addPriorities(IPriorityMediator mediator) {
		// Nothing to add ATM
	}

	@Override
	public void addCompatibilities(ICompatibilityMediator mediator) {
		// Nothing to add ATM
	}

	@Override
	public boolean conjoinChildrenWD() {
		return true;
	}

	@Override
	public void typeCheck(ExtendedPredicate predicate,
			ITypeCheckMediator tcMediator) {
		((PredicateOperatorTypingRule)operatorTypingRule).typeCheck(predicate, tcMediator);
	}

	@Override
	public Predicate getWDPredicate(IExtendedFormula formula,
			IWDMediator wdMediator) {
		return operatorTypingRule.getWDPredicate(formula, wdMediator);
	}
	
}
