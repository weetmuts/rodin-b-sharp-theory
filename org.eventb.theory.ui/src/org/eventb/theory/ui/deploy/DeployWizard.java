package org.eventb.theory.ui.deploy;

import org.eclipse.swt.widgets.Shell;

/**
 * @author maamria
 * 
 */
public class DeployWizard extends AbstractDeployWizard {

	private DeployWizardPageOne wizardPageOne;
	private DeployWizardPageTwo wizardPageTwo;

	public DeployWizard(Shell shell) {
		super(shell);
		setNeedsProgressMonitor(true);
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

	@Override
	protected String getProjectName() {
		// TODO Auto-generated method stub
		return wizardPageOne.getProjectName();
	}

	@Override
	protected String getTheoryName() {
		// TODO Auto-generated method stub
		return wizardPageOne.getTheoryName();
	}

	@Override
	protected boolean forceDeployment() {
		// TODO Auto-generated method stub
		return wizardPageTwo.forceDeployment();
	}

	@Override
	protected boolean deployDependencies() {
		// TODO Auto-generated method stub
		return wizardPageTwo.deployDependencies();
	}

	

	
}
