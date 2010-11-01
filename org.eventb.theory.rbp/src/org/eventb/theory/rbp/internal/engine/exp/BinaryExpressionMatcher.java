package org.eventb.theory.rbp.internal.engine.exp;

import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FreeIdentifier;

import org.eventb.theory.rbp.engine.ExpressionMatcher;
import org.eventb.theory.rbp.engine.IBinding;
import org.eventb.theory.rbp.internal.engine.MatchingFactory;

public class BinaryExpressionMatcher extends  ExpressionMatcher<BinaryExpression>{

	public BinaryExpressionMatcher(){
		super(BinaryExpression.class);
	}
	
	@Override
	protected boolean gatherBindings(BinaryExpression beForm,
			BinaryExpression bePattern, IBinding existingBinding){
		if(beForm.getTag() != bePattern.getTag()){
			return false;
		}
		// for left<s>
		Expression fLeft = beForm.getLeft();
		Expression pLeft = bePattern.getLeft();
		if(pLeft instanceof FreeIdentifier){
			if(!existingBinding.putMapping((FreeIdentifier) pLeft, fLeft)){
				return false;
			}
		}
		else{
			if(!MatchingFactory.match(fLeft, pLeft, existingBinding)){
				return false;
			}
		}
		// for right<s>
		Expression fRight = beForm.getRight();
		Expression pRight = bePattern.getRight();
		if(pRight instanceof FreeIdentifier){
			if(!existingBinding.putMapping((FreeIdentifier) pRight, fRight)){
				return false;
			}
		}
		else{
			if(!MatchingFactory.match(fRight, pRight, existingBinding)){
				return false;
			}
		}
		return true;
	}

	@Override
	protected BinaryExpression cast(Expression e) {
		// TODO Auto-generated method stub
		return (BinaryExpression) e;
	}

	

}
