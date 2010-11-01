package org.eventb.theory.rbp.internal.engine.exp;

import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.Expression;

import org.eventb.theory.rbp.engine.ExpressionMatcher;
import org.eventb.theory.rbp.engine.IBinding;

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
	protected AtomicExpression cast(Expression e) {
		// TODO Auto-generated method stub
		return (AtomicExpression) e;
	}
}