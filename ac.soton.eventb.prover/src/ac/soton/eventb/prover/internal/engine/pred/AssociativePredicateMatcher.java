package ac.soton.eventb.prover.internal.engine.pred;

import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.Predicate;

import ac.soton.eventb.prover.engine.IBinding;
import ac.soton.eventb.prover.internal.engine.PredicateMatcher;

public class AssociativePredicateMatcher extends PredicateMatcher<AssociativePredicate> {


	public AssociativePredicateMatcher(){
		super(AssociativePredicate.class);
	}
	
	@Override
	protected boolean gatherBindings(AssociativePredicate form,
			AssociativePredicate pattern, IBinding existingBinding){
		return false;
		
	}

	@Override
	protected AssociativePredicate cast(Predicate p) {
		// TODO Auto-generated method stub
		return (AssociativePredicate) p;
	}

}
