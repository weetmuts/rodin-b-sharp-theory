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
public class DeployWizard extends Wizard {

	private Shell shell;
	private DeployWizardPageOne wizardPageOne;
	private DeployWizardPageTwo wizardPageTwo;

	public DeployWizard(Shell shell) {
		super();
		setNeedsProgressMonitor(true);
		this.shell = shell;
	}

	/**
	 * Adding the page to the wizard.
	 */

	@Override
	public void addPages() {
		setWindowTitle("Deploy Theory");
		wizardPageOne = new DeployWizardPageOne();
		addPage(wizardPageOne);
		wizardPageTwo = new DeployWizardPageTwo();
		addPage(wizardPageTwo);
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}

	@Override
	public boolean performFinish() {

		final String projectName = wizardPageOne.getProjectName();
		final String originalTheoryName = wizardPageOne.getTheoryName();
		final String chosenName = (wizardPageOne.isUseDiffName() ? wizardPageOne
				.getNewName() : originalTheoryName);
		final boolean force = wizardPageTwo.forceDeployment();

		ITheoryDeployer deployer = null;
		try {
			deployer = TheoryCoreFacade.getTheoryDeployer(originalTheoryName,chosenName,
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
							originalTheoryName));
			return false;
		}
		else {
			MessageDialog.openInformation(shell, "Success", 
					Messages.bind(deploy_deploySuccess, originalTheoryName));
		}
		return true;
	}
}
