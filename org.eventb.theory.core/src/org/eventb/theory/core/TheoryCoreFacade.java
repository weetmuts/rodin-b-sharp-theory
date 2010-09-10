/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core;

import static org.eventb.core.ast.extension.IOperatorProperties.FormulaType;

import org.eventb.core.ast.extension.IOperatorProperties.Notation;
import org.eventb.theory.core.IReasoningTypeElement.ReasoningType;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.sc.TheoryGraphProblem;
import org.rodinp.core.IRodinProblem;

/**
 * @author maamria
 *
 */
public class TheoryCoreFacade {

	// As in "theory unchecked file"
	public static final String THEORY_FILE_EXTENSION = "tuf";
	// As in "theory checked file" 
	public static final String SC_THEORY_FILE_EXTENSION = "tcf";
	
	public static final String POSTFIX = "postfix";
	
	public static final String INFIX = "infix";
	
	public static final String PREFIX = "prefix";
	
	public static final String[] POSSIBLE_NOTATION_TYPES = new String[] {PREFIX, INFIX, POSTFIX};
	
	public static final String BACKWARD_REASONING_TYPE = "backward";
	
	public static final String FORWARD_REASONING_TYPE = "forward";
	
	public static final String BACKWARD_AND_FORWARD_REASONING_TYPE = "both";
	
	public static final String[] POSSIBLE_REASONING_TYPES = new String[]{BACKWARD_REASONING_TYPE, FORWARD_REASONING_TYPE, BACKWARD_AND_FORWARD_REASONING_TYPE};
	
	public static final IRodinProblem getInformationMessageFor(ReasoningType type){
		switch (type) {
		case BACKWARD:
			return TheoryGraphProblem.InferenceRuleBackward;
		case FORWARD:
			return TheoryGraphProblem.InferenceRuleForward;
		case BACKWARD_AND_FORWARD:
			return TheoryGraphProblem.InferenceRuleBoth;
		}
		return null;
	}
	
	public static final ReasoningType getReasoningTypeFor(String type){
		if(type.equals(BACKWARD_REASONING_TYPE))
			return ReasoningType.BACKWARD;
		else if (type.equals(FORWARD_REASONING_TYPE))
			return ReasoningType.FORWARD;
		else if(type.equals(BACKWARD_AND_FORWARD_REASONING_TYPE))
			return ReasoningType.BACKWARD_AND_FORWARD;
		throw new IllegalArgumentException("unknown reasoning type "+ type);
	}
	
	public static final String getStringReasoningType(ReasoningType type){
		switch (type) {
		case BACKWARD:
			return BACKWARD_REASONING_TYPE;
		case FORWARD:
			return FORWARD_REASONING_TYPE;
		default:
			return BACKWARD_AND_FORWARD_REASONING_TYPE;
		}
	}
	
	/**
	 * Converts a string (eg. "postfix") to the corresponding notation.
	 * @param type in string format
	 * @return the corresponding notation
	 */
	public static Notation convertToType(String type){
		if(type.equals(POSTFIX)){
			return Notation.POSTFIX;
		}
		else if(type.equals(INFIX)){
			return Notation.INFIX;
		}
		else {
			return Notation.PREFIX;
		}
	}
	
	/**
	 * Converts a notation to a describing string e.g, Notation.POSTFIX -> "postfix".
	 * @param n the notation
	 * @return the string of the notaion
	 */
	public static String convertTypeToStr(Notation n){
		switch(n){
			case INFIX: return INFIX;
			case POSTFIX: return POSTFIX;
			case PREFIX: return PREFIX;
			default: return "undefined";
		}
	}
	
	/**
	 * TRUE -> expression
	 * FALSE -> predicate
	 * @param isExpression
	 * @return
	 */
	public static final FormulaType getFormulaType(boolean isExpression){
		if(isExpression) return FormulaType.EXPRESSION;
		else return FormulaType.PREDICATE;
	}
	
	public static final boolean isExpressionOperator(FormulaType type){
		return type.equals(FormulaType.EXPRESSION);
	}
	
	/**
	 * <p>Returns the configuration used by theory files.</p>
	 * @return the configuration
	 */
	public static String getTheoryConfiguration(){
		return TheoryPlugin.THEORY_CONFIGURATION;
	}
	
}
