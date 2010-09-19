/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * @author maamria
 *
 */
public interface ISCImportTheory extends IInternalElement{

	IInternalElementType<ISCImportTheory> ELEMENT_TYPE = 
		RodinCore.getInternalElementType(TheoryPlugin.PLUGIN_ID + ".scImportTheory");
	
	public boolean hasImportedTheory() throws RodinDBException;
	
	public ISCTheoryRoot getImportedTheory() throws RodinDBException;
	
	public void setImportedTheory(ISCTheoryRoot theory, IProgressMonitor monitor) throws RodinDBException;
}
