/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.pm;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedPredicate;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.pm.basis.ExtendedPredicateMatcher;
import org.eventb.core.pm.basis.IBinding;
import org.eventb.theory.internal.core.maths.PredicateOperatorExtension;

/**
 * 
 * @author maamria
 * @since 1.0
 */
public class TheoryExtendedPredicateMatcher extends ExtendedPredicateMatcher<PredicateOperatorExtension> {

	public TheoryExtendedPredicateMatcher() {
		super(PredicateOperatorExtension.class);
	}

	@Override
	protected boolean gatherBindings(ExtendedPredicate form, ExtendedPredicate pattern, IBinding existingBinding) {
		if(form.getTag() != pattern.getTag()){
			return false;
		}
		Expression[] formChildren = form.getChildExpressions();
		Expression[] patChildren = pattern.getChildExpressions();
		for (int i = 0 ; i < formChildren.length; i++){
			Expression patChild = patChildren[i];
			if(patChild instanceof FreeIdentifier){
				if(!existingBinding.putExpressionMapping((FreeIdentifier)patChild, formChildren[i])){
					return false;
				}
			}
			else if(!matchingFactory.match(formChildren[i], patChild, existingBinding)){
				return false;
			}
		}
		return true;
	}

}
