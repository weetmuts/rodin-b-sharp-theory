package org.eventb.theory.rbp.engine;

import org.eventb.core.ast.Predicate;

/**
 * <p>Common protocol for a predicate matcher.</p>
 * @author maamria
 */
public interface IPredicateMatcher extends Comparable<IPredicateMatcher>{

	/**
	 * <p>Augments the given <code>existingBinding</code> with new binding as it traverses <code>pattern</code> and <code>form</code>.</p>
	 * @param form the original formula
	 * @param pattern
	 * @param existingBinding
	 * @return whether the matching succeeded
	 */
	public  boolean match(Predicate form, Predicate pattern, IBinding existingBinding);
	
	/**
	 * Returns the priority of this matcher. If a matcher has bigger priority, it is the
	 * one considered for matching.
	 * @return the priority
	 */
	public int getPriority();
	
	/**
	 * Sets the priority of this matcher.
	 * @param priority to set
	 */
	public void setPriority(int priority);
}
