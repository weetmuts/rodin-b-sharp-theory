package org.eventb.core.ast.extensions.pm.engine;

import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.extensions.pm.MatchingFactory;

/**
 * An abstract implementation of a predicate matcher.
 * <p> This class is not intended to be extended by clients.
 * @since 1.0
 * @author maamria
 *
 * @param <E> the type of predicates this matcher works with
 */
public abstract class PredicateMatcher<P extends Predicate> {
	
	protected MatchingFactory matchingFactory;
	protected Class<P> type;
	
	protected PredicateMatcher(Class<P> type){
		this.type = type;
		this.matchingFactory = MatchingFactory.getInstance();
	}

	public boolean match(Predicate form, Predicate pattern,
			Binding existingBinding) {
		P pForm = getPredicate(form);
		P pPattern = getPredicate(pattern);
		// check same tag
		if (pForm.getTag() != pPattern.getTag()){
			return false;
		}
		return gatherBindings(pForm, pPattern, existingBinding);
		
	}
	
	/**
	 * Augments the given binding with the matching information.
	 * 
	 * <p> The given formula and pattern can be assumed to have the same tag.
	 * @param form the formula
	 * @param pattern the pattern against which to match
	 * @param existingBinding the binding
	 * @return whether matching succeeded
	 */
	protected abstract boolean gatherBindings(P form, P pattern, Binding existingBinding);	

	/**
	 * Casts the given predicate to the specific type this matcher works with.
	 * @param e the predicate
	 * @return the cast predicate
	 */
	protected abstract P getPredicate(Predicate p);
	
	public Class<P> getType(){
		return type;
	}
}
