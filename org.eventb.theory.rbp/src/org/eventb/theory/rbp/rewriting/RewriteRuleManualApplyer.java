package org.eventb.theory.rbp.rewriting;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.theory.rbp.reasoning.AbstractRulesApplyer;
import org.eventb.theory.rbp.rulebase.IPOContext;

/**
 * A manual rewrite rule applyer implementation.
 * @since 1.0
 * @author maamria
 *
 */
public class RewriteRuleManualApplyer extends AbstractRulesApplyer{
	
	public RewriteRuleManualApplyer(FormulaFactory factory, IPOContext context) {
		super(factory, context);
	}

	/**
	 * Returns the antecedents resulting from applying the specified rule.
	 * <p>
	 * @param predicate the predicate
	 * @param position the position at which to apply the rewrite
	 * @param isGoal whether <code>predicate</code> is the goal or a hypothesis
	 * @param theoryName the name of the theory
	 * @param ruleName the name of the rule
	 * @return the antecedents or <code>null</code> if the rule was not found or is inapplicable.
	 */
	public IAntecedent[] applyRule(Predicate predicate, IPosition position, boolean isGoal, String theoryName, String ruleName){
		return null;
	}
}
