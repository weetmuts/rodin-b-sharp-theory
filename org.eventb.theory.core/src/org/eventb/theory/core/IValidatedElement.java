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

	boolean hasValidatedAttribute() throws RodinDBException;
	
	boolean isValidated() throws RodinDBException;
	
	void setValidated(boolean isValidated, IProgressMonitor monitor) throws RodinDBException;
	
}
