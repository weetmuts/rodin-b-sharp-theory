/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.pm.assoc;

import org.eventb.core.ast.Formula;
import org.eventb.core.pm.IBinding;

/**
 * A basic implementation of a pure (non-commutative) matching problem.
 * @author maamria
 * 
 * TODO FINISH THIS
 *
 */
public abstract class AProblem<F extends Formula<F>> extends AssociativityProblem<F>{

	public AProblem(int tag, F[] formulae, F[] patterns, IBinding existingBinding) {
		super(tag, formulae, patterns, existingBinding);
	}
	
	@Override
	public IBinding solve(boolean acceptPartialMatch) {
		if (!isSolvable) {
			return null;
		}
		return null;
	}
	
	/**
	 * Explores the search space to work out a solution for the matching problem.
	 * @param patternIndex the current pattern index
	 * @param matchStack the match stack
	 * @return whether exploring has succeeded
	 */
	protected boolean explore(int patternIndex, ACMatchStack<F> matchStack){
		return false;
	}
}
