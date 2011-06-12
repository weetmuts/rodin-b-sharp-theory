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

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedPredicate;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.ITypeCheckMediator;
import org.eventb.theory.core.maths.AbstractOperatorTypingRule;
import org.eventb.theory.core.maths.IOperatorArgument;
import org.eventb.theory.core.maths.IPredicateTypeChecker;

/**
 * @author maamria
 *
 */
public class PredicateOperatorTypingRule extends AbstractOperatorTypingRule<Predicate>
implements IPredicateTypeChecker{

	/**
	 * @param extension
	 */
	public PredicateOperatorTypingRule(List<IOperatorArgument> operatorArguments, Predicate wdPredicate) {
		super(operatorArguments, wdPredicate);
	}

	@Override
	public void typeCheck(ExtendedPredicate predicate,
			ITypeCheckMediator mediator) {
		Expression[] childExpressions = predicate.getChildExpressions();
		Type[] argumentTypesAsVars = new Type[arity];
		HashMap<GivenType, Type> parameterToTypeVarMap = new HashMap<GivenType, Type>();
		for (int i = 0; i < typeParameters.size(); i++) {
			parameterToTypeVarMap.put(typeParameters.get(i),
					mediator.newTypeVariable());
		}
		for (int i = 0; i < argumentTypesAsVars.length; i++) {
			argumentTypesAsVars[i] = 
					constructPatternType(operatorArguments.get(i).getArgumentType(),
							parameterToTypeVarMap, mediator);
		}
		for (int i = 0; i < childExpressions.length; i++) {
			Type currentType = childExpressions[i].getType();
			mediator.sameType(argumentTypesAsVars[i], currentType);
		}
		
	}
}
