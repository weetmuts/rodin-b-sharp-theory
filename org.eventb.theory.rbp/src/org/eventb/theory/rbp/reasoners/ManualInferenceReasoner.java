/*******************************************************************************
 * Copyright (c) 2010,2016 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.reasoners;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
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
 * @author htson
 * @version 2.0
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
		return 0;
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
		// final Predicate pred = input.predicate;
		final Predicate[] hyps = input.getHyps();
		final boolean forward = input.isForward();
		final IPRMetadata prMetadata = input.getPRMetadata();
		final String theoryName = prMetadata.getTheoryName();
		final String projectName = prMetadata.getProjectName();
		final String ruleName = prMetadata.getRuleName();
		// final String displayName = prMetadata.description;
		// final IPOContext context = input.context;
		// final IBinding binding = input.binding;

		IPOContext context = getContext(sequent);
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

		// Check if all the input hypotheses are there
		for (Predicate hyp : hyps) {
			if (!sequent.containsHypothesis(hyp)) {
				return ProverFactory.reasonerFailure(this, input,
						"Cannot find hypothesis " + hyp);
			}
		}

		final Predicate goal = sequent.goal();
		if (forward) {
			if (rule instanceof IDeployedInferenceRule) {
				// if expected forward application but rule is not suitable
				IDeployedInferenceRule deployedRule = (IDeployedInferenceRule) rule;
				if (!deployedRule.isSuitableForForwardReasoning()) {
					return ProverFactory.reasonerFailure(this, input,
							"Cannot use the inference rule " + projectName
									+ "::" + theoryName + "::" + ruleName
									+ " for forward reasoning");
				}
				List<IDeployedGiven> hypGivens = deployedRule.getHypGivens();
				if (hypGivens.size() != hyps.length) {
					return ProverFactory.reasonerFailure(this, input,
							"Incorrect number of given hypotheses");
				}

				FormulaFactory factory = sequent.getFormulaFactory();
				ISpecialization specialization = factory.makeSpecialization();

				// Match the input hypotheses with the hypothesis givens of the
				// rule.
				specialization = matchHypothesisGivens(specialization, hyps,
						hypGivens);
				if (specialization == null) {
					return ProverFactory.reasonerFailure(this, input,
							"Fails to match input hypotheses " + hyps
									+ " with hypothesis givens " + hypGivens);
				}

				// Ensure that we can specialize the other non-hypothesis givens
				List<IDeployedGiven> givens = deployedRule.getGivens();
				for (IDeployedGiven given : givens) {
					Predicate pred = given.getGivenClause();
					if (!canBeSpecialized(specialization, pred)) {
						return ProverFactory.reasonerFailure(this, input,
								"Fails to specialize " + pred + " with "
										+ specialization);
					}
				}

				// Ensure that we can specialize the infer
				Predicate infer = deployedRule.getInfer().getInferClause();
				if (!canBeSpecialized(specialization, infer)) {
					return ProverFactory.reasonerFailure(this, input,
							"Fails to specialize " + infer + " with "
									+ specialization);
				}

				// We will have (#givens+1) antecedents.
				IAntecedent[] antecedents = forwardReason(sequent,
						deployedRule, specialization);

				Set<Predicate> neededHyps = new HashSet<Predicate>(
						Arrays.asList(hyps));
				String description = deployedRule.getDescription();
				return ProverFactory.makeProofRule(this, input, null, neededHyps,
						description + " (forward)", antecedents);

			} else { // Statically checked theory
				throw new UnsupportedOperationException(
						"Rule from Statically checked theory is unsupported");
			}

		} else { // backward
			if (rule instanceof IDeployedInferenceRule) {
				// if expected backward application but rule is not suitable
				IDeployedInferenceRule deployedRule = (IDeployedInferenceRule) rule;
				if (!deployedRule.isSuitableForBackwardReasoning()) {
					return ProverFactory.reasonerFailure(this, input,
							"Cannot use the inference rule " + projectName
									+ "::" + theoryName + "::" + ruleName
									+ " for backward reasoning");
				}
				List<IDeployedGiven> hypGivens = deployedRule.getHypGivens();
				if (hypGivens.size() != hyps.length) {
					return ProverFactory.reasonerFailure(this, input,
							"Incorrect number of given hypotheses");
				}

				FormulaFactory factory = sequent.getFormulaFactory();
				ISpecialization specialization = factory.makeSpecialization();

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

				// Match the input hypotheses and the hypothesis givens.
				specialization = matchHypothesisGivens(specialization, hyps,
						hypGivens);
				if (specialization == null) {
					return ProverFactory.reasonerFailure(this, input,
							"Fails to match input hypotheses " + hyps
									+ " with hypothesis givens " + hypGivens);
				}

				IAntecedent[] antecedents = backwardReason(sequent,
						deployedRule, specialization);
				String description = deployedRule.getDescription();
				return ProverFactory.makeProofRule(this, input, goal,
						description + " (backward) on goal", antecedents);

			} else { // Statically checked theory
				throw new UnsupportedOperationException(
						"Rule from Statically checked theory is unsupported");
			}

		}
	}

	/**
	 * @return
	 */
	private ISpecialization matchHypothesisGivens(
			ISpecialization specialization, Predicate[] hyps,
			List<IDeployedGiven> hypGivens) {
		Iterator<IDeployedGiven> iterator = hypGivens.iterator();
		for (int i = 0; i != hyps.length; ++i) {
			IDeployedGiven given = iterator.next();
			Predicate formula = hyps[i];
			// Need to translate the formula to have the same factory as the specialisation.
			// TODO Need to check if the formula is translatable.
			formula = formula.translate(specialization.getFactory());
			Predicate pattern = given.getGivenClause();
			specialization = Matcher.match(specialization, formula, pattern);
			if (specialization == null) {
				return null;
			}
		}
		return specialization;
	}

	/**
	 * @param specialization
	 * @param pred
	 * @return
	 */
	private boolean canBeSpecialized(ISpecialization specialization,
			Predicate pred) {
		for (FreeIdentifier identifier : pred.getFreeIdentifiers()) {
			if (specialization.get(identifier) == null)
				return false;
		}
		return true;
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

	private IAntecedent[] forwardReason(IProverSequent sequent,
			IDeployedInferenceRule rule, ISpecialization specialization) {
		Set<IAntecedent> antecedents = new LinkedHashSet<IAntecedent>();
		List<IDeployedGiven> givens = rule.getGivens();
		for (IDeployedGiven given : givens) {
			Predicate givenPred = given.getGivenClause();
			Predicate subGoal = givenPred.specialize(specialization);
			antecedents.add(ProverFactory.makeAntecedent(subGoal));

			// // add the well-definedness conditions where appropriate
			// Map<FreeIdentifier, Expression> expressionMappings =
			// binding.getExpressionMappings();
			// for (FreeIdentifier identifier : expressionMappings.keySet()){
			// Expression mappedExpression = expressionMappings.get(identifier);
			// Predicate wdPredicate = mappedExpression.getWDPredicate();
			// if (!wdPredicate.equals(ProverUtilities.BTRUE)){
			// if (!sequent.containsHypothesis(wdPredicate))
			// antecedents.add(ProverFactory.makeAntecedent(wdPredicate));
			// }
			// }
		}

		Predicate inferClause = rule.getInfer().getInferClause();
		Predicate newHyp = inferClause.specialize(specialization);
		// add the antecedent corresponding to the infer clause
		IAntecedent mainAntecedent = ProverFactory.makeAntecedent(null,
				Collections.singleton(newHyp), ProverFactory
						.makeSelectHypAction(Collections.singleton(newHyp)));
		antecedents.add(mainAntecedent);
		// // add the well-definedness conditions where appropriate
		// Map<FreeIdentifier, Expression> mappedIdents =
		// finalBinding.getExpressionMappings();
		// for (FreeIdentifier identifier : mappedIdents.keySet()){
		// Expression mappedExpression = mappedIdents.get(identifier);
		// Predicate wdPredicate = mappedExpression.getWDPredicate();
		// if (!wdPredicate.equals(ProverUtilities.BTRUE)){
		// if (!sequent.containsHypothesis(wdPredicate))
		// antecedents.add(ProverFactory.makeAntecedent(wdPredicate));
		// }
		// }

		return antecedents.toArray(new IAntecedent[antecedents.size()]);

	}

	private IAntecedent[] backwardReason(IProverSequent sequent,
			IDeployedInferenceRule rule, ISpecialization specialization) {
		Set<IAntecedent> antecedents = new LinkedHashSet<IAntecedent>();
		List<IDeployedGiven> givens = rule.getGivens();
		for (IDeployedGiven given : givens) {
			Predicate givenPred = given.getGivenClause();
			Predicate subGoal = givenPred.specialize(specialization);
			antecedents.add(ProverFactory.makeAntecedent(subGoal));

			// // add the well-definedness conditions where appropriate
			// Map<FreeIdentifier, Expression> expressionMappings =
			// binding.getExpressionMappings();
			// for (FreeIdentifier identifier : expressionMappings.keySet()){
			// Expression mappedExpression = expressionMappings.get(identifier);
			// Predicate wdPredicate = mappedExpression.getWDPredicate();
			// if (!wdPredicate.equals(ProverUtilities.BTRUE)){
			// if (!sequent.containsHypothesis(wdPredicate))
			// antecedents.add(ProverFactory.makeAntecedent(wdPredicate));
			// }
			// }
		}
		return antecedents.toArray(new IAntecedent[antecedents.size()]);
	}
}
