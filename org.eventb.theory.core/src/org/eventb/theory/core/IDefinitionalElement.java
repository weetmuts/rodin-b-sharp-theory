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
 * Common protocol for a definitional element that can be ignored by the Proof Obligation Generator.
 * <p>This is relevant for rewrite rules that are generated from operator definitions. This ensures that such rules
 * are not concerned with proof obligation generation.</p>
 * @author maamria
 *
 */
public interface IDefinitionalElement extends IInternalElement{
	
	boolean hasDefinitionalAttribute() throws RodinDBException;
	
	boolean isDefinitional() throws RodinDBException;
	
	void setDefinitional(boolean isDefinitional, IProgressMonitor monitor) throws RodinDBException; 

}