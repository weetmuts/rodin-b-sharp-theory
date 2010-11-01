package org.eventb.theory.rbp.internal.engine.exp;

import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.Expression;

import org.eventb.theory.rbp.engine.ExpressionMatcher;
import org.eventb.theory.rbp.engine.IBinding;

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
	protected BoundIdentifier cast(Expression e) {
		return (BoundIdentifier) e;
	}

}
