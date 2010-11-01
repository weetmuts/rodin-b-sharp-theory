package org.eventb.theory.rbp.internal.engine.exp;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.Expression;
import org.eventb.theory.rbp.engine.ExpressionMatcher;
import org.eventb.theory.rbp.engine.IBinding;
import org.eventb.theory.rbp.utils.ProverUtilities;

public class AssociativeExpressionMatcher extends ExpressionMatcher<AssociativeExpression> {

	public AssociativeExpressionMatcher() {
		super(AssociativeExpression.class);
	}

	@Override
	protected boolean gatherBindings(AssociativeExpression form,
			AssociativeExpression pattern, IBinding existingBinding){
		
		// if tag is diff
		if(form.getTag() != pattern.getTag())
			return false;
		boolean isAC = ProverUtilities.isAssociativeCommutative(form.getTag());
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
		return AssociativityHandler.match(formChild1, patternChild1, formChild2, patternChild2, isAC, existingBinding);
	}

	@Override
	protected AssociativeExpression cast(Expression e) {
		// TODO Auto-generated method stub
		return (AssociativeExpression) e;
	}
	
}
