/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.reasoners.input;

import org.eventb.core.IEventBRoot;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerInputWriter;
import org.eventb.core.seqprover.SerializeException;
import org.eventb.core.seqprover.proofBuilder.ReplayHints;
import org.eventb.theory.rbp.rulebase.IPOContext;
import org.eventb.theory.rbp.rulebase.basis.POContext;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinCore;

/**
 * 
 * An implementation of a reasoner input that provides contextual
 * information about the proof obligation in question.
 * 
 * @author maamria
 * @since 1.0
 *
 */
public class ContextualInput implements IReasonerInput{

	public IPOContext context;
	
	/**
	 * Create a contextual reasoner input.
	 * @param context the proof obligation context
	 */
	public ContextualInput(IPOContext context){
		this.context = context;
	}
	
	@Override
	public boolean hasError() {
		return false;
	}

	@Override
	public String getError() {
		return null;
	}

	@Override
	public void applyHints(ReplayHints renaming) {
		// nothing to do
	}
	
	public void serialise(IReasonerInputWriter writer, String key) throws SerializeException{
		writer.putString(key, context.toString());
	}
	
	public static IPOContext deserialise(String serialisedForm){
		IRodinElement element = RodinCore.valueOf(serialisedForm);
		if (element instanceof IEventBRoot){
			return new POContext((IEventBRoot) element);
		}
		return null;
	}
}
