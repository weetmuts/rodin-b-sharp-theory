/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.basis;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Type;
import org.eventb.core.basis.SCIdentifierElement;
import org.eventb.theory.core.ISCOperatorArgument;
import org.eventb.theory.core.TheoryAttributes;
import org.eventb.theory.internal.core.util.CoreUtilities;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author maamria
 *
 */
public class SCOperatorArgument extends SCIdentifierElement implements ISCOperatorArgument{

	/**
	 * @param name
	 * @param parent
	 */
	public SCOperatorArgument(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public boolean hasSCType() throws RodinDBException {
		return hasAttribute(TheoryAttributes.TYPE_ATTRIBUTE);
	}

	@Override
	public Type getSCType(FormulaFactory ff) throws RodinDBException {
		String typeStr = getAttributeValue(TheoryAttributes.TYPE_ATTRIBUTE);
		return CoreUtilities.parseType(typeStr, ff);
	}

	@Override
	public void setSCType(Type type, IProgressMonitor monitor)
			throws RodinDBException {
		setAttributeValue(TheoryAttributes.TYPE_ATTRIBUTE, type.toString(), monitor);
		
	}

	@Override
	public IInternalElementType<? extends IInternalElement> getElementType() {
		return ELEMENT_TYPE;
	}

}
