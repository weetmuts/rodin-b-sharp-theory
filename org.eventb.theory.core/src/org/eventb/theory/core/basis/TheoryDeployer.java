/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.basis;

import static org.eventb.theory.core.TheoryCoreFacadeDB.getDeploymentProject;
import static org.eventb.theory.internal.core.util.DeployUtilities.copyMathematicalExtensions;
import static org.eventb.theory.internal.core.util.DeployUtilities.copyProverExtensions;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IPRRoot;
import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.core.IDeploymentResult;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.ITheoryDeployer;
import org.eventb.theory.core.IUseTheory;
import org.eventb.theory.core.TheoryCoreFacadeDB;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;

/**
 * @author maamria
 * 
 */
public class TheoryDeployer implements ITheoryDeployer {

	protected ISCTheoryRoot theoryRoot;
	protected boolean force;
	protected boolean rebuildProjects;

	protected IRodinFile targetFile;
	protected IDeploymentResult deploymentResult;

	public TheoryDeployer(ISCTheoryRoot theoryRoot, boolean force, 
			boolean rebuildProjects) {
		assert theoryRoot.exists();
		this.theoryRoot = theoryRoot;
		this.force = force;
		this.rebuildProjects = rebuildProjects;
	}

	@Override
	public void run(IProgressMonitor monitor) throws CoreException {
		checkCancellationRequest(monitor);
		try {
			deploy(monitor);
		} catch (CoreException exception) {
			deploymentResult = new DeploymentResult(false,
					exception.getMessage());
		}
		if (!deploymentResult.succeeded()) {
			if (targetFile != null && targetFile.exists()) {
				targetFile.delete(true, monitor);
			}
		}

	}

	@Override
	public boolean deploy(IProgressMonitor monitor)
			throws CoreException {
		String theoryName = theoryRoot.getComponentName();
		IRodinProject targetProject = getDeploymentProject(monitor);
		if(targetProject == null){
			deploymentResult = new DeploymentResult(false, "Deployment project does not exist.");
			return false;
		}
		targetFile = targetProject.getRodinFile(TheoryCoreFacadeDB
				.getDeployedTheoryFullName(theoryName));
		IDeployedTheoryRoot deployedTheoryRoot = (IDeployedTheoryRoot) targetFile
			.getRoot();
		// if force not requested
		if (targetFile.exists() && !force) {
			deploymentResult = new DeploymentResult(false, "Deployed theory "
					+ theoryName + " already exists in the project "
					+ targetProject.getElementName() + ".");
			return false;
		}
		// if force requested
		if (targetFile.exists()) {
			targetFile.delete(true, monitor);
		}
		targetFile.create(true, monitor);
		
		if (!deployedTheoryRoot.exists()) {
			deployedTheoryRoot.create(null, monitor);
		}
		if (!setDeployedTheoryDependencies(theoryRoot, deployedTheoryRoot)) {
			return false;
		}
		boolean accurate = copyMathematicalExtensions(deployedTheoryRoot,
				theoryRoot, monitor)
				&& copyProverExtensions(deployedTheoryRoot, theoryRoot, monitor);

		deployedTheoryRoot.setAccuracy(accurate, monitor);
		deployedTheoryRoot.setComment("GENERATED THEORY FILE: !DO NOT CHANGE!", monitor);
		targetFile.save(monitor, true);
		deploymentResult = new DeploymentResult(true, null);
		if(rebuildProjects)
			TheoryCoreFacadeDB.rebuild(monitor);
		else {
			TheoryCoreFacadeDB.rebuild(targetProject, monitor);
		}
		return true;
	}

	@Override
	public IDeploymentResult getDeploymentResult() {
		return deploymentResult;
	}

	protected void checkCancellationRequest(IProgressMonitor monitor) {
		if (monitor.isCanceled()) {

		}
	}

	protected boolean setDeployedTheoryDependencies(ISCTheoryRoot source,
			IDeployedTheoryRoot target) throws CoreException {
		for (IDeployedTheoryRoot root : TheoryCoreFacadeDB.getDeployedTheories(source.getRodinProject())) {
			if(root.getComponentName().equals(target.getComponentName())){
				continue;
			}
			if (!root.exists()) {
				deploymentResult = new DeploymentResult(false,
						"Failed dependencies : deployed theory "
								+ root.getComponentName()
								+ " does not exist in the project "
								+ TheoryCoreFacadeDB.THEORIES_PROJECT+ ".");
				return false;
			}
			IUseTheory use = target.getUsedTheory(root.getComponentName());
			use.create(null, null);
			use.setUsedTheory(root, null);
		}
		return true;
	}

	protected void repairProofFiles(IRodinProject targetProject)
			throws CoreException {
		IPRRoot[] roots = targetProject
				.getRootElementsOfType(IPRRoot.ELEMENT_TYPE);
		for (IPRRoot root : roots) {
			root.setFormulaFactory(null);
		}
	}
}
