package org.eventb.core.pm.matchers.pred;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.PredicateVariable;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.pm.IBinding;
import org.eventb.core.pm.PredicateMatcher;

/**
 * TODO FIXME this is good when we only have one quantifier variable.
 * @since 1.0
 * @author maamria
 * 
 */
public class QuantifiedPredicateMatcher extends PredicateMatcher<QuantifiedPredicate> {

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
			return existingBinding.putPredicateMapping((PredicateVariable) pPred, fPred);
		} 
		return matchingFactory.match(fPred, pPred, existingBinding);
	}

	@Override
	protected QuantifiedPredicate getPredicate(Predicate p) {
		return (QuantifiedPredicate) p;
	}

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
