/**
 * 
 */
package ac.soton.eventb.prover.internal.prefs;

import java.text.MessageFormat;

import org.eclipse.osgi.util.NLS;

/**
 * @author maamria
 * 
 */
public class Messages {
	
	public static String theory_defaultCategories;
	public static String theory_defaultMainCategory;
	
	private static final String BUNDLE_NAME = "ac.soton.eventb.prover.internal.prefs.messages"; //$NON-NLS-1$

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
