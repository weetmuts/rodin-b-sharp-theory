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
import org.eventb.theory.rbp.base.IRuleBaseManager;
import org.eventb.theory.rbp.base.RuleBaseManager;
import org.eventb.theory.rbp.engine.IBinding;
import org.eventb.theory.rbp.engine.MatchFinder;
import org.eventb.theory.rbp.internal.base.IDeployedInferenceRule;
import org.eventb.theory.rbp.internal.tactics.InferencePositionTacticApplication;
import org.eventb.theory.rbp.internal.tactics.InferenceTacticApplication;
import org.eventb.theory.rbp.reasoners.input.InferenceInput;
import org.eventb.ui.prover.ITacticApplication;

/**
 * @author maamria
 * 
 */
public class InferenceSelector {

	protected MatchFinder finder;
	protected IRuleBaseManager ruleBaseManager;

	public InferenceSelector(FormulaFactory factory) {
		ruleBaseManager = RuleBaseManager.getDefault();
		finder = new MatchFinder(factory);
	}

	public List<ITacticApplication> select(Predicate predicate,
			IProverSequent sequent) {
		List<ITacticApplication> apps = new ArrayList<ITacticApplication>();
		if (predicate == null) {
			// backward
			Predicate goal = sequent.goal();
			List<IDeployedInferenceRule> rules = ruleBaseManager
					.getInferenceRules(ReasoningType.BACKWARD, false);
			boolean addedPredApp = false;
			for (IDeployedInferenceRule rule : rules) {
				IBinding binding = finder.calculateBindings(goal, rule
						.getInfer().getInferClause(), false);
				if (binding != null) {
					if (!addedPredApp) {
						apps.add(new InferenceTacticApplication(
								new InferenceInput(rule.getTheoryName(), rule
										.getRuleName(), rule.getDescription(),
										null, false), rule.getToolTip()));
						addedPredApp = true;
					} else {
						apps.add(new InferencePositionTacticApplication(
								new InferenceInput(rule.getTheoryName(), rule
										.getRuleName(), rule.getDescription(),
										null, false), rule.getToolTip()));
					}
				}
			}
		}
		return apps;
	}

}
