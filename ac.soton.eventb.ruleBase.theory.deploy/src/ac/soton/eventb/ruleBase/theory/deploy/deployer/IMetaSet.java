package ac.soton.eventb.ruleBase.theory.deploy.deployer;

import org.eventb.core.IIdentifierElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;

import ac.soton.eventb.ruleBase.theory.deploy.plugin.TheoryDeployPlugIn;

/**
 * <p>Common protocol for a meta set internal element.</p>
 * @author maamria
 *
 */
public interface IMetaSet extends IIdentifierElement{

	IInternalElementType<IMetaSet> ELEMENT_TYPE = RodinCore
		.getInternalElementType(TheoryDeployPlugIn.PLUGIN_ID + ".metaSet");
	
}
