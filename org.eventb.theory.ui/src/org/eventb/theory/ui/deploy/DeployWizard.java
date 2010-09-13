package org.eventb.theory.ui.deploy;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;


/**
 * @author maamria
 *
 */
public class DeployWizard extends Wizard{
	
	@SuppressWarnings("unused")
	private Shell shell;
	private DeployWizardPageOne wizardPageOne;
	private DeployWizardPageTwo wizardPageTwo;
	
	public DeployWizard(Shell shell){
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
	
	public void init(IWorkbench workbench, IStructuredSelection selection) {}

	@SuppressWarnings("unused")
	@Override
	public boolean performFinish() {
		
		final String projectName = wizardPageOne.getProjectName();
		final String originalTheoryName = wizardPageOne.getTheoryName();
		final String chosenName = 
			(wizardPageOne.isUseDiffName()?
					wizardPageOne.getNewName():
						originalTheoryName);
		return true;
	}
}
