/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.internal.engine.exp;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.theory.core.TheoryCoreFacadeAST;
import org.eventb.theory.rbp.engine.ExpressionMatcher;
import org.eventb.theory.rbp.engine.IBinding;
import org.eventb.theory.rbp.internal.engine.MatchingFactory;

/**
 * @author maamria
 * 
 */
public class ExtendedExpressionMatcher extends
		ExpressionMatcher<ExtendedExpression> {

	public ExtendedExpressionMatcher() {
		super(ExtendedExpression.class);
	}

	@Override
	protected boolean gatherBindings(ExtendedExpression form,
			ExtendedExpression pattern, IBinding existingBinding) {
		// TODO Auto-generated method stub
		if (form.getTag() != pattern.getTag()) {
			return false;
		}
		Expression[] formChildren = form.getChildExpressions();
		Expression[] patChildren = pattern.getChildExpressions();

		if (TheoryCoreFacadeAST.isAssociative(pattern)) {
			if (formChildren.length != 2 || patChildren.length != 2
					|| formChildren.length != patChildren.length) {
				return false;
			}
			if (TheoryCoreFacadeAST.isAC(pattern)) {
				if (!AssociativityHandler.match(formChildren[0],
						patChildren[0], formChildren[1], patChildren[1], true,
						existingBinding)) {
					return false;
				}
			}
			else {
				if (!AssociativityHandler.match(formChildren[0],
						patChildren[0], formChildren[1], patChildren[1], false,
						existingBinding)) {
					return false;
				}
			}
		} else
			for (int i = 0; i < patChildren.length; i++) {
				Expression patChild = patChildren[i];
				if (patChild instanceof FreeIdentifier) {
					if (!existingBinding.putMapping((FreeIdentifier) patChild,
							formChildren[i])) {
						return false;
					}
				} else if (!MatchingFactory.match(formChildren[i], patChild,
						existingBinding)) {
					return false;
				}
			}
		return true;
	}

	@Override
	protected ExtendedExpression cast(Expression e) {
		// TODO Auto-generated method stub
		return (ExtendedExpression) e;
	}

}
