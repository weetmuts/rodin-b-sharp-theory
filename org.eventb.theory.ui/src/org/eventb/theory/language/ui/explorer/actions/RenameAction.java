package org.eventb.theory.language.ui.explorer.actions;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.WorkbenchException;
import org.eventb.theory.core.DatabaseUtilitiesTheoryPath;
import org.eventb.theory.core.ITheoryPathRoot;
import org.eventb.theory.internal.ui.TheoryUIUtils;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;

public class RenameAction implements IObjectActionDelegate{

	private ISelection selection;
	private IWorkbenchPart part;
	private String defaultName = "";

	public RenameAction() {
		super();
	}

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		part = targetPart;

	}

	public void run(IAction action) {
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection ssel = (IStructuredSelection) selection;
			Object obj = ssel.getFirstElement();
			if(!(obj instanceof IInternalElement))
				return;

			final IInternalElement root = (IInternalElement) obj;
			if (!(root.getParent() instanceof IRodinFile))
				return;
			final IRodinFile file = root.getRodinFile();
			final IRodinProject prj = file.getRodinProject();

			InputDialog dialog = new InputDialog(part.getSite().getShell(),
					"Rename File",
					"Please enter the new name", getDefaultName(root),
					new FileInputValidator(prj));

			dialog.open();

			final String bareName = dialog.getValue();

			if (dialog.getReturnCode() == InputDialog.CANCEL)
				return; // Cancel

			assert bareName != null;
			
			try {
			
			RodinCore.run(new IWorkspaceRunnable() {

				public void run(IProgressMonitor monitor) throws CoreException {
					TheoryUIUtils.closeOpenedEditors(file);
					String newName = null;
					if (root instanceof ITheoryPathRoot){
						newName = DatabaseUtilitiesTheoryPath.getTheoryPathFullName(bareName);
						file.rename(newName, false, monitor);
					}
					
					openEditor(file);
				}

			}, null);
			} catch (CoreException e) {
				MessageDialog.openError(null, "Error", "Could not rename component. Make sure there is no resource with the same name.");
				e.printStackTrace();
			}

		}

	}

	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}
	
	/**
	 * Close the open editor for a particular Rodin File
	 * 
	 * @param file
	 *            A Rodin File
	 * @throws WorkbenchException 
	 */
	static void openEditor(IRodinFile file) throws WorkbenchException {
	}
	
	private String getDefaultName(IInternalElement root) {
		if (root instanceof ITheoryPathRoot) {
			return root.getElementName();
		} else {
			return defaultName;
		}
	}

}
