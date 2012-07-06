package org.eventb.core.ast.extensions.pm.engine.exp;

import org.eventb.core.ast.BoolExpression;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.PredicateVariable;
import org.eventb.core.ast.extensions.pm.IBinding;
import org.eventb.core.ast.extensions.pm.engine.ExpressionMatcher;

/**
 * @since 1.0
 * @author maamria
 *
 */
public class BoolExpressionMatcher extends ExpressionMatcher<BoolExpression> {

	public BoolExpressionMatcher() {
		super(BoolExpression.class);
	}

	@Override
	protected boolean gatherBindings(BoolExpression beForm,
			BoolExpression bePattern, IBinding existingBinding) {
		Predicate formPred = beForm.getPredicate();
		Predicate patternPred = bePattern.getPredicate();
		if (patternPred instanceof PredicateVariable) {
			return existingBinding.putPredicateMapping((PredicateVariable) patternPred, formPred);
		} 
		return matchingFactory.match(formPred, patternPred, existingBinding);
	}

	@Override
	protected BoolExpression getExpression(Expression e) {
		return (BoolExpression) e;
	}
}
