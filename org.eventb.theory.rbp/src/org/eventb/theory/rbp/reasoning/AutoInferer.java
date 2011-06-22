/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.reasoning;

import java.util.List;
import java.util.Set;

import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.theory.core.IReasoningTypeElement.ReasoningType;
import org.eventb.theory.rbp.internal.rulebase.IDeployedInferenceRule;
import org.eventb.theory.rbp.reasoners.AutoInferenceReasoner;
import org.eventb.theory.rbp.rulebase.IPOContext;

/**
 * @author maamria
 *
 */
public class AutoInferer extends AbstractRulesApplyer {
	
	private IPOContext context;
	
	public AutoInferer(IPOContext context){
		super(context);
		this.context = context;
	}

	public IAntecedent[] applyInferenceRules(IProverSequent sequent){
		return applyRules(sequent);
	}
	
	/**
	 * Applies automatic inference rules to the given sequent until a fix-point is reached.
	 * @param sequent the prover sequent
	 * @return the resulting antecedents or <code>null</code> if no rules are applicable
	 */
	protected IAntecedent[] applyRules(IProverSequent sequent) {
		// only inference rules that are backward and automatic
		List<IDeployedInferenceRule> rules = manager.getInferenceRules(true, ReasoningType.BACKWARD, context, factory);
		IAntecedent goalAntecedent = ProverFactory.makeAntecedent(sequent.goal());
		InferenceDerivationTree tree = new InferenceDerivationTree(goalAntecedent, null);
		for (IDeployedInferenceRule rule : rules) {
			applyRule(tree, rule);
		}
		Set<IAntecedent> resultAnts = tree.getLeafAntecedents();
		if (resultAnts == null) {
			return null;
		}
		return resultAnts.toArray(new IAntecedent[resultAnts.size()]);
	}

	protected void applyRule(InferenceDerivationTree tree, IDeployedInferenceRule rule) {
		
	}

	protected void addUsedTheory(String name) {
		if (!AutoInferenceReasoner.usedTheories.contains(name))
			AutoInferenceReasoner.usedTheories.add(name);
	}

}
