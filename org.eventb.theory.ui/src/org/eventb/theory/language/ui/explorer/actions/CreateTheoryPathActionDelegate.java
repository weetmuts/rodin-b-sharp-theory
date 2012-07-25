/**
 * 
 */
package org.eventb.theory.language.ui.explorer.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eventb.theory.language.ui.wizard.NewTheoryPathWizard;

/**
 * @author renatosilva
 *
 */
public class CreateTheoryPathActionDelegate implements IViewActionDelegate {

	IViewPart view;
	ISelection selection;
	
	public void init(IViewPart view) {
		this.view = view;
	}
	
	@Override
	public void run(IAction action) {
		BusyIndicator.showWhile(view.getViewSite().getShell().getDisplay(), new Runnable() {
			public void run() {
				NewTheoryPathWizard wizard = new NewTheoryPathWizard();
				WizardDialog dialog = new WizardDialog(view.getViewSite().getShell(), wizard);
				dialog.setTitle(wizard.getWindowTitle());
				dialog.open();
			}
		});

	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}

}
