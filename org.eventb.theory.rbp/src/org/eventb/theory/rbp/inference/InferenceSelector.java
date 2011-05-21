/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.inference;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.theory.core.IReasoningTypeElement.ReasoningType;
import org.eventb.theory.rbp.engine.IBinding;
import org.eventb.theory.rbp.engine.MatchFinder;
import org.eventb.theory.rbp.internal.engine.MatchingFactory;
import org.eventb.theory.rbp.internal.rulebase.IDeployedGiven;
import org.eventb.theory.rbp.internal.rulebase.IDeployedInferenceRule;
import org.eventb.theory.rbp.internal.tactics.InferencePositionTacticApplication;
import org.eventb.theory.rbp.internal.tactics.InferenceTacticApplication;
import org.eventb.theory.rbp.reasoners.input.InferenceInput;
import org.eventb.theory.rbp.rulebase.BaseManager;
import org.eventb.theory.rbp.rulebase.IPOContext;
import org.eventb.ui.prover.ITacticApplication;

/**
 * @author maamria
 * 
 */
public class InferenceSelector {

	protected MatchFinder finder;
	protected BaseManager ruleBaseManager;
	private IPOContext context;

	public InferenceSelector(FormulaFactory factory, IPOContext context) {
		ruleBaseManager = BaseManager.getDefault();
		finder = new MatchFinder(factory);
		this.context = context;
	}

	public List<ITacticApplication> select(Predicate predicate,
			IProverSequent sequent) {
		List<ITacticApplication> apps = new ArrayList<ITacticApplication>();
		if (predicate == null) {
			// backward
			Predicate goal = sequent.goal();
			List<IDeployedInferenceRule> rules = ruleBaseManager
					.getInferenceRules(false , ReasoningType.BACKWARD, context, null);
			boolean addedPredApp = false;
			for (IDeployedInferenceRule rule : rules) {
				IBinding binding = finder.calculateBindings(goal, rule
						.getInfer().getInferClause(), false);
				if (binding != null) {
					if (!addedPredApp) {
						apps.add(new InferenceTacticApplication(
								new InferenceInput(rule.getTheoryName(), rule
										.getRuleName(), rule.getDescription(),
										null, false), rule.getToolTip(), context));
						addedPredApp = true;
					} else {
						apps.add(new InferencePositionTacticApplication(
								new InferenceInput(rule.getTheoryName(), rule
										.getRuleName(), rule.getDescription(),
										null, false), rule.getToolTip(), context));
					}
				}
			}
		} else {
			List<IDeployedInferenceRule> rules = ruleBaseManager
					.getInferenceRules(false, ReasoningType.FORWARD, null, null);
			boolean addedPredApp = false;
			for (IDeployedInferenceRule rule : rules) {
				List<IDeployedGiven> givens = rule.getGivens();
				// the rule is forward meaning we have at least one given
				if (givens.size() < 1) {
					continue;
				}
				// get the first given
				Predicate firstGiven = givens.get(0).getGivenClause();
				// match it
				IBinding binding = finder.calculateBindings(predicate,
						firstGiven, false);
				if (binding == null) {
					continue;
				}
				// in case this hypothesis matches the first given , we continue
				IBinding accumulatedBinding = MatchingFactory.createBinding(
						false, binding.getFormulaFactory());
				accumulatedBinding.insertBinding(binding);
				// we only need to match against the givens bar the 1st one
				List<IDeployedGiven> leftGivens = new ArrayList<IDeployedGiven>();
				leftGivens.addAll(givens);
				// used hyps should not be considered further, the 1st given is
				// there
				List<Predicate> usedHyps = new ArrayList<Predicate>();
				usedHyps.add(firstGiven);
				leftGivens.remove(0);
				// check that each left given is matchable against a distinct
				// hyp
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
				if (allMatched) {
					if (!addedPredApp){
						apps.add(new InferenceTacticApplication(
								new InferenceInput(rule.getTheoryName(), rule
										.getRuleName(), rule.getDescription(),
										predicate, true), rule.getToolTip(), context));
						addedPredApp = true;
					}
					else {
						apps.add(new InferencePositionTacticApplication(
								new InferenceInput(rule.getTheoryName(), rule
										.getRuleName(), rule.getDescription(),
										predicate, true), rule.getToolTip(), context));
					}
				}
			}

		}
		return apps;
	}

}
