/**
 * 
 */
package ac.soton.eventb.ruleBase.theory.ui.util;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.rodinp.core.IOpenable;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

import ac.soton.eventb.prover.prefs.PrefsRepresentative;
import ac.soton.eventb.ruleBase.theory.core.ISCTheoryRoot;
import ac.soton.eventb.ruleBase.theory.core.deploy.DeployManager;
import ac.soton.eventb.ruleBase.theory.ui.editor.TheoryEditor;
import ac.soton.eventb.ruleBase.theory.ui.plugin.TheoryUIPlugIn;

/**
 * @author maamria
 * 
 */
public class TheoryUIUtils {

	/**
	 * <p>
	 * A utility to deploy a SC theory file.
	 * </p>
	 * 
	 * @param theoryName
	 * @param destName
	 * @param projectName
	 * @param shell
	 * @return the status
	 */
	public static boolean deployTheory(final String theoryName,
			final String destName, final String projectName, final Shell shell) {
		final String deployPath = PrefsRepresentative.getTheoriesDirectory();
		// TODO add failure messages
		PrefsRepresentative.checkAndCreateTheoriesDirectory();
		IRunnableWithProgress runnable = new IRunnableWithProgress() {

			
			public void run(IProgressMonitor monitor)
					throws InvocationTargetException, InterruptedException {
				monitor.beginTask("Deploying theory " + theoryName + " ...", 15);
				monitor.subTask("Getting statically checked file ...");
				ISCTheoryRoot targetRoot = (ISCTheoryRoot) TheoryUIUtils
						.getSCTheoryInProject(theoryName, projectName)
						.getRoot();
				monitor.worked(2);
				String destFileName = destName
						+ TheoryUIPlugIn.DEPLOYED_THEORY_FILE_EXT;
				try {
					DeployManager.getInstance().deployTheory(targetRoot,
							destFileName, true, deployPath, monitor);

				} catch (final RodinDBException e) {
					log(e, e.getMessage());
					// in any case delete the temp file if it exists
					cleanUp(projectName, monitor);
					monitor.done();
					throw new InvocationTargetException(e);
				} 
				// in any case delete the temp file if it exists
				cleanUp(projectName, monitor);
				monitor.done();
			}

		};
		ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);
		boolean noProblemEncountered = true;
		try {
			dialog.run(true, false, runnable);
		} catch (InterruptedException exception) {
			noProblemEncountered = false;
		} catch (InvocationTargetException exception) {
			log(exception, exception.getMessage());
			noProblemEncountered = false;
			final Throwable realException = exception.getTargetException();
			final String message = getExceptionMessage(realException);
			MessageDialog.openError(shell,
					Messages.theoryUIUtils_unexpectedError, message);
		} finally {
			if (!noProblemEncountered) {
				// delete the generated theory file if any
				String path = PrefsRepresentative.getTheoriesDirectory()
						+ System.getProperty("file.separator") + destName;
				File fileToDelete = new File(path);
				if (fileToDelete.exists()) {
					fileToDelete.delete();
				}
				// progress monitor of dialog no longer available
				cleanUp(projectName, null);
			} else {
				MessageDialog.openInformation(shell,
						"Deploy Theory",Messages.bind(Messages.theoryUIUtils_deploySuccess,theoryName));
			}
		}
		// FIXME should it be noProblemEncountered?
		return true;
	}

	/**
	 * @param node
	 */
	public static IOpenable getOpenable(Object node) {
		if (node instanceof IRodinElement)
			return ((IRodinElement) node).getOpenable();

		return null;
	}

	/**
	 * <p>Returns an array of Rodin projects currently in the workspace.</p>
	 * @return an array of projects names
	 */
	public static String[] getProjectsNames() {
		IRodinProject[] projs = null;
		ArrayList<String> names = new ArrayList<String>();
		try {
			projs = TheoryUIPlugIn.getRodinDatabase().getRodinProjects();
		} catch (RodinDBException e) {
			e.printStackTrace();
		}
		if (projs != null) {
			for (IRodinProject p : projs) {
				names.add(p.getElementName());
			}
		}
		return names.toArray(new String[names.size()]);
	}

	/**
	 * Returns an array of the names of non-empty SC theories.
	 * <p>
	 * BUG FIX DONE TODO FIXME fixed bug: returning a list of theories some of which can be empty, which 
	 * causes the deploy wizard from the top-menu to behave unexpectedly.
	 * @param project
	 * @return
	 */
	public static String[] getNonEmptySCTheoryNames(String project) {
		IRodinProject proj = TheoryUIPlugIn.getRodinDatabase().getRodinProject(project);
		if (proj == null) {
			return null;
		}
		ISCTheoryRoot[] roots = null;
		try {
			roots = proj.getRootElementsOfType(ISCTheoryRoot.ELEMENT_TYPE);
		} catch (RodinDBException e) {
			e.printStackTrace();
		}
		if (roots == null) {
			return null;
		}
		List<String> thyNames =  new ArrayList<String>();
		for (ISCTheoryRoot root : roots) {
			if(!isTheoryEmpty(root)){
				thyNames.add(root.getElementName());
			}
		}
		if(thyNames.size() == 0)
			return null;
		return thyNames.toArray(new String[thyNames.size()]);
	}
	/**
	 * <p>Returns the full name of the given theory bare name.</p>
	 * @param bareName
	 * @return full name (with extension)
	 */
	public static String getTheoryFileName(String bareName) {
		return bareName + TheoryUIPlugIn.THEORY_FILE_EXT;
	}
	
	/**
	 * Get an EXISTING SC theory rather than the temp SC file bct_tmp.
	 * Returns the 1st encountered file.
	 * @param theoryName
	 * @param projectName
	 * @return
	 */
	public static IRodinFile getSCTheoryInProject(
			String theoryName, String projectName) {
		IRodinFile file = null;
		try {
			ISCTheoryRoot[] roots = RodinCore.getRodinDB().getRodinProject(projectName)
					.getRootElementsOfType(ISCTheoryRoot.ELEMENT_TYPE);
			for (ISCTheoryRoot root : roots) {
				if (root.getElementName().equals(theoryName)) {
					if(root.exists()){
						file = root.getRodinFile();
						break;
					}
				}
			}
		} catch (RodinDBException e) {
			e.printStackTrace();
		}
		return file;
	}
	
	/**
	 * <p>Links to the Event-B editor configured to work with the specified rodin file.</p>
	 * @param rodinFile the file
	 */
	public static void linkToEventBEditor(Object obj) {
		IRodinFile component;
		if (!(obj instanceof IRodinProject)) {
			component = (IRodinFile) getOpenable(obj);
			if (component == null)
				return;
			try {
				IEditorDescriptor desc = PlatformUI.getWorkbench()
						.getEditorRegistry().getDefaultEditor(
								component.getCorrespondingResource().getName());
				IEditorPart editor = TheoryUIPlugIn.getActivePage().openEditor(
						new FileEditorInput(component.getResource()),
						desc.getId());
				if (editor instanceof TheoryEditor) {
					((TheoryEditor) editor)
							.setSelection(new StructuredSelection(obj));
				}
			} catch (PartInitException e) {
				String errorMsg = "Error open Editor";
				MessageDialog.openError(null, null, errorMsg);
				TheoryUIPlugIn.getDefault().getLog().log(
						new Status(IStatus.ERROR, TheoryUIPlugIn.PLUGIN_ID,
								errorMsg, e));
			}
		}
	}

	/**
	 * <p>Facility to log the given exception alongside the given message.</p>
	 * @param exc
	 * @param message
	 */
	public static void log(Throwable exc, String message) {
		if (exc instanceof RodinDBException) {
			final Throwable nestedExc = ((RodinDBException) exc).getException();
			if (nestedExc != null) {
				exc = nestedExc;
			}
		}
		if (message == null) {
			message = "Unknown context"; //$NON-NLS-1$
		}
		IStatus status = new Status(IStatus.ERROR, TheoryUIPlugIn.PLUGIN_ID,
				IStatus.ERROR, message, exc);
		TheoryUIPlugIn.getDefault().getLog().log(status);
	}

	/**
	 * Throw a Core exception.
	 * <p>
	 * 
	 * @param message
	 *            The message for displaying
	 * @throws CoreException
	 *             a Core exception with the status contains the input message
	 */
	public static void throwCoreException(String message) throws CoreException {
		IStatus status = new Status(IStatus.ERROR,
				TheoryUIPlugIn.PLUGIN_ID, IStatus.OK, message, null);
		throw new CoreException(status);
	}

	/**
	 * <p>Utility to clean up and remove the temp file generated when deploying a theroy.</p>
	 * @param projectName
	 * @param deployPath
	 * @param monitor
	 */
	private static void cleanUp(final String projectName, IProgressMonitor monitor){
		try {
			RodinCore.run(new IWorkspaceRunnable() {
				
				public void run(IProgressMonitor monitor)
						throws CoreException {
					monitor.subTask("Cleaning up ...");
					DeployManager.getInstance().cleanUp(projectName);
					monitor.worked(1);
				}

			}, monitor);
		} catch (RodinDBException e1) {
			log(e1, e1.getMessage());
		}
	}

	/**
	 * <p>Returns the message associated with the given exception.</p>
	 * @param e the exception
	 * @return the message
	 */
	private static String getExceptionMessage(Throwable e) {
		final String msg = e.getLocalizedMessage();
		if (msg != null) {
			return msg;
		}
		return e.getClass().getName();
	}
	/**
	 * Returns whether the theory contains no rewrite rules.
	 * @param root
	 * @return
	 */
	public static boolean isTheoryEmpty(ISCTheoryRoot root){
		int l = 0;
		try {
			if(root.exists())
				l=root.getSCRewriteRules().length;
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return  l == 0;
	}
}
