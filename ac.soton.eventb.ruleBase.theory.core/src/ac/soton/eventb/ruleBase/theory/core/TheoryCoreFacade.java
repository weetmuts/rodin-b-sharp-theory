package ac.soton.eventb.ruleBase.theory.core;

import ac.soton.eventb.ruleBase.theory.core.plugin.TheoryPlugin;

/**
 * <p> This class provides access to certain fields and methods that might be of use by external plugins.</p>
 * @author maamria
 *
 */
public class TheoryCoreFacade {
	/**
	 * <p>Returns the configuration used by theory files.</p>
	 * @return the configuration
	 */
	public static String getTheoryConfiguration(){
		return TheoryPlugin.THEORY_CONFIGURATION;
	}
}
