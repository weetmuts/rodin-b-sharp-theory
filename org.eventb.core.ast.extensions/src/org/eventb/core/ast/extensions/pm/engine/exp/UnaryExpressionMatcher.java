package org.eventb.core.ast.extensions.pm.engine.exp;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.ast.extensions.pm.engine.Binding;
import org.eventb.core.ast.extensions.pm.engine.ExpressionMatcher;

/**
 * @since 1.0
 * @author maamria
 *
 */
public class UnaryExpressionMatcher extends ExpressionMatcher<UnaryExpression> {

	public UnaryExpressionMatcher(){
		super(UnaryExpression.class);
	}
	
	@Override
	protected boolean gatherBindings(UnaryExpression ueForm,
			UnaryExpression uePattern, Binding existingBinding){
		Expression formExp = ueForm.getChild();
		Expression patternExp = uePattern.getChild();
		if(patternExp instanceof FreeIdentifier){
			return existingBinding.putExpressionMapping((FreeIdentifier)patternExp, formExp);
		}
		return matchingFactory.match(formExp, patternExp, existingBinding);
	}

	@Override
	protected UnaryExpression getExpression(Expression e) {
		return (UnaryExpression) e;
	}

}
