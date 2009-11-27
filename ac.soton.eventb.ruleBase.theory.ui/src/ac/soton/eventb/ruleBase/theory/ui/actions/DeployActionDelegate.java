package ac.soton.eventb.ruleBase.theory.ui.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.WorkbenchException;

import ac.soton.eventb.ruleBase.theory.ui.editor.actions.DeployTheoryActionDelegate;

/**
 * @see DeployTheoryActionDelegate
 * @author maamria
 *
 */
public class DeployActionDelegate implements IWorkbenchWindowActionDelegate {

	private final String EVENTB_PERSPECTIVE = "org.eventb.ui.perspective.eventb";
	private IWorkbenchWindow window;
	
	@Override
	public void dispose() {}

	@Override
	public void init(IWorkbenchWindow window) {
		this.window = window;

	}

	@Override
	public void run(IAction action) {
		// switch to Event-B perspective
		try {
			window.getWorkbench().showPerspective(EVENTB_PERSPECTIVE, window);
		} catch (WorkbenchException e) {
			e.printStackTrace();
		}
		DeployWizard wizard = new DeployWizard(window.getShell());
		WizardDialog dialog = new WizardDialog(window.getShell(), wizard);
		dialog.open();

	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {}

}
