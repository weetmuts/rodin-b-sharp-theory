package ac.soton.eventb.ruleBase.theory.core.plugin;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class TheoryPlugin extends Plugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "ac.soton.eventb.ruleBase.theory.core";

	// The shared instance
	private static TheoryPlugin plugin;

	public static final int SC_STARTING_INDEX = 1;

	public static final String SC_THEORY_FILE_EXTENSION = "bct";

	// The theory configuration for the SC and POG
	public static final String THEORY_CONFIGURATION = PLUGIN_ID + ".thy";

	public static final String THEORY_FILE_EXTENSION = "but";

	/**
	 * 
	 * @param bareName
	 * @return the statically checked theory file name with extension.
	 */
	public static String getSCTheoryFileName(String bareName) {
		return bareName + "." + SC_THEORY_FILE_EXTENSION;
	}

	/**
	 * 
	 * @param bareName
	 * @return the theory file name with extension.
	 */
	public static String getTheoryFileName(String bareName) {
		return bareName + "." + THEORY_FILE_EXTENSION;
	}
	
	/**
	 * The constructor
	 */
	public TheoryPlugin() {
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static TheoryPlugin getDefault() {
		return plugin;
	}
	
	

}
