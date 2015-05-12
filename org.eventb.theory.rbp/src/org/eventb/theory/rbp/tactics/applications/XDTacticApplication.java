/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.tactics.applications;

import org.eclipse.swt.graphics.Image;
import org.eventb.core.pm.IProofAttempt;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.ITactic;
import org.eventb.theory.rbp.plugin.RbPPlugin;
import org.eventb.theory.rbp.reasoners.XDReasoner;
import org.eventb.theory.rbp.reasoners.input.ContextualInput;
import org.eventb.theory.rbp.rulebase.IPOContext;
import org.eventb.theory.rbp.rulebase.basis.POContext;
import org.eventb.ui.prover.IPredicateApplication;

/**
 * A special tactic application for the translation of all extended formulae to classical Event-B language.
 * @author maamria
 *
 */
public class XDTacticApplication implements IPredicateApplication {
	
	private static final String TACTIC_ID = RbPPlugin.PLUGIN_ID + ".RbPxd";

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
						XDReasoner reasoner = new XDReasoner();
						IReasonerOutput reasonerOutput = reasoner.apply(node.getSequent(), new ContextualInput(context), pm);
						if (reasonerOutput == null) return "! Plugin returned null !";
						if (!(reasonerOutput instanceof IProofRule)) return reasonerOutput;
						IProofRule rule = (IProofRule)reasonerOutput;
						if (node.applyRule(rule)) return null;
						else return "Rule "+rule.getDisplayName()+" is not applicable";
					}
					else {
						return "Contextual information of PO is required";
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