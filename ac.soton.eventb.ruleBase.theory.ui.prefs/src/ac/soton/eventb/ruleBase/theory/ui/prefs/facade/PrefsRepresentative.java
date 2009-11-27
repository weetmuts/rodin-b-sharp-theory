package ac.soton.eventb.ruleBase.theory.ui.prefs.facade;

import ac.soton.eventb.ruleBase.theory.ui.prefs.plugin.TheoryPrefsPlugIn;
import ac.soton.eventb.ruleBase.theory.ui.prefs.util.TheoryPrefsUtils;
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
					TheoryPrefsPlugIn.THEORY_DIRECTORY);
	}
	/**
	 * <p>Returns an array of the pre-defined categories.</p>
	 * @return the available categories
	 */
	public static String[] getCategories(){
		return TheoryPrefsUtils.getAvailableCategories(
				TheoryPrefsPlugIn.THEORY_CATEGORIES, 
				TheoryPrefsPlugIn.CATEGORIES_DELIM);
	}
	/**
	 * <p>Returns the default category to assign to theories.</p>
	 * @return the main category
	 */
	public static String getMainCategory(){
		return TheoryPrefsPlugIn.THEORY_MAIN_CAT;
	}
	/**
	 * <p>Returns the directory to which theories will be deployed.</p>
	 * @return the directory
	 */
	public static String getTheoriesDirectory(){
		return TheoryPrefsPlugIn.THEORY_DIRECTORY;
	}
}
