package org.eventb.core.pm.matchers.pred;

import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pm.basis.IBinding;
import org.eventb.core.pm.basis.PredicateMatcher;

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
	protected LiteralPredicate getPredicate(Predicate p) {
		return (LiteralPredicate) p;
	}
}
