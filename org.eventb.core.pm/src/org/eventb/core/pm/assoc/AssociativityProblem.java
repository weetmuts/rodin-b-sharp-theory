/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.pm.assoc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.pm.IBinding;
import org.eventb.core.pm.Matcher;

/**
 * A basic implementation of an associative problem.
 * 
 * <p>
 * An associative problem is defined by the following :
 * <ol>
 * <li>The tag of the associative formula in question.
 * <li>The array of formulae to be matched.
 * <li>The array of pattern formulae to match against.
 * </ol>
 * <p>
 * An associative problem is said to have been solved if a binding can be found such that
 * applying it to the associative formula whose children are the patterns results in
 * the associative formula (or a sub-formula) whose children are the formulae.
 * <p>
 * TODO describe this more formally
 * 
 * @author maamria
 * @since 1.0
 * 
 */
public abstract class AssociativityProblem<F extends Formula<F>> {

	protected int tag;
	protected List<IndexedFormula<F>> indexedFormulae;
	protected List<IndexedFormula<F>> indexedPatterns;
	protected Matcher matcher;

	protected List<MatchEntry<F>> searchSpace;
	protected List<IndexedFormula<F>> variables;

	protected boolean isSolvable = true;

	/**
	 * Constructs a new associativity problem with the given tag, array of formulae and array of patterns.
	 * 
	 * @param tag
	 *            the tag, must be a valid tag
	 * @param formulae
	 *            the array of formula, must not be <code>null</code>
	 * @param patterns
	 *            the array of patterns, must not be <code>null</code>
	 * @param factory
	 *            the formula factory
	 */
	protected AssociativityProblem(int tag, F[] formulae, F[] patterns, FormulaFactory factory) {
		this.tag = tag;
		this.matcher = new Matcher(factory);
		this.indexedFormulae = getIndexedFormulae(formulae);
		this.indexedPatterns = getIndexedFormulae(patterns);
		this.variables = new ArrayList<IndexedFormula<F>>();
		this.searchSpace = generateSearchSpace();
		if (indexedFormulae.size() < indexedPatterns.size()) {
			isSolvable = false;
		}
	}

	/**
	 * Calculates a binding that solves the associative problem.
	 * 
	 * @param acceptPartialMatch
	 *            whether to accept a partial match
	 * @return the matching result, or <code>null</code> if the problem cannot
	 *         be solved [by this algorithm]
	 */
	public abstract IBinding solve(boolean acceptPartialMatch);

	/**
	 * Returns an indexed formula list based on the formulae supplied.
	 * @param list the formulae list
	 * @return list of indexed formulae
	 */
	protected List<IndexedFormula<F>> getIndexedFormulae(F... list) {
		if (list == null) {
			return null;
		}
		List<IndexedFormula<F>> indexedFormulae = new ArrayList<IndexedFormula<F>>();
		int i = 0;
		for (F formula : list) {
			indexedFormulae.add(new IndexedFormula<F>(i++, formula));
		}
		return indexedFormulae;
	}
	
	/**
	 * Generates the search space for this matching process.
	 * 
	 * <p> The search space is the collection of all matches for all patterns.
	 * @return the search space
	 */
	protected List<MatchEntry<F>> generateSearchSpace() {
		List<MatchEntry<F>> searchSpace = new ArrayList<MatchEntry<F>>();
		for (IndexedFormula<F> indexedPattern : indexedPatterns) {
			if (indexedPattern.isVariable()) {
				variables.add(indexedPattern);
				continue;
			}
			F pattern = indexedPattern.getFormula();
			List<Match<F>> matches = new ArrayList<Match<F>>();
			for (IndexedFormula<F> indexedFormula : indexedFormulae) {
				F formula = indexedFormula.getFormula();
				IBinding binding = matcher.match(formula, pattern, false);
				if (binding != null) {
					matches.add(new Match<F>(indexedFormula, indexedPattern, binding));
				}
			}
			searchSpace.add(new MatchEntry<F>(indexedPattern, matches));
			if (matches.size() == 0) {
				isSolvable = false;
			}
		}
		Collections.sort(searchSpace, new Comparator<MatchEntry<F>>() {
			@Override
			public int compare(MatchEntry<F> o1, MatchEntry<F> o2) {
				if (o1.equals(o2)) {
					return 0;
				}
				if (o1.getRank() > o2.getRank()) {
					return 1;
				}
				if (o1.getRank() < o2.getRank()) {
					return -1;
				}
				return 1;
			}
		});
		return searchSpace;
	}

	@Override
	public String toString() {
		return tag + " : \n Formulae : " + indexedFormulae + "\n Patterns : " + indexedPatterns;
	}
}
