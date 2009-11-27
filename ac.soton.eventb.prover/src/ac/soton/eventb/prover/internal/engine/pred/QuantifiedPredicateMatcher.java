package ac.soton.eventb.prover.internal.engine.pred;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedPredicate;

import ac.soton.eventb.prover.engine.IBinding;
import ac.soton.eventb.prover.internal.engine.PredicateMatcher;
import ac.soton.eventb.prover.utils.GeneralUtilities;

public class QuantifiedPredicateMatcher extends PredicateMatcher<QuantifiedPredicate> {

	public QuantifiedPredicateMatcher() {
		super(QuantifiedPredicate.class);
	}

	@Override
	protected boolean gatherBindings(QuantifiedPredicate qpForm,
			QuantifiedPredicate qpPattern, IBinding existingBinding){
		if(qpForm.getTag() != qpPattern.getTag()){
			return false;
		}
		BoundIdentDecl[] fDec = qpForm.getBoundIdentDecls();
		BoundIdentDecl[] pDec = qpPattern.getBoundIdentDecls();
		if(!boundIdentDecsMatch(fDec, pDec)){
			return false;
		}
		
		Predicate fPred = qpForm.getPredicate();
		Predicate pPred = qpPattern.getPredicate();
		if(!engine.match(fPred, pPred, existingBinding)){
			return false;
		}
		return true;
	}

	@Override
	protected QuantifiedPredicate cast(Predicate p) {
		// TODO Auto-generated method stub
		return (QuantifiedPredicate) p;
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
				if(!GeneralUtilities.canUnifyTypes(fDec.getType(), pDec.getType())){
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
