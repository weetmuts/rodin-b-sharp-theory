package org.eventb.theory.rbp.internal.engine.assoc;

import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.PredicateVariable;

/**
 * An indexed formula contains a formula and an index.
 * <p> An indexed formula is said to be <code>variable</code> iff it contains a predicate variable or a free identifier.
 * @author maamria
 * @since 1.0
 * @param <F> the type of the formula
 */
public class IndexedFormula<F extends Formula<F>> implements Comparable<IndexedFormula<F>> {

	private int index;
	private F formula;
	private int numberOfMatches = 0;
	
	/**
	 * Creates an indexed formula.
	 * @param formula the formula
	 * @param index the index
	 */
	public IndexedFormula(F formula, int index){
		this.formula = formula;
		this.index = index;
	}
	
	/**
	 * Returns the index.
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * Returns the formula.
	 * @return the formula
	 */
	public F getFormula() {
		return formula;
	}
	
	/**
	 * Increments the number of matches of this indexed formula.
	 */
	public void matchAdded(){
		numberOfMatches++;
	}
	
	/**
	 * Returns whether the formula contained in this indexed formula is a variable formula.
	 * @return whether the formula is a variable formula
	 */
	public boolean isVariable(){
		return (formula instanceof FreeIdentifier || formula instanceof PredicateVariable);
	}
	
	public boolean equals(Object o){
		if(this == o){
			return true;
		}
		if(o == null || !(o instanceof IndexedFormula)){
			return false;
		}
		return index == ((IndexedFormula<?>)o).index 
				&& formula.equals(((IndexedFormula<?>)o).formula) 
				&& numberOfMatches == ((IndexedFormula<?>)o).numberOfMatches;
	}
	
	public int hashcode(){
		return index * 43 + formula.hashCode() + numberOfMatches;
	}

	@Override
	public int compareTo(IndexedFormula<F> o) {
		int res = numberOfMatches - o.numberOfMatches;
		if(res == 0){
			if(equals(o)){
				return 0;
			}
			else {
				return 1;
			}
		}
		return res;
	}
	
}