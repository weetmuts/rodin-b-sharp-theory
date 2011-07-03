/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.pm.assoc;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.pm.IBinding;

/**
 * A basic implementation of an AC matching problem.
 * 
 * TODO FINISH THIS
 * @author maamria
 *
 */
public abstract class ACProblem<F extends Formula<F>> extends AssociativityProblem<F> {

	public ACProblem(int tag, F[] formulae, F[] patterns, FormulaFactory factory) {
		super(tag, formulae, patterns, factory);
	}
	
	public IBinding solve(boolean acceptPartialMatch) {
		if (!isSolvable) {
			return null;
		}
		IBinding initialBinding = matcher.getMatchingFactory().createBinding(matcher.getFactory());
		MatchStack<F> matchStack = new MatchStack<F>(matcher);
		if (searchSpace.size() > 0) {
			MatchEntry<F> matchEntry = searchSpace.get(0);
			List<Match<F>> matchesList = matchEntry.getMatches();
			for (Match<F> match : matchesList) {
				matchStack.push(match);
				if (explore(1, matchStack)) {
					IBinding matchBinding = matchStack.getFinalBinding();
					matchBinding.makeImmutable();
					initialBinding.insertBinding( matchBinding);
					break;
				}
			}
		}
		List<IndexedFormula<F>> allFormulae = new ArrayList<IndexedFormula<F>>();
		allFormulae.addAll(indexedFormulae);
		allFormulae.removeAll(matchStack.getUsedUpFormulae());
		if (allFormulae.size() < variables.size()){
			return null;
		}
		int sizeOfVariables = variables.size();
		for (int i = 0 ; i < sizeOfVariables-1; i++){
			// this is a hook
			IndexedFormula<F> var = variables.get(i);
			putVariableMapping(var, allFormulae.get(i), initialBinding);
		}
		if(sizeOfVariables > 0)
			putVariableMapping(variables.get(sizeOfVariables-1), allFormulae.get(sizeOfVariables-1), initialBinding);
		return initialBinding;
	}
	
	protected boolean explore(int patternIndex, MatchStack<F> matchStack){
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
	
	/**
	 * Inserts the variable mapping into the given binding.
	 * @param var the variable
	 * @param indexedFormula the indexed formula
	 * @param initialBinding the binding
	 */
	protected abstract void putVariableMapping(IndexedFormula<F> var, IndexedFormula<F> indexedFormula, IBinding initialBinding);

}
