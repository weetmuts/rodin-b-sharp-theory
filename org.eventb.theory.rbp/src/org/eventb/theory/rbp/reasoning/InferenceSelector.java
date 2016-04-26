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

import org.eventb.core.ast.ISpecialization;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.extensions.pm.Matcher;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.theory.core.IGeneralRule;
import org.eventb.theory.core.IReasoningTypeElement.ReasoningType;
import org.eventb.theory.rbp.reasoners.input.IPRMetadata;
import org.eventb.theory.rbp.reasoners.input.InferenceInput;
import org.eventb.theory.rbp.reasoners.input.PRMetadata;
import org.eventb.theory.rbp.rulebase.BaseManager;
import org.eventb.theory.rbp.rulebase.IPOContext;
import org.eventb.theory.rbp.rulebase.basis.IDeployedGiven;
import org.eventb.theory.rbp.rulebase.basis.IDeployedInferenceRule;
import org.eventb.theory.rbp.tactics.applications.InferenceTacticApplication;
import org.eventb.ui.prover.ITacticApplication;

/**
 * @author maamria, asiehsalehi
 * 
 * the case when inf rule is tyoe of ISCInferenceRule is commented; 
 * for now the (SC) inf rules defined in a theory are not applicable in proving within the theory
 * TODO: like application of theorems, in POs of each inf rule, the inf rules above that should be available
 * 
 */
public class InferenceSelector {

	protected BaseManager ruleBaseManager;
	protected IPOContext context;

	public InferenceSelector(IPOContext context) {
		ruleBaseManager = BaseManager.getDefault();
		this.context = context;
	}

	public List<ITacticApplication> select(Predicate predicate,
			IProverSequent sequent) {
		if (predicate == null) {
			return backwardApplications(sequent);
		}
		// forward
		else {
			return forwardApplications(sequent, predicate);
		}
	}

	/**
	 * @param sequent
	 * @param predicate
	 * @return
	 */
	private List<ITacticApplication> forwardApplications(
			IProverSequent sequent, Predicate predicate) {
		List<IGeneralRule> rules = ruleBaseManager.getInferenceRules(false,
				ReasoningType.FORWARD, context);
		List<ITacticApplication> apps = new ArrayList<ITacticApplication>();
		for (IGeneralRule rule : rules) {
			if (rule instanceof IDeployedInferenceRule) {
				ITacticApplication app = forwardApplication((IDeployedInferenceRule) rule, sequent, predicate);
				if (app != null)
					apps.add(app);
			} else {
				throw new UnsupportedOperationException(
						"Only deployed inference rules are supported");
			}
		}
		return apps;
	}

	/**
	 * @param rule
	 * @param sequent
	 * @param predicate
	 * @return
	 */
	private ITacticApplication forwardApplication(IDeployedInferenceRule rule,
			IProverSequent sequent, Predicate predicate) {
		List<IDeployedGiven> hypGivens = rule.getHypGivens();
		if (hypGivens.isEmpty())
			return null;
		ISpecialization specialization = null;
		List<Predicate> hyps = new ArrayList<Predicate>();
		for (IDeployedGiven hypGiven : hypGivens) {
			if (specialization == null) {
				specialization = Matcher.match(predicate,
						hypGiven.getGivenClause());
				if (specialization != null)
					hyps.add(predicate);
			} else {
				specialization = matchHypGiven(specialization, hypGiven, sequent, hyps);
			}
			if (specialization == null)
				return null;
		}
		
		String projectName = rule.getProjectName();
		String theoryName = rule.getTheoryName();
		String ruleName = rule.getRuleName();
		IPRMetadata prMetadata = new PRMetadata(projectName, theoryName,
				ruleName);
		InferenceInput input = new InferenceInput(prMetadata,
				hyps.toArray(new Predicate[hyps.size()]), true);

		return new InferenceTacticApplication(input);
	}

	/**
	 * @param specialization
	 * @param hyp
	 * @param sequent 
	 * @param hyps 
	 * @return
	 */
	private ISpecialization matchHypGiven(ISpecialization specialization,
			IDeployedGiven hyp, IProverSequent sequent, List<Predicate> hyps) {
		for (Predicate selectHyp : sequent.selectedHypIterable()) {
			ISpecialization clone = specialization.clone();
			clone = Matcher.match(clone, selectHyp, hyp.getGivenClause());
			if (clone != null) {
				hyps.add(selectHyp);
				return clone;
			}
		}
		return null;
	}

	/**
	 * @param predicate
	 * @param sequent
	 * @return
	 */
	private List<ITacticApplication> backwardApplications(IProverSequent sequent) {
		// backward
		List<IGeneralRule> rules = ruleBaseManager.getInferenceRules(false,
				ReasoningType.BACKWARD, context);
		List<ITacticApplication> apps = new ArrayList<ITacticApplication>();
		for (IGeneralRule rule : rules) {
			if (rule instanceof IDeployedInferenceRule) {
				ITacticApplication app = backwardApplication((IDeployedInferenceRule) rule, sequent);
				if (app != null)
					apps.add(app);
			} else {
				throw new UnsupportedOperationException(
						"Only deployed inference rules are supported");
			}
		}
		return apps;
	}

	/**
	 * @param rule
	 * @param predicate
	 * @param sequent
	 * @return
	 */
	private ITacticApplication backwardApplication(
			IDeployedInferenceRule rule, IProverSequent sequent) {
		Predicate goal = sequent.goal();
		ISpecialization specialization = Matcher.match(goal,
				rule.getInfer()
						.getInferClause());
		// if goal does not match infer clause then return the empty list.
		if (specialization == null) {
			return null;
		}	
		// if goal matches infer clause
		List<IDeployedGiven> hypGivens = ((IDeployedInferenceRule) rule)
				.getHypGivens();
		List<Predicate> hyps = new ArrayList<Predicate>(hypGivens.size());
		Iterable<Predicate> selectedHypIterable = sequent.selectedHypIterable();
		for (IDeployedGiven hypGiven : hypGivens) {
			boolean match = false;
			for (Predicate hyp : selectedHypIterable) {
				ISpecialization clone = specialization.clone();
				clone = Matcher.match(clone, hyp, hypGiven.getGivenClause());
				if (clone != null) {
					hyps.add(hyp);
					specialization = clone;
					match = true;
					break;
				}
			}
			if (match == false) {
				break;
			}
		}
		if (hyps.size() == hypGivens.size()) {
			String projectName = rule.getProjectName();
			String theoryName = rule.getTheoryName();
			String ruleName = rule.getRuleName();
			IPRMetadata prMetadata = new PRMetadata(projectName, theoryName,
					ruleName);
			InferenceInput input = new InferenceInput(prMetadata,
					hyps.toArray(new Predicate[hyps.size()]), false);

			return new InferenceTacticApplication(input);
		}
		return null;
	}

}
