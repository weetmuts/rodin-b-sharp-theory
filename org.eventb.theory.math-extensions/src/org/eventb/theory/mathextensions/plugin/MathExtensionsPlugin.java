package org.eventb.theory.mathextensions.plugin;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

public class MathExtensionsPlugin extends Plugin {

	public static final String PLUGIN_ID = "org.eventb.theory.math-extensions";
	
	private static MathExtensionsPlugin plugin;

	public MathExtensionsPlugin(){}
	
	public static MathExtensionsPlugin getDefault(){
		return plugin;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		super.start(bundleContext);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		plugin = null;
		super.stop(bundleContext);
	}

}
