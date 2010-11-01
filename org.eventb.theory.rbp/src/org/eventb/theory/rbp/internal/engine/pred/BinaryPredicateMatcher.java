package org.eventb.theory.rbp.internal.engine.pred;

import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.PredicateVariable;

import org.eventb.theory.rbp.engine.IBinding;
import org.eventb.theory.rbp.engine.PredicateMatcher;
import org.eventb.theory.rbp.internal.engine.MatchingFactory;

/**
 * @since 1.0
 * @author maamria
 *
 */
public class BinaryPredicateMatcher extends PredicateMatcher<BinaryPredicate> {

	public BinaryPredicateMatcher() {
		super(BinaryPredicate.class);
	}

	@Override
	protected boolean gatherBindings(BinaryPredicate bpForm,
			BinaryPredicate bpPattern, IBinding existingBinding){
		if(bpForm.getTag() != bpPattern.getTag()){
			return false;
		}
		Predicate fLeft = bpForm.getLeft();
		Predicate pLeft = bpPattern.getLeft();
		if(pLeft instanceof PredicateVariable){
			return existingBinding.putPredicateMapping(
					(PredicateVariable)pLeft, fLeft);
		}
		else if(!MatchingFactory.match(fLeft, pLeft, existingBinding)){
			return false;
		}
		
		Predicate fRight = bpForm.getRight();
		Predicate pRight = bpPattern.getRight();
		if(pRight instanceof PredicateVariable){
			return existingBinding.putPredicateMapping(
					(PredicateVariable)pRight, fRight);
		}
		return MatchingFactory.match(fRight, pRight, existingBinding);
	}

	@Override
	protected BinaryPredicate cast(Predicate p) {
		// TODO Auto-generated method stub
		return (BinaryPredicate) p;
	}

}
