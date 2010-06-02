package ac.soton.eventb.prover.internal.engine.exp;

import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.Expression;

import ac.soton.eventb.prover.engine.IBinding;
import ac.soton.eventb.prover.internal.engine.ExpressionMatcher;
import ac.soton.eventb.prover.utils.ProverUtilities;

public class AtomicExpressionMatcher extends ExpressionMatcher<AtomicExpression> {

	public AtomicExpressionMatcher(){
		super(AtomicExpression.class);
	}
	
	@Override
	protected boolean gatherBindings(AtomicExpression form,
			AtomicExpression pattern, IBinding existingBinding){
		// tags not equal
		if(form.getTag() != pattern.getTag()){
			return false;
		}
		// tags equal, unify types
		else {
			if(!ProverUtilities.canUnifyTypes(form.getType(), pattern.getType())){
				return false;
			}
		}
		return true;
	}
	
	@Override
	protected AtomicExpression cast(Expression e) {
		// TODO Auto-generated method stub
		return (AtomicExpression) e;
	}
}
