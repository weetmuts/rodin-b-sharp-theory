package ac.soton.eventb.prover.internal.engine.pred;

import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.UnaryPredicate;

import ac.soton.eventb.prover.engine.IBinding;
import ac.soton.eventb.prover.internal.engine.PredicateMatcher;

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
		if(!engine.match(fChild, pChild, existingBinding)){
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
