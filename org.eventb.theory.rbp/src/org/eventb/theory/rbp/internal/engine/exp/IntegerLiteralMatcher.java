package org.eventb.theory.rbp.internal.engine.exp;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.IntegerLiteral;

import org.eventb.theory.rbp.engine.ExpressionMatcher;
import org.eventb.theory.rbp.engine.IBinding;

/**
 * @since 1.0
 * @author maamria
 *
 */
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
