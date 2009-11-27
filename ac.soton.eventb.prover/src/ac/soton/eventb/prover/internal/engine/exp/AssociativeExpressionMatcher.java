package ac.soton.eventb.prover.internal.engine.exp;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.Expression;

import ac.soton.eventb.prover.engine.IBinding;
import ac.soton.eventb.prover.internal.engine.ExpressionMatcher;

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
		
		//*************************Associative only********************************
		//*************************************************************************
		//*************************************************************************
		//*************************************************************************
		if(form.getTag() == AssociativeExpression.BCOMP ||
				form.getTag() == AssociativeExpression.FCOMP){
			
		}
		
		//*************************Associative Commutative*************************
		//*************************************************************************
		//*************************************************************************
		//*************************************************************************
		else {
			IBinding binding = AssociativeMatchingHelper.match(
					formChildren, patternChildren, true, form.getTag(), existingBinding);
			if(binding == null)
				return false;
			binding.makeImmutable();
			if(!existingBinding.insertAllMappings(binding)){
				return false;
			}
		}
		return true;
		
	}

	@Override
	protected AssociativeExpression cast(Expression e) {
		// TODO Auto-generated method stub
		return (AssociativeExpression) e;
	}
	
}
