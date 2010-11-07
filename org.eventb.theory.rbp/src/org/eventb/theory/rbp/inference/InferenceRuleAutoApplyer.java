/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.inference;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.theory.core.IReasoningTypeElement.ReasoningType;
import org.eventb.theory.rbp.engine.IBinding;
import org.eventb.theory.rbp.internal.base.IDeployedGiven;
import org.eventb.theory.rbp.internal.base.IDeployedInferenceRule;
import org.eventb.theory.rbp.reasoners.AutoInferenceReasoner;
import org.eventb.theory.rbp.reasoning.AbstractRulesApplyer;
import org.eventb.theory.rbp.reasoning.InferenceDerivationTree;

/**
 * An implementation of an automatic inference rules applyer.
 * @since 1.0
 * 
 * @author maamria
 * 
 */
public class InferenceRuleAutoApplyer extends AbstractRulesApplyer {

	public InferenceRuleAutoApplyer(FormulaFactory factory) {
		super(factory);
	}

	/**
	 * Applies automatic inference rules to the given sequent until a fix-point is reached.
	 * @param sequent the prover sequent
	 * @return the resulting antecedents or <code>null</code> if no rules are applicable
	 */
	public IAntecedent[] applyRules(IProverSequent sequent) {
		// only inference rules that are backward and automatic
		List<IDeployedInferenceRule> rules = manager.getInferenceRules(
				ReasoningType.BACKWARD, true);
		IAntecedent goalAntecedent = ProverFactory.makeAntecedent(sequent.goal());
		InferenceDerivationTree tree = new InferenceDerivationTree(goalAntecedent, null);
		for (IDeployedInferenceRule rule : rules) {
			applyRule(tree, rule);
		}
		Set<IAntecedent> resultAnts = tree.getLeafAntecedents();
		if (resultAnts == null) {
			return null;
		}
		return resultAnts.toArray(
				new IAntecedent[resultAnts.size()]);
	}

	protected void applyRule(InferenceDerivationTree tree, IDeployedInferenceRule rule) {
		if (tree.continueDeriving()) {
			IAntecedent ant = tree.getAntecedent();
			Predicate goal = ant.getGoal();
			Predicate infer = rule.getInfer().getInferClause();
			IBinding binding = finder.calculateBindings(goal, infer, false);
			if (binding != null) {
				List<IDeployedGiven> givens = rule.getGivens();
				Set<IAntecedent> ants = new LinkedHashSet<IAntecedent>();
				for (IDeployedGiven given : givens) {
					Predicate pred = (Predicate) simpleBinder.bind(
							given.getGivenClause(), binding, false);
					IAntecedent a = ProverFactory.makeAntecedent(pred);
					ants.add(a);
				}
				tree.setAntecedents(ants);
				for (InferenceDerivationTree derivTree : tree.getInferenceTrees()) {
					applyRule(derivTree, rule);
				}
			}
		}
	}

	protected void addUsedTheory(String name) {
		if (!AutoInferenceReasoner.usedTheories.contains(name))
			AutoInferenceReasoner.usedTheories.add(name);
	}

}
