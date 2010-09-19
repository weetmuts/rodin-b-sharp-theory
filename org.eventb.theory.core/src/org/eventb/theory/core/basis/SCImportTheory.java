/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.basis;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.internal.core.Util;
import org.eventb.theory.core.ISCImportTheory;
import org.eventb.theory.core.ISCTheoryRoot;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author maamria
 *
 */
@SuppressWarnings("restriction")
public class SCImportTheory extends TheoryElement implements ISCImportTheory {

	/**
	 * @param name
	 * @param parent
	 */
	public SCImportTheory(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public ISCTheoryRoot getImportedTheory() throws RodinDBException {
		IRodinElement target = getImportSCTheoryHandle();
		if (!(target instanceof ISCTheoryRoot)) {
			throw Util.newRodinDBException("error retrieving statically checked theory "+ target.getElementName());
		}
		return (ISCTheoryRoot) target;
	}

	@Override
	public void setImportedTheory(ISCTheoryRoot theory, IProgressMonitor monitor)
			throws RodinDBException {
		setAttributeValue(EventBAttributes.SCTARGET_ATTRIBUTE, theory, monitor);

	}

	private IRodinElement getImportSCTheoryHandle() throws RodinDBException {
		return getAttributeValue(EventBAttributes.SCTARGET_ATTRIBUTE);
	}
	
	@Override
	public IInternalElementType<? extends IInternalElement> getElementType() {
		// TODO Auto-generated method stub
		return ELEMENT_TYPE;
	}

	
	@Override
	public boolean hasImportedTheory() throws RodinDBException {
		// TODO Auto-generated method stub
		return hasAttribute(EventBAttributes.SCTARGET_ATTRIBUTE);
	}

}
