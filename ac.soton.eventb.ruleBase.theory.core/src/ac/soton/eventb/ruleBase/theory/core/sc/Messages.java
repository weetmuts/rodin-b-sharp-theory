/*******************************************************************************
 * Copyright (c) 2006, 2008 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added scuser_LoadingRootModuleError
 *     University of Dusseldorf - added theorem attribute
 *******************************************************************************/
package ac.soton.eventb.ruleBase.theory.core.sc;

/**
 * @author maamria
 *
 */
import java.text.MessageFormat;

import org.eclipse.osgi.util.NLS;

public final class Messages {

	// build
	public static String build_cleaning;

	public static String build_extracting;
	public static String build_runningSC;
	
	public static String progress_TheoryAxioms;
	public static String progress_TheoryRewriteRules;
	public static String progress_TheorySets;
	public static String progress_TheoryVariables;
	public static String progress_TheoryCategories;
	public static String progress_TheoryRewriteRuleRHSs;
	
	public static String scuser_AutoUndefWarning;
	public static String scuser_AxiomLabelConflict;
	public static String scuser_LhsAndRhsNotSynClassMatching;
	public static String scuser_LHSUndef;
	public static String scuser_RewriteRuleLabelConflict;
	public static String scuser_RHSIdentsNotSubsetOfLHSIdents;
	public static String scuser_RHSUndef;
	public static String scuser_RuleSideNotTheoryFormula;
	public static String scuser_RuleTypeMismatch;
	public static String scuser_TheorySetNameConflict;
	public static String scuser_UntypedTheorySetError;
	public static String scuser_UntypedVariableError;
	public static String scuser_VariableNameConflict;
	public static String scuser_RhsLabelConflict;
	public static String scuser_CondUndef;
	public static String scuser_UncondManyNoneRhs;
	public static String scuser_UncondRuleWithNonTrueCond;
	public static String scuser_CondAttrUndef;
	public static String scuser_RuleWithNoRHSs;
	public static String scuser_PredicateNotTypingPredError;
	public static String scuser_CategoryNotPredefines;
	public static String scuser_DuplicateCategory;
	public static String scuser_InterUndefWarning;
	public static String scuser_CompleteUndefWarning;
	public static String scuser_ToolTipNotSupplied;
	public static String scuser_NoToolTipWarning;
	public static String scuser_NoRuleDescWarning;
	public static String scuser_DescNotSupplied;
	public static String scuser_LHSIsIdentErr;
	
	private static final String BUNDLE_NAME = "ac.soton.eventb.ruleBase.theory.core.sc.messages";

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