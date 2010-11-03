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
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.LanguageVersion;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.ITypeCheckMediator;
import org.eventb.theory.core.maths.AbstractOperatorTypingRule;
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
	public PredicateOperatorTypingRule(Predicate directDefinition, Predicate wdPredicate) {
		super(directDefinition, wdPredicate, false);
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
			argumentTypesAsVars[i] = 
					constructPatternType(argumentsTypes.get(i).getArgumentType(),
							parameterToTypeVarMap, mediator);
		}
		for (int i = 0; i < childExpressions.length; i++) {
			Type currentType = childExpressions[i].getType();
			mediator.sameType(argumentTypesAsVars[i], currentType);
		}
		
	}

	@Override
	protected Predicate getParsedFormula(String raw, FormulaFactory factory) {
		IParseResult result = factory.parsePredicate(raw, LanguageVersion.V2, raw);
		if(result.hasProblem()){
			return null;
		}
		Predicate newPred = result.getParsedPredicate();
		return newPred;
	}

	
}
