/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.maths.extensions;

import java.util.HashMap;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.InvalidExpressionException;
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

/**
 * @author maamria
 *
 */
@SuppressWarnings("restriction")
public class ExpressionOperatorExtension extends AbstractOperatorExtension implements IExpressionExtension{

	private boolean isAssociative = false;
	boolean isCommutative = false;
	
	
	public ExpressionOperatorExtension(FormulaFactory factory,
			String operatorID, String syntax, FormulaType formulaType,
			Notation notation, boolean isAssociative, boolean isCommutative,
			Formula<?> directDefinition, Predicate wdCondition, 
			HashMap<String, Type> opArguments) {
		super(factory, operatorID, syntax, formulaType, notation, directDefinition, wdCondition, opArguments);
		
		this.isAssociative = isAssociative;
		this.isCommutative = isCommutative;
		
	}

	@Override
	public IExtensionKind getKind() {
		return new ExtensionKind(notation, formulaType,
				ExtensionFactory.makeAllExpr(ExtensionFactory
						.makeArity(opArguments.size(),
								opArguments.size())), isAssociative);

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
	public boolean verifyType(Type proposedType,
			Expression[] childExprs, Predicate[] childPreds) {
		try {
			return instantiations.verifyType(proposedType);
		} catch (InvalidExpressionException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public Type typeCheck(ExtendedExpression expression,
			ITypeCheckMediator tcMediator) {
		Expression[] children = expression.getChildExpressions();
		Expression directDefExpr = (Expression) directDefinition;
		if (children.length != opArguments.size()) {
			return null;
		}
		String[] arguments = opArguments.keySet().toArray(
				new String[children.length]);
		for (int i = 0; i < arguments.length; i++) {
			instantiations.addArgumentMapping(arguments[i],
					children[i]);
		}
		Type[] argumentTypes = opArguments.values().toArray(
				new Type[arguments.length]);
		for (int i = 0; i < children.length; i++) {
			if (!instantiations.unifyTypes(argumentTypes[i],
					children[i].getType())) {
				tcMediator.sameType(argumentTypes[i],
						children[i].getType());
				return null;
			}
		}
		try {
			return instantiations.synthesiseType(directDefExpr
					.getType());
		} catch (InvalidExpressionException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Type synthesizeType(Expression[] childExprs,
			Predicate[] childPreds, ITypeMediator mediator) {

		try {
			return instantiations.getFinalType();
		} catch (InvalidExpressionException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean isATypeConstructor() {
		return false;
	}

}
