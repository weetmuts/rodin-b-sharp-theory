package org.eventb.theory.ui.explorer.actions;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eventb.theory.core.DatabaseUtilities;
import org.eventb.theory.internal.ui.TheoryUIUtils;
import org.eventb.theory.ui.wizard.NewTheoryWizard;
import org.eventb.ui.EventBUIPlugin;
import org.rodinp.core.RodinCore;

public class NewTheoryActionDelegate implements IViewActionDelegate {

	IViewPart view;
	
	public void init(IViewPart view) {
		this.view = view;

	}

	public void run(IAction action) {
		try {
			DatabaseUtilities.ensureDeploymentProjectExists();
		} catch (CoreException e) {
			TheoryUIUtils.log(e, "Error while ensuring existence of MathExtensions project");
		}
		BusyIndicator.showWhile(view.getViewSite().getShell().getDisplay(), new Runnable() {
			public void run() {
				IStructuredSelection sel = (IStructuredSelection) view.getViewSite().getSelectionProvider().getSelection();
				//The wizard uses IRodinProjects not IProjects
				//get the corresponding IRodinProject
				if (sel.getFirstElement() instanceof IProject) {
					IProject project = (IProject)sel.getFirstElement();
					sel = new StructuredSelection(RodinCore.getRodinDB().getRodinProject(project.getName()));
					
				}
				NewTheoryWizard wizard = new NewTheoryWizard();
				wizard.init(EventBUIPlugin.getDefault().getWorkbench(),sel);
				WizardDialog dialog = new WizardDialog(EventBUIPlugin.getActiveWorkbenchShell(), wizard);
				dialog.open();
			}
		});

	}

	public void selectionChanged(IAction action, ISelection selection) {
		
	}

}
