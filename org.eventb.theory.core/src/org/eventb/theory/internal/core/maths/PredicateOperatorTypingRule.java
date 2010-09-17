/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.internal.core.maths;

import java.util.HashMap;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedPredicate;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.ITypeCheckMediator;
import org.eventb.theory.internal.core.util.MathExtensionsUtilities;

/**
 * @author maamria
 *
 */
public class PredicateOperatorTypingRule extends AbstractOperatorTypingRule
implements IPredicateTypeChecker{

	/**
	 * @param extension
	 */
	public PredicateOperatorTypingRule(Predicate wdPredicate) {
		super(wdPredicate);
	}

	@Override
	public void typeCheck(ExtendedPredicate predicate,
			ITypeCheckMediator mediator) {
		Expression[] childExpressions = predicate.getChildExpressions();
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
		
	}

	@Override
	public Type getResultantType() {
		// TODO Auto-generated method stub
		// no type
		return null;
	}
	
	@Override
	public Predicate getWDPredicate() {
		// TODO Auto-generated method stub
		return wdPredicate;
	}

	
}
