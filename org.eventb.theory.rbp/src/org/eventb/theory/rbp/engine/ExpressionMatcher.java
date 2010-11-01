package org.eventb.theory.rbp.engine;

import org.eventb.core.ast.Expression;

/**
 * An abstract implementation of an expression matcher.
 * @since 1.0
 * @author maamria
 *
 * @param <E> the type of expressions this matcher works with
 */
public abstract class ExpressionMatcher<E extends Expression> implements IExpressionMatcher {

	protected Class<E> type;
	protected int priority;
	
	protected ExpressionMatcher(Class<E> type){
		this.type = type;
	}

	public boolean match(Expression form, Expression pattern,
			IBinding existingBinding) {
		E eForm = cast(form);
		E ePattern = cast(pattern);
		return gatherBindings(eForm, ePattern, existingBinding);
		
	}

	/**
	 * Augments the given binding with the matching information.
	 * @param form the formula
	 * @param pattern the pattern against which to match
	 * @param existingBinding the binding
	 * @return whether matching succeeded
	 */
	protected abstract boolean gatherBindings(E form, E pattern, IBinding existingBinding);
	
	/**
	 * Casts the given expression to the specific type this matcher works with.
	 * @param e the expression
	 * @return the cast expression
	 */
	protected abstract E cast(Expression e);
	
	/**
	 * Returns the type of expressions handled by this matcher.
	 * @return the type of expressions
	 */
	public Class<E> getType(){
		return type;
	}
	
	public int compareTo(IExpressionMatcher m){
		 return priority - m.getPriority();
	}
	
	public int getPriority(){
		return priority;
	}
	
	public void setPriority(int priority){
		this.priority = priority;
	}
	
}
