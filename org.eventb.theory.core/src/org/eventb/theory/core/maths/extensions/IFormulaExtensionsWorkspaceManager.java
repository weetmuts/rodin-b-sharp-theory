/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.maths.extensions;

import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IEventBRoot;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.ITheoryRoot;
import org.rodinp.core.IElementChangedListener;

/**
 * Common protocol for a workspace manager for theories; both deployed and statically checked.
 * 
 * <p> The project manager keeps a tab on the deployed theories in case any changes occur.
 * 
 * <p> The project manager is required to keep a cache for retrieved extensions, and listen on any changes
 * to update the cache accordingly.
 * 
 * <p> A non-functional requirement is that the manager needs to provide fast service when called upon.
 * 
 * @author maamria
 *
 */
public interface IFormulaExtensionsWorkspaceManager extends IElementChangedListener{

	/**
	 * Returns the extensions corresponding to all deployed theories in the Event-B project.
	 * @return deployed extensions
	 * @throws CoreException
	 */
	public Set<IFormulaExtension> getDeployedExtensions() throws CoreException;
	
	/**
	 * Returns any dirty extensions for the given root.
	 * @param root the Event-B root
	 * @param factory the formula factory
	 * @return all dirty extensions
	 * @throws CoreException
	 */
	public Set<IFormulaExtension> getDirtyExtensions(IEventBRoot root, FormulaFactory factory) throws CoreException;
	
	/**
	 * Returns all the needed extension by the given SC theory.
	 * @param root the SC theory
	 * @return all needed extensions
	 * @throws CoreException
	 */
	public Set<IFormulaExtension> getNeededExtensions(ISCTheoryRoot root) throws CoreException;
	
	/**
	 * Returns all the needed extension by the given theory.
	 * @param root the theory
	 * @return all needed extensions
	 * @throws CoreException
	 */
	public Set<IFormulaExtension> getNeededExtensions(ITheoryRoot root) throws CoreException;
	
}
