/*******************************************************************************
 * Copyright (c) 2012, 2020 University of Southampton and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.language.ui.explorer.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;
import org.eventb.theory.language.ui.wizard.NewTheoryPathWizard;

/**
 * @author renatosilva
 *
 */
public class CreateTheoryPathActionDelegate implements IViewActionDelegate {

	IViewPart view;
	ISelection selection;
	
	public void init(IViewPart view) {
		this.view = view;
	}
	
	@Override
	public void run(IAction action) {
		BusyIndicator.showWhile(view.getViewSite().getShell().getDisplay(), new Runnable() {
			public void run() {
				NewTheoryPathWizard wizard = new NewTheoryPathWizard();
				if (selection instanceof IStructuredSelection) {
					wizard.init(PlatformUI.getWorkbench(), (IStructuredSelection) selection);
				} else {
					wizard.init(PlatformUI.getWorkbench(), StructuredSelection.EMPTY);
				}
				WizardDialog dialog = new WizardDialog(view.getViewSite().getShell(), wizard);
				dialog.setTitle(wizard.getWindowTitle());
				dialog.open();
			}
		});

	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}

}
