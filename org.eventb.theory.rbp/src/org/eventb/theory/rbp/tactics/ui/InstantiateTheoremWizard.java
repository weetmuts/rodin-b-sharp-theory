package org.eventb.theory.rbp.tactics.ui;

import org.eclipse.jface.wizard.Wizard;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.theory.rbp.rulebase.IPOContext;

public class InstantiateTheoremWizard extends Wizard {
	
	private IPOContext poContext;
	private FormulaFactory factory;
	
	public InstantiateTheoremWizard(IPOContext poContext, FormulaFactory factory) {
		setWindowTitle("Instantiate Theorem Wizard");
		this.poContext = poContext;
		this.factory = factory;
	}

	@Override
	public void addPages() {
		addPage(new InstantiateTheoremWizardPageOne(poContext, factory));
	}

	@Override
	public boolean performFinish() {
		return false;
	}

}
