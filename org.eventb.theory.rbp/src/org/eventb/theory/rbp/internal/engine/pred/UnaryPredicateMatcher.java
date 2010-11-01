package org.eventb.theory.rbp.internal.engine.pred;

import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.UnaryPredicate;

import org.eventb.theory.rbp.engine.IBinding;
import org.eventb.theory.rbp.engine.PredicateMatcher;
import org.eventb.theory.rbp.internal.engine.MatchingFactory;

public class UnaryPredicateMatcher extends PredicateMatcher<UnaryPredicate> {

	public UnaryPredicateMatcher() {
		super(UnaryPredicate.class);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected boolean gatherBindings(UnaryPredicate upForm, UnaryPredicate upPattern,
			IBinding existingBinding){
		if(upPattern.getTag() != upForm.getTag()){
			return false;
		}
		
		Predicate fChild = upForm.getChild();
		Predicate pChild = upPattern.getChild();
		if(!MatchingFactory.match(fChild, pChild, existingBinding)){
			return false;
		}
		return true;
	}


	@Override
	protected UnaryPredicate cast(Predicate p) {
		// TODO Auto-generated method stub
		return (UnaryPredicate) p;
	}

}
