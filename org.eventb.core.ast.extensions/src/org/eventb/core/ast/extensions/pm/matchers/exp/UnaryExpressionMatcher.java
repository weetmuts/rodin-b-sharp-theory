package org.eventb.core.ast.extensions.pm.matchers.exp;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.ast.extensions.pm.ExpressionMatcher;
import org.eventb.core.ast.extensions.pm.IBinding;

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
			UnaryExpression uePattern, IBinding existingBinding){
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
