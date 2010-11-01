package org.eventb.theory.rbp.internal.engine.exp;

import org.eventb.core.ast.BoolExpression;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Predicate;

import org.eventb.theory.rbp.engine.ExpressionMatcher;
import org.eventb.theory.rbp.engine.IBinding;
import org.eventb.theory.rbp.internal.engine.MatchingFactory;

public class BoolExpressionMatcher extends  ExpressionMatcher<BoolExpression> {

	public BoolExpressionMatcher(){
		super(BoolExpression.class);
	}
	
	@Override
	protected boolean gatherBindings(BoolExpression beForm, BoolExpression bePattern,
			IBinding existingBinding) {
		Predicate formPred = beForm.getPredicate();
		Predicate patternPred = bePattern.getPredicate();
		if(!MatchingFactory.match(formPred, patternPred, existingBinding)){
			return false;
		}
		return true;
	}
	@Override
	protected BoolExpression cast(Expression e) {
		// TODO Auto-generated method stub
		return (BoolExpression) e;
	}
}
