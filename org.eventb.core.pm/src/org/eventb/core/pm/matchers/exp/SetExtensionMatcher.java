package org.eventb.core.pm.matchers.exp;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.pm.ExpressionMatcher;
import org.eventb.core.pm.IBinding;
import org.eventb.core.pm.assoc.ACExpressionProblem;
import org.eventb.core.pm.assoc.AssociativityProblem;

/**
 * TODO emulate associative matching
 * @since 1.0
 * @author maamria
 *
 */
public class SetExtensionMatcher extends ExpressionMatcher<SetExtension> {

	public SetExtensionMatcher(){
		super(SetExtension.class);
	}
	
	@Override
	protected boolean gatherBindings(SetExtension form, SetExtension pattern,
			IBinding existingBinding)  {
		Expression[] formMembers = form.getMembers();
		Expression[] patternMembers = pattern.getMembers();
		AssociativityProblem<Expression> problem = new ACExpressionProblem(Formula.BUNION, 
				formMembers, patternMembers, existingBinding.getFormulaFactory());
		IBinding binding = problem.solve(false);
		return false;
	}

	@Override
	protected SetExtension getExpression(Expression e) {
		return (SetExtension) e;
	}

}
