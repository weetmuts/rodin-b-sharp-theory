package org.eventb.theory.rbp.reasoning;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.theory.rbp.rewriting.RewriteRuleManualApplyer;
import org.eventb.theory.rbp.rulebase.IPOContext;

/**
 * <p>An implementation of a manual rewriter.</p>
 * @author maamria
 *
 */
public class ManualRewriter {

	private RewriteRuleManualApplyer applyer ;
	
	private IPOContext context;
	
	public ManualRewriter(IPOContext context){
		this.context = context;
	}
	
	public void setFormulaFactory(FormulaFactory factory){
		applyer = new RewriteRuleManualApplyer(factory, context);
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
