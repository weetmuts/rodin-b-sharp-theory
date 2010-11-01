package org.eventb.theory.rbp.internal.engine.pred;

import org.eventb.core.ast.MultiplePredicate;
import org.eventb.core.ast.Predicate;
import org.eventb.theory.rbp.engine.IBinding;
import org.eventb.theory.rbp.engine.PredicateMatcher;

/**
 * @since 1.0
 * @author maamria
 *
 */
public class MultiplePredicateMatcher extends
		PredicateMatcher<MultiplePredicate> {

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
	protected MultiplePredicate cast(Predicate p) {
		// TODO Auto-generated method stub
		return (MultiplePredicate) p;
	}

}
