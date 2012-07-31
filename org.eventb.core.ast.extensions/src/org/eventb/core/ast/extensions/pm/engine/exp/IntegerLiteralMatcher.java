package org.eventb.core.ast.extensions.pm.engine.exp;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.extensions.pm.IBinding;
import org.eventb.core.ast.extensions.pm.engine.ExpressionMatcher;

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
	protected IntegerLiteral getExpression(Expression e) {
		return (IntegerLiteral) e;
	}
}