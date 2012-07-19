/**
 * 
 */
package org.eventb.theory.core;

import org.eventb.theory.core.plugin.TheoryPlugin;
import org.rodinp.core.IRodinProject;

/**
 * @author RenatoSilva
 *
 */
public class DatabaseUtilitiesTheoryPath{
	
	// As in "theory path unchecked language"
	public static final String THEORY_PATH_FILE_EXTENSION = "tul";
	// As in "theory path checked language"
	public static final String SC_THEORY_PATH_FILE_EXTENSION = "tcl";
	
	// The theory path configuration for the SC and POG
	public static final String THEORY_PATH_CONFIGURATION = TheoryPlugin.PLUGIN_ID + ".tul";
	
	/**
	 * Returns the full name of a theory path file.
	 * 
	 * @param name
	 *            the name
	 * @return the full name
	 */
	public static String getTheoryPathFullName(String name) {
		return name + "." + THEORY_PATH_FILE_EXTENSION;
	}
	
	/**
	 * Returns the full name of a theory path file.
	 * 
	 * @param name
	 *            the name
	 * @return the full name
	 */
	public static String getSCTheoryPathFullName(String name) {
		return name + "." + SC_THEORY_PATH_FILE_EXTENSION;
	}
	
	public static String getFullDescriptionAvailableTheory(IRodinProject rodinProject, IDeployedTheoryRoot deployedTheory){
		return "["+rodinProject.getElementName()+"]."+deployedTheory.getComponentName();
	}

}
