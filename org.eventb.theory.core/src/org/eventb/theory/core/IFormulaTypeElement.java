/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core;

import static org.eventb.core.ast.extension.IOperatorProperties.FormulaType;

import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

/**
 * @author maamria
 *
 */
public interface IFormulaTypeElement extends IInternalElement{

	boolean hasFormulaType() throws RodinDBException;
	
	FormulaType getFormulaType() throws RodinDBException;
	
	void setFormulaType(FormulaType type, IProgressMonitor monitor) throws RodinDBException;
	
}
