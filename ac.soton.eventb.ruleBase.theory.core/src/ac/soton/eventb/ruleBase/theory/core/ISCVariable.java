package ac.soton.eventb.ruleBase.theory.core;

import org.eventb.core.ISCIdentifierElement;
import org.eventb.core.ISCPredicateElement;
import org.eventb.core.ITraceableElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;

import ac.soton.eventb.ruleBase.theory.core.plugin.TheoryPlugin;

/**
 * <p>Common protocol for a statically checked variable.</p>
 * <p>SC Variables do not have a typing attribute. Rather, they have a typing predicate. This is used by POG to generate the hypotheses.</p>
 * <p>This interface is not intended to be implemented by clients.</p>
 * 
 * @author maamria
 *
 */
public interface ISCVariable extends ITraceableElement, ISCIdentifierElement, ISCPredicateElement{

	IInternalElementType<ISCVariable> ELEMENT_TYPE = RodinCore
		.getInternalElementType(TheoryPlugin.PLUGIN_ID + ".scVariable");
}
