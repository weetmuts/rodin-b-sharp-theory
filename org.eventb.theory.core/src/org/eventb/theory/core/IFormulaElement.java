/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for a formula element.
 * 
 * @author maamria
 *
 */
public interface IFormulaElement extends IInternalElement{

	/**
	 * Returns whether the formula attribute is set or not.
	 * @return whether the attribute is present
	 * @throws RodinDBException
	 */
	boolean hasFormula() throws RodinDBException;
	
	/**
	 * Returns the formula associated with this element is definitional.
	 * @return the set formula
	 * @throws RodinDBException
	 */
	String getFormula() throws RodinDBException;
	
	/**
	 * Sets this element formula attribute to the given value.
	 * @param formula the new formula
	 * @param monitor the progress monitor
	 * @throws RodinDBException
	 */
	void setFormula(String formula, IProgressMonitor monitor) throws RodinDBException;
	
}
