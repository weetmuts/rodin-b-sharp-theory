/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.ui.deploy;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Shell;

/**
 * @author maamria
 *
 */
public class RepairWizard extends Wizard{

	RepairWizardPage page;
	Shell shell;
	
	public RepairWizard(Shell shell) {
		super();
		this.shell = shell;
		
	}
	
	@Override
	public void addPages() {
		setWindowTitle("Repair Proof Files");
		this.page = new RepairWizardPage("rWizardPage");
		addPage(page);

	}
	
	@Override
	public boolean performFinish() {
		return false;
	}

}
