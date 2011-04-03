/**
 * 
 */
package org.eventb.theory.internal.ui;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eventb.core.ast.extension.IOperatorProperties.FormulaType;
import org.eventb.theory.core.IDatatypeDefinition;
import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.ITheoryRoot;
import org.eventb.theory.core.ITypeArgument;
import org.eventb.theory.core.ITypeParameter;
import org.eventb.theory.core.DatabaseUtilities;
import org.eventb.theory.ui.editor.TheoryEditor;
import org.eventb.theory.ui.plugin.TheoryUIPlugIn;
import org.osgi.framework.Bundle;
import org.rodinp.core.IOpenable;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * @author maamria
 * 
 */
public class TheoryUIUtils {

	/**
	 * Returns an {@link Image} based on a {@link Bundle} and resource entry
	 * path.
	 * 
	 * @param symbolicName
	 *            the symbolic name of the {@link Bundle}.
	 * @param path
	 *            the path of the resource entry.
	 * @return the {@link Image} stored in the file at the specified path.
	 */
	public static Image getPluginImage(String symbolicName, String path) {
		try {
			URL url = getPluginImageURL(symbolicName, path);
			if (url != null) {
				return getPluginImageFromUrl(url);
			}
		} catch (Throwable e) {
			// Ignore any exceptions
		}
		return null;
	}

	/**
	 * Maps URL to images.
	 */
	private static Map<URL, Image> m_URLImageMap = new HashMap<URL, Image>();

	/**
	 * Returns an {@link Image} based on given {@link URL}.
	 */
	private static Image getPluginImageFromUrl(URL url) {
		try {
			try {
				if (m_URLImageMap.containsKey(url)) {
					return (Image) m_URLImageMap.get(url);
				}
				InputStream stream = url.openStream();
				Image image;
				try {
					image = getImage(stream);
					m_URLImageMap.put(url, image);
				} finally {
					stream.close();
				}
				return image;
			} catch (Throwable e) {
				// Ignore any exceptions
			}
		} catch (Throwable e) {
			// Ignore any exceptions
		}
		return null;
	}

	/**
	 * Returns an {@link Image} encoded by the specified {@link InputStream}.
	 * 
	 * @param stream
	 *            the {@link InputStream} encoding the image data
	 * @return the {@link Image} encoded by the specified input stream
	 */
	protected static Image getImage(InputStream stream) throws IOException {
		try {
			Display display = Display.getCurrent();
			ImageData data = new ImageData(stream);
			if (data.transparentPixel > 0) {
				return new Image(display, data, data.getTransparencyMask());
			}
			return new Image(display, data);
		} finally {
			stream.close();
		}
	}

	/**
	 * Returns an {@link URL} based on a {@link Bundle} and resource entry path.
	 */
	private static URL getPluginImageURL(String symbolicName, String path) {
		// try runtime plugins
		{
			Bundle bundle = Platform.getBundle(symbolicName);
			if (bundle != null) {
				return bundle.getEntry(path);
			}
		}
		// no such resource
		return null;
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
	 * <p>
	 * Links to the Event-B editor configured to work with the specified rodin
	 * file.
	 * </p>
	 * 
	 * @param rodinFile
	 *            the file
	 */
	public static void linkToEventBEditor(Object obj) {
		IRodinFile component;
		if (!(obj instanceof IRodinProject)) {
			component = (IRodinFile) getOpenable(obj);
			if (component == null)
				return;
			try {
				IEditorDescriptor desc = PlatformUI
						.getWorkbench()
						.getEditorRegistry()
						.getDefaultEditor(
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
				TheoryUIPlugIn
						.getDefault()
						.getLog()
						.log(new Status(IStatus.ERROR,
								TheoryUIPlugIn.PLUGIN_ID, errorMsg, e));
			}
		}
	}

	/**
	 * <p>
	 * Returns an array of Rodin projects currently in the workspace.
	 * </p>
	 * 
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
	 * <p>
	 * Facility to log the given exception alongside the given message.
	 * </p>
	 * 
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
		IStatus status = new Status(IStatus.ERROR, TheoryUIPlugIn.PLUGIN_ID,
				IStatus.OK, message, null);
		throw new CoreException(status);
	}

	/**
	 * Returns the type parameters defined in the theory parent of the given
	 * rodin element, removing any used parameters.
	 * 
	 * @param element
	 *            the element
	 * @return the type parameters
	 */

	public static String[] getUnusedTypeParameters(final IRodinElement element)
			throws RodinDBException {
		if (!(element instanceof ITypeArgument)) {
			return new String[0];
		}
		ITheoryRoot root = element.getAncestor(ITheoryRoot.ELEMENT_TYPE);
		if (root == null) {
			return new String[0];
		}
		ITypeParameter[] tps = root.getTypeParameters();
		String all[] = null;
		try {
			all = new Extractor<ITypeParameter, String>() {

				@Override
				public String get(ITypeParameter s) throws CoreException {
					if (!s.hasIdentifierString())
						return null;
					return s.getIdentifierString();
				}

			}.extract(tps, new String[tps.length]);
		} catch (CoreException e1) {
			e1.printStackTrace();
		}

		IDatatypeDefinition def = element
				.getAncestor(IDatatypeDefinition.ELEMENT_TYPE);
		if (def == null) {
			return new String[0];
		}
		ITypeArgument tas[] = def.getTypeArguments();
		String[] used = null;
		try {
			used = (new Extractor<ITypeArgument, String>() {

				@Override
				public String get(ITypeArgument s) throws CoreException {
					if (s.getElementName().equals(element.getElementName())
							|| !s.hasGivenType()) {
						return null;
					}
					return s.getGivenType();
				}

			}).extract(tas, new String[tas.length]);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		List<String> list = new ArrayList<String>();
		list.addAll(Arrays.asList(all));
		list.removeAll(Arrays.asList(used));
		return list.toArray(new String[list.size()]);
	}

	/**
	 * Utility to extract information from objects.
	 * 
	 * @author maamria
	 * 
	 * @param <S>
	 *            type of the origin
	 * @param <T>
	 *            type of the extracted information
	 */
	public abstract static class Extractor<S, T> {

		public T[] extract(S[] ss, T[] ts) throws CoreException {
			for (int i = 0; i < ts.length; i++) {
				ts[i] = get(ss[i]);
			}
			return ts;
		}

		public abstract T get(S s) throws CoreException;
	}

	/**
	 * Get an EXISTING SC theory rather than the temp SC file bct_tmp. Returns
	 * the 1st encountered file.
	 * 
	 * @param theoryName
	 * @param projectName
	 * @return
	 */
	public static IRodinFile getSCTheoryInProject(String theoryName,
			String projectName) {
		IRodinFile file = null;
		try {
			ISCTheoryRoot[] roots = RodinCore.getRodinDB()
					.getRodinProject(projectName)
					.getRootElementsOfType(ISCTheoryRoot.ELEMENT_TYPE);
			for (ISCTheoryRoot root : roots) {
				if (root.getElementName().equals(theoryName)) {
					if (root.exists()) {
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

	public static boolean createDeployEmptyTheoryDialog(Shell shell,
			ITheoryRoot root) {
		ISCTheoryRoot scRoot = root.getSCTheoryRoot();
		if (!scRoot.exists() || DatabaseUtilities.isTheoryEmptyOrNotAccurate(scRoot)) {
			MessageDialog.openError(shell, "Error",
					"Cannot deploy inaccurate or empty theory " + root.getComponentName()
							+ ".");
			return false;
		}
		return true;
	}

	public static boolean createDeployDeployedTheoryDialog(Shell shell,
			ITheoryRoot root) {
		IDeployedTheoryRoot depRoot = root.getDeployedTheoryRoot();
		if (depRoot.exists()) {
			MessageDialog.openError(shell, "Error",
					"Cannot deploy deployed theory " + root.getComponentName()
							+ ".");
			return false;
		}
		return true;
	}

	public static boolean createUndeployUndeployedTheoryDialog(Shell shell,
			ITheoryRoot root) {
		IDeployedTheoryRoot depRoot = root.getDeployedTheoryRoot();
		if (!depRoot.exists()) {
			MessageDialog.openError(shell, "Error",
					"Theory " + root.getComponentName()
							+ " has not been deployed.");
			return false;
		}
		return true;
	}

	public static boolean createConfirmUndeployTheoryDialog(Shell shell,
			ITheoryRoot root) {
		return MessageDialog.openConfirm(
				shell,
				"Confirm",
				"Confirm undeploying deployed theory "
						+ root.getComponentName() + ".");

	}

	/**
	 * Runs the given operation in a progress dialog.
	 * 
	 * @param op
	 *            a runnable operation
	 * @since 1.3
	 */
	public static void runWithProgress(final IWorkspaceRunnable op,
			final ISchedulingRule rule) {
		final Shell shell = PlatformUI.getWorkbench().getDisplay()
				.getActiveShell();
		final ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell) {
			@Override
			protected boolean isResizable() {
				return true;
			}
		};

		final IRunnableWithProgress wrap = new IRunnableWithProgress() {

			@Override
			public void run(IProgressMonitor monitor)
					throws InvocationTargetException, InterruptedException {
				try {
					RodinCore.run(op, rule, monitor);
				} catch (CoreException e) {
					throw new InvocationTargetException(e);
				}
			}
		};
		try {
			dialog.run(true, true, wrap);
		} catch (InterruptedException exception) {
			return;
		} catch (InvocationTargetException exception) {
			final Throwable realException = exception.getTargetException();
			realException.printStackTrace();
			final String message = realException.getMessage();
			MessageDialog.openError(shell, "Unexpected Error", message);
			return;
		}
	}

	/**
	 * TRUE -> expression FALSE -> predicate
	 * 
	 * @param isExpression
	 * @return
	 */
	public static final FormulaType getFormulaType(boolean isExpression) {
		if (isExpression)
			return FormulaType.EXPRESSION;
		else
			return FormulaType.PREDICATE;
	}

	/**
	 * Closes any open editors for the given theory.
	 * 
	 * @param theoryName
	 *            the theory name
	 * @param projectName
	 *            the project
	 */
	public static void closeEditorsFor(String theoryName, String projectName) {
		IWorkbench workbench = TheoryUIPlugIn.getDefault().getWorkbench();
		IWorkbenchPage page = workbench.getActiveWorkbenchWindow()
				.getActivePage();
		IEditorReference[] parts = page.findEditors(
				new FileEditorInput(DatabaseUtilities
						.getTheory(theoryName,
								DatabaseUtilities.getRodinProject(projectName))
						.getRodinFile().getResource()), null,
				IWorkbenchPage.MATCH_INPUT);
		for (IEditorReference ref : parts) {
			IEditorPart part = (IEditorPart) ref.getPart(true);
			part.getSite().getPage().closeEditor(part, false);
		}
	}

	/**
	 * Returns the image for explorer display of the given theory.
	 * <p> Theories that have deployed counterparts have a different (blue rather than green) icon.
	 * @param root the theory root
	 * @return the image
	 */
	public static Image getTheoryImage(ITheoryRoot root) {
		IDeployedTheoryRoot deplRoot = DatabaseUtilities.getDeployedTheory(
				root.getElementName(), root.getRodinProject());
		if (deplRoot.exists()) {
			return TheoryImage.getImage(ITheoryImages.IMG_DTHEORY);
		}
		return TheoryImage.getImage(ITheoryImages.IMG_THEORY);
	}
}
