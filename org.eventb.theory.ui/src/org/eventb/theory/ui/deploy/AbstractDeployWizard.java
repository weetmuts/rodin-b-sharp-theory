/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.ui.deploy;

import static org.eventb.theory.internal.ui.Messages.deploy_deployFailure;
import static org.eventb.theory.internal.ui.Messages.deploy_deploySuccess;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eventb.theory.core.IDeploymentResult;
import org.eventb.theory.core.ITheoryDeployer;
import org.eventb.theory.core.TheoryCoreFacade;
import org.eventb.theory.internal.ui.Messages;
import org.eventb.theory.internal.ui.TheoryUIUtils;

/**
 * @author maamria
 *
 */
public abstract class AbstractDeployWizard extends Wizard{

	private Shell shell;
	
	public AbstractDeployWizard(Shell shell) {
		super();
		setNeedsProgressMonitor(true);
		this.shell = shell;
	}
	
	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}
	
	protected abstract String getProjectName();
	
	protected abstract String getTheoryName();
	
	protected abstract boolean forceDeployment();
	
	protected abstract boolean deployDependencies();
	
	
	@Override
	public boolean performFinish() {

		final String projectName = getProjectName();
		final String theoryName = getTheoryName();
		final boolean force = forceDeployment();

		ITheoryDeployer deployer = null;
		try {
			deployer = TheoryCoreFacade.getTheoryDeployer(theoryName,
					projectName, force);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		if (deployer == null) {
			return false;
		}

		TheoryUIUtils.runWithProgress(deployer);
		IDeploymentResult deploymentResult = deployer.getDeploymentResult();
		if(!deploymentResult.succeeded()){
			MessageDialog.openError(shell, "Error", 
					Messages.bind(deploy_deployFailure+"\n"+deploymentResult.getErrorMessage(), 
							theoryName));
			return false;
		}
		else {
			MessageDialog.openInformation(shell, "Success", 
					Messages.bind(deploy_deploySuccess, theoryName));
		}
		return true;
	}
}
