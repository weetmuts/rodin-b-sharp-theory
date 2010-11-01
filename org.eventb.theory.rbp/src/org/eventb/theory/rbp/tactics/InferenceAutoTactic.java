/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.tactics;

import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.AutoTactics;
import org.eventb.core.seqprover.tactics.BasicTactics;
import org.eventb.theory.rbp.reasoners.AutoInferenceReasoner;

/**
 * The automatic tactic for applying inference rules.
 * 
 * <p> At the moment, only rules that can be applied in a backward fashion can be used automatically.
 * 
 * @since 1.0
 * 
 * @author maamria
 *
 */
public class InferenceAutoTactic extends AutoTactics.AbsractLazilyConstrTactic{

	@Override
	protected ITactic getSingInstance() {
		return BasicTactics.reasonerTac(new AutoInferenceReasoner(), null);
	}

}
