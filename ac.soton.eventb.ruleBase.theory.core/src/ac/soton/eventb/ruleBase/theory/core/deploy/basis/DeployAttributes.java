package ac.soton.eventb.ruleBase.theory.core.deploy.basis;

import org.rodinp.core.IAttributeType;
import org.rodinp.core.RodinCore;

import ac.soton.eventb.ruleBase.theory.core.plugin.TheoryPlugin;

/**
 * A class prividing access to registered attributes.
 * <p>
 * @author maamria
 *
 */
public class DeployAttributes {

	public static IAttributeType.Boolean SOUND_ATTRIBUTE = RodinCore
		.getBooleanAttrType(TheoryPlugin.PLUGIN_ID + ".sound");
}
