/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.tactics;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pm.IProofState;
import org.eventb.core.pm.IUserSupport;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.ui.prover.IProofCommand;
import org.rodinp.core.RodinDBException;

public class InstantiateTheoremTactic implements IProofCommand {

	@Override
	public boolean isApplicable(IUserSupport us, Predicate hyp, String input) {
		final IProofState currentPO = us.getCurrentPO();
		if (currentPO == null)
			return false;
		final IProofTreeNode node = currentPO.getCurrentNode();
		return (node != null) && node.isOpen();
	}

	@Override
	public void apply(IUserSupport us, Predicate hyp, String[] inputs,
			IProgressMonitor monitor) throws RodinDBException {
		// nothing to do
	}

}
