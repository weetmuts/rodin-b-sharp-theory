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
 * Common protocol for an element that has a reference to a given type i.e., type paramter in a theory.
 * 
 * @see ITypeParameter
 * 
 * @author maamria
 *
 */
public interface IGivenTypeElement extends IInternalElement{
	
	boolean hasGivenType() throws RodinDBException;
	
	String getGivenType() throws RodinDBException;
	
	void setGivenType(String type, IProgressMonitor monitor) throws RodinDBException;

}
