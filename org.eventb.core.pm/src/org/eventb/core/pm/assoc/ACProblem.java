/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.pm.assoc;

import java.util.List;

import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;

/**
 * 
 * @author maamria
 *
 */
public final class ACProblem<F extends Formula<F>> extends AssociativityProblem<F> {

	public ACProblem(int tag, F[] formulae, F[] patterns, FormulaFactory factory) {
		super(tag, formulae, patterns, factory);
	}

	protected boolean explore(int patternIndex, MatchStack<F> matchStack){
		System.out.println(patternIndex);
		if (patternIndex < 1){
			// we backtracked too much
			return false;
		}
		if (patternIndex == indexedPatterns.size()){
			// finished all of them
			return true;
		}
		MatchEntry<F> matchEntry = searchSpace.get(patternIndex);
		List<Match<F>> matchesList = matchEntry.getMatches();
		for (Match<F> match : matchesList){
			if (matchStack.isMatchAcceptable(match)){
				matchStack.push(match);
				return explore(patternIndex+1, matchStack);
			}
		}
		matchStack.pop();
		return explore(patternIndex-1, matchStack);
		
	}

}
