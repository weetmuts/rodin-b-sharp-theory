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
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for statically checked theory roots.
 * 
 * <p> This interface is not intended to be implemented by clients.
 * 
 * @see ITheoryRoot
 * 
 * @author maamria
 *
 */
public interface ISCTheoryRoot extends IEventBRoot, IAccuracyElement, IConfigurationElement, 
ITraceableElement, IFormulaExtensionsSource, IExtensionRulesSource{

	IInternalElementType<ISCTheoryRoot> ELEMENT_TYPE = RodinCore
		.getInternalElementType(TheoryPlugin.PLUGIN_ID + ".scTheoryRoot");
	
	public ISCImportTheory getImportTheory(String name);
	
	public ISCImportTheory[] getImportTheories() throws RodinDBException;
	
	/**
	 * <p>Returns the global type environment of this SC theory.</p>
	 * @param factory
	 * @return the type environment
	 * @throws RodinDBException
	 */
	ITypeEnvironment getTypeEnvironment(FormulaFactory factory)
			throws RodinDBException;
	
	/**
	 * <p>Returns the deployed theory file corresponding to the given <code>bareName</code>.</p>
	 * <p>This is handle-only method.</p>
	 * @param bareName
	 * @return the rodin file
	 */
	IRodinFile getDeployedTheoryFile(String bareName);
	/**
	 * <p>Returns the deployed theory root corresponding to this element.</p>
	 * <p>This is handle-only method.</p>
	 * @return the deployed theory root
	 */
	IDeployedTheoryRoot getDeployedTheoryRoot();
	/**
	 * <p>Returns the deployed theory root corresponding to the given <code>bareName</code>.</p>
	 * <p>This is handle-only method.</p>
	 * @param bareName
	 * @return the deployed theory root
	 */
	IDeployedTheoryRoot getDeployedTheoryRoot(String bareName);
	
}
