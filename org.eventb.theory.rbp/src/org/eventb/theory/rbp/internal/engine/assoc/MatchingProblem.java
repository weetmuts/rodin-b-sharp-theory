package org.eventb.theory.rbp.internal.engine.assoc;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.eventb.core.ast.Formula;
import org.eventb.theory.rbp.engine.IBinding;

/**
 * 
 * @author maamria
 *
 * @param <F> the type of the formula
 * @since 1.0
 */
public abstract class MatchingProblem<F extends Formula<F>> 
	implements IMatchingProblem<F>
{
	
	protected Map<IndexedFormula<F>, Map<IndexedFormula<F>,IBinding>> searchSpace;
	protected Set<IndexedFormula<F>> variables;
	protected IBinding existingBinding;
	protected IBinding solution;
	
	/**
	 * Creates a fresh matching problem.
	 */
	public MatchingProblem(IBinding existingBinding){
		this.searchSpace = new TreeMap<IndexedFormula<F>, Map<IndexedFormula<F>,IBinding>>();
		this.existingBinding = existingBinding;
		this.variables = new LinkedHashSet<IndexedFormula<F>>();
	}

	@Override
	public void addMatches(IndexedFormula<F> pattern,
			Map<IndexedFormula<F>, IBinding> matches) {
		searchSpace.put(pattern, matches);
	}

	@Override
	public void addVariable(IndexedFormula<F> variable) {
		// TODO Auto-generated method stub
		variables.add(variable);
	}
	
	
	
}
