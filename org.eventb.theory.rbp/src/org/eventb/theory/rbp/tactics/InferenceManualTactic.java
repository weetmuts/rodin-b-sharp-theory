/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.tactics;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.IPSStatus;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pm.IProofAttempt;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.theory.rbp.inference.InferenceSelector;
import org.eventb.theory.rbp.rulebase.IPOContext;
import org.eventb.theory.rbp.rulebase.POContext;
import org.eventb.ui.prover.DefaultTacticProvider;
import org.eventb.ui.prover.ITacticApplication;
import org.eventb.ui.prover.ITacticProvider;

/**
 * The manual tactic for applying interactive inference rules.
 * 
 * @author maamria
 *
 */
public class InferenceManualTactic extends DefaultTacticProvider implements ITacticProvider{
	
	public List<ITacticApplication> getPossibleApplications(
			IProofTreeNode node, Predicate hyp, String globalInput) {
		if (node.getProofTree().getOrigin() instanceof IProofAttempt){
			IProofAttempt attempt = (IProofAttempt) node.getProofTree().getOrigin();
			IPSStatus status = attempt.getStatus();
			IPOContext poContext = new POContext(status);
			FormulaFactory factory = node.getFormulaFactory();
			InferenceSelector selector = new InferenceSelector(factory, poContext);
			return selector.select(hyp, node.getSequent());
		}
		// Contextual information needed
		return new ArrayList<ITacticApplication>();
		
	}
}
