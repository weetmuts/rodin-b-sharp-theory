/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.tactics.ui;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eventb.theory.rbp.rulebase.basis.IDeployedTheorem;

public class TheoremSelectorWizardPage extends WizardPage {
	
	private static final String THEOREM_COL = "Theorem";
	private static final String NAME_COL = "Name";
	
	private Combo projectCombo;
	private Combo theoryCombo;
	private Table table;
	private TableColumn nameColumn;
	private TableColumn theoremColumn;
	
	private TheoremsRetriever retriever;
	private String selectedProject = null;
	private String selectedTheory = null;
	private String selectedTheorem = null;

	public TheoremSelectorWizardPage(TheoremsRetriever retriever) {
		super("selectTheorem");
		setTitle("Select theorem");
		setDescription("Select polymorphic theorem to instantiate");
		this.retriever = retriever;
	}

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		container.setLayout(new GridLayout(2, false));
		
		Label lblProject = new Label(container, SWT.NONE);
		lblProject.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblProject.setText("Project:");
		
		projectCombo = new Combo(container, SWT.READ_ONLY | SWT.BORDER
				| SWT.SINGLE);
		projectCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		projectCombo.setItems(retriever.getRodinProjects());
		projectCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				String value = projectCombo.getItem(projectCombo
						.getSelectionIndex());
				if (selectedProject != null && selectedProject.equals(value)) {
					return;
				}
				selectedProject = value;
				selectedTheorem = null;
				selectedTheory = null;
				theoryCombo.setItems(retriever.getTheories(selectedProject));
				dialogChanged();
			}
		});
		
		Label lblTheory = new Label(container, SWT.NONE);
		lblTheory.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblTheory.setText("Theory:");
		
		theoryCombo = new Combo(container, SWT.READ_ONLY | SWT.BORDER
				| SWT.SINGLE);
		theoryCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		theoryCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				String value = theoryCombo.getItem(theoryCombo
						.getSelectionIndex());
				if (selectedTheory != null && selectedTheory.equals(value)) {
					return;
				}
				selectedTheory = value;
				selectedTheorem = null;
				table.removeAll();
				for (IDeployedTheorem thy : retriever.getDeployedTheorems(selectedProject, selectedTheory)){
					TableItem item = new TableItem(table, SWT.NONE);
					item.setText(0, thy.getName());
					item.setText(1, thy.getTheorem().toStringWithTypes());
				}
				dialogChanged();
			}
		});
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		ScrolledComposite scrolledComposite = new ScrolledComposite(container, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		
		table = new Table(scrolledComposite, SWT.BORDER | SWT.FULL_SELECTION);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				selectedTheorem = table.getSelection()[0].getText(0);
				dialogChanged();
			}
		});
		
		nameColumn = new TableColumn(table, SWT.LEFT);
		nameColumn.setWidth(100);
		nameColumn.setText(NAME_COL);
		
		theoremColumn = new TableColumn(table, SWT.LEFT);
		theoremColumn.setWidth(420);
		theoremColumn.setText(THEOREM_COL);
		scrolledComposite.setContent(table);
		
		dialogChanged();
		setControl(container);
	}
	
	/**
	 * Ensures that both text fields are set correctly.
	 */
	protected void dialogChanged() {
		if(selectedProject == null){
			theoryCombo.setEnabled(false);
			table.setEnabled(false);
			updateStatus("Project must be specified");
			return;
		}
		theoryCombo.setEnabled(true);
		if (selectedTheory == null) {
			table.setEnabled(false);
			updateStatus("Theory must be specified");
			return;
		}
		table.setEnabled(true);
		if(selectedTheorem == null){
			updateStatus("Theorem must be specified");
			return;
		}
		updateStatus(null);
	}

	private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);

	}

	public String getSelectedProject() {
		return selectedProject;
	}

	public String getSelectedTheory() {
		return selectedTheory;
	}

	public String getSelectedTheorem() {
		return selectedTheorem;
	}
	
}
