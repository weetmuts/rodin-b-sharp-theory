/**
 * 
 */
package org.eventb.theory.internal.ui;

import java.text.MessageFormat;

import org.eclipse.osgi.util.NLS;

/**
 * @author maamria
 * 
 */
public class Messages {
	
	public static String deploy_deploySuccess;
	public static String deploy_deployFailure;
	
	public static String rule_isAutomatic;
	public static String rewriteRule_isComplete;
	public static String rewriteRule_isConditional;
	public static String rewriteRule_isIncomplete;
	public static String rule_isInteractive;
	public static String rule_isNotAutomatic;
	public static String rewriteRule_isUnconditional;
	public static String rule_isUnInteractive;
	
	public static String operator_isExpression;
	public static String operator_isPredicate;
	public static String operator_isAssociative;
	public static String operator_isNotAssociative;
	public static String operator_isCommutative;
	public static String operator_isNotCommutative;
	
	
	public static String theoryUIUtils_deploySuccess;
	public static String theoryUIUtils_deployConfirm;
	public static String theoryUIUtils_unexpectedError;
	
	private static final String BUNDLE_NAME = "org.eventb.theory.internal.ui.messages"; //$NON-NLS-1$

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
