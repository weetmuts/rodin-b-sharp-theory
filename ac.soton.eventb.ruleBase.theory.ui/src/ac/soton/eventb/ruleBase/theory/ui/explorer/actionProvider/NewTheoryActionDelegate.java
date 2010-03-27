package ac.soton.eventb.ruleBase.theory.ui.explorer.actionProvider;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eventb.ui.EventBUIPlugin;
import org.rodinp.core.RodinCore;

import ac.soton.eventb.ruleBase.theory.ui.wizard.NewTheoryFileWizard;

public class NewTheoryActionDelegate implements IViewActionDelegate {

	IViewPart view;
	
	public void init(IViewPart view) {
		this.view = view;

	}

	public void run(IAction action) {
		BusyIndicator.showWhile(view.getViewSite().getShell().getDisplay(), new Runnable() {
			public void run() {
				IStructuredSelection sel = (IStructuredSelection) view.getViewSite().getSelectionProvider().getSelection();
				//The wizard uses IRodinProjects not IProjects
				//get the corresponding IRodinProject
				if (sel.getFirstElement() instanceof IProject) {
					IProject project = (IProject)sel.getFirstElement();
					sel = new StructuredSelection(RodinCore.getRodinDB().getRodinProject(project.getName()));
					
				}
				NewTheoryFileWizard wizard = new NewTheoryFileWizard();
				wizard.init(EventBUIPlugin.getDefault().getWorkbench(),
						sel);
				WizardDialog dialog = new WizardDialog(EventBUIPlugin
						.getActiveWorkbenchShell(), wizard);
				dialog.create();
				// SWTUtil.setDialogSize(dialog, 500, 500);
				dialog.open();
			}
		});

	}

	public void selectionChanged(IAction action, ISelection selection) {
		// nothing to do

	}

}
