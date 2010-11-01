/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.ui.internal.deploy;

import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eventb.internal.ui.RodinProjectSelectionDialog;
import org.rodinp.core.IRodinProject;

/**
 * @author maamria
 * 
 */
@SuppressWarnings("restriction")
public abstract class AbstractDeployWizardPageOne extends WizardPage{
	
	protected String theoryName;
	protected boolean rebuildProjects;
	protected Button btnRebuildProjects;
	

	protected AbstractDeployWizardPageOne() {
		super("deployWizard");
		setTitle("Deploy theory ");
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		container.setLayout(new GridLayout(3, false));
		customise(container);
		
		
		
		btnRebuildProjects = new Button(container, SWT.CHECK);
		btnRebuildProjects.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		btnRebuildProjects.setText("Rebuild workspace projects (recommended).");
		btnRebuildProjects.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				rebuildProjects = btnRebuildProjects.getSelection();
				
			}
		});
		
		initialise();
		dialogChanged();
		setControl(container);
		
		
	}


	protected abstract void customise(Composite container) ;

	protected void initialise() {
		btnRebuildProjects.setSelection(true);
		rebuildProjects = true;
	}

	public String getTheoryName() {
		return theoryName;
	}
	
	public boolean rebuildProjects(){
		return rebuildProjects;
	}
	
	/**
	 * Ensures that both text fields are set correctly.
	 */
	protected void dialogChanged() {
		btnRebuildProjects.setEnabled(true);
		updateStatus(null);
	}

	private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}
	
	/**
	 * Uses the RODIN project selection dialog to choose the new value for the
	 * project field.
	 */

	protected void handleBrowse(Text text) {
		RodinProjectSelectionDialog dialog = new RodinProjectSelectionDialog(
				getShell(), null, false, "Project Selection",
				"Select a RODIN project");
		if (dialog.open() == Window.OK) {
			Object[] result = dialog.getResult();
			if (result.length == 1) {
				text.setText(((IRodinProject) result[0]).getElementName());
				dialogChanged();
			}
		}
	}
}