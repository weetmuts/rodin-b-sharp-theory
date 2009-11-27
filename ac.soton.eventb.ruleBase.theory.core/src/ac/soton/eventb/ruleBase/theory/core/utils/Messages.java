/*******************************************************************************
 * Copyright (c) 2006-2007 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package ac.soton.eventb.ruleBase.theory.core.utils;

import java.text.MessageFormat;

import org.eclipse.osgi.util.NLS;

/**
 * @author maamria
 * 
 */
public final class Messages {

	private static final String BUNDLE_NAME = "ac.soton.eventb.ruleBase.theory.core.utils.messages";

	static {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	public static String database_SCFormulaParseFailure;
	public static String database_SCFormulaTCFailure;
	public static String database_SCPredicateParseFailure;
	public static String database_SCPredicateTCFailure;
	public static String database_SCIdentifierNameParseFailure;
	public static String database_SCIdentifierTypeParseFailure;
	
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