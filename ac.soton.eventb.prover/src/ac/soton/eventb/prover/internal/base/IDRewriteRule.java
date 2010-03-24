package ac.soton.eventb.prover.internal.base;

import java.util.List;

import org.eventb.core.ast.Formula;
import org.eventb.core.ast.ITypeEnvironment;

/**
 * <p>Common protocol for a deployed rewrite rule in a deployed theory file.</p>
 * <p>Objects of this type correspond directly to an element in a theory file in the deployment directory.</p>
 * <p>Objects of this type should be immutable (up to the containing theory).</p>
 * @author maamria
 *
 */
public interface IDRewriteRule {
	/**
	 * <p>Returns the description of this rule.</p>
	 * @return the description
	 */
	public String getDescription();
	
	/**
	 * <p>Returns the left hand side of the rule.</p>
	 * @return the lhs
	 */
	public Formula<?> getLeftHandSide();
	
	/**
	 * <p>Returns a list of this rule right hand sides.</p>
	 * @return all rhs's
	 */
	public List<IDRuleRightHandSide> getRightHandSides();
	
	/**
	 * <p>Returns the name of the rule.</p>
	 * @return the rule name
	 */
	public String getRuleName();
	
	/**
	 * <p>Returns the name of the parent theory.</p>
	 * @return parent theory name without extension
	 */
	public String getTheoryName();
	
	/**
	 * <p>Returns the tool tip associated with this rule.</p>
	 * @return the tool tip
	 */
	public String getToolTip();
	
	/**
	 * <p>Returns the type environment under which the sides of the rule are typecheck.</p>
	 * @return the rule type environment
	 */
	public ITypeEnvironment getTypeEnvironment();
	
	/**
	 * <p>Returns whether the rule can be used by the automatic prover.</p>
	 * @return whether the rule is automatic
	 */
	public boolean isAutomatic();
	
	/**
	 * <p>Returns whether the right hand sides of the rule are complete.</p>
	 * @return whether the rule is complete
	 */
	public boolean isComplete();
	
	/**
	 * <p>Returns whether this rule is conditional or unconditional.</p>
	 * @return whether the rule is conditional
	 */
	public boolean isConditional();
	
	/**
	 * <p>Returns whether the left hand side of this rule is an expression.</p>
	 * <p> Obviously, this method is redundant since that can be found out by checking the rule lhs. Nonetheless, this is provided as a facility.</p>
	 * @return whether lhs of rule is an expression
	 */
	public boolean isExpression();
	
	/**
	 * <p>Returns whether the rule can be used interactively.</p>
	 * @return whether the rule is interactive
	 */
	public boolean isInteracive();
	
	/**
	 * <p>Returns whether this rule is sound i.e., its proof obligations have all been either discharged or reviewed.</p>
	 * @return whether the rule is sound
	 */
	public boolean isSound();
	
}
