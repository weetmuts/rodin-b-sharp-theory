/*******************************************************************************
 * Copyright (c) 2016 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University of Southampton - initial API and implementation
 *******************************************************************************/

package org.eventb.theory.rbp.reasoners;

import org.eventb.core.IEventBRoot;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.theory.rbp.rulebase.IPOContext;
import org.eventb.theory.rbp.rulebase.basis.POContext;
import org.rodinp.core.IInternalElement;

/**
 * <p>
 * Common abstract implementation for context dependent reasoners. 
 * </p>
 *
 * @author htson
 * @version 0.1
 * @see ManualInferenceReasoner
 * @see ManualRewriteReasoner
 * @since 4.0
 */
public class AbstractContextDependentReasoner {

	/**
	 * Method for getting the PO Context of a prover sequent.
	 * 
	 * @param sequent
	 *            the input prover sequent.
	 * @return the PO context corresponding to the input sequent or
	 *         <code>null</code> if the sequent does not have any context.
	 */
	protected IPOContext getContext(IProverSequent sequent) {
		Object origin = sequent.getOrigin();
		if (origin instanceof IInternalElement) {
			IInternalElement root = ((IInternalElement) origin).getRoot();
			if (root instanceof IEventBRoot) {
				return new POContext((IEventBRoot) root);
			}
		}
		return null;
	}

}
