package ac.soton.eventb.ruleBase.theory.core.deploy.basis;

import org.eventb.core.IPredicateElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;

import ac.soton.eventb.ruleBase.theory.core.IRightHandSideElement;
import ac.soton.eventb.ruleBase.theory.core.plugin.TheoryPlugin;

/**
 * <p>Common protocol for a rule right hand side internal element. 
 * It has a right hand side and a condition (predicate).</p>
 * @author maamria
 *
 */
public interface IDeployedRuleRHS extends IRightHandSideElement,
IPredicateElement{
	
	IInternalElementType<IDeployedRuleRHS> ELEMENT_TYPE = RodinCore
		.getInternalElementType(TheoryPlugin.PLUGIN_ID + ".deployedRuleRHS");
}
