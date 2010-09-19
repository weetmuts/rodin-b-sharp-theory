/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ICommentedElement;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for an import clause of a theory.
 * 
 * @author maamria
 *
 */
public interface IImportTheory extends IInternalElement, ICommentedElement{

	IInternalElementType<IImportTheory> ELEMENT_TYPE = 
		RodinCore.getInternalElementType(TheoryPlugin.PLUGIN_ID + ".importTheory");
	
	/**
	 * Returns whether the target attribute is set.
	 * @return whether target us present
	 * @throws RodinDBException
	 */
	public boolean hasImportedTheory() throws RodinDBException;
	
	/**
	 * Returns the imported theory name.
	 * @return the imported name
	 * @throws RodinDBException
	 */
	public String getImportedTheoryName() throws RodinDBException;
	
	/**
	 * Returns the SC theory that is the target of this import.
	 * @return the SC imported theory
	 * @throws RodinDBException
	 */
	public ISCTheoryRoot getImportedTheory() throws RodinDBException;
	
	/**
	 * Returns the unchecked version of the imported theory.
	 * @return the unchecked theory
	 * @throws RodinDBException
	 */
	public ITheoryRoot getUncheckedImportedTheory() throws RodinDBException;
	
	/**
	 * Sets the imported theory to the given name.
	 * @param name the name of the target
	 * @param monitor the progress monitor
	 * @throws RodinDBException
	 */
	public void setImportedTheory(String name, IProgressMonitor monitor) throws RodinDBException;
	
}
