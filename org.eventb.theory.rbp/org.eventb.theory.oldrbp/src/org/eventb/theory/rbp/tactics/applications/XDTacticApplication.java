/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.tactics.applications;

import static org.eventb.core.seqprover.tactics.BasicTactics.reasonerTac;

import org.eclipse.swt.graphics.Image;
import org.eventb.core.pm.IProofAttempt;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IReasonerInput;
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
				if (node == null || !node.isOpen()) {
					return "Node already has children";
				}
				final Object origin = node.getProofTree().getOrigin();
				if (!(origin instanceof IProofAttempt)) {
					return "Contextual information of PO is required";
				}

				final IProofAttempt pa = (IProofAttempt) origin;
				final IPOContext context = new POContext(pa.getComponent()
						.getPORoot());
				XDReasoner reasoner = new XDReasoner();
				IReasonerInput input = new ContextualInput(context);
				return reasonerTac(reasoner, input).apply(node, pm);
			}
		};
	}

	@Override
	public Image getIcon() {
		return null;
	}

	@Override
	public String getTooltip() {
		return null;
	}
}
