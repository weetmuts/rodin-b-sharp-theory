package org.eventb.core.ast.extensions.pm.engine.exp;

import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.extensions.pm.engine.Binding;
import org.eventb.core.ast.extensions.pm.engine.ExpressionMatcher;

/**
 * TODO FIXME check only the index and type, could be more intricate?
 * 
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
			BoundIdentifier biPattern, Binding existingBinding){
		if(biForm.getBoundIndex() != biPattern.getBoundIndex()){
			return false;
		}
		return existingBinding.unifyTypes(biForm.getType(), biPattern.getType(), true);
	}
	
	@Override
	protected BoundIdentifier getExpression(Expression e) {
		return (BoundIdentifier) e;
	}

}
