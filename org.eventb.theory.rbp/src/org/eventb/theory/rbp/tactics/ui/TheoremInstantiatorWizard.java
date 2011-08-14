/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.tactics.ui;

import java.util.List;

import org.eclipse.jface.wizard.Wizard;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.theory.rbp.rulebase.basis.IDeployedTheorem;

public class TheoremInstantiatorWizard extends Wizard {

	private List<IDeployedTheorem> deployedTheorems;
	private FormulaFactory factory;
	private TheoremInstantiatorWizardPage page;
	
	private List<String> theoremStrs = null;
	
	public TheoremInstantiatorWizard(List<IDeployedTheorem> deployedTheorems, FormulaFactory factory) {
		setWindowTitle("Instantiate theorem");
		this.deployedTheorems =  deployedTheorems;
		this.factory = factory;
	}

	@Override
	public void addPages() {
		page = new TheoremInstantiatorWizardPage(deployedTheorems, factory);
		addPage(page);
	}

	@Override
	public boolean performFinish() {
		theoremStrs = page.getTheoremsStrings();
		return true;
	}
	
	public List<String> getTheoremsStrings(){
		return theoremStrs;
	}

}
