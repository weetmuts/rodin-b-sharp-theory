package org.eventb.theory.internal.core.pog;

import org.eventb.core.pog.POGCore;
import org.eventb.core.tool.IModuleType;
import org.eventb.internal.core.pog.modules.BaseModule;
import org.eventb.theory.core.plugin.TheoryPlugin;

/**
 * 
 * @author maamria
 *
 */
@SuppressWarnings("restriction")
public class TheoryPOGModule extends BaseModule {

	public static final IModuleType<TheoryPOGModule> MODULE_TYPE = 
		POGCore.getModuleType(TheoryPlugin.PLUGIN_ID + ".theoryModule"); 
	
	
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

}
