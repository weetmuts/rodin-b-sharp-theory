/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.basis;

import static org.eventb.theory.core.TheoryAttributes.HAS_ERROR_ATTRIBUTE;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.theory.core.ISCDirectOperatorDefinition;
import org.eventb.theory.core.ISCNewOperatorDefinition;
import org.eventb.theory.core.ISCOperatorArgument;
import org.eventb.theory.core.ISCOperatorWDCondition;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author maamria
 *
 */
public class SCNewOperatorDefinition extends TheoryElement implements ISCNewOperatorDefinition{

	/**
	 * @param name
	 * @param parent
	 */
	public SCNewOperatorDefinition(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public boolean hasHasErrorAttribute() throws RodinDBException {
		return hasAttribute(HAS_ERROR_ATTRIBUTE);
	}

	@Override
	public boolean hasError() throws RodinDBException {
		return getAttributeValue(HAS_ERROR_ATTRIBUTE);
	}

	@Override
	public void setHasError(boolean hasError, IProgressMonitor monitor) throws RodinDBException {
		setAttributeValue(HAS_ERROR_ATTRIBUTE, hasError, monitor);
	}

	@Override
	public ISCOperatorArgument getOperatorArgument(String name) {
		return getInternalElement(ISCOperatorArgument.ELEMENT_TYPE, name);
	}

	@Override
	public ISCOperatorArgument[] getOperatorArguments() throws RodinDBException {
		return getChildrenOfType(ISCOperatorArgument.ELEMENT_TYPE);
	}

	@Override
	public ISCOperatorWDCondition getOperatorWDCondition(String name) {
		return getInternalElement(ISCOperatorWDCondition.ELEMENT_TYPE, name);
	}


	@Override
	public ISCOperatorWDCondition[] getIOperatorWDConditions()
			throws RodinDBException {
		return getChildrenOfType(ISCOperatorWDCondition.ELEMENT_TYPE);
	}

	@Override
	public ISCDirectOperatorDefinition getDirectOperatorDefinition(String name) {
		return getInternalElement(ISCDirectOperatorDefinition.ELEMENT_TYPE, name);
	}

	@Override
	public ISCDirectOperatorDefinition[] getDirectOperatorDefinitions()
			throws RodinDBException {
		return getChildrenOfType(ISCDirectOperatorDefinition.ELEMENT_TYPE);
	}

	@Override
	public IInternalElementType<? extends IInternalElement> getElementType() {
		return ELEMENT_TYPE;
	}
}
