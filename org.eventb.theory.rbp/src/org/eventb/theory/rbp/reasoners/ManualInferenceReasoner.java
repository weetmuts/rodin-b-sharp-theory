/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.reasoners;

import java.util.Set;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerInputReader;
import org.eventb.core.seqprover.IReasonerInputWriter;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.SerializeException;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.theory.rbp.plugin.RbPPlugin;
import org.eventb.theory.rbp.reasoners.input.ContextualInput;
import org.eventb.theory.rbp.reasoners.input.InferenceInput;
import org.eventb.theory.rbp.reasoning.ManualInferer;
import org.eventb.theory.rbp.rulebase.IPOContext;

/**
 * @author maamria
 *
 */
public class ManualInferenceReasoner extends ContextAwareReasoner{

	public static final String REASONER_ID = RbPPlugin.PLUGIN_ID + ".manualInferenceReasoner";
	
	private static final String FORWARD_VAL = "forward";
	private static final String BACKWARD_VAL = "backward";
	private static final String DESC_KEY = "ruleDesc";
	private static final String FORWARD_KEY = "isForward";
	private static final String RULE_KEY = "inferenceRule";
	private static final String THEORY_KEY = "theory";
	private static final String PROJECT_KEY = "project";
	
	@Override
	public String getReasonerID() {
		return REASONER_ID;
	}
	
	@Override
	public IReasonerOutput apply(IProverSequent sequent, IReasonerInput reasonerInput,
			IProofMonitor pm) {
		final InferenceInput input = (InferenceInput) reasonerInput;
		final Predicate pred = input.predicate;
		final boolean forward = input.forward;
		final String theoryName = input.theoryName;
		final String projectName = input.projectName;
		final String ruleName = input.ruleName;
		final String displayName = input.description;
		final IPOContext context = input.context;
		
		ManualInferer inferer = new ManualInferer(context);
		
		final Predicate goal = sequent.goal();
		if (pred == null) {
			IAntecedent[] antecedents = inferer.getAntecedents(sequent, pred, forward, projectName, theoryName, ruleName);
			if(antecedents == null){
				return ProverFactory.reasonerFailure(this, input, 
						"Rule "+ruleName+" is not applicable to "+goal +".");
			}
			return ProverFactory.makeProofRule(this, input, goal,
					displayName +" on goal", antecedents);
		} else {
			// forward
			if (!sequent.containsHypothesis(pred)) {
				return ProverFactory.reasonerFailure(this, input,
						"Nonexistent hypothesis: " + pred);
			}
			IAntecedent[] antecedents = inferer.getAntecedents(sequent, pred, forward, projectName, theoryName, ruleName);
			if(antecedents == null){
				return ProverFactory.reasonerFailure(this, input, 
						"Rule "+ruleName+" is not applicable to "+pred +".");
			}
			return ProverFactory.makeProofRule(this, input, null, pred,displayName+ " on "+pred, antecedents);
		}
	}

	@Override
	public void serializeInput(IReasonerInput input, IReasonerInputWriter writer)
			throws SerializeException {
		super.serializeInput(input, writer);
		writer.putString(FORWARD_KEY, ((InferenceInput) input).forward ? FORWARD_VAL : BACKWARD_VAL);
		writer.putString(THEORY_KEY, ((InferenceInput) input).theoryName);
		writer.putString(RULE_KEY, ((InferenceInput) input).ruleName);
		writer.putString(DESC_KEY, ((InferenceInput) input).description);
		writer.putString(PROJECT_KEY, ((InferenceInput) input).projectName);
		writer.putString(CONTEXT_INPUT_KEY, ((InferenceInput) input).context.toString());
	}

	@Override
	public IReasonerInput deserializeInput(IReasonerInputReader reader)
			throws SerializeException {
		final String forString = reader.getString(FORWARD_KEY);
		final String theoryString = reader.getString(THEORY_KEY);
		final String projectString = reader.getString(PROJECT_KEY);
		final String ruleString = reader.getString(RULE_KEY);
		final String ruleDesc = reader.getString(DESC_KEY);
		final String poContextStr = reader.getString(CONTEXT_INPUT_KEY);
		final IPOContext context = ContextualInput.deserialise(poContextStr);
		if(context == null){
			throw new SerializeException(new IllegalStateException(
				"PO contextual information cannot be retrieved!"));
		}
		final boolean forward = forString.equals(FORWARD_VAL);
		Set<Predicate> neededHyps = reader.getNeededHyps();
		final int length = neededHyps.size();
		if (length == 0) {
			// backward
			return new InferenceInput(projectString, theoryString, ruleString, ruleDesc, null, forward, context);
		}
		// forward
		if (length != 1) {
			throw new SerializeException(new IllegalStateException(
					"Expected exactly one needed hypothesis!"));
		}
		Predicate pred = null;
		for (Predicate hyp : neededHyps) {
			pred = hyp;
		}
		return new InferenceInput(projectString, theoryString, ruleString, ruleDesc,pred, forward, context);
	}

	@Override
	public String getSignature() {
		return REASONER_ID;
	}
}
