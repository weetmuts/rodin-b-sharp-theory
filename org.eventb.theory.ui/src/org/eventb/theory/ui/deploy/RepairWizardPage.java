/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.ui.deploy;

import java.util.ArrayList;

import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.List;
import org.eventb.internal.ui.RodinProjectSelectionDialog;
import org.eventb.theory.core.TheoryCoreFacade;
import org.eventb.theory.ui.plugin.TheoryUIPlugIn;
import org.rodinp.core.IRodinProject;
import org.eclipse.swt.custom.ScrolledComposite;

/**
 * @author maamria
 *
 */
@SuppressWarnings("restriction")
public class RepairWizardPage extends WizardPage{
	private Text text;
	private Button btnRepairAllProof;
	private String projectName;
	
	public RepairWizardPage(String pageName) {
		super(pageName);
		setTitle("Repair Proof Files");
		setDescription("Repair proof files in the given project.");
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		setControl(container);
		container.setLayout(new GridLayout(3, false));
		
		Label label = new Label(container, SWT.NONE);
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label.setText("Project:");
		
		text = new Text(container, SWT.BORDER);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		text.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if(text.getText().equals(projectName)){
					return;
				}
				projectName = text.getText();
				dialogChanged();
			}
		});
		
		Button button = new Button(container, SWT.NONE);
		button.setText("Browse...");
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleBrowse();
			}
		});
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		btnRepairAllProof = new Button(container, SWT.CHECK);
		btnRepairAllProof.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		btnRepairAllProof.setText("Repair all proof files.");
		btnRepairAllProof.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(btnRepairAllProof.getSelection()){
					list.setEnabled(false);
				}
				else{
					list.setEnabled(true);
				}
				
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		});
		btnRepairAllProof.setEnabled(false);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		Label label_1 = new Label(container, SWT.NONE);
		label_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		dialogChanged();
		setControl(container);
		new Label(container, SWT.NONE);
		
		List list = new List(container, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
		list.setEnabled(false);
		new Label(container, SWT.NONE);
	}

	/**
	 * Ensures that both text fields are set correctly.
	 */
	void dialogChanged() {
		if(projectName == null || projectName.equals("")){
			updateStatus("Project must be specified");
			list.removeAll();
			list.setEnabled(false);
			btnRepairAllProof.setEnabled(false);
			return;
		}
		IRodinProject project = TheoryCoreFacade.getRodinProject(projectName);
		if(project != null){
			btnRepairAllProof.setEnabled(true);
			if(!btnRepairAllProof.getSelection()){
				list.setEnabled(true);
			}
			ArrayList<String> rootsList =TheoryCoreFacade.getProofFileNames(project);
			list.setItems(rootsList.toArray(new String[rootsList.size()]));
		}
		else {
			list.removeAll();
			list.setEnabled(false);
			btnRepairAllProof.setEnabled(false);
		}
		updateStatus(null);
	}
	
	/**
	 * Uses the RODIN project selection dialog to choose the new value for the
	 * project field.
	 */
	
	void handleBrowse() {
		projectName = text.getText();
		IRodinProject rodinProject;
		if (projectName.equals(""))
			rodinProject = null;
		else
			rodinProject = TheoryUIPlugIn.getRodinDatabase().getRodinProject(
					projectName);

		RodinProjectSelectionDialog dialog = new RodinProjectSelectionDialog(
				getShell(), rodinProject, false, "Project Selection",
				"Select a RODIN project");
		if (dialog.open() == Window.OK) {
			Object[] result = dialog.getResult();
			if (result.length == 1) {
				text.setText(((IRodinProject) result[0])
						.getElementName());
				dialogChanged();
			}
		}
	}
	
	private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
		
	}
}
