package org.eventb.core.ast.extensions.pm.engine.pred;

import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.extensions.pm.engine.Binding;
import org.eventb.core.ast.extensions.pm.engine.PredicateMatcher;

/**
 * @since 1.0
 * @author maamria
 *
 */
public class LiteralPredicateMatcher extends PredicateMatcher<LiteralPredicate> {

	public LiteralPredicateMatcher() {
		super(LiteralPredicate.class);
	}

	@Override
	protected boolean gatherBindings(LiteralPredicate form,
			LiteralPredicate pattern, Binding existingBinding){
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
