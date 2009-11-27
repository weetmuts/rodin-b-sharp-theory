package ac.soton.eventb.prover.internal.engine.exp;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.IntegerLiteral;

import ac.soton.eventb.prover.engine.IBinding;
import ac.soton.eventb.prover.internal.engine.ExpressionMatcher;

public class IntegerLiteralMatcher extends ExpressionMatcher<IntegerLiteral> {

	public IntegerLiteralMatcher(){
		super(IntegerLiteral.class);
	}
	
	@Override
	protected boolean gatherBindings(IntegerLiteral form, IntegerLiteral pattern,
			IBinding existingBinding) {
		if(!form.equals(pattern)){
			return false;
		}
		return true;
	}

	@Override
	protected IntegerLiteral cast(Expression e) {
		// TODO Auto-generated method stub
		return (IntegerLiteral) e;
	}
}
