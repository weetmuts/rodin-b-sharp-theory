package org.eventb.theory.rbp.internal.engine.exp;

import org.eventb.core.ast.BoolExpression;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.PredicateVariable;

import org.eventb.theory.rbp.engine.ExpressionMatcher;
import org.eventb.theory.rbp.engine.IBinding;
import org.eventb.theory.rbp.internal.engine.MatchingFactory;

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
		return MatchingFactory.match(formPred, patternPred, existingBinding);
	}

	@Override
	protected BoolExpression cast(Expression e) {
		// TODO Auto-generated method stub
		return (BoolExpression) e;
	}
}
