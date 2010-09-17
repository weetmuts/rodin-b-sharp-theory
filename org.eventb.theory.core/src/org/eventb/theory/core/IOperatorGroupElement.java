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
 * @author maamria
 *
 */
public interface IOperatorGroupElement extends IInternalElement{

	boolean hasOperatorGroup() throws RodinDBException;
	
	String getOperatorGroup() throws RodinDBException;
	
	void setOperatorGroup(String newGroup, IProgressMonitor monitor)throws RodinDBException;
	
}
