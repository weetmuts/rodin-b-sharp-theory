package org.eventb.theory.ui.deploy;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eventb.theory.core.ITheoryRoot;
import org.eventb.ui.eventbeditor.IEventBEditor;

public class DeployTheoryActionDelegate implements IEditorActionDelegate {

	IEventBEditor<ITheoryRoot> editor;

	
	public void run(IAction action) {
		SimpleDeployWizard wizard = new SimpleDeployWizard(
				null, (ITheoryRoot) editor.getRodinInput());
		WizardDialog wd = new WizardDialog(null,
				wizard);
		wd.setTitle(wizard.getWindowTitle());
		wd.open();
	}

	
	public void selectionChanged(IAction action, ISelection selection) {}

	
	@SuppressWarnings("unchecked")
	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		if (targetEditor instanceof IEventBEditor) {
			editor = (IEventBEditor<ITheoryRoot>) targetEditor;
		}
	}

}
