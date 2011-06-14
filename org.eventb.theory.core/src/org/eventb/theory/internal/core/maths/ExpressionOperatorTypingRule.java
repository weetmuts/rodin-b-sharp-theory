/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.internal.core.maths;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.InvalidExpressionException;
import org.eventb.core.ast.LanguageVersion;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.IExtendedFormula;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.ast.extension.ITypeCheckMediator;
import org.eventb.core.ast.extension.ITypeMediator;
import org.eventb.theory.core.AstUtilities;
import org.eventb.theory.core.maths.AbstractOperatorTypingRule;
import org.eventb.theory.core.maths.IExpressionTypeChecker;
import org.eventb.theory.core.maths.IOperatorArgument;
import org.eventb.theory.core.maths.IOperatorExtension;
import org.eventb.theory.internal.core.util.MathExtensionsUtilities;

/**
 * @author maamria
 * 
 */
public class ExpressionOperatorTypingRule extends AbstractOperatorTypingRule implements IExpressionTypeChecker {

	protected Type resultantType;
	protected boolean isAssociative;

	public ExpressionOperatorTypingRule(List<IOperatorArgument> operatorArguments, Predicate wdPredicate, Type resultantType, boolean isAssociative) {
		super(operatorArguments, wdPredicate);
		this.resultantType = resultantType;
		this.isAssociative = isAssociative;
		this.typeParameters.addAll(MathExtensionsUtilities.getGivenTypes(resultantType));
	}

	public String toString() {
		return super.toString() + "=>" + resultantType.toString();
	}

	@Override
	public boolean verifyType(Type proposedType, Expression[] childExprs, Predicate[] childPreds) {
		if (childExprs.length != arity && !isAssociative)
			return false;
		Map<GivenType, Type> calculatedInstantiations = new HashMap<GivenType, Type>();
		if (!unifyTypes(resultantType, proposedType, calculatedInstantiations)) {
			return false;
		}
		if (isAssociative) {
			for (int i = 0; i < arity; i++) {
				if (!unifyTypes(resultantType, childExprs[i].getType(), calculatedInstantiations)) {
					return false;
				}
			}
			return true;
		}
		for (int i = 0; i < arity; i++) {
			if (!unifyTypes(operatorArguments.get(i).getArgumentType(), childExprs[i].getType(), calculatedInstantiations)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public Type typeCheck(ExtendedExpression expression, ITypeCheckMediator mediator) {
		Expression[] childExpressions = expression.getChildExpressions();
		if (isAssociative) {
			final Type t = mediator.newTypeVariable();
			for (Expression child : childExpressions) {
				mediator.sameType(child.getType(), t);
			}
			return t;
		}
		if (childExpressions.length != arity) {
			return null;
		}
		Type[] argumentTypesAsVars = new Type[arity];
		HashMap<GivenType, Type> parameterToTypeVarMap = new HashMap<GivenType, Type>();
		for (GivenType givenType : typeParameters) {
			parameterToTypeVarMap.put(givenType, mediator.newTypeVariable());
		}
		for (int i = 0; i < argumentTypesAsVars.length; i++) {
			argumentTypesAsVars[i] = constructPatternType(operatorArguments.get(i).getArgumentType(), parameterToTypeVarMap, mediator);
		}

		for (int i = 0; i < childExpressions.length; i++) {
			Type currentType = childExpressions[i].getType();
			mediator.sameType(argumentTypesAsVars[i], currentType);
		}
		return constructPatternType(resultantType, parameterToTypeVarMap, mediator);
	}

	@Override
	public Type synthesizeType(Expression[] childExprs, Predicate[] childPreds, ITypeMediator mediator) {
		Type[] types = MathExtensionsUtilities.getTypes(childExprs);
		return synthesizeType(types, mediator.getFactory());
	}

	protected Type synthesizeType(Type[] childrenTypes, FormulaFactory factory) {
		if (isAssociative) {
			// associative operators always have 2 or more children
			return childrenTypes[0];
		}

		Expression typeExpression = resultantType.toExpression(factory);
		String rawTypeExp = typeExpression.toString();
		Expression exp = factory.parseExpression(rawTypeExp, LanguageVersion.V2, null).getParsedExpression();
		Map<FreeIdentifier, Expression> typeSubs = getTypeSubstitutions(childrenTypes, factory);
		// FIXED Bug if type substitutions is empty that means we are dealing with a generic operator, 
		// hence need proposed type and type check
		if (typeSubs == null || typeSubs.isEmpty())
			return null;
		ITypeEnvironment typeEnvironment = generateTypeParametersTypeEnvironment(typeSubs, factory);
		exp.typeCheck(typeEnvironment);
		Expression actTypeExpression = exp.substituteFreeIdents(typeSubs, factory);
		try {
			return actTypeExpression.toType();
		} catch (InvalidExpressionException e) {
			e.printStackTrace();
		}
		return null;
	}

	protected Map<FreeIdentifier, Expression> getTypeSubstitutions(Type[] childrenTypes, FormulaFactory factory) {
		Map<FreeIdentifier, Expression> subs = new HashMap<FreeIdentifier, Expression>();
		Map<GivenType, Type> instantiations = new HashMap<GivenType, Type>();
		if (isAssociative) {
			if (!isValidTypeInstantiation(0, childrenTypes[0], instantiations)) {
				return null;
			}
		} else {
			for (int i = 0; i < childrenTypes.length; i++) {

				if (!isValidTypeInstantiation(i, childrenTypes[i], instantiations)) {
					return null;
				}
			}
		}
		for (GivenType gType : instantiations.keySet()) {
			subs.put(factory.makeFreeIdentifier(gType.getName(), null, instantiations.get(gType).toExpression(factory).getType()), instantiations.get(gType).toExpression(factory));
		}
		return subs;
	}

	@Override
	protected Formula<?> unflatten(IExtendedFormula formula, FormulaFactory factory) {
		 IFormulaExtension extension = formula.getExtension();
		 if (((IOperatorExtension) extension).isAssociative())
			 return AstUtilities.unflatten(extension,formula.getChildExpressions(), factory);
		 else 
			 return (Formula<?>) formula;
	}

}
