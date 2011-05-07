/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.sc.states;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Type;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.state.ISCState;
import org.eventb.core.tool.IStateType;
import org.eventb.theory.core.plugin.TheoryPlugin;

/**
 * Common protocol for a repository state that holds information about the currently processed datatype definition.
 * <p>
 * This state keeps a pointer to the current datatype being processed , the pointer is the datatype identifier.
 * <p> This interface is not intended to be implemented or extended by clients.
 * @author maamria
 *
 */
public interface IDatatypeTable extends ISCState{
	
	public final IStateType<IDatatypeTable> STATE_TYPE = SCCore.getToolStateType(TheoryPlugin.PLUGIN_ID + ".datatypeTable");
	
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
	 * A call to method <code>checkName(String)</code> should be made to ensure unique names.
	 * @param name
	 * @param typeArgs
	 */
	public void addDatatype(String name, String[] typeArgs);
	
	/**
	 * Checks whether the current datatype has any base constructors; 
	 * that is a constructor that does not refer to the type expression of the defined datatype.
	 * @return whether the current datatype has a base constructor
	 */
	public boolean datatypeHasBaseConstructor();
	
	/**
	 * Checks if <code>name</code> is a different identifier to any 
	 * existing entries including: datatype names, constructor names and destructor names.
	 * @param name
	 * @return
	 */
	public String checkName(String name);
	
	/**
	 * Returns whether the given identifier is allowed to occur within a type of a destructor.
	 * <p> An identifier is not allowed to occur in destructor's type is it has not been declared and it is not referenced type.
	 * @param identifier the identifier to check
	 * @return whether the identifier is allowed to occur
	 */
	public boolean isAllowedIdentifier(String  identifier);
	
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
