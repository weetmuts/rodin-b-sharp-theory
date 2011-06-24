/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.reasoners;

import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerInputReader;
import org.eventb.core.seqprover.IReasonerInputWriter;
import org.eventb.core.seqprover.ISignatureReasoner;
import org.eventb.core.seqprover.SerializeException;
import org.eventb.theory.rbp.reasoners.input.ContextualInput;
import org.eventb.theory.rbp.rulebase.IPOContext;

/**
 * Basic implementation for proof obligations context aware reasoner.
 * @author maamria
 * @since 1.0
 *
 */
public abstract class ContextAwareReasoner implements ISignatureReasoner{
	
	protected static final String CONTEXT_INPUT_KEY = "poContext";
	
	@Override
	public void serializeInput(IReasonerInput input, IReasonerInputWriter writer)
			throws SerializeException {
		((ContextualInput) input).serialise(writer, CONTEXT_INPUT_KEY);
	}
	
	@Override
	public IReasonerInput deserializeInput(IReasonerInputReader reader)
			throws SerializeException {
		final String contextStr = reader.getString(CONTEXT_INPUT_KEY);
		IPOContext context = ContextualInput.deserialise(contextStr);
		if (context == null){
			throw new SerializeException(new IllegalStateException(
				"PO contextual information cannot be retrieved!"));
		}
		return new ContextualInput(context);
	}
}
