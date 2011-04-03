package org.eventb.theory.ui.internal.deploy;

import org.eclipse.swt.widgets.Shell;

/**
 * @author maamria
 * 
 */
public class DeployWizard extends AbstractDeployWizard {

	public DeployWizard(Shell shell) {
		super(shell);
	}
	
	@Override
	protected AbstractDeployWizardPageOne getPageOne() {
		// TODO Auto-generated method stub
		return new DeployWizardPageOne();
	}
}
