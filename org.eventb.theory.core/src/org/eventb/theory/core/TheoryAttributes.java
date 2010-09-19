/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core;

import org.eventb.theory.core.plugin.TheoryPlugin;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.RodinCore;

/**
 * This class provides access to the attributes defined in this plug-in.
 * 
 * @author maamria
 * 
 */
public class TheoryAttributes {

	public static IAttributeType.Boolean ASSOCIATIVE_ATTRIBUTE = RodinCore
			.getBooleanAttrType(TheoryPlugin.PLUGIN_ID + ".associative");

	public static IAttributeType.Boolean COMMUTATIVE_ATTRIBUTE = RodinCore
			.getBooleanAttrType(TheoryPlugin.PLUGIN_ID + ".commutative");

	public static IAttributeType.String FORMULA_ATTRIBUTE = RodinCore
			.getStringAttrType(TheoryPlugin.PLUGIN_ID + ".formula");
	// true if expression, false if predicate
	public static IAttributeType.Boolean FORMULA_TYPE_ATTRIBUTE = RodinCore
			.getBooleanAttrType(TheoryPlugin.PLUGIN_ID + ".formulaType");

	public static IAttributeType.String NOTATION_TYPE_ATTRIBUTE = RodinCore
			.getStringAttrType(TheoryPlugin.PLUGIN_ID + ".notationType");

	public static IAttributeType.String SYNTAX_SYMBOL_ATTRIBUTE = RodinCore
			.getStringAttrType(TheoryPlugin.PLUGIN_ID + ".syntaxSymbol");

	public static IAttributeType.String TYPE_ATTRIBUTE = RodinCore
			.getStringAttrType(TheoryPlugin.PLUGIN_ID + ".type");

	public static IAttributeType.Boolean AUTOMATIC_ATTRIBUTE = RodinCore
			.getBooleanAttrType(TheoryPlugin.PLUGIN_ID + ".auto");

	public static IAttributeType.Boolean INTERACTIVE_ATTRIBUTE = RodinCore
			.getBooleanAttrType(TheoryPlugin.PLUGIN_ID + ".interactive");

	public static IAttributeType.Boolean COMPLETE_ATTRIBUTE = RodinCore
			.getBooleanAttrType(TheoryPlugin.PLUGIN_ID + ".complete");

	public static IAttributeType.String TOOL_TIP_ATTRIBUTE = RodinCore
			.getStringAttrType(TheoryPlugin.PLUGIN_ID + ".toolTip");

	public static IAttributeType.String DESC_ATTRIBUTE = RodinCore
			.getStringAttrType(TheoryPlugin.PLUGIN_ID + ".desc");
	
	public static IAttributeType.Boolean DEFINITIONAL_ATTRIBUTE = RodinCore
			.getBooleanAttrType(TheoryPlugin.PLUGIN_ID + ".definitional");
	
	public static IAttributeType.String GIVEN_TYPE_ATTRIBUTE = RodinCore
		.getStringAttrType(TheoryPlugin.PLUGIN_ID + ".givenType");
	
	public static IAttributeType.Boolean HAS_ERROR_ATTRIBUTE = RodinCore
	 	.getBooleanAttrType(TheoryPlugin.PLUGIN_ID + ".hasError");
	
	public static IAttributeType.String REASONING_TYPE_ATTRIBUTE = RodinCore
		.getStringAttrType(TheoryPlugin.PLUGIN_ID + ".reasoningType");
	
	public static IAttributeType.Boolean MODIFIED_ATTRIBUTE = RodinCore
		.getBooleanAttrType(TheoryPlugin.PLUGIN_ID + ".modified");
	
	public static IAttributeType.Boolean VALIDATED_ATTRIBUTE = RodinCore
 		.getBooleanAttrType(TheoryPlugin.PLUGIN_ID + ".validated");
	
	public static IAttributeType.String GROUP_ID_ATTRIBUTE = RodinCore
		.getStringAttrType(TheoryPlugin.PLUGIN_ID + ".groupID");
}
