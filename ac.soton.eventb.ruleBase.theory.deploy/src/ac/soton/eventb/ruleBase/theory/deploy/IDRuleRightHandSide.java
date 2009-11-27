package ac.soton.eventb.ruleBase.theory.deploy;

import org.eventb.core.ast.Formula;
import org.eventb.core.ast.Predicate;

/**
 * <p>Common protocol for a rule right hand side in a rewrite rule in a theory file.</p>
 * <p>Objects of this type correspond directly to certain elements in theory fils residing in the deployment directory.</p>
 * <p>Objects of this type are immutable.</p>
 * @author maamria
 *
 */
public interface IDRuleRightHandSide {
	/**
	 * <p>Returns the condition of this right hand side.</p>
	 * @return the condition
	 */
	public Predicate getCondition();
	
	/**
	 * <p>Returns the right hand side formula.</p>
	 * @return the formula
	 */
	public Formula<?> getRHSFormula();
	
	/**
	 * <p>Returns the name of this rhs.</p>
	 * @return the name
	 */
	public String getRHSName();
	
}
