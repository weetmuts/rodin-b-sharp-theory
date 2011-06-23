package org.eventb.core.pm.matchers.exp;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.PredicateVariable;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.pm.basis.ExpressionMatcher;
import org.eventb.core.pm.basis.IBinding;

/**
 * @since 1.0
 * @author maamria
 *
 */
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
			if(!existingBinding.putExpressionMapping((FreeIdentifier) pExp, fExp)){
				return false;
			}
		}
		else {
			if(!matchingFactory.match(fExp, pExp, existingBinding)){
				return false;
			}
		}
		
		Predicate fPred = qeForm.getPredicate();
		Predicate pPred = qePattern.getPredicate();
		if(pPred instanceof PredicateVariable){
			return existingBinding.putPredicateMapping((PredicateVariable) pPred, fPred);
			
		}
		return matchingFactory.match(fPred, pPred, existingBinding);
	}

	@Override
	protected QuantifiedExpression getExpression(Expression e) {
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
				if(!existingBinding.canUnifyTypes(fDec.getType(), pDec.getType())){
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