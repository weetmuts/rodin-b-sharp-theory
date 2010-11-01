package org.eventb.theory.rbp.base;

import java.util.List;

import org.eventb.core.ast.Formula;
import org.eventb.theory.core.IReasoningTypeElement.ReasoningType;
import org.eventb.theory.rbp.internal.base.IDeployedInferenceRule;
import org.eventb.theory.rbp.internal.base.IDeployedRewriteRule;
import org.rodinp.core.IElementChangedListener;

/**
 * <p>A common protocol for a rule base manager.</p>
 * @author maamria
 *
 */
public interface IRuleBaseManager extends IElementChangedListener{

	public final static boolean loadDefinitionalRules = false;
	
	/**
	 * Returns the list of rewrite rules.
	 * @return rewrite rules
	 */
	public List<IDeployedRewriteRule> getRewriteRules();
	
	/**
	 * Returns the list of rewrite rules satisfying the given criteria.
	 * @param automatic
	 * @param clazz
	 * @return rewrite rules
	 */
	@SuppressWarnings("rawtypes")
	public List<IDeployedRewriteRule> getRewriteRules(
			boolean automatic,Class<? extends Formula> clazz);
	
	/**
	 * Returns the list of rewrite rules satisfying the given criteria.
	 * @param automatic
	 * @param conditional
	 * @return rewrite rules
	 */
	public List<IDeployedRewriteRule> getRewriteRules(boolean automatic);
	
	/**
	 * Returns the list of deployed inference rules.
	 * @return deployed rules
	 */
	public List<IDeployedInferenceRule> getInferenceRules();
	
	/**
	 * Returns the list of inference rules suitable for the given reasoning type.
	 * @param type the reasoning type
	 * @return inference rules
	 */
	public List<IDeployedInferenceRule> getInferenceRules(ReasoningType type);
	
	/**
	 * Returns the list of inference rules that are either automatic or interactive.
	 * @param automatic 
	 * @return inference rules
	 */
	public List<IDeployedInferenceRule> getInferenceRules(boolean automatic);
	
	/**
	 * Returns the list of inference rules satisfying the given criteria.
	 * @param type reasoning type
	 * @param automatic 
	 * @return inference rules
	 */
	public List<IDeployedInferenceRule> getInferenceRules(ReasoningType type, boolean automatic);
	
	/**
	 * Returns the deployed inference rule by the given name.
	 * @param ruleName the name of the rule
	 * @param theoryName the theory name
	 * @return the deployed rule
	 */
	public IDeployedInferenceRule getInferenceRule(String ruleName, String theoryName);
	
	/**
	 * Returns the interactive rule specified by its name, the name of its parent theory as well as the runtime class of its lhs.
	 * @param ruleName name of the rule
	 * @param theoryName name of the parent theory
	 * @param clazz the class of the lhs of the rule
	 * @return the rule or <code>null</code> if not found
	 */
	public <E extends Formula<? extends Formula<?>>> IDeployedRewriteRule getInteractiveRule(
			String ruleName, String theoryName, Class<E> clazz);
	
}
