/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.tactics;

import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;

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
public class InferenceAutoTactic implements ITactic{

	@Override
	public Object apply(IProofTreeNode ptNode, IProofMonitor pm) {
		// TODO Auto-generated method stub
		return null;
	}

}
