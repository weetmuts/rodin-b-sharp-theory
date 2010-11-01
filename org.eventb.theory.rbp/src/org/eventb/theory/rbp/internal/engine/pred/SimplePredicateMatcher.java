package org.eventb.theory.rbp.internal.engine.pred;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.SimplePredicate;

import org.eventb.theory.rbp.engine.IBinding;
import org.eventb.theory.rbp.engine.PredicateMatcher;
import org.eventb.theory.rbp.internal.engine.MatchingFactory;

public class SimplePredicateMatcher extends PredicateMatcher<SimplePredicate> {

	public SimplePredicateMatcher() {
		super(SimplePredicate.class);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected boolean gatherBindings(SimplePredicate spForm,
			SimplePredicate spPattern, IBinding existingBinding){
		if(spForm.getTag() != spPattern.getTag()){
			return false;
		}
		Expression fExp = spForm.getExpression();
		Expression pExp = spPattern.getExpression();
		
		if(!MatchingFactory.match(fExp, pExp, existingBinding)){
			return false;
		}
		return true;
	}

	@Override
	protected SimplePredicate cast(Predicate p) {
		// TODO Auto-generated method stub
		return (SimplePredicate) p;
	}
}
