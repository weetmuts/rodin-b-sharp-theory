package ac.soton.eventb.prover.rewriter;

import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofRule.IAntecedent;

import ac.soton.eventb.prover.internal.rewriter.RuleManualApplyer;

/**
 * <p>An implementation of a manual rewriter.</p>
 * @author maamria
 *
 */
public class RuleBaseManualRewriter {

	private RuleManualApplyer applyer ;
	
	public RuleBaseManualRewriter(){
		applyer = RuleManualApplyer.getInstance();
	}
	
	/**
	 * Returns the antecedents resulting from applying the specified rule.
	 * <p>
	 * @param pred to which the rule was applicable
	 * @param position 
	 * @param isGoal 
	 * @param theoryName
	 * @param ruleName
	 * @return the antecedents or <code>null</code> if the rule is not found or inapplicable
	 */
	public IAntecedent[] getAntecedents(Predicate pred, IPosition position, boolean isGoal, String theoryName, String ruleName){
		return applyer.applyRule(pred, position, isGoal, theoryName, ruleName);
	}
}
