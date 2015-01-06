package org.eventb.core.ast.extensions.pm.engine.pred;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.extensions.pm.engine.Binding;
import org.eventb.core.ast.extensions.pm.engine.PredicateMatcher;

/**
 * @since 1.0
 * @author maamria
 *
 */
public class RelationalPredicateMatcher extends PredicateMatcher<RelationalPredicate>{

	public RelationalPredicateMatcher() {
		super(RelationalPredicate.class);
	}

	@Override
	protected boolean gatherBindings(RelationalPredicate rpForm,
			RelationalPredicate rpPattern, Binding existingBinding){
		Expression pLeft = rpPattern.getLeft();
		Expression fLeft = rpForm.getLeft();
		if(pLeft instanceof FreeIdentifier){
			if(!existingBinding.putExpressionMapping((FreeIdentifier)pLeft, fLeft)){
				return false;
			}
		}
		else {
			if(!matchingFactory.match(fLeft, pLeft, existingBinding)){
				return false;
			}
		}
		Expression pRight = rpPattern.getRight();
		Expression fRight = rpForm.getRight();
		if(pRight instanceof FreeIdentifier){
			return existingBinding.putExpressionMapping((FreeIdentifier) pRight, fRight);
		}
		return matchingFactory.match(fRight, pRight, existingBinding);
	}

	@Override
	protected RelationalPredicate getPredicate(Predicate p) {
		return (RelationalPredicate) p;
	}

}
