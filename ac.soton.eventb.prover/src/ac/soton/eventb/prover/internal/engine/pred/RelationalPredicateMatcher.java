package ac.soton.eventb.prover.internal.engine.pred;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;

import ac.soton.eventb.prover.engine.IBinding;
import ac.soton.eventb.prover.internal.engine.PredicateMatcher;

public class RelationalPredicateMatcher extends PredicateMatcher<RelationalPredicate>{

	public RelationalPredicateMatcher() {
		super(RelationalPredicate.class);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected boolean gatherBindings(RelationalPredicate rpForm,
			RelationalPredicate rpPattern, IBinding existingBinding){
		if(rpForm.getTag() != rpPattern.getTag()){
			return false;
		}
		
		Expression pLeft = rpPattern.getLeft();
		Expression fLeft = rpForm.getLeft();
		if(pLeft instanceof FreeIdentifier){
			if(!existingBinding.putMapping((FreeIdentifier)pLeft, fLeft)){
				return false;
			}
		}
		else {
			if(!engine.match(fLeft, pLeft, existingBinding)){
				return false;
			}
		}
		
		Expression pRight = rpPattern.getRight();
		Expression fRight = rpForm.getRight();
		if(pRight instanceof FreeIdentifier){
			if(!existingBinding.putMapping((FreeIdentifier) pRight, fRight)){
				return false;
			}
		}
		else {
			if(!engine.match(fRight, pRight, existingBinding)){
				return false;
			}
		}
		return true;
	}

	@Override
	protected RelationalPredicate cast(Predicate p) {
		// TODO Auto-generated method stub
		return (RelationalPredicate) p;
	}

}
