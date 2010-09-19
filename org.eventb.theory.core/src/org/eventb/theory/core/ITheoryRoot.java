/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.theory.core;

import org.eventb.core.ICommentedElement;
import org.eventb.core.IConfigurationElement;
import org.eventb.core.IEventBRoot;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * 
 * @author maamria
 *
 */
public interface ITheoryRoot extends 
	IEventBRoot, ICommentedElement, IConfigurationElement {

	IInternalElementType<ITheoryRoot> ELEMENT_TYPE = RodinCore
		.getInternalElementType(TheoryPlugin.PLUGIN_ID + ".theoryRoot");
	
	public IImportTheory getImportTheory(String name);
	
	public IImportTheory[] getImportTheories() throws RodinDBException;
	
	public ITypeParameter getTypeParameter(String name);
	
	public ITypeParameter[] getTypeParameters() throws RodinDBException;
	
	public IDatatypeDefinition getDatatypeDefinition(String name);
	
	public IDatatypeDefinition[] getDatatypeDefinitions() throws RodinDBException;
	
	public INewOperatorDefinition getNewOperatorDefinition(String name);
	
	public INewOperatorDefinition[] getNewOperatorDefinitions() throws RodinDBException;
	
	public IProofRulesBlock getProofRulesBlock(String name);
	
	public IProofRulesBlock[] getProofRulesBlocks() throws RodinDBException;
	
	public ITheorem getTheorem(String name);
	
	public ITheorem[] getTheorems() throws RodinDBException;
	
	/**
	 * <p>Returns the SC theory file corresponding to the given <code>bareName</code>.</p>
	 * <p>This is handle-only method.</p>
	 * @param bareName
	 * @return the rodin file
	 */
	IRodinFile getSCTheoryFile(String bareName);
	/**
	 * <p>Returns the SC theory root corresponding to this element.</p>
	 * <p>This is handle-only method.</p>
	 * @return the SC theory root
	 */
	ISCTheoryRoot getSCTheoryRoot();
	/**
	 * <p>Returns the SC theory root corresponding to the given <code>bareName</code>.</p>
	 * <p>This is handle-only method.</p>
	 * @param bareName
	 * @return the SC theory root
	 */
	ISCTheoryRoot getSCTheoryRoot(String bareName);
	/**
	 * <p>Returns the set corresponding to the given name.</p>
	 * <p>This is handle-only method.</p>
	 * @param name of the set
	 * @return
	 */

}
