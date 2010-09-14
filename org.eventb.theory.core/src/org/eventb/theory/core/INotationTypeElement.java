/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core;

import static org.eventb.core.ast.extension.IOperatorProperties.Notation;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.extension.IOperatorProperties;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for elements that can have a notation type attribute.
 * 
 * @see IOperatorProperties.Notation
 * 
 * @author maamria
 *
 */
public interface INotationTypeElement extends IInternalElement{

	boolean hasNotationType() throws RodinDBException;
	
	Notation getNotationType() throws RodinDBException;
	
	void setNotationType(Notation notation, IProgressMonitor monitor) throws RodinDBException;
	
}
