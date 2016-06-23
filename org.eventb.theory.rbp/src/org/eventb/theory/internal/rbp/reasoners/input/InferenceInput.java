/*******************************************************************************
 * Copyright (c) 2010,2016 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.internal.rbp.reasoners.input;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IReasonerInputReader;
import org.eventb.core.seqprover.IReasonerInputWriter;
import org.eventb.core.seqprover.SerializeException;
import org.eventb.core.seqprover.proofBuilder.ReplayHints;

/**
 * <p>
 * An implementation of an inference reasoner input.
 * </p>
 * 
 * @author maamria
 * @author htson: Re-implemented based on {@link PRMetadataReasonerInput}.
 * @version 2.0
 * @since 1.0
 */
public class InferenceInput extends PRMetadataReasonerInput {

	private boolean forward;
	private Predicate[] hyps;
	
	private static final String FORWARD_VAL = "forward";
	private static final String BACKWARD_VAL = "backward";
	private static final String FORWARD_KEY = "isForward";
	private static final String HYPS_KEY = "hyps";
	
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
	public InferenceInput(IPRMetadata prMetadata, Predicate[] hyps, boolean forward) {
		super(prMetadata);

		assert hyps != null;
		
		this.forward = forward;
		this.hyps = hyps;
	}

	/**
	 * @param reader
	 * @throws SerializeException
	 */
	public InferenceInput(IReasonerInputReader reader)
			throws SerializeException {
		super(reader);
		this.forward = FORWARD_VAL.equals(reader.getString(FORWARD_KEY));
		if (forward) {
			hyps = reader.getPredicates(HYPS_KEY);
			if (hyps == null) {
				throw new SecurityException("No inference hypotheses stored");
			}
		} else {
			hyps = new Predicate[0];
		}
	}

	@Override
	public void applyHints(ReplayHints renaming) {
		Predicate[] translatedHyps = new Predicate[hyps.length];
		for (int i = 0; i != hyps.length; ++i) {
			translatedHyps[i] = renaming.applyHints(hyps[i]);
		}
		hyps = translatedHyps;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IReasonerInput#getError()
	 */
	@Override
	public String getError() {
		return null;
	}

	/**
	 * @return
	 */
	public boolean isForward() {
		return forward;
	}

	/**
	 * @param writer 
	 * @throws SerializeException 
	 * 
	 */
	public void serialize(IReasonerInputWriter writer) throws SerializeException {
		super.serialise(writer);
		writer.putString(FORWARD_KEY, forward ? FORWARD_VAL : BACKWARD_VAL);
		if (forward)
			writer.putPredicates(HYPS_KEY, hyps);
	}

	/**
	 * @return
	 */
	public Predicate[] getHyps() {
		return hyps;
	}

}
