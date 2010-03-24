package ac.soton.eventb.prover.prefs;

import ac.soton.eventb.prover.internal.prefs.TheoryPrefsUtils;
import ac.soton.eventb.prover.plugin.ProverPlugIn;

/**
 * <p> This the facade of the preferences plugin. It provides access to fields that are deemed 
 * useful to other plugins. Cliens should use this class only.</p>
 * 
 * @author maamria
 *
 */
public class PrefsRepresentative {

	/**
	 * <p>This method when called makes sure the deployment directory exists with the DTD file in it.</p>
	 * @return whether the operation is successful
	 */
	public static boolean checkAndCreateTheoriesDirectory(){
		return TheoryPrefsUtils.checkAndCreateTheoryDirectory(
				ProverPlugIn.THEORY_DIRECTORY);
	}
	/**
	 * <p>Returns an array of the pre-defined categories.</p>
	 * @return the available categories
	 */
	public static String[] getCategories(){
		return TheoryPrefsUtils.getAvailableCategories(
				ProverPlugIn.THEORY_CATEGORIES, 
				ProverPlugIn.CATEGORIES_DELIM);
	}
	/**
	 * <p>Returns the default category to assign to theories.</p>
	 * @return the main category
	 */
	public static String getMainCategory(){
		return ProverPlugIn.THEORY_MAIN_CAT;
	}
	/**
	 * <p>Returns the directory to which theories will be deployed.</p>
	 * @return the directory
	 */
	public static String getTheoriesDirectory(){
		return ProverPlugIn.THEORY_DIRECTORY;
	}
}
