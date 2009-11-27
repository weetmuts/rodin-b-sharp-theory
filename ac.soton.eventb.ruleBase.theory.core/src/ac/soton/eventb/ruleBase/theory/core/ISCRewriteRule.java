package ac.soton.eventb.ruleBase.theory.core;

import org.eventb.core.IAccuracyElement;
import org.eventb.core.ILabeledElement;
import org.eventb.core.ITraceableElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

import ac.soton.eventb.ruleBase.theory.core.plugin.TheoryPlugin;

/**
 * <p>Common protocol for a statically checked rewrite rule.</p>
 * <p>This interface is not intended to be implemented by clients.</p>
 * @author maamria
 *
 */
public interface ISCRewriteRule extends ITraceableElement, ILabeledElement,
		ISCLeftHandSideElement, IAutomaticElement , IInteractiveElement, ICompleteElement,
		IAccuracyElement, IToolTipElement, IDescriptionElement{

	IInternalElementType<ISCRewriteRule> ELEMENT_TYPE = RodinCore
			.getInternalElementType(TheoryPlugin.PLUGIN_ID + ".scRewriteRule");
	/**
	 * <p>Returns the SC right hand side sub-element with the given name.</p>
	 * <p>This is handle-only method.</p>
	 * @param name of rhs
	 * @return the SC rhs sub-element of the rule
	 */
	ISCRewriteRuleRightHandSide getSCRuleRHS(String name);
	/**
	 * <p>Returns all the SC rhs sub-elements of the rule.</p>
	 * @return all rhs sub-elements
	 * @throws RodinDBException
	 */
	ISCRewriteRuleRightHandSide[] getSCRuleRHSs() throws RodinDBException;
}
