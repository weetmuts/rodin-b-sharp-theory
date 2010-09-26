/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.basis;

import static org.eventb.theory.internal.core.util.DeployUtilities.copyMathematicalExtensions;
import static org.eventb.theory.internal.core.util.DeployUtilities.copyProverExtensions;
import static org.eventb.theory.internal.core.util.DeployUtilities.duplicate;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.core.IDeploymentResult;
import org.eventb.theory.core.ISCImportTheory;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.ITheoryDeployer;
import org.eventb.theory.core.IUseTheory;
import org.eventb.theory.core.TheoryCoreFacade;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;

/**
 * @author maamria
 *
 */
public class TheoryDeployer implements ITheoryDeployer{

	protected ISCTheoryRoot theoryRoot;
	protected IRodinFile targetFile;
	protected boolean force; 
	protected IDeploymentResult deploymentResult;

	public TheoryDeployer(ISCTheoryRoot theoryRoot, boolean force){
		assert theoryRoot.exists();
		this.theoryRoot = theoryRoot;
		this.force = force;
	}
	
	@Override
	public void run(IProgressMonitor monitor) throws CoreException {
		checkCancellationRequest(monitor);
		try{
			deploy(monitor);
		}catch(CoreException exception){
			deploymentResult =  new DeploymentResult(false, exception.getMessage());
		}
		if(!deploymentResult.succeeded()){
			if(targetFile != null && targetFile.exists()){
				targetFile.delete(true, monitor);
			}
		}
		
	}

	@Override
	public synchronized boolean deploy(IProgressMonitor monitor) throws CoreException {
		IRodinProject project = theoryRoot.getRodinProject();
		String theoryName = theoryRoot.getComponentName();
		// check dependencies
		ISCImportTheory importedTheories[] = theoryRoot.getImportTheories();
		for(ISCImportTheory impor : importedTheories){
			IDeployedTheoryRoot deployed = impor.getImportedTheory().getDeployedTheoryRoot();
			if(!deployed.exists()){
				deploymentResult = new DeploymentResult(false, 
						"Theory " + theoryName+" depends on non-deployed theory "+ deployed.getComponentName()+".");
				return false;
			}
		}
		
		targetFile = project.getRodinFile(TheoryCoreFacade.getDeployedTheoryFullName(theoryName));
		// if force not requested
		if(targetFile.exists() && !force){
			deploymentResult = new DeploymentResult(false, 
					"Deployed theory " + theoryName+" already exists in the project "+ project.getElementName()+".");
			return false;
		}
		// if force requested
		if(targetFile.exists()){
			targetFile.delete(true, monitor);
		}
		targetFile.create(true, monitor);
		IDeployedTheoryRoot deployedTheoryRoot = (IDeployedTheoryRoot) targetFile.getRoot();
		if(!deployedTheoryRoot.exists()){
			deployedTheoryRoot.create(null, monitor);
		}
		if(!setDeployedTheoryDependencies(theoryRoot, deployedTheoryRoot)){
			return false;
		}
		boolean accurate = copyMathematicalExtensions(deployedTheoryRoot, theoryRoot) &&
							copyProverExtensions(deployedTheoryRoot, theoryRoot);

		deployedTheoryRoot.setAccuracy(accurate, monitor);
		targetFile.save(monitor, true);
		deploymentResult = new DeploymentResult(true, null);
		return true;
	}

	@Override
	public synchronized IDeploymentResult getDeploymentResult() {
		return deploymentResult;
	}
	
	protected void checkCancellationRequest(IProgressMonitor monitor){
		if (monitor.isCanceled()){
			
		}
	}
	
	protected boolean setDeployedTheoryDependencies(ISCTheoryRoot source,
			IDeployedTheoryRoot target)
	throws CoreException{
		for(IUseTheory use : source.getUsedTheories()){
			if(!use.getUsedTheory().exists()){
				deploymentResult = new DeploymentResult(false, 
						"Deployed theory " + use.getUsedTheory().getComponentName()+" does not exist in the project "+ 
						use.getRodinProject().getElementName()+".");
				return false;
			}
			duplicate(use, IUseTheory.ELEMENT_TYPE, target, null);
		}
		for (ISCImportTheory impor : source.getImportTheories()){
			IDeployedTheoryRoot deployedRoot = TheoryCoreFacade.getDeployedTheory(
					impor.getImportedTheory().getComponentName(),
					target.getRodinProject());
			if(!deployedRoot.exists()){
				deploymentResult = new DeploymentResult(false, 
						"Deployed theory " + deployedRoot.getComponentName()+" does not exist in the project "+ 
						deployedRoot.getRodinProject().getElementName()+".");
				return false;
			}
			IUseTheory use = target.getUsedTheory(impor.getElementName());
			use.create(null, null);
			use.setUsedTheory(deployedRoot, null);
		}
		return true;
	}
}
