package ac.soton.eventb.prover.internal.engine.pred;

import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.Predicate;

import ac.soton.eventb.prover.engine.IBinding;
import ac.soton.eventb.prover.internal.engine.PredicateMatcher;

public class BinaryPredicateMatcher extends PredicateMatcher<BinaryPredicate> {

	public BinaryPredicateMatcher() {
		super(BinaryPredicate.class);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected boolean gatherBindings(BinaryPredicate bpForm,
			BinaryPredicate bpPattern, IBinding existingBinding){
		if(bpForm.getTag() != bpPattern.getTag()){
			return false;
		}
		Predicate fLeft = bpForm.getLeft();
		Predicate pLeft = bpPattern.getLeft();
		if(!engine.match(fLeft, pLeft, existingBinding)){
			return false;
		}
		
		Predicate fRight = bpForm.getRight();
		Predicate pRight = bpPattern.getRight();
		if(!engine.match(fRight, pRight, existingBinding)){
			return false;
		}
		return true;
	}

	@Override
	protected BinaryPredicate cast(Predicate p) {
		// TODO Auto-generated method stub
		return (BinaryPredicate) p;
	}

}
