/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.internal.ast.maths;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.ExtensionFactory;
import org.eventb.core.ast.extension.ICompatibilityMediator;
import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.core.ast.extension.IExtendedFormula;
import org.eventb.core.ast.extension.IExtensionKind;
import org.eventb.core.ast.extension.IOperatorProperties.Notation;
import org.eventb.core.ast.extension.IPriorityMediator;
import org.eventb.core.ast.extension.ITypeCheckMediator;
import org.eventb.core.ast.extension.ITypeMediator;
import org.eventb.core.ast.extension.IWDMediator;
import org.eventb.core.ast.maths.OperatorExtensionProperties;
import org.eventb.internal.core.ast.extension.ExtensionKind;

/**
 * An implementation of an expression operator extension.
 * @since 1.0
 * @author maamria
 * 
 */
@SuppressWarnings("restriction")
public class ExpressionOperatorExtension extends AbstractOperatorExtension
		implements IExpressionExtension {

	public ExpressionOperatorExtension(OperatorExtensionProperties properties,
			boolean isCommutative, boolean isAssociative,
			IOperatorTypingRule typingRule,
			Object source) {

		super(properties, isCommutative, isAssociative, typingRule,source);
	}

	@Override
	public IExtensionKind getKind() {
		if(properties.getNotation().equals(Notation.INFIX) && isAssociative){
			return new ExtensionKind(properties.getNotation(), properties.getFormulaType(), 
					ExtensionFactory.TWO_OR_MORE_EXPRS, true);
		}
		return super.getKind();

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
		// Nothing to add ATM
	}

	@Override
	public void addCompatibilities(ICompatibilityMediator mediator) {
		if (isAssociative)
			mediator.addAssociativity(getId());
	}

	@Override
	public boolean verifyType(Type proposedType, Expression[] childExprs,
			Predicate[] childPreds) {
		return ((ExpressionOperatorTypingRule) operatorTypingRule).verifyType(
				proposedType, childExprs, childPreds);
	}

	@Override
	public Type typeCheck(ExtendedExpression expression,
			ITypeCheckMediator tcMediator) {
		return ((ExpressionOperatorTypingRule) operatorTypingRule).typeCheck(
				expression, tcMediator);
	}

	@Override
	public Type synthesizeType(Expression[] childExprs, Predicate[] childPreds,
			ITypeMediator mediator) {
		return ((ExpressionOperatorTypingRule) operatorTypingRule)
				.synthesizeType(childExprs, childPreds, mediator);
	}

	@Override
	public boolean isATypeConstructor() {
		return false;
	}
	
}
