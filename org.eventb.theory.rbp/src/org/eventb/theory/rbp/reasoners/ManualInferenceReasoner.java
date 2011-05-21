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
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerInputReader;
import org.eventb.core.seqprover.IReasonerInputWriter;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.SerializeException;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.theory.rbp.plugin.RbPPlugin;
import org.eventb.theory.rbp.reasoners.input.InferenceInput;
import org.eventb.theory.rbp.reasoning.ManualInferer;
import org.eventb.theory.rbp.rulebase.IPOContext;

/**
 * @author maamria
 *
 */
public class ManualInferenceReasoner implements IReasoner{

	private static final String FORWARD_VAL = "forward";
	private static final String BACKWARD_VAL = "backward";

	public static final String REASONER_ID = RbPPlugin.PLUGIN_ID + ".manualInferenceReasoner";
	
	private static final String DESC_KEY = "ruleDesc";
	private static final String FORWARD_KEY = "pos";
	private static final String RULE_KEY = "rewRule";
	private static final String THEORY_KEY = "theory";
	
	private ManualInferer inferer;
	
	public ManualInferenceReasoner(IPOContext context){
		inferer = new ManualInferer(context);
	}
	
	@Override
	public String getReasonerID() {
		// TODO Auto-generated method stub
		return REASONER_ID;
	}

	@Override
	public void serializeInput(IReasonerInput input, IReasonerInputWriter writer)
			throws SerializeException {
		writer.putString(FORWARD_KEY, ((InferenceInput) input).forward ? FORWARD_VAL : BACKWARD_VAL);
		writer.putString(THEORY_KEY, ((InferenceInput) input).theoryName);
		writer.putString(RULE_KEY, ((InferenceInput) input).ruleName);
		writer.putString(DESC_KEY, ((InferenceInput) input).ruleDesc);
		
	}

	@Override
	public IReasonerInput deserializeInput(IReasonerInputReader reader)
			throws SerializeException {
		final String forString = reader.getString(FORWARD_KEY);
		final String theoryString = reader.getString(THEORY_KEY);
		final String ruleString = reader.getString(RULE_KEY);
		final String ruleDesc = reader.getString(DESC_KEY);
		final boolean forward = forString.equals(FORWARD_VAL);
		
		Set<Predicate> neededHyps = reader.getNeededHyps();

		final int length = neededHyps.size();
		if (length == 0) {
			// backward
			return new InferenceInput(theoryString, ruleString, ruleDesc, null, forward);
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
		return new InferenceInput(theoryString, ruleString, ruleDesc,pred, forward);
	}

	@Override
	public IReasonerOutput apply(IProverSequent sequent, IReasonerInput reasonerInput,
			IProofMonitor pm) {
		inferer.setFormulaFactory(sequent.getFormulaFactory());
		final InferenceInput input = (InferenceInput) reasonerInput;
		final Predicate pred = input.pred;
		final boolean forward = input.forward;
		final String theoryName = input.theoryName;
		final String ruleName = input.ruleName;
		final String displayName = input.ruleDesc;
		
		final Predicate goal = sequent.goal();
		if (pred == null) {
			IAntecedent[] antecedents = getAntecedents(sequent, pred, forward, theoryName, ruleName);
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
			IAntecedent[] antecedents = getAntecedents(sequent, pred, forward, theoryName, ruleName);
			if(antecedents == null){
				return ProverFactory.reasonerFailure(this, input, 
						"Rule "+ruleName+" is not applicable to "+pred +".");
			}
			return ProverFactory.makeProofRule(this, input, null, pred,displayName+ " on "+pred, antecedents);
		}
	}
	
	protected IAntecedent[] getAntecedents(IProverSequent sequent, Predicate pred, boolean forward, String theoryName, String ruleName){
		return inferer.getAntecedents(sequent, pred, forward, theoryName, ruleName);
	}

}
