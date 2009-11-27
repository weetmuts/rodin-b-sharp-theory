package ac.soton.eventb.prover.internal.engine.pred;

import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.Predicate;

import ac.soton.eventb.prover.engine.IBinding;
import ac.soton.eventb.prover.internal.engine.PredicateMatcher;

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
