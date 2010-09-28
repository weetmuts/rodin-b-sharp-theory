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
import org.eventb.core.IPRRoot;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.theory.core.ISCTheoryRoot;
import org.rodinp.core.IElementChangedListener;
import org.rodinp.core.IRodinFile;

/**
 * Common protocol for a project manager for theories; both deployed and statically checked.
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
public interface IFormulaExtensionsProjectManager extends IElementChangedListener{

	/**
	 * Returns the extensions corresponding to all deployed theories in the Event-B project.
	 * @return deployed extensions
	 * @throws CoreException
	 */
	public Set<IFormulaExtension> getDeployedExtensions() throws CoreException;
	
	/**
	 * Returns the deployed extensions in the deployed theories whose name is not execluded.
	 * <p> If a deployed theory is execluded, all deployed theories that use it are also execluded.
	 * @param execludedTheory the SC theory
	 * @return deployed extensions
	 * @throws CoreException
	 */
	public Set<IFormulaExtension> getDeployedExtensions(ISCTheoryRoot execludedTheory) throws CoreException;
	
	/**
	 * Returns the dirty extension in the SC theory.
	 * @param concernedTheory the SC theory to use
	 * @return the dirty extensions
	 * @throws CoreException
	 */
	public Set<IFormulaExtension> getDirtyExtensions(ISCTheoryRoot concernedTheory) throws CoreException;
	
	/**
	 * Returns the formula extensions corresponding to the given proof file.
	 * @param proofFile the proof file
	 * @return proof file formula extensions
	 * @throws CoreException
	 */
	public Set<IFormulaExtension> getProofFileExtensions(IPRRoot proofFile) throws CoreException;
	
	/**
	 * Sets the formula extensions of the given proof file. The extensions are those recently supplied to the proof
	 * status file corresponding to the given proof file.
	 * @param proofFile the proof file
	 * @throws CoreException
	 */
	public void setProofFileExtensions(IPRRoot proofFile) throws CoreException;
	
	/**
	 * Returns common files used for the computation of the factory for the
	 * given file root.
	 * 
	 * <p>
	 * Subsequently, the builder (SC, POG and POM) will consider that there is a
	 * dependency from returned files to the given one.
	 * </p>
	 * 
	 * @param root
	 *            an event-b root
	 * @return a set of rodin files
	 */
	public Set<IRodinFile> getCommonFiles(IEventBRoot root) throws CoreException;
	
	public boolean isVoid();
	
}
