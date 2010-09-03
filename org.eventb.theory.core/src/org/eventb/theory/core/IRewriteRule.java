package org.eventb.theory.core;

import org.eventb.core.ICommentedElement;
import org.eventb.core.ILabeledElement;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * <p>Common protocol for a rewrite rule.</p>
 * <p>A rewrite rule has a label, a left hand side formula, a tool tip, a description and a comment.</p>
 * <p>A rewrite rule can be automatic, interactive and/or complete.</p>
 * @author maamria
 *
 */
public interface IRewriteRule extends ICommentedElement, ILabeledElement,
		IFormulaElement, IAutomaticElement, IInteractiveElement, ICompleteElement,
		IToolTipElement, IDescriptionElement{

	IInternalElementType<IRewriteRule> ELEMENT_TYPE = RodinCore
			.getInternalElementType(TheoryPlugin.PLUGIN_ID + ".rewriteRule");
	/**
	 * <p>Returns the right hand side sub-element with the given name.</p>
	 * <p>This is handle-only method.</p>
	 * @param name of the rhs
	 * @return the designated rhs
	 */
	IRewriteRuleRightHandSide getRuleRHS(String name);
	/**
	 * <p>Returns all right hand side sub-elements of the rule.</p>
	 * @return all right hand side sub-elements of the rule
	 * @throws RodinDBException
	 */
	IRewriteRuleRightHandSide[] getRuleRHSs() throws RodinDBException;
	
}
