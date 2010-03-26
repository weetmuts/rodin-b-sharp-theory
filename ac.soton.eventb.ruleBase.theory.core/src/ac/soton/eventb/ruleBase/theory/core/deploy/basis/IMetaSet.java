package ac.soton.eventb.ruleBase.theory.core.deploy.basis;

import org.eventb.core.IIdentifierElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;

import ac.soton.eventb.ruleBase.theory.core.plugin.TheoryPlugin;

/**
 * <p>Common protocol for a meta set internal element.</p>
 * @author maamria
 *
 */
public interface IMetaSet extends IIdentifierElement{

	IInternalElementType<IMetaSet> ELEMENT_TYPE = RodinCore
		.getInternalElementType(TheoryPlugin.PLUGIN_ID + ".metaSet");
	
}
