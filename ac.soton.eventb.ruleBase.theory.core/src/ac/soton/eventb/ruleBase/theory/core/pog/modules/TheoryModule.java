package ac.soton.eventb.ruleBase.theory.core.pog.modules;

import org.eventb.core.pog.POGCore;
import org.eventb.core.tool.IModuleType;

import ac.soton.eventb.ruleBase.theory.core.plugin.TheoryPlugin;

/**
 * 
 * @author maamria
 *
 */
public class TheoryModule extends BaseModule {

	public static final IModuleType<TheoryModule> MODULE_TYPE = 
		POGCore.getModuleType(TheoryPlugin.PLUGIN_ID + ".theoryModule"); 
	
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

}
