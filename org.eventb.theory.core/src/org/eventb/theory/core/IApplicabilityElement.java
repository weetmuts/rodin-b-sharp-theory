/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
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
 * 
 * Common protocol for rule applicability elements.
 * 
 * <p> A rule applicability refers to how it should be used by the provers (automatically/interactively).
 * 
 * @author maamria
 *
 */
public interface IApplicabilityElement extends IInternalElement{
	
	/**
	 * Enumeration for applicability types.
	 * <p> Methods {@link DatabaseUtilities}.getString({@link RuleApplicability}),
	 * {@link DatabaseUtilities}.getRuleApplicability({@link String}) and 
	 * {@link DatabaseUtilities}.getRuleApplicability(boolean, boolean) provide a neat API for handling 
	 * applicability objects.
	 * @author maamria
	 *
	 */
	public static enum RuleApplicability{
		AUTOMATIC, INTERACTIVE, AUTOMATIC_AND_INTERACTIVE
	}
	
	/**
	 * Returns whether the applicability attribute is set.
	 * @return whether the applicability attribute is set
	 * @throws RodinDBException
	 */
	public boolean hasApplicabilityAttribute() throws RodinDBException;
	
	/**
	 * Returns the applicability of this element
	 * @return the applicability
	 * @throws RodinDBException
	 */
	public RuleApplicability getApplicability() throws RodinDBException;
	
	/**
	 * Returns whether this element can be used automatically.
	 * @return whether this element can be used automatically
	 * @throws RodinDBException
	 */
	public boolean isAutomatic() throws RodinDBException;
	
	/**
	 * Returns whether this element can be used interactively.
	 * @return whether this element can be used interactively
	 * @throws RodinDBException
	 */
	public boolean isInteractive() throws RodinDBException;
	 
	/**
	 * Sets the applicability of this element to the given value.
	 * @param applicability the applicability
	 * @param monitor the progress monitor
	 * @throws RodinDBException
	 */
	public void setApplicability(RuleApplicability applicability, IProgressMonitor monitor) throws RodinDBException;
	
}
