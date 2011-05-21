/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.inference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.theory.rbp.engine.IBinding;
import org.eventb.theory.rbp.internal.engine.MatchingFactory;
import org.eventb.theory.rbp.internal.rulebase.IDeployedGiven;
import org.eventb.theory.rbp.internal.rulebase.IDeployedInferenceRule;
import org.eventb.theory.rbp.reasoning.AbstractRulesApplyer;
import org.eventb.theory.rbp.rulebase.IPOContext;

/**
 * An implementation of an inference rules manual applyer.
 * 
 * @since 1.0
 * @author maamria
 * 
 */
public class InferenceRuleManualApplyer extends AbstractRulesApplyer {

	public InferenceRuleManualApplyer(FormulaFactory factory, IPOContext context) {
		super(factory, context);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Returns the antecedents resulting from applying the specified rule.
	 * <p>
	 * 
	 * @param sequent
	 *            the prover sequent
	 * @param pred
	 *            the predicate
	 * @param forward
	 *            whether the rule should be applied in a forward fashion
	 * @param theoryName
	 *            the theory of the rule
	 * @param ruleName
	 *            the rule name
	 * @return the antecedents or <code>null</code> if the rule was not found or
	 *         is inapplicable.
	 */
	public IAntecedent[] applyRule(IProverSequent sequent, Predicate pred,
			boolean forward, String theoryName, String ruleName) {
		IDeployedInferenceRule rule = manager.getInferenceRule(theoryName, ruleName, context, factory);
		if (rule == null) {
			return null;
		}
		if (!(forward && rule.isSuitableForForwardReasoning())
				&& !(!forward && rule.isSuitableForBackwardReasoning())) {
			return null;
		}

		if (forward && !sequent.containsHypothesis(pred)) {
			return null;
		}
		if (forward)
			return forwardReason(sequent, pred, rule);
		else
			return backwardReason(sequent, rule);
	}

	protected IAntecedent[] backwardReason(IProverSequent sequent,
			IDeployedInferenceRule rule) {
		Predicate goal = sequent.goal();
		Predicate infer = rule.getInfer().getInferClause();
		IBinding binding = finder.calculateBindings(goal, infer, false);
		if (binding != null) {
			List<IDeployedGiven> givens = rule.getGivens();
			IAntecedent[] antecedents = new IAntecedent[givens.size()];
			int i = 0;
			for (IDeployedGiven given : givens) {
				Predicate subGoal = (Predicate) simpleBinder.bind(
						given.getGivenClause(), binding, false);
				antecedents[i] = ProverFactory.makeAntecedent(subGoal);
				i++;
			}
			return antecedents;
		}
		return null;
	}

	protected IAntecedent[] forwardReason(IProverSequent sequent, 
			Predicate hypothesis, IDeployedInferenceRule rule){
		List<IDeployedGiven> givens = rule.getGivens();
		Predicate firstGiven = givens.get(0).getGivenClause();
		IBinding binding = finder.calculateBindings(hypothesis, firstGiven, false);
		if(binding == null){
			return null;
		}
		// in case this hypothesis matches the first given , we continue
		IBinding accumulatedBinding = MatchingFactory.createBinding(
				false, binding.getFormulaFactory());
		accumulatedBinding.insertBinding(binding);
		// we only need to match against the givens bar the 1st one
		List<IDeployedGiven> leftGivens = new ArrayList<IDeployedGiven>();
		leftGivens.addAll(givens);
		// used hyps should not be considered further, the 1st given is there
		List<Predicate> usedHyps = new ArrayList<Predicate>();
		usedHyps.add(hypothesis);
		leftGivens.remove(0);
		// check that each left given is matchable against a distinct hyp
		boolean allMatched = true;
		for (IDeployedGiven given : leftGivens) {
			boolean hasMatch = false;
			// find a match in hyps
			for (Predicate hyp : sequent.selectedHypIterable()) {
				// if this hyp is used already
				if (usedHyps.contains(hyp)) {
					continue;
				}
				// match?
				IBinding gBinding = finder.calculateBindings(hyp,
						given.getGivenClause(), false);
				if (gBinding == null) {
					continue;
				}
				if (accumulatedBinding.isBindingInsertable(gBinding)) {
					accumulatedBinding.insertBinding(gBinding);
					usedHyps.add(hyp);
					hasMatch = true;
					break;
				}
			}
			if (!hasMatch) {
				allMatched = false;
				break;
			}
		}
		if (!allMatched) {
			return null;
		}
		accumulatedBinding.makeImmutable();
		Predicate newHyp = (Predicate) simpleBinder.bind(rule.getInfer().getInferClause(), accumulatedBinding, false);
		return new IAntecedent[]{
				ProverFactory.makeAntecedent(null, Collections.singleton(newHyp), 
						ProverFactory.makeSelectHypAction(Collections.singleton(newHyp)))};
		
	}
}
