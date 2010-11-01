package org.eventb.theory.rbp.internal.engine.pred;

import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.Predicate;

import org.eventb.theory.rbp.engine.IBinding;
import org.eventb.theory.rbp.engine.PredicateMatcher;

/**
 * @since 1.0
 * @author maamria
 *
 */
public class LiteralPredicateMatcher extends PredicateMatcher<LiteralPredicate> {

	public LiteralPredicateMatcher() {
		super(LiteralPredicate.class);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected boolean gatherBindings(LiteralPredicate form,
			LiteralPredicate pattern, IBinding existingBinding){
		if(!form.equals(pattern)){
			return false;
		}
		return true;
	}

	@Override
	protected LiteralPredicate cast(Predicate p) {
		// TODO Auto-generated method stub
		return (LiteralPredicate) p;
	}
}
