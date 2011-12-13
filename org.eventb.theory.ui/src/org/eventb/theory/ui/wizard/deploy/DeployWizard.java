package org.eventb.theory.ui.wizard.deploy;

import java.util.Set;

import org.eclipse.jface.wizard.Wizard;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.internal.ui.Messages;

public class DeployWizard extends Wizard {

	private DeployWizardPageOne pageOne;
	private DeployWizardPageTwo pageTwo;

	public DeployWizard() {
		setWindowTitle(Messages.wizard_deployTitle);
	}

	@Override
	public void addPages() {
		pageOne = new DeployWizardPageOne();
		addPage(pageOne);
		pageTwo = new DeployWizardPageTwo();
		addPage(pageTwo);
	}

	@Override
	public boolean performFinish() {
		return true;
	}

	public Set<ISCTheoryRoot> selectedTheories() {
		return pageOne.getSelectedTheories();
	}

	public boolean rebuildProject() {
		return pageOne.rebuildProject();
	}

	@Override
	public boolean canFinish() {
		// only finish on second page
		if (getContainer().getCurrentPage() == pageOne)
			return false;
		else
			return true;
	}

}
