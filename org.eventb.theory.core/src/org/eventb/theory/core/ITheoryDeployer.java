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
 * @author maamria
 *
 */
public interface ITheoryDeployer extends IWorkspaceRunnable{

	public void analyse() throws CoreException;
	
	public boolean deploy(IProgressMonitor monitor) throws CoreException;
	
	public IDeploymentResult getDeploymentResult();
	
}
