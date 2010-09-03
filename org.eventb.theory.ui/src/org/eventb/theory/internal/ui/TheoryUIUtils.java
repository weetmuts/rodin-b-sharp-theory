/**
 * 
 */
package org.eventb.theory.internal.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eventb.theory.core.IDatatypeDefinition;
import org.eventb.theory.core.ITheoryRoot;
import org.eventb.theory.core.ITypeArgument;
import org.eventb.theory.core.ITypeParameter;
import org.eventb.theory.ui.editor.TheoryEditor;
import org.eventb.theory.ui.plugin.TheoryUIPlugIn;
import org.rodinp.core.IOpenable;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

/**
 * @author maamria
 * 
 */
public class TheoryUIUtils {

	/**
	 * @param node
	 */
	public static IOpenable getOpenable(Object node) {
		if (node instanceof IRodinElement)
			return ((IRodinElement) node).getOpenable();

		return null;
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
	 * <p>Returns the full name of the given theory bare name.</p>
	 * @param bareName
	 * @return full name (with extension)
	 */
	public static String getTheoryFileName(String bareName) {
		return bareName + TheoryUIPlugIn.THEORY_FILE_EXT;
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
	 * Returns the type parameters defined in the theory parent of the given rodin element, 
	 * removing any used parameters.
	 * @param element the element
	 * @return the type parameters
	 */
	
	public static String[] getUnusedTypeParameters(final IRodinElement element) 
		throws RodinDBException{
		if(!(element instanceof ITypeArgument)){
			return new String[0];
		}
		ITheoryRoot root = element.getAncestor(ITheoryRoot.ELEMENT_TYPE);
		if(root == null){
			return new String[0];
		}
		ITypeParameter[] tps = root.getTypeParameters();
		String all[] = null;
		try {
			all = new Extractor<ITypeParameter, String>(){

				@Override
				public String get(ITypeParameter s) throws CoreException {
					if(!s.hasIdentifierString()) return null;
					return s.getIdentifierString();
				}
				
			}.extract(tps, new String[tps.length]);
		} catch (CoreException e1) {
			e1.printStackTrace();
		}
		
		IDatatypeDefinition def = element.getAncestor(IDatatypeDefinition.ELEMENT_TYPE);
		if(def == null){
			return new String[0];
		}
		ITypeArgument tas[] = def.getTypeArguments();
		String[] used = null;
		try {
			used = (new Extractor<ITypeArgument, String>(){

					@Override
					public String get(ITypeArgument s) throws CoreException{
						if(s.getElementName().equals(element.getElementName())
								|| !s.hasGivenType()){
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
	 * @author maamria
	 *
	 * @param <S> type of the origin
	 * @param <T> type of the extracted information
	 */
	public abstract static class Extractor<S,T>{
		
		public T[] extract(S[] ss, T[] ts) throws CoreException{
			for(int i = 0 ; i < ts.length; i++){
				ts[i] = get(ss[i]);
			}
			return ts;
		}
		
		public abstract T get(S s) throws CoreException;
	}
	
}
