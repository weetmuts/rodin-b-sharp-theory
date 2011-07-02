/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.tactics.ui;

import org.eclipse.jface.wizard.Wizard;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.theory.rbp.rulebase.basis.IDeployedTheorem;

public class TheoremInstantiatorWizard extends Wizard {

	private IDeployedTheorem deployedTheorem;
	private FormulaFactory factory;
	private TheoremInstantiatorWizardPage page;
	
	private String theoremStr = null;
	
	public TheoremInstantiatorWizard(IDeployedTheorem deployedTheorem, FormulaFactory factory) {
		setWindowTitle("Instantiate theorem");
		this.deployedTheorem =  deployedTheorem;
		this.factory = factory;
	}

	@Override
	public void addPages() {
		page = new TheoremInstantiatorWizardPage(deployedTheorem, factory);
		addPage(page);
	}

	@Override
	public boolean performFinish() {
		theoremStr = page.getTheoremString();
		return true;
	}
	
	public String getTheoremString(){
		return theoremStr;
	}

}
