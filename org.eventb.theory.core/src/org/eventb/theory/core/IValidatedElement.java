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
 * Common protocol for a validated element. A validated element is an internal element which has all its associated proof obligations
 * discharged or reviewed.
 * 
 * @author maamria
 *
 */
public interface IValidatedElement extends IInternalElement{

	/**
	 * Returns whether this element has the validated attribute set.
	 * @return whether this element has the validated attribute set
	 * @throws RodinDBException
	 */
	boolean hasValidatedAttribute() throws RodinDBException;
	
	/**
	 * Returns whether this element has been validated.
	 * @return whether this element has been validated
	 * @throws RodinDBException
	 */
	boolean isValidated() throws RodinDBException;
	
	/**
	 * Sets the validated attribute of this element to the given value.
	 * @param isValidated whether the element is validated
	 * @param monitor the progress monitor
	 * @throws RodinDBException
	 */
	void setValidated(boolean isValidated, IProgressMonitor monitor) throws RodinDBException;
	
}
