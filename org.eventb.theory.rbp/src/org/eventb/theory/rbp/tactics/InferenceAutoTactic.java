/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.tactics;

import org.eventb.core.IPSStatus;
import org.eventb.core.pm.IProofAttempt;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.ITactic;
import org.eventb.theory.rbp.reasoners.AutoInferenceReasoner;
import org.eventb.theory.rbp.rulebase.IPOContext;
import org.eventb.theory.rbp.rulebase.basis.POContext;

/**
 * The automatic tactic for applying inference rules.
 * 
 * <p> At the moment, only rules that can be applied in a backward fashion can be used automatically.
 * 
 * @since 1.0
 * 
 * @author maamria
 *
 */
public class InferenceAutoTactic implements ITactic{

	@Override
	public Object apply(IProofTreeNode node, IProofMonitor pm) {
		if (node.getProofTree().getOrigin() instanceof IProofAttempt){
			if (!node.isOpen()){
				return "Root already has children";
			}
			IProofAttempt attempt = (IProofAttempt) node.getProofTree().getOrigin();
			IPSStatus status = attempt.getStatus();
			IPOContext poContext = new POContext(status, attempt.getFormulaFactory());
			AutoInferenceReasoner reasoner = new AutoInferenceReasoner();
			reasoner.setContext(poContext);
			IReasonerOutput reasonerOutput = reasoner.apply(node.getSequent(), null, pm);
			if (reasonerOutput == null) return "! Plugin returned null !";
			if (!(reasonerOutput instanceof IProofRule)) return reasonerOutput;
			IProofRule rule = (IProofRule)reasonerOutput;
			if (node.applyRule(rule)) return null;
			else return "Rule "+rule.getDisplayName()+" is not applicable";
		}
		return "Contextual information of PO is required";
	}

}
