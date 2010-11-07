package org.eventb.theory.rbp.internal.engine.assoc;

import java.util.Map;

import org.eventb.core.ast.Formula;
import org.eventb.theory.rbp.engine.IBinding;

/**
 * 
 * 
 * 
 * @author maamria
 *
 * @param <F> the type of the formula
 * @since 1.0
 */
public interface IMatchingProblem<F extends Formula<F>> {
	
	/**
	 * An enumeration describing the complexity of the matching problem.
	 * <p> A matching problem is said to be ground iff all patterns are not variable formulae i.e., predicate variables or free identifiers.
	 * <p> A matching problem is said to be linear if any variable formula occurs at most once.
	 * <p> A matching problem is said to be non-linear if it is not ground nor linear.
	 * @author maamria
	 * @since 1.0
	 *
	 */
	public enum COMPLEXITY {GROUND, LINEAR, NON_LINEAR}
	
	public void addMatches(IndexedFormula<F> pattern, Map<IndexedFormula<F>, IBinding> matches);
	
	public void addVariable(IndexedFormula<F> variable);
	
	public IBinding solve();

}
