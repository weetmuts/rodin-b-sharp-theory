/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.tactics.ui;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eventb.theory.rbp.rulebase.IPOContext;
import org.eventb.theory.rbp.rulebase.basis.IDeployedTheorem;

public class TheoremSelectorWizard extends Wizard {

	private TheoremSelectorWizardPage page;
	private TheoremsRetriever retriever;
	
	private String theoremToAdd = null;
	
	public TheoremSelectorWizard(IPOContext poContext) {
		setWindowTitle("Select theorem");
		retriever = new TheoremsRetriever(poContext);
	}

	@Override
	public void addPages() {
		page = new TheoremSelectorWizardPage(retriever);
		addPage(page);
	}

	@Override
	public boolean performFinish() {
		IDeployedTheorem deployedTheorem = retriever.getDeployedTheorem(page.getSelectedProject(),
				page.getSelectedTheory(), page.getSelectedTheorem());
		if (deployedTheorem != null){
			if (!deployedTheorem.hasTypeParameters()){
				theoremToAdd = deployedTheorem.getTheorem().toStringWithTypes();
				return true;
			}
			TheoremInstantiatorWizard wizard = new TheoremInstantiatorWizard(deployedTheorem, retriever.getFactory());
			WizardDialog dialog = new WizardDialog(wizard.getShell(), wizard);
			dialog.setTitle(wizard.getWindowTitle());
			dialog.open();
			theoremToAdd = wizard.getTheoremString();
			if(theoremToAdd == null){
				return false;
			}
		}
		return true;
	}
	
	public String getTheorem(){
		return theoremToAdd;
	}

}
