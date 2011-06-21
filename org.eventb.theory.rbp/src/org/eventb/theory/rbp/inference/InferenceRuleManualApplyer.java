/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.inference;

import java.util.List;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pm.IMatchingResult;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ProverFactory;
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
		IMatchingResult binding = finder.match(goal, infer, false);
		if (binding != null) {
			List<IDeployedGiven> givens = rule.getGivens();
			IAntecedent[] antecedents = new IAntecedent[givens.size()];
			int i = 0;
			for (IDeployedGiven given : givens) {
				Predicate subGoal = (Predicate) simpleBinder.bind(given.getGivenClause(), binding);
				antecedents[i] = ProverFactory.makeAntecedent(subGoal);
				i++;
			}
			return antecedents;
		}
		return null;
	}

	protected IAntecedent[] forwardReason(IProverSequent sequent, 
			Predicate hypothesis, IDeployedInferenceRule rule){
		return null;
		
	}
}
