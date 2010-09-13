/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.maths;

import java.util.HashMap;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedPredicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.ast.extension.IPredicateExtension;
import org.eventb.core.ast.extension.ITypeCheckMediator;
import org.eventb.theory.core.maths.extensions.MathExtensionsFacilitator;

/**
 * @author maamria
 *
 */
public class PredicateOperatorTypingRule extends AbstractOperatorTypingRule<IPredicateExtension>
implements IPredicateTypeChecker{

	/**
	 * @param extension
	 */
	public PredicateOperatorTypingRule(IFormulaExtension extension) {
		super(extension);
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
			argumentTypesAsVars[i] = MathExtensionsFacilitator
					.constructPatternTypeFor(argumentsTypes.get(i).getArgumentType(),
							parameterToTypeVarMap, mediator);
		}
		for (int i = 0; i < childExpressions.length; i++) {
			Type currentType = childExpressions[i].getType();
			mediator.sameType(argumentTypesAsVars[i], currentType);
		}
		
	}

	@Override
	protected IPredicateExtension getExtension(IFormulaExtension extension) {
		return (IPredicateExtension) extension;
	}

	
}
