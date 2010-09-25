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
public interface ISCTheoryRoot extends IEventBRoot, IAccuracyElement, IConfigurationElement, 
ITraceableElement, IFormulaExtensionsSource, IExtensionRulesSource, Comparable<ISCTheoryRoot>{

	IInternalElementType<ISCTheoryRoot> ELEMENT_TYPE = RodinCore
		.getInternalElementType(TheoryPlugin.PLUGIN_ID + ".scTheoryRoot");
	
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
	
	/**
	 * Returns a handle to an import theory element with the given name.
	 * @param name of import theory
	 * @return the import theory
	 */
	ISCImportTheory getImportTheory(String name);
	
	/**
	 * Returns the import theory children elements of this element.
	 * @return all import theories
	 * @throws RodinDBException
	 */
	ISCImportTheory[] getImportTheories() throws RodinDBException;
	
	/**
	 * <p>Returns the global type environment of this SC theory.</p>
	 * @param factory
	 * @return the type environment
	 * @throws RodinDBException
	 */
	ITypeEnvironment getTypeEnvironment(FormulaFactory factory)
			throws RodinDBException;
	
}
