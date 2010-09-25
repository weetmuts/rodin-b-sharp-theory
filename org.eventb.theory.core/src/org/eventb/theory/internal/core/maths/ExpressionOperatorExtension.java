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
import org.eventb.core.ast.extension.ExtensionFactory;
import org.eventb.core.ast.extension.ICompatibilityMediator;
import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.core.ast.extension.IExtendedFormula;
import org.eventb.core.ast.extension.IExtensionKind;
import org.eventb.core.ast.extension.IOperatorProperties.FormulaType;
import org.eventb.core.ast.extension.IOperatorProperties.Notation;
import org.eventb.core.ast.extension.IPriorityMediator;
import org.eventb.core.ast.extension.ITypeCheckMediator;
import org.eventb.core.ast.extension.ITypeMediator;
import org.eventb.core.ast.extension.IWDMediator;
import org.eventb.internal.core.ast.extension.ExtensionKind;
import org.eventb.theory.core.maths.AbstractOperatorExtension;
import org.eventb.theory.core.maths.IOperatorTypingRule;

/**
 * @author maamria
 * 
 */
@SuppressWarnings("restriction")
public class ExpressionOperatorExtension extends AbstractOperatorExtension
		implements IExpressionExtension {
	
	public ExpressionOperatorExtension(String operatorID, String syntax,
			FormulaType formulaType, Notation notation, String groupID,
			boolean isCommutative, boolean isAssociative, IOperatorTypingRule typingRule,
			Expression directDefinition, Object source){
		
		super(operatorID, syntax, formulaType, notation, groupID, isCommutative, isAssociative, 
				typingRule, directDefinition, source);
	}
	
	@Override
	public IExtensionKind getKind() {
		return new ExtensionKind(notation, formulaType,
				ExtensionFactory.makeAllExpr(ExtensionFactory.makeArity(
						operatorTypingRule.getArity(), operatorTypingRule.getArity())), 
						isAssociative);

	}

	@Override
	public Predicate getWDPredicate(IExtendedFormula formula,
			IWDMediator wdMediator) {
		return operatorTypingRule.getWDPredicate(formula, wdMediator);
	}
	
	@Override
	public boolean conjoinChildrenWD() {
		return true;
	}

	@Override
	public void addPriorities(IPriorityMediator mediator) {
		// TODO Auto-generated method stub
	}

	@Override
	public void addCompatibilities(ICompatibilityMediator mediator) {
		if (isAssociative)
			mediator.addAssociativity(operatorID);
	}

	@Override
	public boolean verifyType(Type proposedType, Expression[] childExprs,
			Predicate[] childPreds) {
		return ((ExpressionOperatorTypingRule)operatorTypingRule).verifyType(proposedType, childExprs, childPreds);
	}

	@Override
	public Type typeCheck(ExtendedExpression expression,
			ITypeCheckMediator tcMediator) {
		return ((ExpressionOperatorTypingRule)operatorTypingRule).typeCheck(expression, tcMediator);
	}

	@Override
	public Type synthesizeType(Expression[] childExprs, Predicate[] childPreds,
			ITypeMediator mediator) {
		return ((ExpressionOperatorTypingRule)operatorTypingRule).synthesizeType(childExprs, childPreds, mediator);
	}

	@Override
	public boolean isATypeConstructor() {
		return false;
	}
	
	@Override
	public boolean isAssociative() {
		// TODO Auto-generated method stub
		return isAssociative;
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
	public IOperatorTypingRule getTypingRule() {
		// TODO Auto-generated method stub
		return operatorTypingRule;
	}
}
