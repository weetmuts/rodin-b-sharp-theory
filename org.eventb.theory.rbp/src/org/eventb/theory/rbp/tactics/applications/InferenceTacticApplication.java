/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.tactics.applications;

import org.eclipse.swt.graphics.Image;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.tactics.BasicTactics;
import org.eventb.theory.rbp.plugin.RbPPlugin;
import org.eventb.theory.rbp.reasoners.ManualInferenceReasoner;
import org.eventb.theory.rbp.reasoners.input.InferenceInput;
import org.eventb.ui.prover.IPredicateApplication;

/**
 * @author maamria
 *
 */
public class InferenceTacticApplication implements IPredicateApplication{

	private static final String TACTIC_ID = RbPPlugin.PLUGIN_ID + ".RbP1";
	
	private InferenceInput input;
	
	public InferenceTacticApplication(InferenceInput input){
		this.input = input;
	}
	
	@Override
	public ITactic getTactic(String[] inputs, String globalInput) {
		ManualInferenceReasoner reasoner = new ManualInferenceReasoner();
		return BasicTactics.reasonerTac(reasoner, input);
	}

	@Override
	public String getTacticID() {
		return TACTIC_ID;
	}

	@Override
	public Image getIcon() {
		return null;
	}

	@Override
	public String getTooltip() {
		return input.description;
	}

}
