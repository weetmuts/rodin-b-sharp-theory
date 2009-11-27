package ac.soton.eventb.prover.internal.engine.exp;

import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.Expression;

import ac.soton.eventb.prover.engine.IBinding;
import ac.soton.eventb.prover.internal.engine.ExpressionMatcher;

public class BoundIdentifierMatcher extends ExpressionMatcher<BoundIdentifier> {

	public BoundIdentifierMatcher() {
		super(BoundIdentifier.class);
	}
	
	@Override
	protected boolean gatherBindings(BoundIdentifier biForm,
			BoundIdentifier biPattern, IBinding existingBinding){
		if(biForm.getBoundIndex() != biPattern.getBoundIndex()){
			return false;
		}
		return true;
	}
	
	@Override
	protected BoundIdentifier cast(Expression e) {
		return (BoundIdentifier) e;
	}

}
