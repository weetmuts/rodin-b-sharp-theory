package ac.soton.eventb.prover.base;

import java.util.List;

import ac.soton.eventb.prover.internal.base.IDRewriteRule;

/**
 * <p>A common protocol for a rule base manager.</p>
 * @author maamria
 *
 */
public interface IRuleBaseManager {

	/**
	 * Returns the list if interactive rules that are applicable to the given formula class.
	 * @param clazz the class of the formula
	 * @return an unmodifiable list of potential applicable rules
	 */
	public List<IDRewriteRule> getInteractiveRules(Class<?> clazz);
	/**
	 * Returns the list of automatic unconditional rules that are applicable to the given formula class.
	 * @param clazz the class of the formula
	 * @return an unmodifiable list of potential applicable rules
	 */
	public List<IDRewriteRule> getAutoUnconditionalRules(Class<?> clazz);
	/**
	 * Returns the list of automatic conditional rules that are applicable to the given formula class.
	 * @param clazz the class of the formula
	 * @return an unmodifiable list of potential applicable rules
	 */
	public List<IDRewriteRule> getAutoConditionalRules(Class<?> clazz);
	/**
	 * Returns the interactive rule specified by its name, the name of its parent theory as well as the runtime class of its lhs.
	 * @param ruleName name of the rule
	 * @param theoryName name of the parent theory
	 * @param clazz the class of the lhs of the rule
	 * @return the rule or <code>null</code> if not found
	 */
	public IDRewriteRule getInteractiveRule(String ruleName, String theoryName, Class<?> clazz);
	
}
