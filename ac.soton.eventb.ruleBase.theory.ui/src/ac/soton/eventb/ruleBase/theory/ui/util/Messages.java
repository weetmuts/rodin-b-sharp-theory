/**
 * 
 */
package ac.soton.eventb.ruleBase.theory.ui.util;

import java.text.MessageFormat;

import org.eclipse.osgi.util.NLS;

/**
 * @author maamria
 * 
 */
public class Messages {
	
	public static String rewriteRule_isAutomatic;
	public static String rewriteRule_isComplete;
	public static String rewriteRule_isConditional;
	public static String rewriteRule_isIncomplete;
	public static String rewriteRule_isInteractive;
	public static String rewriteRule_isNotAutomatic;
	public static String rewriteRule_isUnconditional;
	public static String rewriteRule_isUnInteractive;
	
	public static String theoryPage_tabTitle;
	public static String theoryPage_title;
	
	public static String theoryUIUtils_deployCleanupError;
	public static String theoryUIUtils_deploySuccess;
	public static String theoryUIUtils_unexpectedError;
	
	private static final String BUNDLE_NAME = "ac.soton.eventb.ruleBase.theory.ui.util.messages"; //$NON-NLS-1$

	static {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
		// Do not instantiate
	}

	/**
	 * Bind the given message's substitution locations with the given string
	 * values.
	 * 
	 * @param message
	 *            the message to be manipulated
	 * @param bindings
	 *            An array of objects to be inserted into the message
	 * @return the manipulated String
	 */
	public static String bind(String message, Object... bindings) {
		return MessageFormat.format(message, bindings);
	}

}
