package ac.soton.eventb.prover.internal.engine.exp;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.Expression;

import ac.soton.eventb.prover.engine.IBinding;
import ac.soton.eventb.prover.internal.engine.ExpressionMatcher;
import ac.soton.eventb.prover.utils.ProverUtilities;

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
		
		// get the children
		Expression[] formChildren = form.getChildren();
		Expression[] patternChildren = pattern.getChildren();
		
		// if the pattern has more children
		if(patternChildren.length > formChildren.length)
			return false;
		boolean isAC = ProverUtilities.isAssociativeCommutative(pattern.getTag());
		
		IBinding binding = AssociativeHelper.match(
				formChildren, patternChildren, isAC, form.getTag(), existingBinding);
		if(binding == null)
			return false;
		binding.makeImmutable();
		if(!existingBinding.insertAllMappings(binding)){
			return false;
		}
		return true;
		
	}

	@Override
	protected AssociativeExpression cast(Expression e) {
		// TODO Auto-generated method stub
		return (AssociativeExpression) e;
	}
	
}
