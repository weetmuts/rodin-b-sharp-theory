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
import org.eventb.core.ast.extension.ExtensionFactory;
import org.eventb.core.ast.extension.ICompatibilityMediator;
import org.eventb.core.ast.extension.IExtendedFormula;
import org.eventb.core.ast.extension.IExtensionKind;
import org.eventb.core.ast.extension.IOperatorProperties.FormulaType;
import org.eventb.core.ast.extension.IOperatorProperties.Notation;
import org.eventb.core.ast.extension.IPredicateExtension;
import org.eventb.core.ast.extension.IPriorityMediator;
import org.eventb.core.ast.extension.ITypeCheckMediator;
import org.eventb.core.ast.extension.IWDMediator;
import org.eventb.internal.core.ast.extension.ExtensionKind;
import org.eventb.theory.core.maths.AbstractOperatorExtension;
import org.eventb.theory.core.maths.IOperatorTypingRule;

/**
 * @author maamria
 * 
 */
@SuppressWarnings("restriction")
public class PredicateOperatorExtension extends AbstractOperatorExtension
		implements IPredicateExtension {
	
	public PredicateOperatorExtension(String operatorID, String syntax,
			FormulaType formulaType, Notation notation, String groupID,
			boolean isCommutative, IOperatorTypingRule typingRule,
			Predicate directDefinition, Object source){
		
		super(operatorID, syntax, formulaType, notation, groupID, isCommutative, false, 
				typingRule, directDefinition, source);
	}

	@Override
	public IExtensionKind getKind() {
		return new ExtensionKind(notation, formulaType,
				ExtensionFactory.makeAllExpr(ExtensionFactory.makeArity(
						operatorTypingRule.getArity(), operatorTypingRule.getArity())), false);
	}

	@Override
	public void addPriorities(IPriorityMediator mediator) {
		// TODO Auto-generated method stub
	}

	@Override
	public void addCompatibilities(ICompatibilityMediator mediator) {
		// TODO Auto-generated method stub
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
	
	public IOperatorTypingRule getTypingRule(){
		return  operatorTypingRule;
	}

	@Override
	public boolean isAssociative() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public FormulaType getFormulaType() {
		// TODO Auto-generated method stub
		return formulaType;
	}

	@Override
	public Notation getNotation() {
		// TODO Auto-generated method stub
		return notation;
	}

	@Override
	public Predicate getWDPredicate(IExtendedFormula formula,
			IWDMediator wdMediator) {
		return operatorTypingRule.getWDPredicate(formula, wdMediator);
	}

}
