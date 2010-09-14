package org.eventb.theory.ui.deploy;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;


/**
 * @see DeployTheoryActionDelegate
 * @author maamria
 *
 */
public class DeployActionDelegate implements IWorkbenchWindowActionDelegate {
	private IWorkbenchWindow window;
	
	
	public void dispose() {}

	
	public void init(IWorkbenchWindow window) {
		this.window = window;

	}

	
	public void run(IAction action) {
		
		DeployWizard wizard = new DeployWizard(window.getShell());
		WizardDialog dialog = new WizardDialog(window.getShell(), wizard);
		dialog.open();

	}

	
	public void selectionChanged(IAction action, ISelection selection) {}

}
