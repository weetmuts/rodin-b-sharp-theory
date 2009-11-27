package ac.soton.eventb.prover.internal.engine.pred;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.SimplePredicate;

import ac.soton.eventb.prover.engine.IBinding;
import ac.soton.eventb.prover.internal.engine.PredicateMatcher;

public class SimplePredicateMatcher extends PredicateMatcher<SimplePredicate> {

	public SimplePredicateMatcher() {
		super(SimplePredicate.class);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected boolean gatherBindings(SimplePredicate spForm,
			SimplePredicate spPattern, IBinding existingBinding){
		if(spForm.getTag() != spPattern.getTag()){
			return false;
		}
		Expression fExp = spForm.getExpression();
		Expression pExp = spPattern.getExpression();
		
		if(!engine.match(fExp, pExp, existingBinding)){
			return false;
		}
		return true;
	}

	@Override
	protected SimplePredicate cast(Predicate p) {
		// TODO Auto-generated method stub
		return (SimplePredicate) p;
	}
}
