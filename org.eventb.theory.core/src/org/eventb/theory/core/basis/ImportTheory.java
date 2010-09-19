/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.basis;

import static org.eventb.core.EventBAttributes.TARGET_ATTRIBUTE;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.theory.core.IImportTheory;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.ITheoryRoot;
import org.eventb.theory.core.TheoryCoreFacade;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

/**
 * @author maamria
 *
 */
public class ImportTheory extends TheoryElement implements IImportTheory {

	/**
	 * @param name
	 * @param parent
	 */
	public ImportTheory(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public boolean hasImportedTheory() throws RodinDBException {
		// TODO Auto-generated method stub
		return hasAttribute(TARGET_ATTRIBUTE);
	}


	@Override
	public String getImportedTheoryName() throws RodinDBException {
		// TODO Auto-generated method stub
		return getAttributeValue(TARGET_ATTRIBUTE);
	}

	@Override
	public ISCTheoryRoot getImportedTheory() throws RodinDBException {
		// TODO Auto-generated method stub
		String theoryName = getImportedTheoryName();
		IRodinFile file = getRodinProject().getRodinFile(theoryName + "."+TheoryCoreFacade.SC_THEORY_FILE_EXTENSION);
		return (ISCTheoryRoot) file.getRoot();
	}

	@Override
	public ITheoryRoot getUncheckedImportedTheory() throws RodinDBException {
		// TODO Auto-generated method stub
		String theoryName = getImportedTheoryName();
		IRodinFile file = getRodinProject().getRodinFile(theoryName + "."+TheoryCoreFacade.THEORY_FILE_EXTENSION);
		return (ITheoryRoot) file.getRoot();
	}

	@Override
	public void setImportedTheory(String name, IProgressMonitor monitor)
			throws RodinDBException {
		// TODO Auto-generated method stub
		setAttributeValue(TARGET_ATTRIBUTE, name, monitor);

	}

	@Override
	public IInternalElementType<? extends IInternalElement> getElementType() {
		// TODO Auto-generated method stub
		return ELEMENT_TYPE;
	}

}
