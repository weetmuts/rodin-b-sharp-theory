/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.reasoners;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.reasonerInputs.EmptyInputReasoner;
import org.eventb.theory.rbp.plugin.RbPPlugin;
import org.eventb.theory.rbp.reasoning.AutoInferer;
import org.eventb.theory.rbp.rulebase.IPOContext;
import org.eventb.theory.rbp.utils.ProverUtilities;

/**
 * @author maamria
 * 
 */
public class AutoInferenceReasoner extends EmptyInputReasoner implements IContextAwareReasoner {

	private static final String DISPLAY_NAME = "RbP1";
	public static List<String> usedTheories = new ArrayList<String>();
	private static final String REASONER_ID = RbPPlugin.PLUGIN_ID + ".autoInferenceReasoner";

	private AutoInferer autoInferer;
	
	public void setContext(IPOContext context){
		autoInferer = new AutoInferer(context);
	}
	
	@Override
	public String getReasonerID() {
		return REASONER_ID;
	}

	@Override
	public IReasonerOutput apply(IProverSequent seq, IReasonerInput input,
			IProofMonitor pm) {
		IAntecedent[] antecedents = autoInferer.applyInferenceRules(seq);
		if (antecedents == null) {
			return ProverFactory.reasonerFailure(this, input, "Inference "
					+ getReasonerID() + " is not applicable for "
					+ seq.goal() + ".");
		}
		// Generate the successful reasoner output
		return ProverFactory.makeProofRule(this, null, seq.goal(), getDisplayName(), antecedents);
	}
	
	protected String getDisplayName() {
		String toDisplay = DISPLAY_NAME + ProverUtilities.printListedItems(usedTheories);
		// clear the list of used theories now
		usedTheories.clear();
		return toDisplay;
	}

	@Override
	public String getSignature() {
		return "";
	}

}
