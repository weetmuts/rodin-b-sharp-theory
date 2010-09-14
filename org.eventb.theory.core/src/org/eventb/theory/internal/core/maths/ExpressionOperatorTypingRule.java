/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.internal.core.maths;

import java.util.HashMap;
import java.util.Map;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.InvalidExpressionException;
import org.eventb.core.ast.LanguageVersion;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.ast.extension.ITypeCheckMediator;
import org.eventb.core.ast.extension.ITypeMediator;
import org.eventb.theory.internal.core.util.MathExtensionsUtilities;

/**
 * @author maamria
 *
 */
public class ExpressionOperatorTypingRule extends AbstractOperatorTypingRule<IExpressionExtension>
implements IExpressionTypeChecker{
	
	protected Type resultantType;
	
	public ExpressionOperatorTypingRule(IExpressionExtension extension, Type resultantType) {
		super(extension);
		this.resultantType = resultantType;
	}

	@Override
	public boolean verifyType(Type proposedType, Expression[] childExprs,
			Predicate[] childPreds) {
		if(childExprs.length != arity)
			return false;
		Map<GivenType, Type> calculatedInstantiations = new HashMap<GivenType, Type>();
		if(!unifyTypes(resultantType, proposedType, calculatedInstantiations)){
			return false;
		}
		for(int i = 0 ; i < arity ; i++){
			if(!unifyTypes(argumentsTypes.get(i).getArgumentType(), childExprs[i].getType(), calculatedInstantiations)){
				return false;
			}
		}
		return true;
	}

	@Override
	public Type typeCheck(ExtendedExpression expression,
			ITypeCheckMediator mediator) {
		Expression[] childExpressions = expression.getChildExpressions();
		if (childExpressions.length != arity) {
			return null;
		}
		Type[] argumentTypesAsVars = new Type[arity];
		HashMap<Type, Type> parameterToTypeVarMap = new HashMap<Type, Type>();
		for (int i = 0; i < typeParameters.size(); i++) {
			parameterToTypeVarMap.put(typeParameters.get(i),
					mediator.newTypeVariable());
		}
		for (int i = 0; i < argumentTypesAsVars.length; i++) {
			argumentTypesAsVars[i] = MathExtensionsUtilities
					.constructPatternType(argumentsTypes.get(i).getArgumentType(),
							parameterToTypeVarMap, mediator);
		}

		for (int i = 0; i < childExpressions.length; i++) {
			Type currentType = childExpressions[i].getType();
			mediator.sameType(argumentTypesAsVars[i], currentType);
		}
		return MathExtensionsUtilities.constructPatternType(resultantType,
				parameterToTypeVarMap, mediator);
	}

	@Override
	public Type synthesizeType(Expression[] childExprs, Predicate[] childPreds,
			ITypeMediator mediator) {
		Type[] types = MathExtensionsUtilities.getTypes(childExprs);
		return synthesizeType(types, mediator.getFactory());
	}
	
	protected Type synthesizeType(Type[] childrenTypes, FormulaFactory factory){
		Expression typeExpression = resultantType.toExpression(factory);
		String rawTypeExp = typeExpression.toString();
		Expression exp = factory.parseExpression(rawTypeExp,
				LanguageVersion.V2, null).getParsedExpression();
		Map<FreeIdentifier, Expression> typeSubs = getTypeSubstitutions(childrenTypes, factory);
		if(typeSubs == null)
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
	
	

	@Override
	protected IExpressionExtension getExtension(IFormulaExtension extension) {
		return (IExpressionExtension) extension;
	}
	
}
