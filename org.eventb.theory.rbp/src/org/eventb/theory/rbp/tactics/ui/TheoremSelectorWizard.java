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

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.theory.rbp.rulebase.IPOContext;
import org.eventb.theory.rbp.rulebase.basis.IDeployedTheorem;

public class TheoremSelectorWizard extends Wizard {

	private TheoremSelectorWizardPage page;
	private TheoremsRetriever retriever;
	private ITypeEnvironment typeEnvironment;
	
	private List<String> theoremsToAdd = null;
	
	public TheoremSelectorWizard(IPOContext poContext, ITypeEnvironment typeEnvironment) {
		setWindowTitle("Select theorem");
		retriever = new TheoremsRetriever(poContext);
		this.typeEnvironment = typeEnvironment;
	}

	@Override
	public void addPages() {
		page = new TheoremSelectorWizardPage(retriever);
		addPage(page);
	}

	@Override
	public boolean performFinish() {
		List<IDeployedTheorem> deployedTheorems = retriever.getDeployedTheorems(page.getSelectedProject(),
				page.getSelectedTheory(), page.getSelectedTheorem());
		if (!deployedTheorems.isEmpty()){
			if (!hasTypeParameters(deployedTheorems)){
				theoremsToAdd = getStrings(deployedTheorems);
				return true;
			}
			TheoremInstantiatorWizard wizard = new TheoremInstantiatorWizard(deployedTheorems, retriever.getFactory(), typeEnvironment);
			WizardDialog dialog = new WizardDialog(wizard.getShell(), wizard);
			dialog.setTitle(wizard.getWindowTitle());
			dialog.open();
			theoremsToAdd = wizard.getTheoremsStrings();
			if(theoremsToAdd == null || theoremsToAdd.isEmpty()){
				return false;
			}
		}
		return true;
	}
	
	public List<String> getTheorems(){
		return theoremsToAdd;
	}
	
	private boolean hasTypeParameters(List<IDeployedTheorem> list){
		for (IDeployedTheorem thy : list){
			if (thy.hasTypeParameters()){
				return true;
			}
		}
		return false;
	}
	
	private List<String> getStrings(List<IDeployedTheorem> theorems){
		List<String> strings = new ArrayList<String>();
		for (IDeployedTheorem theorem : theorems){
			strings.add(theorem.getTheorem().toStringWithTypes());
		}
		return strings;
	}

}
