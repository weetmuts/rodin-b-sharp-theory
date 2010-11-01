/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.reasoners;

import java.util.Set;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IPosition;
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
import org.eventb.theory.rbp.reasoners.input.DefinitionExpansionInput;
import org.eventb.theory.rbp.reasoning.DefinitionExpander;

/**
 * @author maamria
 *
 */
public class DefinitionExpansionReasoner  implements IReasoner{
	
	public static final String REASONER_ID = RbPPlugin.PLUGIN_ID + ".definitionExpansionReasoner";
	private static final String POSITION_KEY = "pos";
	private static final String OPERATOR_ID_KEY = "opId";
	private static final String OPERATOR_SYN_KEY = "opSyn";
	
	private DefinitionExpander expander;
	
	public DefinitionExpansionReasoner(){
		expander = new DefinitionExpander();
	}
	
	public IReasonerOutput apply(IProverSequent seq, IReasonerInput reasonerInput,
			IProofMonitor pm) {
		expander.setFormulaFactory(seq.getFormulaFactory());
		final DefinitionExpansionInput input = (DefinitionExpansionInput) reasonerInput;
		final Predicate hyp = input.pred;
		final IPosition position = input.position;
		final String operatorID = input.operatorID;
		final String syntax = input.syntax;
		String displayName = syntax + " expansion";
		
		final Predicate goal = seq.goal();
		if (hyp == null) {
			IAntecedent[] antecedents = getAntecedents(goal, position, true);
			if(antecedents == null){
				return ProverFactory.reasonerFailure(this, input, 
						"Operator definition of "+operatorID+" is not expandable at "+goal +" at position "+position);
			}
			return ProverFactory.makeProofRule(this, input, goal,
					displayName +" on goal", antecedents);
		} else {
			// Hypothesis rewriting
			if (!seq.containsHypothesis(hyp)) {
				return ProverFactory.reasonerFailure(this, input,
						"Nonexistent hypothesis: " + hyp);
			}
			IAntecedent[] antecedents = getAntecedents(hyp, position, false);
			if(antecedents == null){
				return ProverFactory.reasonerFailure(this, input, 
						"Operator definition of "+operatorID+" is not expandable at "+hyp +" at position "+position);
			}
			return ProverFactory.makeProofRule(this, input, null, hyp,displayName+ " on "+hyp, antecedents);
		}
	}

	protected IAntecedent[] getAntecedents(Predicate pred, IPosition position, boolean isGoal){
		return expander.getAntecedents(pred, position, isGoal);
	}
	
	public IReasonerInput deserializeInput(IReasonerInputReader reader)
			throws SerializeException {
		final String posString = reader.getString(POSITION_KEY);
		final String operatorId = reader.getString(OPERATOR_ID_KEY);
		final String syntax = reader.getString(OPERATOR_SYN_KEY);
		final IPosition position = FormulaFactory.makePosition(posString);
		
		Set<Predicate> neededHyps = reader.getNeededHyps();

		final int length = neededHyps.size();
		if (length == 0) {
			// Goal rewriting
			return new DefinitionExpansionInput(operatorId, syntax,null, position);
		}
		// Hypothesis rewriting
		if (length != 1) {
			throw new SerializeException(new IllegalStateException(
					"Expected exactly one needed hypothesis!"));
		}
		Predicate pred = null;
		for (Predicate hyp : neededHyps) {
			pred = hyp;
		}
		return new DefinitionExpansionInput(operatorId,syntax,pred, position);
	}
	
	public String getReasonerID() {
		return REASONER_ID;
	}

	public void serializeInput(IReasonerInput input, IReasonerInputWriter writer)
			throws SerializeException {
		writer.putString(POSITION_KEY, ((DefinitionExpansionInput) input).position.toString());
		writer.putString(OPERATOR_ID_KEY , ((DefinitionExpansionInput) input).operatorID);
		writer.putString(OPERATOR_SYN_KEY , ((DefinitionExpansionInput) input).syntax);
	}

}
