package org.eventb.core.pm.matchers.pred;

import org.eventb.core.ast.MultiplePredicate;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pm.basis.IBinding;
import org.eventb.core.pm.basis.PredicateMatcher;

/**
 * @since 1.0
 * @author maamria
 *
 */
public class MultiplePredicateMatcher extends PredicateMatcher<MultiplePredicate> {

	public MultiplePredicateMatcher() {
		super(MultiplePredicate.class);
	}

	@Override
	protected boolean gatherBindings(MultiplePredicate form,
			MultiplePredicate pattern, IBinding existingBinding) {
		// nothing to do at the moment for Partition
		return false;
	}

	@Override
	protected MultiplePredicate getPredicate(Predicate p) {
		return (MultiplePredicate) p;
	}

}
