/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.basis;

import static org.eventb.theory.core.TheoryAttributes.VALIDATED_ATTRIBUTE;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.basis.SCPredicateElement;
import org.eventb.theory.core.ISCTheorem;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author maamria
 *
 */
public class SCTheorem extends SCPredicateElement implements ISCTheorem{

	/**
	 * @param name
	 * @param parent
	 */
	public SCTheorem(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public IInternalElementType<? extends IInternalElement> getElementType() {
		// TODO Auto-generated method stub
		return ELEMENT_TYPE;
	}
	
	@Override
	public boolean hasValidatedAttribute() throws RodinDBException{
		return hasAttribute(VALIDATED_ATTRIBUTE);
	}
	
	@Override
	public boolean isValidated() throws RodinDBException{
		return getAttributeValue(VALIDATED_ATTRIBUTE);
	}
	
	@Override
	public void setValidated(boolean isValidated, IProgressMonitor monitor) throws RodinDBException{
		setAttributeValue(VALIDATED_ATTRIBUTE, isValidated, monitor);
	}

}
