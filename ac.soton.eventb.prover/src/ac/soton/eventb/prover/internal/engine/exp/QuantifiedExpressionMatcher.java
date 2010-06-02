package ac.soton.eventb.prover.internal.engine.exp;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedExpression;

import ac.soton.eventb.prover.engine.IBinding;
import ac.soton.eventb.prover.internal.engine.ExpressionMatcher;
import ac.soton.eventb.prover.utils.ProverUtilities;

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
		if(!boundIdentDecsMatch(fDec, pDec)){
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
			if(!engine.match(fExp, pExp, existingBinding)){
				return false;
			}
		}
		
		Predicate fPred = qeForm.getPredicate();
		Predicate pPred = qePattern.getPredicate();
		if(!engine.match(fPred, pPred, existingBinding)){
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
	private boolean boundIdentDecsMatch(BoundIdentDecl[] formulaDecs, BoundIdentDecl[] patternDecs){
		if(formulaDecs.length == patternDecs.length){
			int index = 0;
			for(BoundIdentDecl pDec: patternDecs){
				BoundIdentDecl fDec = formulaDecs[index];
				if(!ProverUtilities.canUnifyTypes(fDec.getType(), pDec.getType())){
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
