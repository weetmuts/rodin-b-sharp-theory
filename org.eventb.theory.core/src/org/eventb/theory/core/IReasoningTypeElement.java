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

	/**
	 * Reasoning type enumeration.
	 * @author maamria
	 *
	 */
	public static enum ReasoningType {BACKWARD, FORWARD, BACKWARD_AND_FORWARD}
	
	/**
	 * Returns whether the attribute is present.
	 * @return whether the attribute is present
	 * @throws RodinDBException
	 */
	boolean hasReasoningAttribute() throws RodinDBException;
	
	/**
	 * Return whether this inference rule is suitable for backward reasoning.
	 * @return whether this inference rule is suitable for backward reasoning
	 * @throws RodinDBException
	 */
	boolean isSuitableForBackwardReasoning() throws RodinDBException;
	
	/**
	 * Return whether this inference rule is suitable for forward reasoning.
	 * @return whether this inference rule is suitable for forward reasoning
	 * @throws RodinDBException
	 */
	boolean isSuitableForForwardReasoning() throws RodinDBException;
	
	/**
	 * Return whether this inference rule is suitable for backward and forward reasoning.
	 * @return whether this inference rule is suitable for backward and forward reasoning
	 * @throws RodinDBException
	 */
	boolean isSuitableForAllReasoning() throws RodinDBException;
	
	/**
	 * Sets the reasoning type of this inference rule to the given value.
	 * @param type the reasoning type
	 * @param monitor the progress monitor
	 * @throws RodinDBException
	 */
	void setReasoningType(ReasoningType type, IProgressMonitor monitor) throws RodinDBException;
	
}
