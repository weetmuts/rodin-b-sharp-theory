/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.reasoning;

import java.util.List;

import org.eventb.core.ast.Predicate;
import org.eventb.core.pm.IBinding;
import org.eventb.core.pm.SimpleBinder;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.theory.rbp.rulebase.IPOContext;
import org.eventb.theory.rbp.rulebase.basis.IDeployedGiven;
import org.eventb.theory.rbp.rulebase.basis.IDeployedInferenceRule;

/**
 * @author maamria
 *
 */
public class ManualInferer extends AbstractRulesApplyer{
	
	private SimpleBinder binder;
	
	public ManualInferer(IPOContext context){
		super(context);
		this.binder = new SimpleBinder(context.getParentRoot().getFormulaFactory());
	}
	
	/**
	 * Returns the antecedents resulting from applying the specified rule.
	 * <p>
	 * @param pred to which the rule was applicable
	 * @param position 
	 * @param isGoal 
	 * @param theoryName
	 * @param ruleName
	 * @return the antecedents or <code>null</code> if the rule is not found or inapplicable
	 */
	public IAntecedent[] getAntecedents(IProverSequent sequent, Predicate pred, boolean forward, String theoryName, String ruleName){
		return applyRule(sequent, pred, forward, theoryName, ruleName);
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
		IBinding binding = finder.match(goal, infer, false);
		if (binding != null) {
			List<IDeployedGiven> givens = rule.getGivens();
			IAntecedent[] antecedents = new IAntecedent[givens.size()];
			int i = 0;
			for (IDeployedGiven given : givens) {
				Predicate subGoal = (Predicate) binder.bind(given.getGivenClause(), binding);
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
