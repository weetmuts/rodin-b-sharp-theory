/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.internal.core.maths.extensions;

import org.eclipse.core.runtime.CoreException;
import org.eventb.theory.core.IInternalTheory;
import org.eventb.theory.core.ISCDatatypeDefinition;
import org.eventb.theory.core.ISCNewOperatorDefinition;
import org.eventb.theory.core.ISCTypeParameter;
import org.rodinp.core.IInternalElementType;

/**
 * @author maamria
 *
 */
public class InternalTheoryTransformer extends AbstractTheoryTransformer<IInternalTheory>{

	@Override
	public IInternalElementType<IInternalTheory> getElementType()
			throws CoreException {
		// TODO Auto-generated method stub
		return IInternalTheory.ELEMENT_TYPE;
	}

	@Override
	protected ISCNewOperatorDefinition[] getOperators(IInternalTheory source)
			throws CoreException {
		// TODO Auto-generated method stub
		return source.getSCNewOperatorDefinitions();
	}

	@Override
	protected ISCDatatypeDefinition[] getDatatypes(IInternalTheory source)
			throws CoreException {
		// TODO Auto-generated method stub
		return source.getSCDatatypeDefinitions();
	}


	@Override
	protected ISCTypeParameter[] getTypeParameters(IInternalTheory source)
			throws CoreException {
		// TODO Auto-generated method stub
		return source.getSCTypeParameters();
	}

	

}
