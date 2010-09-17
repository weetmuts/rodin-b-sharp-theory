/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.internal.core.maths.extensions;

import org.eclipse.core.runtime.CoreException;
import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.core.ISCDatatypeDefinition;
import org.eventb.theory.core.ISCNewOperatorDefinition;
import org.eventb.theory.core.ISCTypeParameter;
import org.rodinp.core.IInternalElementType;

/**
 * @author maamria
 *
 */
public class DeployedTheoryTransformer extends AbstractTheoryTransformer<IDeployedTheoryRoot>{


	@Override
	public IInternalElementType<IDeployedTheoryRoot> getElementType()
			throws CoreException {
		// TODO Auto-generated method stub
		return IDeployedTheoryRoot.ELEMENT_TYPE;
	}


	@Override
	protected ISCNewOperatorDefinition[] getOperators(IDeployedTheoryRoot source)
			throws CoreException {
		// TODO Auto-generated method stub
		return source.getSCNewOperatorDefinitions();
	}

	@Override
	protected ISCDatatypeDefinition[] getDatatypes(IDeployedTheoryRoot source)
			throws CoreException {
		// TODO Auto-generated method stub
		return source.getSCDatatypeDefinitions();
	}

	@Override
	protected ISCTypeParameter[] getTypeParameters(IDeployedTheoryRoot source)
			throws CoreException {
		// TODO Auto-generated method stub
		return source.getSCTypeParameters();
	}

}
