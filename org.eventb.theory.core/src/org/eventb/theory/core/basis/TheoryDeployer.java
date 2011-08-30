/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.basis;

import static org.eventb.theory.internal.core.util.DeployUtilities.copyDeployedElements;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.theory.core.DatabaseUtilities;
import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.core.IDeploymentResult;
import org.eventb.theory.core.ISCImportTheory;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.ITheoryDeployer;
import org.eventb.theory.core.IUseTheory;
import org.eventb.theory.internal.core.util.CoreUtilities;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;

/**
 * @author maamria
 * 
 */
public final class TheoryDeployer implements ITheoryDeployer {

	protected ISCTheoryRoot theoryRoot;
	protected boolean rebuildProjects;

	protected IRodinFile targetFile;
	protected IDeploymentResult deploymentResult;

	public TheoryDeployer(ISCTheoryRoot theoryRoot, boolean rebuildProjects) {
		assert theoryRoot.exists();
		this.theoryRoot = theoryRoot;
		this.rebuildProjects = rebuildProjects;
	}

	@Override
	public void run(IProgressMonitor monitor) throws CoreException {
		checkCancellationRequest(monitor);
		try {
			deploy(monitor);
		} catch (CoreException exception) {
			deploymentResult = new DeploymentResult(false, exception.getMessage());
		}
	}

	@Override
	public boolean deploy(IProgressMonitor monitor) throws CoreException {
		String theoryName = theoryRoot.getComponentName();
		IRodinProject targetProject = theoryRoot.getRodinProject();
		// need to check if the target project is null
		{
			if (targetProject == null) {
				deploymentResult = new DeploymentResult(false, "Deployment project does not exist.");
				return false;
			}
		}
		// check for failed or outdated dependencies
		{
			if(!checkDeployedTheoryDependencies(theoryRoot)){
				return false;
			}
		}
		// need to check for conflicts between the theory to be deployed and any
		// deployed theories
		{
			// first get the symbols defined in the theory to be deployed
			Set<String> theorySymbols = CoreUtilities.getSyntacticSymbolsOfHierarchy(theoryRoot);
			// get the symbols in deployed theories in other hierarchies
			Map<String, Set<String>> otherSymbols = CoreUtilities.getDeployedSyntaxSymbolsOfOtherHierarchies(theoryRoot);
			// check that there is not clash
			for (String deployedTheoryName : otherSymbols.keySet()) {
				if (!Collections.disjoint(theorySymbols, otherSymbols.get(deployedTheoryName))) {
					deploymentResult = new DeploymentResult(false, "Syntax symbols defined in statically checked theory '" + theoryName
							+ "' clash with symbols defined in deployed theory '" + deployedTheoryName + "'.");
					return false;
				}
			}

		}
		// create the target file
		targetFile = targetProject.getRodinFile(DatabaseUtilities.getDeployedTheoryFullName(theoryName));
		IDeployedTheoryRoot deployedTheoryRoot = (IDeployedTheoryRoot) targetFile.getRoot();

		// force always requested
		if (targetFile.exists()) {
			targetFile.delete(true, monitor);
		}
		targetFile.create(true, monitor);

		if (!deployedTheoryRoot.exists()) {
			deployedTheoryRoot.create(null, monitor);
		}
		// set dependencies
		{
			setDeployedTheoryDependencies(theoryRoot, deployedTheoryRoot);
		}
		// copy the elements across
		
		boolean accurate = copyDeployedElements(deployedTheoryRoot, theoryRoot, monitor);
//		boolean accurate = copyMathematicalExtensions(deployedTheoryRoot, theoryRoot, monitor) && copyProverExtensions(deployedTheoryRoot, theoryRoot, monitor);
		// miscellaneous information
		deployedTheoryRoot.setAccuracy(accurate, monitor);
		deployedTheoryRoot.setComment("GENERATED THEORY FILE: !DO NOT CHANGE!", monitor);
		targetFile.save(monitor, true);
		deploymentResult = new DeploymentResult(true, null);
		// in case rebuilding is requested by the user
		{
			if (rebuildProjects)
				DatabaseUtilities.rebuild(monitor);
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

	protected void setDeployedTheoryDependencies(ISCTheoryRoot source, IDeployedTheoryRoot target) throws CoreException {
		ISCImportTheory[] imports = source.getImportTheories();
		for (ISCImportTheory importThy : imports) {
			if (importThy.hasImportTheory()) {
				ISCTheoryRoot importedRoot = importThy.getImportTheory();
				IDeployedTheoryRoot deployedCounterpart = importedRoot.getDeployedTheoryRoot();
				if (deployedCounterpart.exists() ) {
					IUseTheory use = target.getUsedTheory(deployedCounterpart.getComponentName());
					use.create(null, null);
					use.setUsedTheory(deployedCounterpart, null);
				}
				
			}
		}
	}
	
	protected boolean checkDeployedTheoryDependencies(ISCTheoryRoot source) throws CoreException {
		ISCImportTheory[] imports = source.getImportTheories();
		for (ISCImportTheory importThy : imports) {
			if (importThy.hasImportTheory()) {
				ISCTheoryRoot importedRoot = importThy.getImportTheory();
				if(!checkDeployedTheoryDependencies(importedRoot)){
					return false;
				}
				IDeployedTheoryRoot deployedCounterpart = importedRoot.getDeployedTheoryRoot();
				if (!deployedCounterpart.exists()) {
					deploymentResult = new DeploymentResult(false, "Failed dependencies : deployed theory '" + deployedCounterpart.getComponentName()
							+ "' does not exist in the project '" + importedRoot.getRodinProject().getElementName() + "'.");
					return false;
				}
				if(deployedCounterpart.hasOutdatedAttribute() && deployedCounterpart.isOutdated()){
					deploymentResult = new DeploymentResult(false, "Failed dependencies : deployed theory '" + deployedCounterpart.getComponentName()
							+ "' is outdated in the project '" + importedRoot.getRodinProject().getElementName() + "'.");
					return false;
				}
			}
		}
		return true;
	}
}
