package ac.soton.eventb.ruleBase.theory.ui.editor.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;

import ac.soton.eventb.ruleBase.theory.core.ITheoryRoot;

/**
 * An action that gets triggered when the user presses the theory deploy button associated with the theory editor.
 * <p>
 * @author maamria
 *
 */
public class DeployTheoryActionDelegate implements IEditorActionDelegate {

	IEventBEditor<ITheoryRoot> editor;

	
	public void run(IAction action) {
		IWorkbenchPartSite site = editor.getSite();
		IRodinFile file = editor.getRodinInput().getRodinFile();
		IRodinProject project = editor.getRodinInput().getRodinFile().getRodinProject();
		TheoryDeployer deployer = new TheoryDeployer(file.getBareName(), 
				project, site.getShell());
		deployer.deploy();
	}

	
	public void selectionChanged(IAction action, ISelection selection) {}

	
	@SuppressWarnings("unchecked")
	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		if (targetEditor instanceof IEventBEditor) {
			editor = (IEventBEditor<ITheoryRoot>) targetEditor;
		}
	}

}
