/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.tactics.ui;

import org.eclipse.jface.wizard.Wizard;
import org.eventb.theory.rbp.rulebase.IPOContext;

public class TheoremWizard extends Wizard {

	IPOContext poContext;
	
	public TheoremWizard(IPOContext poContext) {
		setWindowTitle("Instantiate theorem");
		this.poContext = poContext;
	}

	@Override
	public void addPages() {
		TheoremWizardPage page = new TheoremWizardPage(poContext);
		addPage(page);
	}

	@Override
	public boolean performFinish() {
		return false;
	}

}
