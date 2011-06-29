/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.tactics.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eventb.theory.core.DatabaseUtilities;
import org.eventb.theory.rbp.rulebase.IPOContext;
import org.rodinp.core.IRodinProject;

public class TheoremWizardPage extends WizardPage {
	
	private Combo projectCombo;
	private Combo theoryCombo;
	private Table table;
	private TableColumn nameColumn;
	private TableColumn theoremColumn;
	
	IPOContext poContext;

	public TheoremWizardPage(IPOContext poContext) {
		super("instantiateTheorem");
		setTitle("Instantiate theorem");
		setDescription("Select polymorphic theorem to instantiate");
		this.poContext = poContext;
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
		lblProject.setText("Project");
		
		projectCombo = new Combo(container, SWT.NONE);
		projectCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		projectCombo.setItems(getProjects());
		
		Label lblTheory = new Label(container, SWT.NONE);
		lblTheory.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblTheory.setText("Theory");
		
		theoryCombo = new Combo(container, SWT.NONE);
		theoryCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		ScrolledComposite scrolledComposite = new ScrolledComposite(container, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true, true, 1, 1));
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		
		table = new Table(scrolledComposite, SWT.BORDER | SWT.FULL_SELECTION);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		nameColumn = new TableColumn(table, SWT.NONE);
		nameColumn.setWidth(86);
		nameColumn.setText("Name");
		
		theoremColumn = new TableColumn(table, SWT.LEFT);
		theoremColumn.setWidth(426);
		theoremColumn.setText("Theorem");
		scrolledComposite.setContent(table);
	}

	private String[] getProjects(){
		List<String> list = new ArrayList<String>();
		IRodinProject rodinProject = poContext.getParentRoot().getRodinProject();
		list.add(rodinProject.getElementName());
		if (!poContext.inMathExtensions()){
			list.add(DatabaseUtilities.THEORIES_PROJECT);
		}
		return list.toArray(new String[list.size()]);
	}
	
}
