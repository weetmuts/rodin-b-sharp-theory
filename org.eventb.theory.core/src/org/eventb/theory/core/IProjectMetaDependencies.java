/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core;

import org.eventb.core.IEventBRoot;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * @author maamria
 *
 */
public interface IProjectMetaDependencies extends IEventBRoot{

	public IInternalElementType<IProjectMetaDependencies> ELEMENT_TYPE = 
		RodinCore.getInternalElementType(TheoryPlugin.PLUGIN_ID + ".metaDependencies");
	
	public IDeployedTheoryEntry getDeployedTheoryEntry(String name);
	
	public IDeployedTheoryEntry[] getDeployedTheoryEntries() throws RodinDBException;
	
	public ISCTheoryEntry getTheoryEntry(String name);
	
	public ISCTheoryEntry[] getTheoryEntries() throws RodinDBException;
	
	public IGeneratedRootEntry getRootEntry(String name);
	
	public IGeneratedRootEntry[] getRootEntries() throws RodinDBException;
	
}
