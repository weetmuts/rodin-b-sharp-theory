/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.reasoners.input;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.proofBuilder.ReplayHints;
import org.eventb.theory.rbp.rulebase.IPOContext;

/**
 * An implementation of an inference reasoner input.
 * 
 * @since 1.0
 * 
 * @author maamria
 * 
 */
public class InferenceInput extends ContextualInput {

	public boolean forward;
	public String description;
	public String ruleName;
	public String theoryName;
	public String projectName;
	public Predicate predicate;

	/**
	 * Constructs an input with the given parameters.
	 * 
	 * @param theoryName
	 *            the parent theory
	 * @param ruleName
	 *            the name of the rule to apply
	 * @param ruleDesc
	 *            the description to display if rule applied successfully
	 * @param pred
	 *            the predicate
	 * @param forward
	 *            whether the rule is for forward reasoning
	 * @param context
	 *            the PO context
	 */
	public InferenceInput(String projectName,String theoryName, String ruleName, String ruleDesc, Predicate predicate, boolean forward, IPOContext context) {
		super(context);
		this.projectName = projectName;
		this.forward = forward;
		this.description = ruleDesc;
		this.ruleName = ruleName;
		this.theoryName = theoryName;
		this.predicate = predicate;
	}

	@Override
	public void applyHints(ReplayHints renaming) {
		if (predicate != null) {
			predicate = renaming.applyHints(predicate);
		}
	}
}
