/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.tactics;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.IEventBRoot;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pm.IProofAttempt;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.theory.rbp.reasoning.RewritesSelector;
import org.eventb.theory.rbp.rulebase.IPOContext;
import org.eventb.theory.rbp.rulebase.basis.POContext;
import org.eventb.ui.prover.DefaultTacticProvider;
import org.eventb.ui.prover.ITacticApplication;
import org.eventb.ui.prover.ITacticProvider;

/**
 * 
 * @author maamria
 *
 */
public class TheoremInstantiationTactic extends DefaultTacticProvider implements ITacticProvider {

	public List<ITacticApplication> getPossibleApplications(
			IProofTreeNode node, Predicate hyp, String globalInput) {
		if (node.getProofTree().getOrigin() instanceof IProofAttempt){
			IProofAttempt attempt = (IProofAttempt) node.getProofTree().getOrigin();
			IPOContext poContext = new POContext(
					(IEventBRoot) attempt.getComponent().getPORoot());
			boolean isGoal = hyp == null;
			Predicate pred = ( isGoal ? node.getSequent().goal() : hyp);
			return pred.inspect(new RewritesSelector(pred, isGoal, poContext));
		}
		// Contextual information needed
		return new ArrayList<ITacticApplication>();
		
	}
	
}
