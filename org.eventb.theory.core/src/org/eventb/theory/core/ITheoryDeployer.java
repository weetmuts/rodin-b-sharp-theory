/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Common protocol for a theory deployer.
 * 
 * <p> A theory deployer can deploy a SC theory file to the current project.
 * <p> Information about the success of the deployment msut be provided.
 * 
 * @author maamria
 *
 */
public interface ITheoryDeployer extends IWorkspaceRunnable{
	
	/**
	 * Deploys a given SC theory to the current project.
	 * @param monitor the progress monitor
	 * @return whether deployment has been attempted
	 * @throws CoreException
	 */
	public boolean deploy(IProgressMonitor monitor) throws CoreException;
	
	/**
	 * Returns the deployment result of this operation.
	 * @return the deployment result
	 */
	public IDeploymentResult getDeploymentResult();
	
}
