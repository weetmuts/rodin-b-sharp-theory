package org.eventb.theory.core.plugin;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

public class TheoryPlugin extends Plugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.eventb.theory.core";

	// The theory configuration for the SC and POG
	public static final String THEORY_CONFIGURATION = PLUGIN_ID + ".thy";
	
	// The shared instance
	private static TheoryPlugin plugin;
	
	public static final int SC_STARTING_INDEX = 1;
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