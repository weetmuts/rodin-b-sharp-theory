/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.reasoners;

import org.eventb.core.seqprover.ISignatureReasoner;
import org.eventb.theory.rbp.rulebase.IPOContext;

/**
 * Common protocol for proof obligations context aware reasoner.
 * @author maamria
 * @since 1.0
 *
 */
public interface IContextAwareReasoner extends ISignatureReasoner{

	/**
	 * Sets the context in which this reasoner is called. 
	 * <p>The context includes the trace of the proof obligation on which 
	 * this reasoner is invoked.
	 * @param context the proof obligation context
	 */
	public void setContext(IPOContext context);
	
}
