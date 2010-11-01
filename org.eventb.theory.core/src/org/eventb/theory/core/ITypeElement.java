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
 * Common protocol for an element that has a type.
 * 
 * @see IMetavariable
 * 
 * @author maamria
 *
 */
public interface ITypeElement extends IInternalElement{

	/**
	 * Returns whether the type attribute is set.
	 * @return whether the type attribute is se
	 * @throws RodinDBException
	 */
	boolean hasType() throws RodinDBException;
	
	/**
	 * Returns the type associated with this element.
	 * @return the type
	 * @throws RodinDBException
	 */
	String getType() throws RodinDBException;
	
	/**
	 * Sets the type associated with this element to the given type.
	 * @param type the type
	 * @param monitor the progress monitor
	 * @throws RodinDBException
	 */
	void setType(String type, IProgressMonitor monitor) throws RodinDBException;
	
}