package org.eventb.theory.rbp.internal.engine.assoc;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.theory.rbp.engine.IBinding;
import org.eventb.theory.rbp.internal.engine.MatchingFactory;

/**
 * A very basic associative commutative matcher. This is level 0 of the AC matching procedure.
 * @since 1.0
 * @author maamria
 *
 */
public abstract class AssocCommutMatcher00<F extends Formula<F>> {

	protected FormulaFactory factory;
	protected IBinding existingBinding;
	
	protected AssocCommutMatcher00(IBinding existingBinding){
		this.existingBinding = existingBinding;
		this.factory = existingBinding.getFormulaFactory();
	}
	
	// flattening expected
	public boolean match(F[] instances, F[] patterns, int tag){
		// here we deal with the basic case
		// X op val =AC? a op b op ... op i
		// patterns has 2 formulae
		if(patterns.length != 2)
			return false;
		// ensure we have enough instances to work with
		if(instances.length < patterns.length){
			return false;
		}
		List<IndexedFormula<F>> variablePatterns = new ArrayList<IndexedFormula<F>>();
		IndexedFormula<F>[] indexedPatterns = index(patterns, variablePatterns, true);
		// this ensure it is linear problem we are working with
		if(variablePatterns.size() != 1){
			return false;
		}
		IndexedFormula<F>[] indexedInstances = index(instances, null, false);
		Set<MatchingEntries<F>> possibleMatches = new TreeSet<MatchingEntries<F>>();
		for (IndexedFormula<F> indexedPattern : indexedPatterns){
			if(indexedPattern.isVariable())
				continue;
			MatchingEntries<F> matches = computeMatches(indexedPattern, indexedInstances);
			// matching fails
			if(matches.hasNoMatches()){
				return false;
			}
			possibleMatches.add(matches);
		}
		return true;
	}
	
	/**
	 * Computes all possible matches for the given indexed pattern in the array of possible instances.
	 * @param pattern the indexed pattern
	 * @param instances the array of indexed formulae
	 * @return all possible matches of pattern
	 */
	protected MatchingEntries<F> computeMatches(IndexedFormula<F> pattern, IndexedFormula<F>[] instances){
		MatchingEntries<F> matches = new MatchingEntries<F>(pattern);
		for (IndexedFormula<F> instance : instances){
			IBinding binding = MatchingFactory.createBinding(instance.getFormula(), pattern.getFormula(), false, factory);
			if(MatchingFactory.match(instance.getFormula(), pattern.getFormula(), binding)){
				if(existingBinding.isBindingInsertable(binding)){
					matches.addMatch(instance, binding);
				}
			}
		}
		return matches;
	}
	
	/**
	 * Indexed all given formulae.
	 * @param formulae the formulae
	 * @param variables list of variable formulae or <code>null</code> if recording variable formulae is not desired
	 * @param arePatterns whether the supplied formulae are patterns in which case <code>variables</code> cannot be <code>null</code>
	 * @return the indexed formulae array
	 */
	protected IndexedFormula<F>[] index(F[] formulae, List<IndexedFormula<F>> variables, boolean arePatterns){
		IndexedFormula<F>[] indexed = getIndexedFormulaeArray(formulae);
		int i = 0;
		for (F f : formulae){
			indexed[i] = new IndexedFormula<F>(f, i++);
			if(arePatterns && indexed[i].isVariable())
				variables.add(indexed[i]);
		}
		return indexed;
	}
	
	/**
	 * Returns whether the matching problem whose child variable patterns are the given patterns is linear i.e., each
	 * variable formula occurs exactly once.
	 * @param flattenedPatterns the list of variable patterns
	 * @return whether the matching problem is linear
	 */
	protected boolean isLinear(List<IndexedFormula<F>> variablePatterns){
		if(variablePatterns.size() <= 1){
			return true;
		}
		for (int i= 0 ; i < variablePatterns.size() ; i++){
			// we start at 1 in the inner loop
			for (int j = 1; j < variablePatterns.size() ; j++){
				if(i == j)
					continue;
				if(variablePatterns.get(i).equals(variablePatterns.get(j))){
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * Returns an indexed formula array of the same size as the given array of formulae.
	 * @param formulae the formulae
	 * @return an indexed formulae array
	 */
	protected abstract IndexedFormula<F>[] getIndexedFormulaeArray(F[] formulae);
}
