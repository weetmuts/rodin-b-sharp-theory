/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.reasoners.input;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.proofBuilder.ReplayHints;

/**
 * An implementation of an inference reasoner input.
 * @since 1.0
 * 
 * @author maamria
 *
 */
public class InferenceInput implements IReasonerInput{

	public boolean forward;
	public Predicate pred;
	public String ruleDesc;
	public String ruleName;
	public String theoryName;
	
	/**
	 * Constructs an input with the given parameters.
	 * @param theoryName the parent theory
	 * @param ruleName the name of the rule to apply
	 * @param ruleDesc the description to display if rule applied successfully
	 * @param pred 
	 * @param forward whether the rule is for forward reasoning
	 */
	public InferenceInput(String theoryName, String ruleName, String ruleDesc,
			Predicate pred, boolean forward){
		this.forward = forward;
		this.pred = pred;
		this.ruleDesc = ruleDesc;
		this.ruleName = ruleName;
		this.theoryName = theoryName;
	}
	
	@Override
	public boolean hasError() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getError() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void applyHints(ReplayHints renaming) {
		if(pred !=null){
			renaming.applyHints(pred);
		}
	}

}
