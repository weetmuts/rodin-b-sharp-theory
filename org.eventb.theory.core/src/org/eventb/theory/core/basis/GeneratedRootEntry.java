/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.basis;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IEventBRoot;
import org.eventb.theory.core.IExtensionsSourceEntry;
import org.eventb.theory.core.IGeneratedRootEntry;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author maamria
 *
 */
public class GeneratedRootEntry extends TheoryElement implements
		IGeneratedRootEntry {

	/**
	 * @param name
	 * @param parent
	 */
	public GeneratedRootEntry(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public IEventBRoot getGeneratedRoot() throws RodinDBException {
		IRodinElement source = getSource();
		// TODO Auto-generated method stub
		return (IEventBRoot) source;
	}

	@Override
	public void setGeneratedRoot(IEventBRoot root, IProgressMonitor monitor)
			throws RodinDBException {
		setSource(root, monitor);

	}

	@Override
	public IExtensionsSourceEntry getSourceEntry(String name) {
		// TODO Auto-generated method stub
		return getInternalElement(IExtensionsSourceEntry.ELEMENT_TYPE, name);
	}

	@Override
	public IExtensionsSourceEntry[] getSourceEntries() throws RodinDBException {
		// TODO Auto-generated method stub
		return getChildrenOfType(IExtensionsSourceEntry.ELEMENT_TYPE);
	}

	@Override
	public IInternalElementType<? extends IInternalElement> getElementType() {
		// TODO Auto-generated method stub
		return ELEMENT_TYPE;
	}

}
