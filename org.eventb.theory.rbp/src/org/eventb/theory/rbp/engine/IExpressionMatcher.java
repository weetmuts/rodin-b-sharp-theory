package org.eventb.theory.rbp.engine;

import org.eventb.core.ast.Expression;


/**
 * <p>Common protocol for an expression matcher.</p>
 * 
 * @author maamria
 */
public interface IExpressionMatcher extends Comparable<IExpressionMatcher>{
	
	/**
	 * <p>Augments the given <code>existingBinding</code> with new binding as it traverses <code>pattern</code> and <code>form</code>.</p>
	 * <p> Callers should ensure that at runtime <code>form</code> and <code>pattern</code> are of the same class.<p>
	 * @param form the original formula
	 * @param pattern
	 * @param existingBinding
	 * @return whether the matching succeeded
	 */
	public  boolean match(Expression form, Expression pattern, IBinding existingBinding);
	
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
