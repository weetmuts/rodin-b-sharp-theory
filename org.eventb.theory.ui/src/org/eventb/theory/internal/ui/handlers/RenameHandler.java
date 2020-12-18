/*******************************************************************************
 * Copyright (c) 2006, 2020 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *     Systerel - added default name
 *     Systerel - added renaming of proof files
 *     Systerel - added renaming of occurrences
 *     Systerel - refactored to use commands and handlers
 *     CentraleSupélec - imported in theory plug-in and adapted
 *******************************************************************************/
package org.eventb.theory.internal.ui.handlers;

import static org.eclipse.core.runtime.SubMonitor.convert;
import static org.eclipse.ui.PlatformUI.getWorkbench;
import static org.eclipse.ui.handlers.HandlerUtil.getActiveShell;
import static org.eclipse.ui.handlers.HandlerUtil.getCurrentSelection;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Set;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IEventBProject;
import org.eventb.theory.core.DatabaseUtilities;
import org.eventb.theory.core.ITheoryRoot;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IAttributeType.Handle;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.indexer.IDeclaration;
import org.rodinp.core.indexer.IIndexQuery;
import org.rodinp.core.indexer.IOccurrence;
import org.rodinp.core.location.IAttributeLocation;
import org.rodinp.core.location.IAttributeSubstringLocation;

/**
 * The handler for the 'rename' command on theories.
 */
public class RenameHandler extends AbstractHandler {

	private static class RenamesTheoryDialog extends InputDialog {

		private Button checkbox;
		private boolean selected;

		public RenamesTheoryDialog(Shell parentShell, String initialValue, boolean initialSelected,
				IInputValidator validator) {
			super(parentShell, "Rename theory", "Rename theory", initialValue, validator);
			selected = initialSelected;
		}

		public boolean updateReferences() {
			return selected;
		}

		@Override
		protected Control createDialogArea(Composite parent) {
			final Composite composite = (Composite) super.createDialogArea(parent);
			checkbox = new Button(composite, SWT.CHECK);
			checkbox.setText("Update references");
			checkbox.setSelection(selected);
			return composite;
		}

		@Override
		protected void buttonPressed(int buttonId) {
			if (buttonId == IDialogConstants.OK_ID) {
				selected = checkbox.getSelection();
			} else {
				selected = false;
			}
			super.buttonPressed(buttonId);
		}

	}

	private static class TheoryNameValidator implements IInputValidator {

		private IRodinProject project;

		public TheoryNameValidator(IRodinProject project) {
			this.project = project;
		}

		@Override
		public String isValid(String newText) {
			IRodinFile file = project.getRodinFile(DatabaseUtilities.getTheoryFullName(newText));
			if (file != null && file.exists()) {
				return "File name " + newText + " already exists.";
			}
			return null;
		}
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final ISelection currentSelection = getCurrentSelection(event);
		if (!(currentSelection instanceof IStructuredSelection)) {
			return null;
		}
		final Shell shell = getActiveShell(event);
		if (shell == null) {
			return null;
		}
		final ITheoryRoot root = getSelectedRoot((IStructuredSelection) currentSelection);
		if (root == null) {
			return null;
		}
		final IRodinProject prj = root.getRodinProject();
		final String fileName = root.getElementName();
		final RenamesTheoryDialog dialog = new RenamesTheoryDialog(shell, fileName, true, new TheoryNameValidator(prj));
		dialog.open();
		if (dialog.getReturnCode() == InputDialog.CANCEL)
			return null;

		final String newBareName = dialog.getValue();
		assert newBareName != null;

		final ProgressMonitorDialog progress = new ProgressMonitorDialog(shell);
		runWithRunnableContext(progress, shell, new RenameTask(root, newBareName, dialog.updateReferences()));

		return null;
	}

	public static void runWithRunnableContext(IRunnableContext context, Shell shell, final IRunnableWithProgress op) {
		try {
			context.run(true, true, op);
		} catch (InterruptedException exception) {
			Thread.currentThread().interrupt();
			return;
		} catch (InvocationTargetException exception) {
			final Throwable realException = exception.getTargetException();
			realException.printStackTrace();
			String message = realException.getLocalizedMessage();
			MessageDialog.openError(shell, "Unexpected error. See log for details.", message);
			return;
		}
	}

	/**
	 * Return the selected element if it is a {@link ITheoryRoot}. Else return null.
	 */
	private ITheoryRoot getSelectedRoot(IStructuredSelection selection) {
		if (selection.size() != 1)
			return null;

		final Object obj = selection.getFirstElement();
		if (!(obj instanceof IInternalElement))
			return null;

		final IInternalElement root = (IInternalElement) obj;
		if (!(root instanceof ITheoryRoot))
			return null;

		return (ITheoryRoot) root;
	}

	private static class RenameTask implements IRunnableWithProgress {

		private static final Set<IOccurrence> NO_OCCURRENCES = Collections.emptySet();

		private final ITheoryRoot root;
		private final String newBareName;
		private final boolean updateReferences;

		public RenameTask(ITheoryRoot root, String newBareName, boolean updateReferences) {
			this.root = root;
			this.newBareName = newBareName;
			this.updateReferences = updateReferences;
		}

		@Override
		public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
			final String msg = "Renaming " + root.getElementName() + " to " + newBareName;
			final SubMonitor subMonitor = convert(monitor, msg, 2 + 8);
			final Set<IOccurrence> occurrences = getOccurrences(subMonitor.newChild(2));
			if (monitor.isCanceled())
				return;
			final IWorkspaceRunnable op = new RenameOperation(root, newBareName, occurrences);
			try {
				RodinCore.run(op, subMonitor.newChild(8));
			} catch (RodinDBException e) {
				throw new InvocationTargetException(e);
			} finally {
				monitor.done();
			}
		}

		private Set<IOccurrence> getOccurrences(SubMonitor monitor) throws InterruptedException {
			monitor.subTask("Indexing components...");
			monitor.worked(1);
			if (!updateReferences) {
				return NO_OCCURRENCES;
			}
			final IIndexQuery query = RodinCore.makeIndexQuery();
			query.waitUpToDate(monitor);
			if (monitor.isCanceled())
				return NO_OCCURRENCES;
			final IDeclaration decl = query.getDeclaration(root);
			if (decl == null) {
				return NO_OCCURRENCES;
			}
			return query.getOccurrences(decl);
		}

	}

	private static class RenameOperation implements IWorkspaceRunnable {

		private final Set<IOccurrence> occurrences;
		private final IRodinProject prj;
		private final IRodinFile file;
		private final ITheoryRoot root;
		private final String fileName;
		private final String newBareName;

		public RenameOperation(ITheoryRoot root, String newBareName, Set<IOccurrence> occurrences) {
			this.occurrences = occurrences;
			this.prj = root.getRodinProject();
			this.file = root.getRodinFile();
			this.root = root;
			this.fileName = root.getElementName();
			this.newBareName = newBareName;
		}

		@Override
		public void run(IProgressMonitor monitor) throws RodinDBException {
			monitor.subTask("Performing renaming...");
			// Two default renamings + one per occurrence to rename
			final int nbOccurrences = occurrences.size();
			final SubMonitor subMonitor = convert(monitor, 2 + nbOccurrences);
			renameTheoryFile(subMonitor.newChild(1));
			renamePRFile(subMonitor.newChild(1));
			renameInOccurrences(subMonitor.newChild(nbOccurrences));
		}

		public boolean cancelRenaming(String newName) {
			final String message = "There are already proofs for theory " + newName + " in this project.\\n"
					+ "By continuing this rename operation, these proofs will be lost.\\n"
					+ "Do you want to preserve these proofs and cancel this renaming?";
			class Question implements Runnable {
				private boolean response;

				@Override
				public void run() {
					response = MessageDialog.openQuestion(getWorkbench().getModalDialogShellProvider().getShell(), null,
							message);
				}

				public boolean getResponse() {
					return response;
				}
			}
			final Question question = new Question();
			PlatformUI.getWorkbench().getDisplay().syncExec(question);
			return question.getResponse();
		}

		private void renameTheoryFile(SubMonitor monitor) throws RodinDBException {
			final String newName = DatabaseUtilities.getTheoryFullName(newBareName);
			file.rename(newName, false, monitor);
		}

		private void renamePRFile(IProgressMonitor monitor) throws RodinDBException {
			final IEventBProject evbProject = prj.getAdapter(IEventBProject.class);
			final IRodinFile proofFile = evbProject.getPRFile(root.getElementName());
			if (proofFile.exists()) {
				final String newName = EventBPlugin.getPRFileName(newBareName);
				final IRodinFile pRFile = evbProject.getPRFile(newBareName);
				if (pRFile.exists()) {
					if (cancelRenaming(newBareName)) {
						return;
					}
					proofFile.rename(newName, true, monitor);
				} else {
					proofFile.rename(newName, false, monitor);
				}
			}
		}

		/**
		 * Replace the occurrence of old file name specified by location by the new name
		 *
		 * @throws RodinDBException
		 */
		private void replaceStringOccurrence(String oldName, String newName, IAttributeLocation location,
				IAttributeType.String type, IProgressMonitor monitor) throws RodinDBException {
			final IInternalElement element = location.getElement();
			final String attribute = element.getAttributeValue(type);

			// new value of attribute
			final String newAttribute;
			// value of occurrence specified by location
			final String occurrence;
			if (location instanceof IAttributeSubstringLocation) {
				// the new value of attribute is result from replacing
				// the occurrence by newName in old value of attribute
				final IAttributeSubstringLocation subStringLoc = (IAttributeSubstringLocation) location;
				final int start = subStringLoc.getCharStart();
				final int end = subStringLoc.getCharEnd();
				occurrence = attribute.substring(start, end);
				newAttribute = attribute.substring(0, start) + newName + attribute.substring(start);
			} else {
				occurrence = attribute;
				newAttribute = newName;
			}

			if (occurrence.equals(oldName)) {
				element.setAttributeValue(type, newAttribute, monitor);
				element.getRodinFile().save(monitor, false);
			}
		}

		/**
		 * Replace the occurrence of old handle specified by location by the new handle
		 *
		 * @throws RodinDBException
		 */
		private void replaceHandleOccurrence(IAttributeLocation loc, Handle type, IProgressMonitor monitor)
				throws RodinDBException {
			final IInternalElement element = loc.getElement();
			final IRodinElement attribute = element.getAttributeValue(type);
			if (attribute.equals(file)) {
				element.setAttributeValue(type, root.getSimilarElement(file), monitor);
				element.getRodinFile().save(monitor, false);
			}
		}

		private void renameInOccurrences(SubMonitor monitor) throws RodinDBException {
			for (IOccurrence occ : occurrences) {
				if (occ.getLocation() instanceof IAttributeLocation) {
					final IAttributeLocation loc = (IAttributeLocation) occ.getLocation();
					final IAttributeType type = loc.getAttributeType();
					if (type instanceof IAttributeType.String) {
						replaceStringOccurrence(fileName, newBareName, loc, (IAttributeType.String) type, monitor);
					} else if (type instanceof IAttributeType.Handle) {
						replaceHandleOccurrence(loc, (IAttributeType.Handle) type, monitor);
					}
				}
				monitor.worked(1);
			}
		}
	}

}
