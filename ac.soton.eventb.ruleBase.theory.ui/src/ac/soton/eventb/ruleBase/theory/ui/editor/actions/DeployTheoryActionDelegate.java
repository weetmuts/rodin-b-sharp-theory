package ac.soton.eventb.ruleBase.theory.ui.editor.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eventb.ui.eventbeditor.IEventBEditor;

import ac.soton.eventb.ruleBase.theory.core.ITheoryRoot;

/**
 * An action that gets triggered when the user presses the theory deploy button associated with the theory editor.
 * <p>
 * @author maamria
 *
 */
public class DeployTheoryActionDelegate implements IEditorActionDelegate {

	IEventBEditor<ITheoryRoot> editor;

	@Override
	public void run(IAction action) {
		IWorkbenchPartSite site = editor.getSite();
		IWorkbenchWindow window = site.getWorkbenchWindow();
		String bareName = editor.getRodinInput().getRodinFile().getBareName();
		String pName = editor.getRodinInput().getRodinFile().getRodinProject().getElementName();
		DeployTheoryWizard wizard = new DeployTheoryWizard(bareName, pName, editor.getSite().getShell());
		WizardDialog dialog = new WizardDialog(window.getShell(), wizard);
		dialog.open();
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {}

	@SuppressWarnings("unchecked")
	@Override
	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		if (targetEditor instanceof IEventBEditor) {
			editor = (IEventBEditor<ITheoryRoot>) targetEditor;
		}
	}

}
