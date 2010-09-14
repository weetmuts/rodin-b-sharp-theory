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
 * Common protocol for a reasoning type element.
 * 
 * @see IReasoningTypeElement.ReasoningType
 * 
 * @author maamria
 *
 */
public interface IReasoningTypeElement extends IInternalElement{

	public static enum ReasoningType {BACKWARD, FORWARD, BACKWARD_AND_FORWARD}
	
	boolean hasReasoningAttribute() throws RodinDBException;
	
	boolean isSuitableForBackwardReasoning() throws RodinDBException;
	
	boolean isSuitableForForwardReasoning() throws RodinDBException;
	
	boolean isSuitableForAllReasoning() throws RodinDBException;
	
	void setReasoningType(ReasoningType type, IProgressMonitor monitor) throws RodinDBException;
	
}
