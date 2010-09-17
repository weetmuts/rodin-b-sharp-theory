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
IFormulaExtensionsSource, IExtensionRulesSource{

	IInternalElementType<IDeployedTheoryRoot> ELEMENT_TYPE = RodinCore
			.getInternalElementType(TheoryPlugin.PLUGIN_ID + ".deployedTheoryRoot");

}
