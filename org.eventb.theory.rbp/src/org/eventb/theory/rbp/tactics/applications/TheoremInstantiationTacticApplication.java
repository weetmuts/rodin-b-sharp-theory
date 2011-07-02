/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.tactics.applications;

import org.eclipse.jface.wizard.WizardDialog;
import org.eventb.core.pm.IProofAttempt;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.reasonerInputs.SingleStringInput;
import org.eventb.theory.rbp.plugin.RbPPlugin;
import org.eventb.theory.rbp.reasoners.InstantiateTheoremReasoner;
import org.eventb.theory.rbp.rulebase.IPOContext;
import org.eventb.theory.rbp.rulebase.basis.POContext;
import org.eventb.theory.rbp.tactics.ui.TheoremSelectorWizard;
import org.eventb.ui.prover.ITacticApplication;

/**
 * A tactic that enables instantiating and adding theorems.
 * @author maamria
 * 
 */
public class TheoremInstantiationTacticApplication implements ITacticApplication {

	private static final String TACTIC_ID = RbPPlugin.PLUGIN_ID + ".RbPtheorems";

	@Override
	public String getTacticID() {
		return TACTIC_ID;
	}
	
	@Override
	public ITactic getTactic(String[] inputs, String globalInput) {
		return new ITactic() {
			@Override
			public Object apply(IProofTreeNode node, IProofMonitor pm) {
				if (node != null && node.isOpen()) {
					final Object origin = node.getProofTree().getOrigin();
					if (origin instanceof IProofAttempt) {
						final IProofAttempt pa = (IProofAttempt) origin;
						final IPOContext context = new POContext(pa.getComponent().getPORoot());
						// Show wizard
						TheoremSelectorWizard wizard = new TheoremSelectorWizard(context);
						WizardDialog dialog = new WizardDialog(wizard.getShell(), wizard);
						dialog.setTitle(wizard.getWindowTitle());
						dialog.open();
						// get the theorem if any
						String theorem = wizard.getTheorem();
						if (theorem == null) {
							return "No theorem provided";
						}
						InstantiateTheoremReasoner reasoner = new InstantiateTheoremReasoner();
						IReasonerOutput reasonerOutput = reasoner.apply(node.getSequent(), 
								new SingleStringInput(theorem), pm);
						if (reasonerOutput == null) return "! Plugin returned null !";
						if (!(reasonerOutput instanceof IProofRule)) return reasonerOutput;
						IProofRule rule = (IProofRule)reasonerOutput;
						if (node.applyRule(rule)) return null;
						else return "Theorem '"+theorem+"' cannot be added";
					}
				}
				return "Root already has children";
			}
		};
	}
}
