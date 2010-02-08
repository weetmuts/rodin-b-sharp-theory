/*******************************************************************************
 * Copyright (c) 2006, 2010 ETH Zurich and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - replaced inherited by extended, event variable by parameter
 *     Systerel - separation of file and root element
 *     Systerel - added implicit children for events
 *     Systerel - added theorem attribute of IDerivedPredicateElement
 * 	   Systerel - fixed bug #2884774 : display guards marked as theorems
 * 	   Systerel - fixed bug #2936324 : Extends clauses in pretty print
 ******************************************************************************/
package ac.soton.eventb.ruleBase.theory.ui.editor;

import java.util.StringTokenizer;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ICommentedElement;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

import ac.soton.eventb.ruleBase.theory.core.ISet;
import ac.soton.eventb.ruleBase.theory.core.ITheoryRoot;
import ac.soton.eventb.ruleBase.theory.core.IVariable;

/**
 * Common implementation for conversion from a Rodin file to a string (used for
 * the pretty print page of event-B editors).
 * 
 * @author htson
 */
@SuppressWarnings("restriction")
public abstract class AstConverter {
	
	protected String SPACE = "";
	protected String HEADER = "";
	protected String FOOTER = "";
	protected String BEGIN_MASTER_KEYWORD = "";
	protected String BEGIN_KEYWORD_1 = "";
	protected String END_MASTER_KEYWORD = "";
	protected String END_KEYWORD_1 = "";
	protected String BEGIN_LEVEL_0 = "";
	protected String BEGIN_LEVEL_1 = "";
	protected String BEGIN_LEVEL_2 = "";
	protected String BEGIN_LEVEL_3 = "";
	protected String END_LEVEL_0 = "";
	protected String END_LEVEL_1 = "";
	protected String END_LEVEL_2 = "";
	protected String END_LEVEL_3 = "";
	protected String EMPTY_LINE = "";
	protected String BEGIN_MULTILINE = "";
	protected String END_MULTILINE = "";
	protected String BEGIN_LINE = "";
	protected String END_LINE = "";
	protected String BEGIN_COMPONENT_NAME = "";
	protected String END_COMPONENT_NAME = "";
	protected String BEGIN_COMPONENT_NAME_SEPARATOR = null;
	protected String END_COMPONENT_NAME_SEPARATOR = null;
	protected String BEGIN_COMMENT = "";
	protected String END_COMMENT = "";
	protected String BEGIN_COMMENT_SEPARATOR = "//";
	protected String END_COMMENT_SEPARATOR = null;
	protected String BEGIN_SET_IDENTIFIER = "";
	protected String END_SET_IDENTIFIER = "";
	protected String BEGIN_SET_IDENTIFIER_SEPARATOR = null;
	protected String END_SET_IDENTIFIER_SEPARATOR = null;
	protected String BEGIN_VARIABLE_IDENTIFIER = "";
	protected String END_VARIABLE_IDENTIFIER = "";
	protected String BEGIN_VARIABLE_IDENTIFIER_SEPARATOR = null;
	protected String END_VARIABLE_IDENTIFIER_SEPARATOR = ":";
	protected String BEGIN_VARIABLE_TYPE = "";
	protected String END_VARIABLE_TYPE = "";
	protected String BEGIN_VARIABLE_TYPE_SEPARATOR = null;
	protected String END_VARIABLE_TYPE_SEPARATOR = null;
	protected String BEGIN_RULE_LABEL = "";
	protected String END_RULE_LABEL = "";
	protected String BEGIN_RULE_LABEL_SEPARATOR = null;
	protected String END_RULE_LABEL_SEPARATOR = ":";
	protected String BEGIN_RULE_LHS = "";
	protected String END_RULE_LHS = "";
	protected String BEGIN_RULE_LHS_SEPARATOR = null;
	protected String END_RULE_LHS_SEPARATOR = null;
	protected String BEGIN_CC = "";
	protected String END_CC = "";
	protected String BEGIN_CC_SEPARATOR = null;
	protected String END_CC_SEPARATOR = null;
	protected String BEGIN_AUTO = "";
	protected String END_AUTO = "";
	protected String BEGIN_AUTO_SEPARATOR = null;
	protected String END_AUTO_SEPARATOR = null;
	protected String BEGIN_INTER = "";
	protected String END_INTER = "";
	protected String BEGIN_INTER_SEPARATOR = null;
	protected String END_INTER_SEPARATOR = null;
	protected String BEGIN_DESC = "";
	protected String END_DESC = "";
	protected String BEGIN_DESC_SEPARATOR = null;
	protected String END_DESC_SEPARATOR = null;
	protected String BEGIN_TIP = "";
	protected String END_TIP = "";
	protected String BEGIN_TIP_SEPARATOR = null;
	protected String END_TIP_SEPARATOR = null;
	protected String BEGIN_RHS_LABEL = "";
	protected String END_RHS_LABEL = "";
	protected String BEGIN_RHS_LABEL_SEPARATOR = null;
	protected String END_RHS_LABEL_SEPARATOR = ":";
	protected String BEGIN_RHS_C = "";
	protected String END_RHS_C = "";
	protected String BEGIN_RHS_C_SEPARATOR = null;
	protected String END_RHS_C_SEPARATOR = null;
	protected String BEGIN_RHS = "";
	protected String END_RHS= "";
	protected String BEGIN_RHS_SEPARATOR = null;
	protected String END_RHS_SEPARATOR = null;
	
	
	// The content string of the form text
	private StringBuilder htmlString;
	
	public AstConverter() {
		htmlString = new StringBuilder("");
	}
	
	
	public String getText(IProgressMonitor monitor, IInternalElement root) {
		htmlString.setLength(0);
		htmlString.append(HEADER);
		addDeclaration(root);
		if(root instanceof ITheoryRoot){
			ITheoryRoot thy = (ITheoryRoot) root;
			addSets(thy, monitor);
			addMetaVariables(thy, monitor);
		}
		masterKeyword("END");
		htmlString.append(FOOTER);

		return htmlString.toString();
	}
	
	private void addMetaVariables(ITheoryRoot root, IProgressMonitor monitor){
		IVariable[] vars;
		try {
			vars = root.getChildrenOfType(IVariable.ELEMENT_TYPE);
		} catch (RodinDBException e) {
			EventBEditorUtils.debugAndLogError(e, "Cannot get variables for "
					+ root.getRodinFile().getElementName());
			return;
		}
		if (vars.length != 0) {
			masterKeyword("METAVARIABLES");
			for (IVariable var: vars) {
				beginLevel1();
				try {
					final String handle = var.getHandleIdentifier();
					final String ident = wrapString(var.getIdentifierString());
					final String hyperlink = makeHyperlink(handle, ident);
					appendVarIdent(hyperlink);
					appendVarType(wrapString(var.getTypingString()));
				} catch (RodinDBException e) {
					EventBEditorUtils.debugAndLogError(e,
							"Cannot get details for variable "
									+ var.getElementName());
				}
				addComment(var);
				endLevel1();
			}
		}
	}
	
	private void appendVarIdent(String hyperlink){
		append(hyperlink, BEGIN_VARIABLE_IDENTIFIER, END_VARIABLE_IDENTIFIER, 
				BEGIN_VARIABLE_IDENTIFIER_SEPARATOR, END_VARIABLE_IDENTIFIER_SEPARATOR);
	}
	
	private void appendVarType(String type){
		append(type, BEGIN_VARIABLE_TYPE, END_VARIABLE_TYPE, 
				BEGIN_VARIABLE_TYPE_SEPARATOR, END_VARIABLE_TYPE_SEPARATOR);
	}
	
	private void addSets(ITheoryRoot root, IProgressMonitor monitor){
		ISet[] sets;
		try {
			sets = root.getChildrenOfType(ISet.ELEMENT_TYPE);
		} catch (RodinDBException e) {
			EventBEditorUtils
					.debugAndLogError(e, "Cannot get sets for "
							+ root.getRodinFile().getElementName());
			return;
		}
		if (sets.length != 0) {
			masterKeyword("SETS");
			for (ISet set: sets) {
				beginLevel1();
				try {
					appendSetIdentifier(makeHyperlink(
							set.getHandleIdentifier(), wrapString(set
									.getIdentifierString())));
				} catch (RodinDBException e) {
					EventBEditorUtils.debugAndLogError(e,
							"Cannot get the identifier string for carrier set "
									+ set.getElementName());
					e.printStackTrace();
				}
				addComment(set);
				endLevel1();
			}
		}
	}
	

	private void addDeclaration(IInternalElement root) {
		// Print the Machine/Context name
		beginLevel0();
		masterKeyword("THEORY");
		endLevel0();
		beginLevel1();
		final String handle = root.getHandleIdentifier();
		final String bareName = root.getRodinFile().getBareName();
		appendComponentName(makeHyperlink(handle, wrapString(bareName)));
		if (root instanceof ICommentedElement) {
			addComment((ICommentedElement) root);
		}
		endLevel1();
		return;
	}

	

	private void appendSetIdentifier(String identifier) {
		append(identifier, BEGIN_SET_IDENTIFIER, END_SET_IDENTIFIER,
				BEGIN_SET_IDENTIFIER_SEPARATOR,
				END_SET_IDENTIFIER_SEPARATOR);
	}



	/**
	 * Append the comment attached to this element, if any.
	 * 
	 * @param element the commented element
	 */
	private void addComment(ICommentedElement element) {
		try {
			if (element.hasComment()) {
				String comment = element.getComment();
				if (comment.length() != 0)
					appendComment(wrapString(comment));
			}
		} catch (RodinDBException e) {
			// ignore
			if (UIUtils.DEBUG)
				e.printStackTrace();
		}
	}

	private void beginLevel0() {
		htmlString.append(BEGIN_LEVEL_0);
	}

	private void beginLevel1() {
		htmlString.append(BEGIN_LEVEL_1);
	}

	
	private void beginLevel2() {
		htmlString.append(BEGIN_LEVEL_2);
	}

	private void beginLevel3() {
		htmlString.append(BEGIN_LEVEL_3);
	}

	private void endLevel0() {
		htmlString.append(END_LEVEL_0);
	}

	private void endLevel1() {
		htmlString.append(END_LEVEL_1);
	}

	private void endLevel2() {
		htmlString.append(END_LEVEL_2);
	}

	private void endLevel3() {
		htmlString.append(END_LEVEL_3);
	}

	private void emptyLine() {
		htmlString.append(EMPTY_LINE);
	}
	
	private void masterKeyword(String str) {
		htmlString.append(BEGIN_MASTER_KEYWORD);
		htmlString.append(str);
		htmlString.append(END_MASTER_KEYWORD);
	}
	
	private void keyword(String str, int level) {
		switch (level) {
		case 0:
			htmlString.append(BEGIN_MASTER_KEYWORD);
			break;
		case 1:
			htmlString.append(BEGIN_KEYWORD_1);
			break;
		}
		
		htmlString.append(str);
		
		switch (level) {
		case 0:
			htmlString.append(END_MASTER_KEYWORD);
			break;
		case 1:
			htmlString.append(END_KEYWORD_1);
			break;
		}
	}

	private void appendComponentName(String label) {
		append(label, BEGIN_COMPONENT_NAME, END_COMPONENT_NAME,
				BEGIN_COMPONENT_NAME_SEPARATOR, END_COMPONENT_NAME_SEPARATOR);
	}



	private void appendComment(String comment) {
		append(comment, BEGIN_COMMENT, END_COMMENT, BEGIN_COMMENT_SEPARATOR,
				END_COMMENT_SEPARATOR);
	}



	private void append(String s, String begin, String end,
			String beginSeparator, String endSeparator) {
		StringTokenizer stringTokenizer = new StringTokenizer(s, "\n");
		if (stringTokenizer.countTokens() <= 1) {
			htmlString.append(begin);
			if (beginSeparator != null) {
				htmlString.append(SPACE);
				htmlString.append(beginSeparator);
				htmlString.append(SPACE);
			}
			htmlString.append(s);
			if (endSeparator != null) {
				htmlString.append(SPACE);
				htmlString.append(endSeparator);
				htmlString.append(SPACE);
			}
			htmlString.append(end);
		}
		else {
			// Printing multi-line
			htmlString.append(BEGIN_MULTILINE);
			while (stringTokenizer.hasMoreTokens()) {
				String text = stringTokenizer.nextToken();
				htmlString.append(BEGIN_LINE);
				htmlString.append(begin);
				if (beginSeparator != null) {
					htmlString.append(SPACE);
					htmlString.append(beginSeparator);
					htmlString.append(SPACE);
				}
				htmlString.append(text);
				if (endSeparator != null) {
					htmlString.append(SPACE);
					htmlString.append(endSeparator);
					htmlString.append(SPACE);
				}
				htmlString.append(end);
				htmlString.append(END_LINE);
			}
			htmlString.append(END_MULTILINE);
		}
		
	}
	
	protected abstract String makeHyperlink(String link, String text);

	protected abstract String wrapString(String text);

}
