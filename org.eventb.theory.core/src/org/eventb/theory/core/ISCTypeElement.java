/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Type;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

/**
 * @author maamria
 *
 */
public interface ISCTypeElement extends IInternalElement{

	boolean hasSCType() throws RodinDBException;
	
	Type getSCType(FormulaFactory ff) throws RodinDBException;
	
	void setSCType(Type type, IProgressMonitor monitor) throws RodinDBException;
	
}
