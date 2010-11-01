/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.internal.engine.pred;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedPredicate;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;
import org.eventb.theory.rbp.engine.IBinding;
import org.eventb.theory.rbp.engine.PredicateMatcher;
import org.eventb.theory.rbp.internal.engine.MatchingFactory;

/**
 * @since 1.0
 * @author maamria
 *
 */
public class ExtendedPredicateMatcher extends PredicateMatcher<ExtendedPredicate>{

	public ExtendedPredicateMatcher() {
		super(ExtendedPredicate.class);
	}

	@Override
	protected boolean gatherBindings(ExtendedPredicate form,
			ExtendedPredicate pattern, IBinding existingBinding) {
		// TODO Auto-generated method stub
		if(form.getTag() != pattern.getTag()){
			return false;
		}
		Expression[] formChildren = form.getChildExpressions();
		Expression[] patChildren = pattern.getChildExpressions();
		for (int i = 0 ; i < formChildren.length; i++){
			Expression patChild = patChildren[i];
			if(patChild instanceof FreeIdentifier){
				if(!existingBinding.putMapping((FreeIdentifier)patChild, formChildren[i])){
					return false;
				}
			}
			else if(!MatchingFactory.match(formChildren[i], patChild, existingBinding)){
				return false;
			}
		}
		return true;
	}

	@Override
	protected ExtendedPredicate cast(Predicate p) {
		// TODO Auto-generated method stub
		return (ExtendedPredicate) p;
	}

}
