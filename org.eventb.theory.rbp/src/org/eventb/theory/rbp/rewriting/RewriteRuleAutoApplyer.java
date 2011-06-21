package org.eventb.theory.rbp.rewriting;

import java.util.List;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.theory.rbp.internal.rulebase.IDeployedRewriteRule;
import org.eventb.theory.rbp.reasoners.AutoRewriteReasoner;
import org.eventb.theory.rbp.reasoning.AbstractRulesApplyer;
import org.eventb.theory.rbp.rulebase.IPOContext;

/**
 * Automatic unconditional rules applyer implementation.
 * @author maamria
 *
 */
public class RewriteRuleAutoApplyer extends AbstractRulesApplyer{
	
	public RewriteRuleAutoApplyer(FormulaFactory factory, IPOContext context) {
		super(factory, context);
	}

	/**
	 * Returns the formula resulting from applying all the (applicable) automatic unconditional rules.
	 * @param original the formula to rewrite
	 * @return the rewritten formula
	 */
	public Formula<?> applyRules(Formula<?> original){
		List<IDeployedRewriteRule> rules = getRules(original);
		Formula<?> result = original;
		for(IDeployedRewriteRule rule: rules){
			result = applyRule(result, rule);
		}
		return result;
	}
	
	private List<IDeployedRewriteRule> getRules(Formula<?> original) {
		if (original instanceof Expression){
			return manager.getExpressionRewriteRules(true, ((Expression)original).getClass(), context, factory);
		}
		return manager.getPredicateRewriteRules(true, ((Predicate)original).getClass(), context, factory);
	}

	private Formula<?> applyRule(Formula<?> original, IDeployedRewriteRule rule){
		return null;
	}
	
	protected void addUsedTheory(String name){
		if(!AutoRewriteReasoner.usedTheories.contains(name))
			AutoRewriteReasoner.usedTheories.add(name);
	}
	
	
	
	
}
