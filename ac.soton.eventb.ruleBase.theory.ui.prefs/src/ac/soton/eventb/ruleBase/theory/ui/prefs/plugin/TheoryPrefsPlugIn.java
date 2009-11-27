package ac.soton.eventb.ruleBase.theory.ui.prefs.plugin;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import ac.soton.eventb.ruleBase.theory.ui.prefs.util.Messages;
import ac.soton.eventb.ruleBase.theory.ui.prefs.util.TheoryPrefsUtils;

/**
 * The activator class controls the plug-in life cycle
 */
public class TheoryPrefsPlugIn extends AbstractUIPlugin {

	// string delimiter for categories stored as a comma separated strings
	public static final String CATEGORIES_DELIM = ",";

	// The plug-in ID
	public static final String PLUGIN_ID = "ac.soton.eventb.ruleBase.theory.ui.prefs";
	// store key for available cats
	public static final String THEORY_CAT_KEY = "theory.cat";
	// the available cats
	public static String THEORY_CATEGORIES;
	// default categories
	public static final String THEORY_DEFAULT_CAT = Messages.theory_defaultCategories;
	// default deploy directory
	public static final String THEORY_DEFAULT_DIR = System.getProperty("user.home") +
		System.getProperty("file.separator") + "rodin";
	// deploy dir store key
	public static final String THEORY_DIR_KEY = "theory.dir";
	// the deployment directory
	public static String THEORY_DIRECTORY;
	// the global category
	public static final String THEORY_MAIN_CAT = Messages.theory_defaultMainCategory;
	// The shared instance
	private static TheoryPrefsPlugIn plugin;
	
	/**
	 * The constructor
	 */
	public TheoryPrefsPlugIn() {
		super();
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		initializePreferences();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Initialise the preferences.
	 */
	private void initializePreferences() {
		final IPreferenceStore store = getPreferenceStore();
		// init deployment dir
		THEORY_DIRECTORY = store.getString(THEORY_DIR_KEY);
		// init categories
		THEORY_CATEGORIES = store.getString(THEORY_CAT_KEY);
		// make sure deployment dir exists and has a DTD file in it
		TheoryPrefsUtils.checkAndCreateTheoryDirectory(THEORY_DIRECTORY);
	}
	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static TheoryPrefsPlugIn getDefault() {
		return plugin;
	}
	
}

