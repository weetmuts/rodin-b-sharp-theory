package ac.soton.eventb.prover.internal.engine.exp;

import org.eventb.core.ast.BoolExpression;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Predicate;

import ac.soton.eventb.prover.engine.IBinding;
import ac.soton.eventb.prover.internal.engine.ExpressionMatcher;

public class BoolExpressionMatcher extends  ExpressionMatcher<BoolExpression> {

	public BoolExpressionMatcher(){
		super(BoolExpression.class);
	}
	
	@Override
	protected boolean gatherBindings(BoolExpression beForm, BoolExpression bePattern,
			IBinding existingBinding) {
		Predicate formPred = beForm.getPredicate();
		Predicate patternPred = bePattern.getPredicate();
		if(!engine.match(formPred, patternPred, existingBinding)){
			return false;
		}
		return true;
	}
	@Override
	protected BoolExpression cast(Expression e) {
		// TODO Auto-generated method stub
		return (BoolExpression) e;
	}
}
