package org.eventb.theory.rbp.internal.engine.assoc;

import java.util.List;
import java.util.Map;

import org.eventb.core.ast.Formula;
import org.eventb.theory.rbp.engine.IBinding;

/**
 * Common protocol for associative matchers.
 * 
 * @author maamria
 *
 * @since 1.0
 * @param <F> the type of the formula
 */
public interface IMatcher<F extends Formula<F>> {
	
	/**
	 * Matches the array of pattern formulae against the array of instance formulae.
	 * @param instances the array of instance formulae
	 * @param patterns the array of pattern formulae
	 * @param tag the formula tag
	 * @param isExtended whether the formula is an extended formula
	 * @param acceptPartialMatch whether to accept partial matches
	 * @return whether a match has been established
	 */
	public boolean match(F[] instances, F[] patterns, int tag, boolean isExtended, boolean acceptPartialMatch);
	
	/**
	 * Adds a variable mapping (expression or predicate) to the given binding.
	 * The mapping is constructed between the formula in <code>indexedFormula</code>
	 * and the associative commutative formula constructed from the list of the formulae
	 * in the indexed formulae list <code>indexedFormulae</code> with the given tag.
	 * @param indexedFormula the variable of the mapping
	 * @param indexedFormulae the list of indexed formulae
	 * @param tag the formula tag
	 * @param isExtended whether the tag concerns an extended formula
	 * @param binding the binding in which to insert
	 * @return whether the mapping has successfully been inserted
	 */
	public boolean addVariableMapping(IndexedFormula<F> indexedFormula,
			List<IndexedFormula<F>> indexedFormulae, int tag, boolean isExtended, IBinding binding) ;
	
	/**
	 * Computes all possible matches for the given indexed pattern in the array of possible instances.
	 * @param pattern the indexed pattern
	 * @param instances the array of indexed formulae
	 * @return all possible matches of pattern
	 */
	public Map<IndexedFormula<F>, IBinding> computeMatches(IndexedFormula<F> pattern, List<IndexedFormula<F>> instances);
	
	/**
	 * Returns a list of formulae contained in each of the indexed formulae.
	 * @param indexedFormulae the list of indexed formulae
	 * @return the formulae
	 */
	public List<F> getFormulae(List<IndexedFormula<F>> indexedFormulae);

}
