package org.eventb.theory.rbp.internal.engine.pred;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.PredicateVariable;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.theory.rbp.engine.IBinding;
import org.eventb.theory.rbp.engine.PredicateMatcher;
import org.eventb.theory.rbp.internal.engine.MatchingFactory;

/**
 * @since 1.0
 * @author maamria
 * 
 */
public class QuantifiedPredicateMatcher extends
		PredicateMatcher<QuantifiedPredicate> {

	public QuantifiedPredicateMatcher() {
		super(QuantifiedPredicate.class);
	}

	@Override
	protected boolean gatherBindings(QuantifiedPredicate qpForm,
			QuantifiedPredicate qpPattern, IBinding existingBinding) {
		if (qpForm.getTag() != qpPattern.getTag()) {
			return false;
		}
		BoundIdentDecl[] fDec = qpForm.getBoundIdentDecls();
		BoundIdentDecl[] pDec = qpPattern.getBoundIdentDecls();
		if (!boundIdentDecsMatch(fDec, pDec, existingBinding)) {
			return false;
		}

		Predicate fPred = qpForm.getPredicate();
		Predicate pPred = qpPattern.getPredicate();
		if (pPred instanceof PredicateVariable) {
			return existingBinding.putPredicateMapping(
					(PredicateVariable) pPred, fPred);
		} 
		return MatchingFactory.match(fPred, pPred, existingBinding);
	}

	@Override
	protected QuantifiedPredicate cast(Predicate p) {
		// TODO Auto-generated method stub
		return (QuantifiedPredicate) p;
	}

	/**
	 * TODO FIXME this is incomplete
	 * 
	 * @param fDecs
	 * @param pDecs
	 * @return
	 */
	private boolean boundIdentDecsMatch(BoundIdentDecl[] formulaDecs,
			BoundIdentDecl[] patternDecs, IBinding existingBinding) {
		if (formulaDecs.length == patternDecs.length) {
			int index = 0;
			for (BoundIdentDecl pDec : patternDecs) {
				BoundIdentDecl fDec = formulaDecs[index];
				if (!existingBinding.canUnifyTypes(fDec.getType(),
						pDec.getType())) {
					return false;
				}
				index++;
			}
			return true;
		} else
			return false;
	}
}
