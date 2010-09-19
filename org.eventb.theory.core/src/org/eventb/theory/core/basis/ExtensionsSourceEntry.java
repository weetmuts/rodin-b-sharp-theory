/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.basis;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.theory.core.IExtensionsSourceEntry;
import org.eventb.theory.core.IFormulaExtensionsSource;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author maamria
 *
 */
public class ExtensionsSourceEntry extends TheoryElement implements
		IExtensionsSourceEntry {

	/**
	 * @param name
	 * @param parent
	 */
	public ExtensionsSourceEntry(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public IFormulaExtensionsSource getExtensionsSource()
			throws RodinDBException {
		return (IFormulaExtensionsSource) getSource();
	}

	@Override
	public void setExtensionsSource(IFormulaExtensionsSource source,
			IProgressMonitor monitor) throws RodinDBException {
		setSource(source, monitor);

	}

	@Override
	public IInternalElementType<? extends IInternalElement> getElementType() {
		// TODO Auto-generated method stub
		return ELEMENT_TYPE;
	}

}
