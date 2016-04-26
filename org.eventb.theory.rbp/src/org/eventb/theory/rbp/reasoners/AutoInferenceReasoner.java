/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.reasoners;

import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerInputReader;
import org.eventb.core.seqprover.IReasonerInputWriter;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.SerializeException;
import org.eventb.core.seqprover.reasonerInputs.EmptyInput;
import org.eventb.theory.rbp.plugin.RbPPlugin;
import org.eventb.theory.rbp.reasoning.AutoInferer;
import org.eventb.theory.rbp.rulebase.IPOContext;

/**
 * @author maamria
 * 
 */
public class AutoInferenceReasoner extends AbstractContextDependentReasoner
		implements IReasoner {

	private static final String REASONER_ID = RbPPlugin.PLUGIN_ID + ".autoInferenceReasoner";
	
	private static final String DISPLAY_NAME = "RbP1";
	
	@Override
	public String getReasonerID() {
		return REASONER_ID;
	}

	@Override
	public IReasonerOutput apply(IProverSequent seq, IReasonerInput input,
			IProofMonitor pm) {
		IPOContext context = getContext(seq);
		AutoInferer autoInferer = new AutoInferer(context);
		IAntecedent[] antecedents = autoInferer.applyInferenceRules(seq);
		if (antecedents == null) {
			return ProverFactory.reasonerFailure(this, input, "Inference "
					+ getReasonerID() + " is not applicable for "
					+ seq.goal() + ".");
		}
		// Generate the successful reasoner output
		return ProverFactory.makeProofRule(this, input, seq.goal(),
				getDisplayName(), antecedents);
	}
	
	protected String getDisplayName() {
		return DISPLAY_NAME;
	}

	@Override
	public void serializeInput(IReasonerInput input, IReasonerInputWriter writer)
			throws SerializeException {
		// Do nothing
	}

	@Override
	public IReasonerInput deserializeInput(IReasonerInputReader reader)
			throws SerializeException {
		return new EmptyInput();
	}
}
