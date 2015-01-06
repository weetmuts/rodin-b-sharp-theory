package org.eventb.core.ast.extensions.pm.assoc;

import static org.eventb.core.internal.ast.extensions.pm.assoc.AssocUtils.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.eventb.core.ast.Formula;
import org.eventb.core.ast.extensions.maths.AstUtilities;
import org.eventb.core.ast.extensions.pm.Matcher;
import org.eventb.core.ast.extensions.pm.engine.Binding;
import org.eventb.core.internal.ast.extensions.pm.assoc.IndexedFormula;
import org.eventb.core.internal.ast.extensions.pm.assoc.Match;
import org.eventb.core.internal.ast.extensions.pm.assoc.MatchEntry;

/**
 * An implementation of an AC matching algorithm based (but not limited to) on ideas of
 *  Stripper-Collector algorithm as defined in
 *
 * "Associative-Commutative Rewriting on Large Terms" by Steven Eker
 *
 * TODO use an efficient representation of the problem, e.g., red-black trees
 * @author maamria
 *
 * @since 2.0
 */
public abstract class StripperCollectorMatcher<F extends Formula<F>> {

	protected int tag;
	protected List<IndexedFormula<F>> indexedFormulae;
	protected LinkedList<F> formulae;
	
	protected List<IndexedFormula<F>> indexedPatterns;
	protected LinkedList<F> patterns;
	
	protected List<IndexedFormula<F>> variables;
	
	protected Binding existingBinding;
	protected Matcher matcher;
	
	protected boolean solvable = true;
	private List<MatchEntry<F>> searchSpace;

	/**
	 * Creates a solver for the matching problem specified by the passed arguments.
	 * 
	 * <p> Flattening assumed.
	 * @param tag the formula tag
	 * @param formulae the array of formulae, must not be <code>null</code>
	 * @param patterns the array of patterns, must not be <code>null</code>
	 * @param existingBinding the existing binding
	 */
	public StripperCollectorMatcher(int tag, F[] formulae, F[] patterns, 
			Binding existingBinding){
		AstUtilities.ensureNotNull(formulae, patterns, existingBinding);
		// no need to go further if number of formulae is less than number of patterns
		if (formulae.length < patterns.length){
			solvable = false;
			return ;
		}
		this.tag = tag;
		this.formulae = new LinkedList<F>(Arrays.asList(formulae));
		this.indexedFormulae = getIndexedFormulae(formulae, null);
		this.variables = new ArrayList<IndexedFormula<F>>();
		// collect variables here
		this.indexedPatterns = getIndexedFormulae(patterns, variables);
		this.existingBinding = existingBinding;
		this.matcher = new Matcher(existingBinding.getFormulaFactory());
		this.searchSpace = generateSearchSpace();
	}
	
	public Binding solve(boolean acceptPartialMatch){
		// if not solvable based on the simple obvious criteria, do not go further
		if (!solvable)
			return null;
		// now we know there is a search space
		if (searchSpace.isEmpty()){
			// need to match variables
			for (IndexedFormula<F> indexedVar : variables){
				F variable = indexedVar.getFormula();
				F mappingForVar = getMapping(variable);
				if (mappingForVar != null){
					
				}
			}
		}
		return null;
	}
	
	/**
	 * Returns the appropriate mapping for the given variable, or <code>null</code> if non is stored
	 * in the existing binding.
	 * @param variable the variable to get mapping for
	 * @return the mapping for the variable if any
	 */
	protected abstract F getMapping(F variable);

	/**
	 * Generates the search space for the matching problem.
	 * 
	 * <p> The search space is a list of {@link MatchEntry} objects each of which holds a reference to
	 * an indexed formula and its matches (instances of {@link Match}).
	 * @return the search space
	 */
	private List<MatchEntry<F>> generateSearchSpace(){
		List<MatchEntry<F>> searchSpace = new ArrayList<MatchEntry<F>>();
		for (IndexedFormula<F> indexedPattern : indexedPatterns) {
			F pattern = indexedPattern.getFormula();
			List<Match<F>> matches = new ArrayList<Match<F>>();
			for (IndexedFormula<F> indexedFormula : indexedFormulae) {
				F formula = indexedFormula.getFormula();
				Binding binding = (Binding) matcher.match(formula, pattern, false);
				if (binding != null 
						&& existingBinding.isBindingInsertable(binding)) {
					matches.add(new Match<F>(indexedFormula, indexedPattern, binding));
				}
			}
			// if a formula has no matches then we fail
			if (matches.size() == 0) {
				solvable = false;
				return null;
			}
			searchSpace.add(new MatchEntry<F>(indexedPattern, matches));
		}
		// sort the search space in ascending order by rank
		Collections.sort(searchSpace, MatchEntry.<F>getRankComparator());
		return searchSpace;
	}
	
}
