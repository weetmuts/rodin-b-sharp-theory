package ac.soton.eventb.ruleBase.theory.core;

import org.eventb.core.ISCIdentifierElement;
import org.eventb.core.ITraceableElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;

import ac.soton.eventb.ruleBase.theory.core.plugin.TheoryPlugin;
/**
 * <p>Common protocol for a statically checked theory set.</p>
 * <p>This interface is not intended to be implemented by clients.</p>
 * @author maamria
 *
 */
public interface ISCSet extends ITraceableElement, ISCIdentifierElement {

	IInternalElementType<ISCSet> ELEMENT_TYPE = RodinCore
			.getInternalElementType(TheoryPlugin.PLUGIN_ID + ".scSet");
}
