package org.eventb.theory.ui.deploy;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class RepairActionDelegate implements IWorkbenchWindowActionDelegate {

	private IWorkbenchWindow window;
	
	@Override
	public void run(IAction action) {
		RepairWizard wizard = new RepairWizard(window.getShell());
		WizardDialog dialog = new WizardDialog(window.getShell(), wizard);
		dialog.open();
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {}

	@Override
	public void dispose() {}

	@Override
	public void init(IWorkbenchWindow window) {
		this.window = window;
	}

}
