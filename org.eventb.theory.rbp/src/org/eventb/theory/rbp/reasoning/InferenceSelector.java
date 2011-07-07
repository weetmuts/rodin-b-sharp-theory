/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.reasoning;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.ast.Formula;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pm.IBinding;
import org.eventb.core.pm.Matcher;
import org.eventb.core.pm.assoc.ACPredicateProblem;
import org.eventb.core.pm.assoc.ACProblem;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.theory.core.IReasoningTypeElement.ReasoningType;
import org.eventb.theory.rbp.reasoners.input.InferenceInput;
import org.eventb.theory.rbp.rulebase.BaseManager;
import org.eventb.theory.rbp.rulebase.IPOContext;
import org.eventb.theory.rbp.rulebase.basis.IDeployedGiven;
import org.eventb.theory.rbp.rulebase.basis.IDeployedInferenceRule;
import org.eventb.theory.rbp.tactics.applications.InferenceTacticApplication;
import org.eventb.ui.prover.ITacticApplication;

/**
 * @author maamria
 * 
 */
public class InferenceSelector {

	protected Matcher finder;
	protected BaseManager ruleBaseManager;
	protected IPOContext context;

	public InferenceSelector(IPOContext context) {
		ruleBaseManager = BaseManager.getDefault();
		finder = new Matcher(context.getFormulaFactory());
		this.context = context;
	}

	public List<ITacticApplication> select(Predicate predicate, IProverSequent sequent) {
		List<ITacticApplication> apps = new ArrayList<ITacticApplication>();
		if (predicate == null) {
			// backward
			Predicate goal = sequent.goal();
			List<IDeployedInferenceRule> rules = ruleBaseManager.getInferenceRules(false, ReasoningType.BACKWARD, context);
			for (IDeployedInferenceRule rule : rules) {
				IBinding binding = finder.match(goal, rule.getInfer().getInferClause(), false);
				if (binding != null) {
					apps.add(new InferenceTacticApplication(
							new InferenceInput(rule.getTheoryName(), rule.getRuleName(), rule.getDescription(), null, false, context)));

				}
			}
		} 
		// forward
		else {
			List<IDeployedInferenceRule> rules = ruleBaseManager.getInferenceRules(false, ReasoningType.FORWARD, context);
			for (IDeployedInferenceRule rule : rules) {
				if (rule.getGivens().size() < 1){
					continue;
				}
				IDeployedGiven firstGiven = rule.getGivens().get(0);
				Predicate givenPredicate = firstGiven.getGivenClause();
				IBinding binding = finder.match(predicate, givenPredicate, true);
				if(binding == null){
					continue;
				}
				List<Predicate> otherGivens = new ArrayList<Predicate>();
				for (IDeployedGiven given : rule.getGivens()){
					if(!given.equals(firstGiven)){
						otherGivens.add(given.getGivenClause());
					}
				}
				List<Predicate> otherHyps = new ArrayList<Predicate>();
				for (Predicate hyp : sequent.hypIterable()){
					if (!hyp.equals(predicate)){
						otherHyps.add(hyp);
					}
				}
				ACProblem<Predicate> acProblem = new ACPredicateProblem(
						Formula.LAND, otherHyps.toArray(new Predicate[otherHyps.size()]), 
						otherGivens.toArray(new Predicate[otherGivens.size()]), binding);
				IBinding finalBinding = acProblem.solve(true);
				if (finalBinding == null){
					continue;
				}
				apps.add(new InferenceTacticApplication(new InferenceInput(
						rule.getTheoryName(), rule.getRuleName(), rule.getDescription(),
						predicate, true, context)));
			}

		}
		return apps;
	}

}
