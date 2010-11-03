package org.eventb.theory.rbp.internal.engine.assoc;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eventb.core.ast.Formula;
import org.eventb.theory.rbp.engine.IBinding;

/**
 * Matching entries object holds information about the set of matches of a pattern as well as associated
 * bindings.
 * 
 * @since 1.0
 * 
 * @author maamria
 *
 */
public class MatchingEntries<F extends Formula<F>> implements Comparable<MatchingEntries<F>>{

	private IndexedFormula<F> pattern;
	private Map<IndexedFormula<F>, IBinding> matches;
	
	public MatchingEntries(IndexedFormula<F> pattern){
		this.pattern = pattern;
		this.matches = new LinkedHashMap<IndexedFormula<F>, IBinding>();
	}
	
	public IndexedFormula<F> getPattern(){
		return pattern;
	}
	
	public boolean hasNoMatches(){
		return matches.size() == 0;
	}
	
	public void addMatch(IndexedFormula<F> formula, IBinding binding){
		matches.put(formula, binding);
	}
	
	public boolean equals(Object o){
		if(o == this)
			return true;
		if(o == null || !(o instanceof MatchingEntries)){
			return false;
		}
		return pattern.equals(((MatchingEntries<?>) o).pattern) &&
			matches.equals(((MatchingEntries<?>) o).matches);
	}
	
	public int hashCode(){
		return pattern.hashcode()*31 + matches.hashCode()*7;
	}
	
	public int compareTo(MatchingEntries<F> other){
		if(matches.size() < other.matches.size()){
			return -1;
		}
		if(matches.size() > other.matches.size()){
			return 1;
		}
		if(matches.size() == other.matches.size()){
			if(equals(other)){
				return 0;
			}
		}
		return 1;
	}
}
