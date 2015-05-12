/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.tactics.applications;

import java.util.List;

import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.graphics.Image;
import org.eventb.core.IPOSource;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.pm.IProofAttempt;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.ITactic;
import org.eventb.theory.core.ISCAxiomaticDefinitionAxiom;
import org.eventb.theory.core.ISCTheorem;
import org.eventb.theory.rbp.plugin.RbPPlugin;
import org.eventb.theory.rbp.reasoners.THReasoner;
import org.eventb.theory.rbp.reasoners.input.MultipleStringInput;
import org.eventb.theory.rbp.rulebase.IPOContext;
import org.eventb.theory.rbp.rulebase.basis.POContext;
import org.eventb.theory.rbp.tactics.ui.TheoremSelectorWizard;
import org.eventb.theory.rbp.utils.ProverUtilities;
import org.eventb.ui.prover.IPredicateApplication;
import org.rodinp.core.RodinDBException;

/**
 * A tactic that enables instantiating and adding theorems.
 * @author maamria
 * 
 */
public class THTacticApplication implements IPredicateApplication {

	private static final String TACTIC_ID = RbPPlugin.PLUGIN_ID + ".RbPth";

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
						int order = -1;
						boolean axm = false;
						try {
							IPOSource[] sources = pa.getStatus().getPOSequent().getSources();
							for (IPOSource source : sources){
								if (source.getSource() instanceof ISCTheorem){
									ISCTheorem scTheorem = (ISCTheorem) source.getSource();
									order = scTheorem.getOrder();
									if (scTheorem.getSource() instanceof ISCAxiomaticDefinitionAxiom)
										axm = true;
									break;
								}
							}
						} catch (RodinDBException e) {
							ProverUtilities.log(e, "uanbale to check sources of PO");
							return "Unable to continue";
						}
				
						final IPOContext context = new POContext(pa.getComponent().getPORoot(), order, axm);
						ITypeEnvironment typeEnvironment = node.getSequent().typeEnvironment();
						// Show wizard
						TheoremSelectorWizard wizard = new TheoremSelectorWizard(context, typeEnvironment);
						WizardDialog dialog = new WizardDialog(wizard.getShell(), wizard);
						dialog.setTitle(wizard.getWindowTitle());
						dialog.open();
						// get the theorem if any
						List<String> theorems = wizard.getTheorems();
						if (theorems == null || theorems.isEmpty()) {
							return "No theorem provided";
						}
						THReasoner reasoner = new THReasoner();
						IReasonerOutput reasonerOutput = reasoner.apply(node.getSequent(), 
								new MultipleStringInput(context, theorems), pm);
						if (reasonerOutput == null) return "! Plugin returned null !";
						if (!(reasonerOutput instanceof IProofRule)) return reasonerOutput;
						IProofRule rule = (IProofRule)reasonerOutput;
						if (node.applyRule(rule)) return null;
						else return "Theorems cannot be added";
					}
				}
				return "Root already has children";
			}
		};
	}

	@Override
	public Image getIcon() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTooltip() {
		// TODO Auto-generated method stub
		return null;
	}
}
