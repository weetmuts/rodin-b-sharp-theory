package ac.soton.eventb.prover.internal.engine.exp;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.SetExtension;

import ac.soton.eventb.prover.engine.IBinding;
import ac.soton.eventb.prover.internal.engine.ExpressionMatcher;

public class SetExtensionMatcher extends ExpressionMatcher<SetExtension> {

	public SetExtensionMatcher(){
		super(SetExtension.class);
	}
	
	@Override
	protected boolean gatherBindings(SetExtension form, SetExtension pattern,
			IBinding existingBinding)  {
		return false;
		
	}

	@Override
	protected SetExtension cast(Expression e) {
		// TODO Auto-generated method stub
		return (SetExtension) e;
	}

}
