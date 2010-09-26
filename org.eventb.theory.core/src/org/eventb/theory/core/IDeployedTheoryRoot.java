/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core;

import org.eventb.core.IAccuracyElement;
import org.eventb.core.IEventBRoot;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for a deployed theory.
 * 
 * <p> A deployed theory is a copy of all SC theory elements that do not have errors associated with them.
 * <p> Validity information are added to children elements where appropriate.
 * 
 * @author maamria
 * 
 */
public interface IDeployedTheoryRoot extends IEventBRoot, IAccuracyElement ,
IFormulaExtensionsSource<IDeployedTheoryRoot>, IExtensionRulesSource{

	IInternalElementType<IDeployedTheoryRoot> ELEMENT_TYPE = RodinCore
			.getInternalElementType(TheoryPlugin.PLUGIN_ID + ".deployedTheoryRoot");
	
	
	/**
	 * Returns a handle to an used theory element with the given name.
	 * @param name of used theory
	 * @return the import theory
	 */
	IUseTheory getUsedTheory(String name);
	
	/**
	 * Returns the used theory children elements of this element.
	 * @return all used theories
	 * @throws RodinDBException
	 */
	IUseTheory[] getUsedTheories() throws RodinDBException;

}
