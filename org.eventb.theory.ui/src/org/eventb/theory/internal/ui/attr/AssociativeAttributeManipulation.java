/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.internal.ui.attr;

import static org.eventb.theory.core.TheoryAttributes.ASSOCIATIVE_ATTRIBUTE;
import static org.eventb.theory.internal.ui.Messages.operator_isAssociative;
import static org.eventb.theory.internal.ui.Messages.operator_isNotAssociative;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.internal.ui.eventbeditor.manipulation.AbstractAttributeManipulation;
import org.eventb.theory.core.IAssociativeElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author maamria
 *
 */
public class AssociativeAttributeManipulation extends AbstractAttributeManipulation{

	@Override
	public void setDefaultValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		asAssociativeElement(element).setAssociative(false, monitor);
	}

	@Override
	public boolean hasValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		return asAssociativeElement(element).hasAssociativeAttribute();
	}

	
	@Override
	public String getValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		return asAssociativeElement(element).isAssociative()
			?operator_isAssociative
					:operator_isNotAssociative;
	}
	
	@Override
	public void setValue(IRodinElement element, String value,
			IProgressMonitor monitor) throws RodinDBException {
		if(value.equals(operator_isAssociative)){
			asAssociativeElement(element).setAssociative(true, monitor);
		}
		else
			asAssociativeElement(element).setAssociative(false, monitor);
	}

	@Override
	public void removeAttribute(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		asAssociativeElement(element).removeAttribute(ASSOCIATIVE_ATTRIBUTE, monitor);
		
	}

	@Override
	public String[] getPossibleValues(IRodinElement element,
			IProgressMonitor monitor) {
		return new String[]{operator_isAssociative, operator_isNotAssociative};
	}

	protected IAssociativeElement asAssociativeElement(IRodinElement element){
		assert element instanceof IAssociativeElement;
		return (IAssociativeElement) element;
	}
		

}
