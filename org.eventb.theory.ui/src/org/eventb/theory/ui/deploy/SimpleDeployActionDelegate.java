package org.eventb.theory.ui.deploy;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eventb.theory.core.ITheoryRoot;
import org.eventb.theory.internal.ui.TheoryUIUtils;
import org.eventb.theory.ui.internal.deploy.SimpleDeployWizard;
import org.eventb.ui.eventbeditor.IEventBEditor;

public class SimpleDeployActionDelegate implements IEditorActionDelegate {

	IEventBEditor<ITheoryRoot> editor;

	public void run(IAction action) {
		
		Shell shell = editor.getEditorSite().getShell();
		ITheoryRoot rodinInput = (ITheoryRoot) editor.getRodinInput();
		if (TheoryUIUtils.createDeployDeployedTheoryDialog(shell, rodinInput)
			&& 
			TheoryUIUtils.createDeployEmptyTheoryDialog(shell, rodinInput)) {
			SimpleDeployWizard wizard = new SimpleDeployWizard(null,
					rodinInput);
			WizardDialog wd = new WizardDialog(null, wizard);
			wd.setTitle(wizard.getWindowTitle());
			wd.open();
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

	@SuppressWarnings("unchecked")
	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		if (targetEditor instanceof IEventBEditor) {
			editor = (IEventBEditor<ITheoryRoot>) targetEditor;
		}
	}

}
