package ac.soton.eventb.ruleBase.theory.deploy.deployer;

import org.rodinp.core.IAttributeType;
import org.rodinp.core.RodinCore;

import ac.soton.eventb.ruleBase.theory.deploy.plugin.TheoryDeployPlugIn;

/**
 * A class prividing access to registered attributes.
 * <p>
 * @author maamria
 *
 */
public class DeployAttributes {

	public static IAttributeType.Boolean SOUND_ATTRIBUTE = RodinCore
		.getBooleanAttrType(TheoryDeployPlugIn.PLUGIN_ID + ".sound");
}
