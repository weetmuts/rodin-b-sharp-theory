package org.eventb.core.pm.matchers.exp;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.Expression;
import org.eventb.core.pm.basis.ExpressionMatcher;
import org.eventb.core.pm.basis.IBinding;
import org.eventb.core.pm.basis.engine.MatchingUtilities;

/**
 * TODO better matching
 * @since 1.0
 * @author maamria
 *
 */
public class AssociativeExpressionMatcher extends ExpressionMatcher<AssociativeExpression> {

	public AssociativeExpressionMatcher() {
		super(AssociativeExpression.class);
	}

	@Override
	protected boolean gatherBindings(AssociativeExpression form,
			AssociativeExpression pattern, IBinding existingBinding){
		
		// if tag is different
		if(form.getTag() != pattern.getTag())
			return false;
		boolean isAC = MatchingUtilities.isAssociativeCommutative(form.getTag());
		// get the children
		Expression[] formChildren = form.getChildren();
		Expression[] patternChildren = pattern.getChildren();
		// work with binary representations
		if(formChildren.length != 2 || patternChildren.length != 2
				|| formChildren.length != patternChildren.length){
			return false;
		}
		Expression formChild1 = formChildren[0];
		Expression patternChild1 = patternChildren[0];
		Expression formChild2 = formChildren[1];
		Expression patternChild2 = patternChildren[1];
		return AssociativityHandler.match(formChild1, patternChild1, formChild2, patternChild2, isAC, existingBinding, matchingFactory);
	}

	@Override
	protected AssociativeExpression getExpression(Expression e) {
		return (AssociativeExpression) e;
	}
	
}
