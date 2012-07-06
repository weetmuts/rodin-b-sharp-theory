package org.eventb.core.ast.extensions.pm.engine.exp;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.extensions.pm.IBinding;
import org.eventb.core.ast.extensions.pm.assoc.ACExpressionProblem;
import org.eventb.core.ast.extensions.pm.assoc.AExpressionProblem;
import org.eventb.core.ast.extensions.pm.assoc.AssociativityProblem;
import org.eventb.core.ast.extensions.pm.engine.ExpressionMatcher;

/**
 * 
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
		boolean isAC = isAssociativeCommutative(form.getTag());
		// get the children
		Expression[] formChildren = form.getChildren();
		Expression[] patternChildren = pattern.getChildren();
		AssociativityProblem<Expression> problem = null;
		if (isAC){
			problem = new ACExpressionProblem(form.getTag(), formChildren, patternChildren, existingBinding); 
		}
		else {
			problem = new AExpressionProblem(form.getTag(), formChildren, patternChildren, existingBinding);
		}
		boolean partialMatchAcceptable = existingBinding.isPartialMatchAcceptable();
		IBinding solution = (IBinding) problem.solve(partialMatchAcceptable);
		if (solution != null){
			solution.makeImmutable();
			if (existingBinding.insertBinding(solution)){
				if(partialMatchAcceptable){
					existingBinding.setAssociativeExpressionComplement(solution.getAssociativeExpressionComplement());
				}
				return true;
			}
		}
		return false;
	}

	@Override
	protected AssociativeExpression getExpression(Expression e) {
		return (AssociativeExpression) e;
	}
	
	/**
	 * Make sure tag is for an associative expression.
	 * <p>
	 * This method checks whether the operator is AC.
	 * 
	 * @param tag
	 * @return
	 */
	protected static boolean isAssociativeCommutative(int tag) {
		if (tag == AssociativeExpression.BCOMP
				|| tag == AssociativeExpression.FCOMP) {
			return false;
		}
		return true;
	}
	
}
