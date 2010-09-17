/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for proof rules of mathematical extensions source.
 * 
 * <p> A rules source may contain any number of proof rules blocks that may have rewrite or inference rules defined.
 * <p> A rules source may contain any number of polymorphic theorems.
 * 
 * 
 * @see ISCTheoryRoot
 * @see IDeployedTheoryRoot
 * 
 * @author maamria
 *
 */
public interface IExtensionRulesSource extends IInternalElement{

	/**
	 * Returns a handle to the proof block with the given name.
	 * @param name of the proof block
	 * @return the proof block
	 */
	public ISCProofRulesBlock getProofRulesBlock(String name);

	/**
	 * Returns all proof blocks that are the children of this element.
	 * @return all proof blocks
	 * @throws RodinDBException
	 */
	public ISCProofRulesBlock[] getProofRulesBlocks() throws RodinDBException;

	/**
	 * Returns a handle to the theorem with the given name.
	 * @param name of the theorem
	 * @return the theorem
	 */
	public ISCTheorem getTheorem(String name);

	/**
	 * Returns all theorems that are the children of this element.
	 * @return all theorems
	 * @throws RodinDBException
	 */
	public ISCTheorem[] getTheorems() throws RodinDBException;
}
