package org.eventb.core.ast.extensions.pm.engine.exp;

import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.extensions.pm.IBinding;
import org.eventb.core.ast.extensions.pm.engine.ExpressionMatcher;

/**
 * @since 1.0
 * @author maamria
 *
 */
public class AtomicExpressionMatcher extends ExpressionMatcher<AtomicExpression> {

	public AtomicExpressionMatcher(){
		super(AtomicExpression.class);
	}
	
	@Override
	protected boolean gatherBindings(AtomicExpression form,
			AtomicExpression pattern, IBinding existingBinding){
		// no further action required
		return true;
	}
	
	@Override
	protected AtomicExpression getExpression(Expression e) {
		return (AtomicExpression) e;
	}
}
