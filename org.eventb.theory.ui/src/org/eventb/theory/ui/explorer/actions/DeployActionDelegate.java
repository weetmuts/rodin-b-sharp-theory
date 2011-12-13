package org.eventb.theory.ui.explorer.actions;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eventb.theory.core.DatabaseUtilities;
import org.eventb.theory.internal.ui.TheoryUIUtils;
import org.eventb.theory.ui.wizard.deploy.DeployWizard;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

public class DeployActionDelegate implements IViewActionDelegate {

	IViewPart view;
	ISelection selection;
	
	public void init(IViewPart view) {
		this.view = view;

	}
	
	@Override
	public void run(IAction action) {
		try {
			RodinCore.run(new IWorkspaceRunnable() {
				
				@Override
				public void run(IProgressMonitor arg0) throws CoreException {
					DatabaseUtilities.ensureDeploymentProjectExists();
				}
			}, null);
		} catch (RodinDBException e) {
			TheoryUIUtils.log(e, "Error while ensuring existence of MathExtensions project");
		}
		BusyIndicator.showWhile(view.getViewSite().getShell().getDisplay(), new Runnable() {
			public void run() {
				DeployWizard wizard = new DeployWizard();
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
