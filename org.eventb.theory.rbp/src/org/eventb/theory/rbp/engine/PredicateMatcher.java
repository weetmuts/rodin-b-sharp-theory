package org.eventb.theory.rbp.engine;

import org.eventb.core.ast.Predicate;

/**
 * An abstract implementation of a predicate matcher.
 * @since 1.0
 * @author maamria
 *
 * @param <E> the type of predicates this matcher works with
 */
public abstract class PredicateMatcher<P extends Predicate> implements IPredicateMatcher {
	
	protected Class<P> type;
	protected int priority;
	
	protected PredicateMatcher(Class<P> type){
		this.type = type;
	}

	public boolean match(Predicate form, Predicate pattern,
			IBinding existingBinding) {
		P pForm = cast(form);
		P pPattern = cast(pattern);
		return gatherBindings(pForm, pPattern, existingBinding);
		
	}
	
	/**
	 * Augments the given binding with the matching information.
	 * @param form the formula
	 * @param pattern the pattern against which to match
	 * @param existingBinding the binding
	 * @return whether matching succeeded
	 */
	protected abstract boolean gatherBindings(P form, P pattern, IBinding existingBinding);	

	/**
	 * Casts the given predicate to the specific type this matcher works with.
	 * @param e the predicate
	 * @return the cast predicate
	 */
	protected abstract P cast(Predicate p);
	
	public Class<P> getType(){
		return type;
	}
	
	public int compareTo(IPredicateMatcher m){
		return priority - m.getPriority();
	}
	
	public int getPriority(){
		return priority;
	}
	
	public void setPriority(int priority){
		this.priority = priority;
	}
}
