package ac.soton.eventb.ruleBase.theory.core;

import org.rodinp.core.IAttributeType;
import org.rodinp.core.RodinCore;

import ac.soton.eventb.ruleBase.theory.core.plugin.TheoryPlugin;

/**
 * <p>Utility class that provides access to the registered attribute types through the designated extension point.</p>
 * @author maamria
 * 
 */
public final class TheoryAttributes {

	public static IAttributeType.Boolean AUTOMATIC_ATTRIBUTE = RodinCore
			.getBooleanAttrType(TheoryPlugin.PLUGIN_ID + ".auto");
	
	public static IAttributeType.Boolean INTERACTIVE_ATTRIBUTE = RodinCore
			.getBooleanAttrType(TheoryPlugin.PLUGIN_ID + ".interactive");

	public static IAttributeType.String LHS_ATTRIBUTE = RodinCore
			.getStringAttrType(TheoryPlugin.PLUGIN_ID + ".lhs");

	public static IAttributeType.String RHS_ATTRIBUTE = RodinCore
			.getStringAttrType(TheoryPlugin.PLUGIN_ID + ".rhs");
	
	public static IAttributeType.String CATEGORY_ATTRIBUTE = RodinCore
			.getStringAttrType(TheoryPlugin.PLUGIN_ID + ".category");
	
	public static IAttributeType.Boolean COMPLETE_ATTRIBUTE = RodinCore
			.getBooleanAttrType(TheoryPlugin.PLUGIN_ID + ".complete");
	
	public static IAttributeType.String TYPING_ATTRIBUTE = RodinCore
			.getStringAttrType(TheoryPlugin.PLUGIN_ID + ".type");
	
	public static IAttributeType.String TOOL_TIP_ATTRIBUTE = RodinCore
		.getStringAttrType(TheoryPlugin.PLUGIN_ID + ".toolTip");
	
	public static IAttributeType.String DESC_ATTRIBUTE = RodinCore
		.getStringAttrType(TheoryPlugin.PLUGIN_ID + ".desc");
	
}
