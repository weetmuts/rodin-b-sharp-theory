package ac.soton.eventb.prover.internal.rewriter;

import java.util.List;

import org.eventb.core.ast.Formula;

import ac.soton.eventb.prover.engine.IBinding;
import ac.soton.eventb.prover.internal.base.IDRewriteRule;
import ac.soton.eventb.prover.reasoner.AutoRewriteReasoner;

/**
 * Automatic unconditional rules applyer singleton implementation.
 * @author maamria
 *
 */
public class UnconditionalRuleAutoApplyer extends AbstractRewriteRuleApplyer{

	private static UnconditionalRuleAutoApplyer instance;
	
	/**
	 * Returns the singleton instance.
	 * @return the singleton instance
	 */
	public static UnconditionalRuleAutoApplyer getInstance(){
		if(instance == null )
			instance = new UnconditionalRuleAutoApplyer();
		return instance;
	}
	
	/**
	 * Returns the formula resulting from applying all the (applicable) automatic unconditional rules.
	 * @param original the formula to rewrite
	 * @return the rewritten formula
	 */
	public Formula<?> applyRules(Formula<?> original){
		List<IDRewriteRule> rules = manager.getAutoUnconditionalRules(original.getClass());
		Formula<?> result = original;
		for(IDRewriteRule rule: rules){
			result = applyRule(result, rule);
		}
		return result;
	}
	
	private Formula<?> applyRule(Formula<?> original, IDRewriteRule rule){
		Formula<?> ruleLhs = rule.getLeftHandSide();
		IBinding binding = finder.calculateBindings(original, ruleLhs);
		if(binding == null){
			return original;
		}
		// since rule is unconditional
		Formula<?> ruleRhs = rule.getRightHandSides().get(0).getRHSFormula();
		Formula<?> result = simpleBinder.applyBinding(ruleRhs, binding);
		
		addUsedTheory(rule.getTheoryName());

		return result;
	}
	
	private void addUsedTheory(String name){
		if(!AutoRewriteReasoner.usedTheories.contains(name))
			AutoRewriteReasoner.usedTheories.add(name);
	}
	
	
	
	
}
