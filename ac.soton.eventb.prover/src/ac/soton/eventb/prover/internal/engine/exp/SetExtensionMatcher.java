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
		if(form.getMembers().length == 1 && pattern.getMembers().length == 1){
			Expression formMem = form.getMembers()[0];
			Expression patternMem = pattern.getMembers()[0];
			if(engine.match(formMem, patternMem, existingBinding)){
				return true;
			}
		}
		return false;
		
	}

	@Override
	protected SetExtension cast(Expression e) {
		// TODO Auto-generated method stub
		return (SetExtension) e;
	}

}
