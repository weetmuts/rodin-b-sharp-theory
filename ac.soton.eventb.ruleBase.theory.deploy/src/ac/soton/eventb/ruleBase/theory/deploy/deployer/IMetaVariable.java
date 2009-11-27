package ac.soton.eventb.ruleBase.theory.deploy.deployer;

import org.eventb.core.IIdentifierElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;

import ac.soton.eventb.ruleBase.theory.core.ITypingElement;
import ac.soton.eventb.ruleBase.theory.deploy.plugin.TheoryDeployPlugIn;

/**
 * <p>Common protocol for a meta variable internal element. 
 * It has an identifier and a type.</p>
 * @author maamria
 *
 */
public interface IMetaVariable extends IIdentifierElement, ITypingElement{

	IInternalElementType<IMetaVariable> ELEMENT_TYPE = RodinCore
		.getInternalElementType(TheoryDeployPlugIn.PLUGIN_ID + ".metaVariable");
}
