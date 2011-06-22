/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.pm;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.pm.basis.ExtendedExpressionMatcher;
import org.eventb.core.pm.basis.IBinding;
import org.eventb.theory.core.AstUtilities;
import org.eventb.theory.internal.core.maths.ExpressionOperatorExtension;

/**
 * 
 * @author maamria
 *
 */
public class TheoryExtendedExpressionMatcher extends ExtendedExpressionMatcher<ExpressionOperatorExtension> {

	public TheoryExtendedExpressionMatcher() {
		super(ExpressionOperatorExtension.class);
	}

	@Override
	protected boolean gatherBindings(ExtendedExpression form, ExtendedExpression pattern, IBinding existingBinding) {
		if (form.getTag() != pattern.getTag()) {
			return false;
		}
		Expression[] formChildren = form.getChildExpressions();
		Expression[] patChildren = pattern.getChildExpressions();

		if (AstUtilities.isAssociative(pattern)) {
			if (formChildren.length != 2 || patChildren.length != 2
					|| formChildren.length != patChildren.length) {
				return false;
			}
			if (AstUtilities.isAC(pattern)) {
				if (!AssociativityHandler.match(formChildren[0],
						patChildren[0], formChildren[1], patChildren[1], true,
						existingBinding, matchingFactory)) {
					return false;
				}
			}
			else {
				if (!AssociativityHandler.match(formChildren[0],
						patChildren[0], formChildren[1], patChildren[1], false,
						existingBinding, matchingFactory)) {
					return false;
				}
			}
		} else
			for (int i = 0; i < patChildren.length; i++) {
				Expression patChild = patChildren[i];
				if (patChild instanceof FreeIdentifier) {
					if (!existingBinding.putExpressionMapping((FreeIdentifier) patChild,
							formChildren[i])) {
						return false;
					}
				} else if (!matchingFactory.match(formChildren[i], patChild,
						existingBinding)) {
					return false;
				}
			}
		return existingBinding.canUnifyTypes(form.getType(), pattern.getType());
	}
}
