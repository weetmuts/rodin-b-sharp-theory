package ac.soton.eventb.ruleBase.theory.core;

import org.eventb.core.ILabeledElement;
import org.eventb.core.ISCPredicateElement;
import org.eventb.core.ITraceableElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;

import ac.soton.eventb.ruleBase.theory.core.plugin.TheoryPlugin;
/**
 * <p>Common protocol for a statically checked right hand side element.</p>
 * <p>This interface is not intended to be implemented by clients.</p>
 * @author maamria
 *
 */
public interface ISCRewriteRuleRightHandSide extends ITraceableElement, ILabeledElement,
	ISCRightHandSideElement, ISCPredicateElement
{

	IInternalElementType<ISCRewriteRuleRightHandSide> ELEMENT_TYPE = RodinCore
		.getInternalElementType(TheoryPlugin.PLUGIN_ID + ".scRewriteRuleRHS");
}
