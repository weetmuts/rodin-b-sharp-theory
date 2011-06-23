package org.eventb.core.pm.matchers.exp;

import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.Expression;
import org.eventb.core.pm.basis.ExpressionMatcher;
import org.eventb.core.pm.basis.IBinding;

/**
 * @since 1.0
 * @author maamria
 *
 */
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
		return existingBinding.canUnifyTypes(biForm.getType(), biPattern.getType());
	}
	
	@Override
	protected BoundIdentifier getExpression(Expression e) {
		return (BoundIdentifier) e;
	}

}