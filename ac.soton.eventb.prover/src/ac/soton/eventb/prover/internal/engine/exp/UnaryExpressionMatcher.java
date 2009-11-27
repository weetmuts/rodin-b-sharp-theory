package ac.soton.eventb.prover.internal.engine.exp;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.UnaryExpression;

import ac.soton.eventb.prover.engine.IBinding;
import ac.soton.eventb.prover.internal.engine.ExpressionMatcher;

public class UnaryExpressionMatcher extends ExpressionMatcher<UnaryExpression> {

	public UnaryExpressionMatcher(){
		super(UnaryExpression.class);
	}
	
	@Override
	protected boolean gatherBindings(UnaryExpression ueForm,
			UnaryExpression uePattern, IBinding existingBinding){
		if(ueForm.getTag() != uePattern.getTag()){
			return false;
		}
		
		Expression formExp = ueForm.getChild();
		Expression patternExp = uePattern.getChild();
		
		if(patternExp instanceof FreeIdentifier){
			if(!existingBinding.putMapping((FreeIdentifier)patternExp, formExp)){
				return false;
			}
		}
		else {
			if(!engine.match(formExp, patternExp, existingBinding)){
				return false;
			}
		}
		return true;
	}

	@Override
	protected UnaryExpression cast(Expression e) {
		// TODO Auto-generated method stub
		return (UnaryExpression) e;
	}

}
