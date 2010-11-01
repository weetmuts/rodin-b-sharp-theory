package org.eventb.theory.rbp.internal.engine.exp;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedExpression;

import org.eventb.theory.rbp.engine.ExpressionMatcher;
import org.eventb.theory.rbp.engine.IBinding;
import org.eventb.theory.rbp.internal.engine.MatchingFactory;
import org.eventb.theory.rbp.utils.TypeMatcher;

public class QuantifiedExpressionMatcher extends  ExpressionMatcher<QuantifiedExpression>{

	public QuantifiedExpressionMatcher(){
		super(QuantifiedExpression.class);
	}
	
	@Override
	protected boolean gatherBindings(QuantifiedExpression qeForm,
			QuantifiedExpression qePattern, IBinding existingBinding){
		if(qeForm.getTag() != qePattern.getTag()){
			return false;
		}
		if(qeForm.getTag() == Formula.CSET){
			if(qeForm.getForm() != qePattern.getForm()){
				return false;
			}
		}
		BoundIdentDecl[] fDec = qeForm.getBoundIdentDecls();
		BoundIdentDecl[] pDec = qePattern.getBoundIdentDecls();
		if(!boundIdentDecsMatch(fDec, pDec, existingBinding)){
			return false;
		}
		
		Expression fExp = qeForm.getExpression();
		Expression pExp = qePattern.getExpression();
		if(pExp instanceof FreeIdentifier){
			if(!existingBinding.putMapping((FreeIdentifier) pExp, fExp)){
				return false;
			}
		}
		else {
			if(!MatchingFactory.match(fExp, pExp, existingBinding)){
				return false;
			}
		}
		
		Predicate fPred = qeForm.getPredicate();
		Predicate pPred = qePattern.getPredicate();
		if(!MatchingFactory.match(fPred, pPred, existingBinding)){
			return false;
		}
		return true;
	}

	@Override
	protected QuantifiedExpression cast(Expression e) {
		// TODO Auto-generated method stub
		return (QuantifiedExpression) e;
	}

	
	
	/**
	 *  TODO FIXME this is incomplete
	 * @param fDecs
	 * @param pDecs
	 * @return
	 */
	private boolean boundIdentDecsMatch(BoundIdentDecl[] formulaDecs, 
			BoundIdentDecl[] patternDecs, IBinding existingBinding){
		if(formulaDecs.length == patternDecs.length){
			int index = 0;
			for(BoundIdentDecl pDec: patternDecs){
				BoundIdentDecl fDec = formulaDecs[index];
				if(!TypeMatcher.canUnifyTypes(fDec.getType(), pDec.getType(), existingBinding)){
					return false;
				}
				index++;
			}
			return true;
		}
		else 
			return false;
	}

}
