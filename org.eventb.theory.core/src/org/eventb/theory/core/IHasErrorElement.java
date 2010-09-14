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
 * Common protocol for an internal element that can be said to have an error. 
 * <p>
 * This is useful for the Proof Obligation Generator to know about such elements so that it ignores any
 * ill-defined elements.
 * 
 * @author maamria
 *
 */
public interface IHasErrorElement extends IInternalElement{
	
	boolean hasHasErrorAttribute() throws RodinDBException;
	
	boolean hasError() throws RodinDBException;
	
	void setHasError(boolean hasError, IProgressMonitor monitor) throws RodinDBException;

}
