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
 * Common protocol for an element that can have a syntax symbol.
 * 
 * @author maamria
 *
 */
public interface ISyntaxSymbolElement extends IInternalElement {

	/**
	 * Returns whether the attribute is set.
	 * @return whether the attribute is set
	 * @throws RodinDBException
	 */
	boolean hasSyntaxSymbol() throws RodinDBException;
	
	/**
	 * Returns the syntax symbol associated with this element.
	 * @return the syntax symbol
	 * @throws RodinDBException
	 */
	String getSyntaxSymbol() throws RodinDBException;
	
	/**
	 * Sets the syntax symbol of this element to the given symbol.
	 * @param newSymbol the syntax symbol
	 * @param monitor the progress monitor
	 * @throws RodinDBException
	 */
	void setSyntaxSymbol(String newSymbol, IProgressMonitor monitor) throws RodinDBException;
}
