/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.internal.core.sc.states;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Type;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.state.ISCState;
import org.eventb.core.tool.IStateType;
import org.eventb.theory.core.plugin.TheoryPlugin;

/**
 * Common protocol for a repository state that holds information about the currently processed datatype definition.
 * <br><br>
 * This state keeps a pointer to the current datatype being processed , the pointer is the datatype identifier.
 * @author maamria
 *
 */
public interface IDatatypeTable extends ISCState{
	
	/**
	 * Error codes.
	 * @author maamria
	 *
	 */
	public static enum ERROR_CODE{NAME_IS_A_DATATYPE, NAME_IS_A_CONSTRUCTOR, NAME_IS_A_DESTRUCTOR};
	
	/**
	 * ID used to name the datatype.
	 */
	public final static String DATATYPE_ID = " Datatype";
	/**
	 * ID used by constructors.
	 */
	public final static String CONS_ID = " Constructor";
	
	public final static IStateType<IDatatypeTable> STATE_TYPE = SCCore.getToolStateType(
			TheoryPlugin.PLUGIN_ID + ".datatypeTable");
	
	/**
	 * Augments the decoy factory to include the type expression corresponding to the datatype being processed.
	 * @return the new decoy formula factory
	 */
	public FormulaFactory augmentDecoyFormulaFactory();
	
	/**
	 * Augments the "real" formula factory with the completed datatype definition.
	 * @return the new formula factory
	 */
	public FormulaFactory augmentFormulaFactory();
	
	/**
	 * Resets the pointer to the current datatype because an error has occurred, and there is no point continuing.
	 * @return the original formula factory before the processing of this datatype started
	 */
	public FormulaFactory reset();
	
	/**
	 * Sets the current datatype to be error prone.
	 */
	public void setErrorProne();
	
	/**
	 * Checks whether the current datatype has any errors.
	 * @return whether there is an error
	 */
	public boolean isErrorProne();
	
	/**
	 * A call to method <code>isNameOk(String)</code> should be made to ensure unique names.
	 * @param name
	 * @param typeArgs
	 */
	public void addDatatype(String name, String[] typeArgs);
	
	/**
	 * Checks whether the current datatype has any base constructors; 
	 * that is a constructor that does not refer to the type expression of the defined datatype.
	 * @param typeExpression of the current datatype
	 * @return whether the current datatype has a base constructor
	 */
	public boolean datatypeHasBaseConstructor(Type typeExpression);
	
	/**
	 * Checks if <code>name</code> is a different identifier to any 
	 * existing entries including: datatype names, constructor names and destructor names.
	 * @param name
	 * @return
	 */
	public ERROR_CODE isNameOk(String name);
	
	/**
	 * Adds a constructor to the currently processed datatype.
	 * @param consName the constructor name
	 */
	public void addConstructor(String consName);
	
	/**
	 * Adds a destructor with its type to the currently processed constructor of the currently processed datatype.
	 * @param destName destructor name
	 * @param type destructor type
	 */
	public void addDestructor(String destName, Type type);
	
	
	
}
