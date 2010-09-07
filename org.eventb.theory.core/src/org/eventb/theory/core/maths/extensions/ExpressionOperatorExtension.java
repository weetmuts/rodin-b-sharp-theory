/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.maths.extensions;

import java.util.HashMap;
import java.util.List;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.ExtensionFactory;
import org.eventb.core.ast.extension.ICompatibilityMediator;
import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.core.ast.extension.IExtensionKind;
import org.eventb.core.ast.extension.IOperatorProperties.FormulaType;
import org.eventb.core.ast.extension.IOperatorProperties.Notation;
import org.eventb.core.ast.extension.IPriorityMediator;
import org.eventb.core.ast.extension.ITypeCheckMediator;
import org.eventb.core.ast.extension.ITypeMediator;
import org.eventb.internal.core.ast.extension.ExtensionKind;
import org.eventb.theory.core.maths.ExpressionOperatorTypingRule;
import org.eventb.theory.core.maths.OperatorArgument;

/**
 * @author maamria
 * 
 */
@SuppressWarnings("restriction")
public class ExpressionOperatorExtension extends AbstractOperatorExtension<IExpressionExtension>
		implements IExpressionExtension {

	private boolean isAssociative = false;

	public ExpressionOperatorExtension(
			String operatorID, String syntax, FormulaType formulaType,
			Notation notation, boolean isAssociative, boolean isCommutative,
			Expression directDefinition, Predicate wdCondition,
			HashMap<String, OperatorArgument> opArguments, List<GivenType> typeParameters) {
		super(operatorID, syntax, formulaType, notation, isCommutative,
				directDefinition, wdCondition, opArguments);

		this.isAssociative = isAssociative;
		this.operatorTypeRule = new ExpressionOperatorTypingRule(this, directDefinition.getType());
		List<OperatorArgument> sortedOperatorArguments = 
			MathExtensionsUtilities.getSortedList(opArguments.values());
		for(OperatorArgument arg : sortedOperatorArguments){
			this.operatorTypeRule.addOperatorArgument(arg);
		}
		this.operatorTypeRule.addTypeParameters(typeParameters);
		this.operatorTypeRule.setWDPredicate(wdCondition);
	}

	@Override
	public IExtensionKind getKind() {
		return new ExtensionKind(notation, formulaType,
				ExtensionFactory.makeAllExpr(ExtensionFactory.makeArity(
						opArguments.size(), opArguments.size())), isAssociative);

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
		return getTypingRule().verifyType(proposedType, childExprs, childPreds);
	}

	@Override
	public Type typeCheck(ExtendedExpression expression,
			ITypeCheckMediator tcMediator) {
		return getTypingRule().typeCheck(expression, tcMediator);
	}

	@Override
	public Type synthesizeType(Expression[] childExprs, Predicate[] childPreds,
			ITypeMediator mediator) {
		return getTypingRule().synthesizeType(childExprs, childPreds, mediator);
	}

	@Override
	public boolean isATypeConstructor() {
		return false;
	}

	protected ExpressionOperatorTypingRule getTypingRule(){
		return (ExpressionOperatorTypingRule) operatorTypeRule;
	}
}
