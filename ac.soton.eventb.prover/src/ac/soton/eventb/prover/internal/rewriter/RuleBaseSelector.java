package ac.soton.eventb.prover.internal.rewriter;

import java.util.List;

import org.eventb.core.ast.Formula;

import ac.soton.eventb.prover.base.IRuleBaseManager;
import ac.soton.eventb.prover.base.RuleBaseManager;
import ac.soton.eventb.prover.engine.IBinding;
import ac.soton.eventb.prover.engine.MatchFinder;
import ac.soton.eventb.prover.internal.base.IDRewriteRule;
import ac.soton.eventb.prover.internal.tactic.RewriteRuleApplicabilityInfo;

/**
 * The rule base selector provides the following capabilities:
 * <ul>
 * <li> checks whether a given formula can be rewritten using interactive rules in the rule base.
 * <li> if a rule is applicable to the given formula, it stores the needed information about the interactive rule applicability.
 * </ul>
 *  Applicability information include the name of the rule, the name of the parent theory  as well as its tool tip and description.
 *  It also includes whether the rewrite rule is conditional or not.
 *  <p>
 * @see RewriteRuleApplicabilityInfo
 * @author maamria
 *
 */
public class RuleBaseSelector {

	protected MatchFinder finder;
	protected IRuleBaseManager ruleBaseManager;
	
	private List<RewriteRuleApplicabilityInfo> infos;

	/**
	 * Constructs a selector that will populate the given list <code>infos</code> with applicable rewrite rules.
	 * @param infos the list in which to store applicability information
	 */
	public RuleBaseSelector(List<RewriteRuleApplicabilityInfo> infos) {
		finder = MatchFinder.getDefault();
		ruleBaseManager = RuleBaseManager.getDefault();
		this.infos = infos;
	}

	/**
	 * Returns whether at least one rewrite rule is applicable to <code>form</code>, and in the
	 * process, it populates the list <code>infos</code> with applicability information if any.
	 * @param form the formula
	 * @return <code>true</code> if at least one rule is applicable
	 */
	public boolean select(Formula<?> form) {
		List<IDRewriteRule> rules = ruleBaseManager.getInteractiveRules(form.getClass());
		boolean selected = false;
		for (IDRewriteRule rule : rules) {
			if (canFindABinding(form, rule)){
				infos.add(new 
						RewriteRuleApplicabilityInfo(
								form, rule.getTheoryName(), rule.getRuleName(), rule.isConditional(), rule.getToolTip(), rule.getDescription()));
				selected = true;
			}
		}
		return selected;
	}
	/**
	 * Returns whether the rewrite rule <code>rule</code> is applicable to <code>form</code>.
	 * @param form the theory formula
	 * @param rule the rewrite rule
	 * @return <code>true</code> iff <code>rule</code> is applicable to <code>form</code>
	 */
	protected boolean canFindABinding(Formula<?> form, IDRewriteRule rule){
		Formula<?> lhs = rule.getLeftHandSide();
		IBinding binding = finder.calculateBindings(form, lhs, true);
		if(binding == null)
			return false;
		return true;
	}
}
