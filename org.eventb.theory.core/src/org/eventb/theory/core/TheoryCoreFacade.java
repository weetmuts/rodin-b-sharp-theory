/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.ast.extension.IOperatorProperties.FormulaType;
import org.eventb.core.ast.extension.IOperatorProperties.Notation;
import org.eventb.theory.core.deploy.basis.TheoryDeployer;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.internal.core.util.CoreUtilities;

/**
 * Accessibility class for some fields and methods for other plug-ins.
 * 
 * @author maamria
 *
 */
public class TheoryCoreFacade {

	// As in "theory unchecked file"
	public static final String THEORY_FILE_EXTENSION = "tuf";
	// As in "theory checked file" 
	public static final String SC_THEORY_FILE_EXTENSION = "tcf";
	
	// As in "deployed theory file" 
	public static final String DEPLOYED_THEORY_FILE_EXTENSION = "dtf";
	// The theory configuration for the SC and POG
	public static final String THEORY_CONFIGURATION = TheoryPlugin.PLUGIN_ID + ".thy";
	
	public static final String POSTFIX = "postfix";
	
	public static final String INFIX = "infix";
	
	public static final String PREFIX = "prefix";
	
	public static final String[] POSSIBLE_NOTATION_TYPES = new String[] {PREFIX, INFIX, POSTFIX};
	
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
	
	public static final ITheoryDeployer getTheoryDeployer(String theoryRootName, String project ,boolean force)
	throws CoreException{
		String fullName = theoryRootName + "."+ SC_THEORY_FILE_EXTENSION;
		ISCTheoryRoot theoryRoot = CoreUtilities.getTheoryRoot(fullName, project);
		if(theoryRoot == null){
			return null;
		}
		return new TheoryDeployer(theoryRoot, force);
	}
}
