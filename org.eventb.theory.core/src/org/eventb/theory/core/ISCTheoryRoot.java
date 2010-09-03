/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core;

import org.eventb.core.IAccuracyElement;
import org.eventb.core.IConfigurationElement;
import org.eventb.core.IEventBRoot;
import org.eventb.core.ITraceableElement;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * @author maamria
 *
 */
public interface ISCTheoryRoot extends IEventBRoot, IAccuracyElement, IConfigurationElement, ITraceableElement{

	IInternalElementType<ISCTheoryRoot> ELEMENT_TYPE = RodinCore
		.getInternalElementType(TheoryPlugin.PLUGIN_ID + ".scTheoryRoot");
	
	ISCTypeParameter getSCTypeParameter(String name);
	
	ISCTypeParameter[] getSCTypeParameters() throws RodinDBException;
	
	ISCDatatypeDefinition getSCDatatypeDefinition(String name);
	
	ISCDatatypeDefinition[] getSCDatatypeDefinitions() throws RodinDBException;
	
	
	/**
	 * <p>Returns the global type environment of this SC theory.</p>
	 * @param factory
	 * @return the type environment
	 * @throws RodinDBException
	 */
	ITypeEnvironment getTypeEnvironment(FormulaFactory factory)
			throws RodinDBException;
	
}
