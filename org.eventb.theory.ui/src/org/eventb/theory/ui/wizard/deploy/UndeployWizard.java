package org.eventb.theory.ui.wizard.deploy;

import java.util.Set;

import org.eclipse.jface.wizard.Wizard;
import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.internal.ui.Messages;

public class UndeployWizard extends Wizard {

	private UndeployWizardPage page;
	private Set<IDeployedTheoryRoot> deployedRoots;
	
	public UndeployWizard(Set<IDeployedTheoryRoot> deployedRoots) {
		setWindowTitle(Messages.wizard_undeployTitle);
		this.deployedRoots = deployedRoots;
	}

	@Override
	public void addPages() {
		page = new UndeployWizardPage(deployedRoots);
		addPage(page);
	}

	@Override
	public boolean performFinish() {
		return false;
	}

}
