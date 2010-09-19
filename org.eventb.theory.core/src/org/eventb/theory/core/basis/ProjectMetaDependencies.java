/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.basis;

import org.eventb.core.basis.EventBRoot;
import org.eventb.theory.core.IDeployedTheoryEntry;
import org.eventb.theory.core.IGeneratedRootEntry;
import org.eventb.theory.core.IProjectMetaDependencies;
import org.eventb.theory.core.ISCTheoryEntry;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author maamria
 *
 */
public class ProjectMetaDependencies extends EventBRoot implements
		IProjectMetaDependencies {

	/**
	 * @param name
	 * @param parent
	 */
	public ProjectMetaDependencies(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public IDeployedTheoryEntry getDeployedTheoryEntry(String name) {
		// TODO Auto-generated method stub
		return getInternalElement(IDeployedTheoryEntry.ELEMENT_TYPE, name);
	}


	@Override
	public IDeployedTheoryEntry[] getDeployedTheoryEntries()
			throws RodinDBException {
		// TODO Auto-generated method stub
		return getChildrenOfType(IDeployedTheoryEntry.ELEMENT_TYPE);
	}

	@Override
	public ISCTheoryEntry getTheoryEntry(String name) {
		// TODO Auto-generated method stub
		return getInternalElement(ISCTheoryEntry.ELEMENT_TYPE, name);
	}

	@Override
	public ISCTheoryEntry[] getTheoryEntries() throws RodinDBException {
		// TODO Auto-generated method stub
		return getChildrenOfType(ISCTheoryEntry.ELEMENT_TYPE);
	}

	@Override
	public IGeneratedRootEntry getRootEntry(String name) {
		// TODO Auto-generated method stub
		return getInternalElement(IGeneratedRootEntry.ELEMENT_TYPE, name);
	}


	@Override
	public IGeneratedRootEntry[] getRootEntries() throws RodinDBException {
		// TODO Auto-generated method stub
		return getChildrenOfType(IGeneratedRootEntry.ELEMENT_TYPE);
	}

	@Override
	public IInternalElementType<? extends IInternalElement> getElementType() {
		// TODO Auto-generated method stub
		return ELEMENT_TYPE;
	}

	

}
