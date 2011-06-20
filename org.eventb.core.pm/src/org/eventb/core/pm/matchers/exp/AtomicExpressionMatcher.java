package org.eventb.core.pm.matchers.exp;

import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.Expression;
import org.eventb.core.pm.basis.ExpressionMatcher;
import org.eventb.core.pm.basis.IBinding;

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
		// tags not equal
		if(form.getTag() != pattern.getTag()){
			return false;
		}
		// tags equal, unify types
		return existingBinding.canUnifyTypes(form.getType(), pattern.getType());
	}
	
	@Override
	protected AtomicExpression getExpression(Expression e) {
		return (AtomicExpression) e;
	}
}
