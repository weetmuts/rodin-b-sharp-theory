package org.eventb.theory.rbp.internal.engine.exp;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.theory.rbp.engine.ExpressionMatcher;
import org.eventb.theory.rbp.engine.IBinding;
import org.eventb.theory.rbp.internal.engine.MatchingFactory;

public class UnaryExpressionMatcher extends ExpressionMatcher<UnaryExpression> {

	public UnaryExpressionMatcher(){
		super(UnaryExpression.class);
	}
	
	@Override
	protected boolean gatherBindings(UnaryExpression ueForm,
			UnaryExpression uePattern, IBinding existingBinding){
		if(ueForm.getTag() != uePattern.getTag()){
			return false;
		}
		
		Expression formExp = ueForm.getChild();
		Expression patternExp = uePattern.getChild();
		
		if(patternExp instanceof FreeIdentifier){
			if(!existingBinding.putMapping((FreeIdentifier)patternExp, formExp)){
				return false;
			}
		}
		else {
			if(!MatchingFactory.match(formExp, patternExp, existingBinding)){
				return false;
			}
		}
		return true;
	}

	@Override
	protected UnaryExpression cast(Expression e) {
		// TODO Auto-generated method stub
		return (UnaryExpression) e;
	}

}
