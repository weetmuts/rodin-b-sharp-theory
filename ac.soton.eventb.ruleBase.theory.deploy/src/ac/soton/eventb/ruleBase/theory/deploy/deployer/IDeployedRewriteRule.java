package ac.soton.eventb.ruleBase.theory.deploy.deployer;

import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

import ac.soton.eventb.ruleBase.theory.core.IAutomaticElement;
import ac.soton.eventb.ruleBase.theory.core.ICompleteElement;
import ac.soton.eventb.ruleBase.theory.core.IDescriptionElement;
import ac.soton.eventb.ruleBase.theory.core.IInteractiveElement;
import ac.soton.eventb.ruleBase.theory.core.ILeftHandSideElement;
import ac.soton.eventb.ruleBase.theory.core.ISCRewriteRule;
import ac.soton.eventb.ruleBase.theory.core.IToolTipElement;
import ac.soton.eventb.ruleBase.theory.deploy.plugin.TheoryDeployPlugIn;

/**
 * <p>Common protocol for a deployed rewrite rule internal element.</p>
 * <p>This is similar to <code>ISCRewriteRule</code> with soundness information added to it.</p>
 * @see ISCRewriteRule
 * @author maamria
 *
 */
public interface IDeployedRewriteRule extends ISoundElement, ILeftHandSideElement,
ICompleteElement, IInteractiveElement, IAutomaticElement, IToolTipElement, IDescriptionElement{

	IInternalElementType<IDeployedRewriteRule> ELEMENT_TYPE = RodinCore
		.getInternalElementType(TheoryDeployPlugIn.PLUGIN_ID + ".deployedRewRule");
	
	/**
	 * <p>Returns a right hand side with the given name.</p>
	 * <p>This is handle-only method.</p>
	 * @param rhsName
	 * @return the right hand side with the given names
	 */
	IDeployedRuleRHS getRHS(String rhsName);
	
	/**
	 * <p>Returns all right hand sides of the rule.</p>
	 * @return all rhs's
	 * @throws RodinDBException if a problem occurred
	 */
	IDeployedRuleRHS[] getRHSs() throws RodinDBException;
	
}
