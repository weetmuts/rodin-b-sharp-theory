/*******************************************************************************
 * Copyright (c) 2010,2016 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.reasoners;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ISpecialization;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.extensions.pm.Matcher;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerInputReader;
import org.eventb.core.seqprover.IReasonerInputWriter;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.IVersionedReasoner;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.SerializeException;
import org.eventb.theory.core.IGeneralRule;
import org.eventb.theory.internal.rbp.reasoners.input.IPRMetadata;
import org.eventb.theory.internal.rbp.reasoners.input.InferenceInput;
import org.eventb.theory.internal.rbp.reasoners.input.RewriteInput;
import org.eventb.theory.rbp.plugin.RbPPlugin;
import org.eventb.theory.rbp.rulebase.BaseManager;
import org.eventb.theory.rbp.rulebase.IPOContext;
import org.eventb.theory.rbp.rulebase.basis.IDeployedGiven;
import org.eventb.theory.rbp.rulebase.basis.IDeployedInferenceRule;
import org.eventb.theory.rbp.utils.ProverUtilities;

/**
 * <p>
 * An implementation of a manual inference reasoner for the rule-based prover.
 * </p>
 * <p>
 * <i>htson</i>: This has been re-implemented based on the original version 1.0
 * and the (now removed)
 * {org.eventb.theory.rbp.reasoning.ManualInfer} class.
 * </p>
 * 
 * @author maamria
 * @author htson - re-implemented as a context dependent reasoner.
 * @version 2.0.1
 * @see RewriteInput
 * @see RewriteRuleContent
 * @since 3.1.0
 */
public class ManualInferenceReasoner extends AbstractContextDependentReasoner
		implements IVersionedReasoner {

	public static final String REASONER_ID = RbPPlugin.PLUGIN_ID
			+ ".manualInferenceReasoner";

	@Override
	public String getReasonerID() {
		return REASONER_ID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IVersionedReasoner#getVersion()
	 */
	@Override
	public int getVersion() {
		return 2;
	}

	/**
	 * @precondition The reasoner input must be an {@link InferenceInput}.
	 */
	@Override
	public IReasonerOutput apply(IProverSequent sequent,
			IReasonerInput reasonerInput, IProofMonitor pm) {
		// PRECONDITION
		assert reasonerInput instanceof InferenceInput;

		// Get information from the reasoner input
		final InferenceInput input = (InferenceInput) reasonerInput;
		final Predicate hyp = input.getHypothesis();
		final boolean forward = input.isForward();
		final IPRMetadata prMetadata = input.getPRMetadata();
		final String theoryName = prMetadata.getTheoryName();
		final String projectName = prMetadata.getProjectName();
		final String ruleName = prMetadata.getRuleName();

		IPOContext context = ProverUtilities.getContext(sequent);
		if (context == null) {
			return ProverFactory.reasonerFailure(this, input,
					"Cannot determine the context of the sequent");
		}

		// Get the inference rule (given the meta-data) from the current context
		BaseManager manager = BaseManager.getDefault();
		IGeneralRule rule = manager.getInferenceRule(projectName, theoryName,
				ruleName, context);
		if (rule == null) {
			return ProverFactory.reasonerFailure(this, input,
					"Cannot find inference rule " + projectName + "::"
							+ theoryName + "::" + ruleName
							+ " within the given context for ");
		}

		if (forward) {
			return applyForward(sequent, input, rule, projectName, theoryName,
					ruleName, hyp);
		} else {
			return applyBackward(sequent, input, rule, projectName, theoryName,
					ruleName);
		}
	}

	/**
	 * @param ruleName 
	 * @param theoryName 
	 * @param projectName 
	 * @param rule 
	 * @param sequent 
	 * @param input 
	 * @return
	 */
	private IReasonerOutput applyForward(IProverSequent sequent,
			InferenceInput input, IGeneralRule rule, String projectName,
			String theoryName, String ruleName, Predicate hyp) {
	if (rule instanceof IDeployedInferenceRule) {
			// if expected forward application but rule is not suitable
			IDeployedInferenceRule deployedRule = (IDeployedInferenceRule) rule;
			if (!deployedRule.isSuitableForForwardReasoning()) {
				return ProverFactory.reasonerFailure(this, input,
						"Cannot use the inference rule " + projectName
								+ "::" + theoryName + "::" + ruleName
								+ " for forward reasoning");
			}
			
			if (!sequent.containsHypothesis(hyp)) 
				return ProverFactory.reasonerFailure(this, input,
						"Hypothesis " + hyp + " does not exist");
			
			List<IDeployedGiven> hypGivens = deployedRule.getHypGivens();
			if (hypGivens.size() == 0)
				return ProverFactory
						.reasonerFailure(this, input,
								"Inference rule for forward reasoning must have at least one given hypothesis");
			FormulaFactory factory = sequent.getFormulaFactory();
			ISpecialization specialization = factory.makeSpecialization();
			
			Set<Predicate> neededHyps = new HashSet<Predicate>();
			// Match the input hypotheses with the hypothesis givens of the
			// rule.
			specialization = matchHypothesisGivens(specialization, sequent, hyp,
					hypGivens, neededHyps);
	
			if (specialization == null) {
				return ProverFactory.reasonerFailure(this, input,
						"Fails to match input hypotheses with given hypotheses");
			}
	
			// Ensure that we can specialize the other non-hypothesis givens
			List<IDeployedGiven> givens = deployedRule.getGivens();
			for (IDeployedGiven given : givens) {
				Predicate pred = given.getGivenClause();
				if (!ProverUtilities.canBeSpecialized(specialization, pred)) {
					return ProverFactory.reasonerFailure(this, input,
							"Fails to specialize " + pred + " with "
									+ specialization);
				}
			}
	
			// Ensure that we can specialize the infer
			Predicate infer = deployedRule.getInfer().getInferClause();
			if (!ProverUtilities.canBeSpecialized(specialization, infer)) {
				return ProverFactory.reasonerFailure(this, input,
						"Fails to specialize " + infer + " with "
								+ specialization);
			}
	
			// We will have (#givens+2) antecedents (including a WD-subgoal).
			IAntecedent[] antecedents = ProverUtilities.forwardReasoning(sequent,
					deployedRule, specialization);
	
			String description = deployedRule.getDescription();
			return ProverFactory.makeProofRule(this, input, null, neededHyps,
					description + " (forward) with " + hyp, antecedents);
	
		} else { // Statically checked theory
			throw new UnsupportedOperationException(
					"Rule from Statically checked theory is unsupported");
		}
	
	}

	/**
	 * @param sequent
	 * @param input
	 * @param rule
	 * @param projectName
	 * @param theoryName
	 * @param ruleName
	 * @return
	 */
	private IReasonerOutput applyBackward(IProverSequent sequent,
			InferenceInput input, IGeneralRule rule, String projectName,
			String theoryName, String ruleName) {
		if (rule instanceof IDeployedInferenceRule) {
			// if expected backward application but rule is not suitable
			IDeployedInferenceRule deployedRule = (IDeployedInferenceRule) rule;
			if (!deployedRule.isSuitableForBackwardReasoning()) {
				return ProverFactory.reasonerFailure(this, input,
						"Cannot use the inference rule " + projectName
								+ "::" + theoryName + "::" + ruleName
								+ " for backward reasoning");
			}
			FormulaFactory factory = sequent.getFormulaFactory();
			ISpecialization specialization = factory.makeSpecialization();
			Predicate goal = sequent.goal();
			// Match the goal and the infer clause
			Predicate inferPredicate = deployedRule.getInfer()
					.getInferClause();
			specialization = Matcher.match(specialization, goal,
					inferPredicate);
			if (specialization == null) {
				return ProverFactory.reasonerFailure(this, input,
						"Cannot match the goal " + goal
								+ " with the infer predicate "
								+ inferPredicate);
			}

			List<IDeployedGiven> hypGivens = deployedRule.getHypGivens();
			Set<Predicate> neededHyps = new HashSet<Predicate>();
			// Match the input hypotheses with the hypothesis givens of the
			// rule.
			specialization = matchHypothesisGivens(specialization, sequent, null,
					hypGivens, neededHyps);
	
			if (specialization == null) {
				return ProverFactory.reasonerFailure(this, input,
						"Fails to match input hypotheses with given hypotheses");
			}

			IAntecedent[] antecedents = ProverUtilities.backwardReasoning(
					sequent, deployedRule, specialization);
			String description = deployedRule.getDescription();
			return ProverFactory.makeProofRule(this, input, goal, neededHyps,
					description + " (backward) on goal", antecedents);

		} else { // Statically checked theory
			throw new UnsupportedOperationException(
					"Rule from Statically checked theory is unsupported");
		}

	}

	/**
	 * @param sequent 
	 * @param neededHyps 
	 * @return
	 */
	private ISpecialization matchHypothesisGivens(
			ISpecialization specialization, IProverSequent sequent, Predicate hyp,
			List<IDeployedGiven> hypGivens, Set<Predicate> neededHyps) {
		Iterator<IDeployedGiven> iterator = hypGivens.iterator();
		for (int i = 0; i != hypGivens.size(); ++i) {
			IDeployedGiven given = iterator.next();
			Predicate pattern = given.getGivenClause();
			if (i == 0 && hyp != null) {
				// TODO Need to check if the formula is translatable.
				hyp = hyp.translate(specialization.getFactory());
				specialization = Matcher.match(specialization, hyp, pattern);
				neededHyps.add(hyp);
			} else {
				specialization = matchGivenHypothesis(specialization, pattern, sequent, neededHyps);
				if (specialization == null) {
					return null;
				}
			}
		}
		return specialization;
	}

	/**
	 * @param specialization
	 * @param givenClause
	 * @param sequent
	 * @param neededHyps 
	 * @return
	 */
	private ISpecialization matchGivenHypothesis(
			ISpecialization specialization, Predicate givenClause,
			IProverSequent sequent, Set<Predicate> neededHyps) {
		Iterator<Predicate> iterator = sequent.hypIterable().iterator();
		while (iterator.hasNext()) {
			Predicate formula = iterator.next();
			ISpecialization clone = specialization.clone();
			clone = Matcher.match(clone, formula, givenClause);
			if (clone != null) {
				neededHyps.add(formula);
				return clone;
			}
		}
		return null;
	}

	@Override
	public void serializeInput(IReasonerInput input, IReasonerInputWriter writer)
			throws SerializeException {
		// PRECONDITION
		assert input instanceof InferenceInput;
		((InferenceInput) input).serialize(writer);
	}

	@Override
	public IReasonerInput deserializeInput(IReasonerInputReader reader)
			throws SerializeException {
		return new InferenceInput(reader);
	}
}
