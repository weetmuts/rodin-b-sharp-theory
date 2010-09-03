/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core;

import org.eventb.core.ICommentedElement;
import org.eventb.core.ILabeledElement;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * @author maamria
 *
 */
public interface INewOperatorDefinition extends ICommentedElement, ILabeledElement,
	ISyntaxSymbolElement, IFormulaTypeElement, INotationTypeElement,
	IAssociativeElement, ICommutativeElement{

	IInternalElementType<INewOperatorDefinition> ELEMENT_TYPE = 
		RodinCore.getInternalElementType(TheoryPlugin.PLUGIN_ID + ".newOperatorDefinition");
	
	IOperatorArgument getOperatorArgument(String name);
	
	IOperatorArgument[] getOperatorArguments() throws RodinDBException;
	
	IOperatorWDCondition getOperatorWDCondition(String name);
	
	IOperatorWDCondition[] getIOperatorWDConditions() throws RodinDBException;
	
	IDirectOperatorDefinition getDirectOperatorDefinition(String name);
	
	IDirectOperatorDefinition[] getDirectOperatorDefinitions() throws RodinDBException;
	
}
