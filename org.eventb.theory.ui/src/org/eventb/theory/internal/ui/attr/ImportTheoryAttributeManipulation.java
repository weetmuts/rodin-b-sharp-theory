/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.internal.ui.attr;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.internal.ui.eventbeditor.manipulation.AbstractAttributeManipulation;
import org.eventb.theory.core.DB_TCFacade;
import org.eventb.theory.core.IImportTheoryElement;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.TheoryAttributes;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;


public class ImportTheoryAttributeManipulation extends
		AbstractAttributeManipulation {

	@Override
	public void setDefaultValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		// no default
		
	}

	@Override
	public boolean hasValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		// TODO Auto-generated method stub
		return asImportTheoryElement(element).hasImportTheory();
	}

	@Override
	public String getValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		// TODO Auto-generated method stub
		return asImportTheoryElement(element).getImportTheory().getComponentName();
	}

	@Override
	public void setValue(IRodinElement element, String value,
			IProgressMonitor monitor) throws RodinDBException {
		IRodinProject proj = element.getRodinProject();
		ISCTheoryRoot root = DB_TCFacade.getSCTheory(value, proj);
		if(root != null && root.exists())
			asImportTheoryElement(element).setImportTheory(root, monitor);
		
	}

	@Override
	public void removeAttribute(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		asImportTheoryElement(element).removeAttribute(TheoryAttributes.IMPORT_THEORY_ATTRIBUTE, monitor);
		
	}

	@Override
	public String[] getPossibleValues(IRodinElement element,
			IProgressMonitor monitor) {
		// TODO Auto-generated method stub
		return new String[0];
	}

	protected IImportTheoryElement asImportTheoryElement(IRodinElement element){
		return (IImportTheoryElement) element;
	}
	
}
