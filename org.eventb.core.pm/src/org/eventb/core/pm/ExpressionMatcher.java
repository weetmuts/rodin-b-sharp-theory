package org.eventb.core.pm;

import org.eventb.core.ast.Expression;

/**
 * An abstract implementation of an expression matcher.
 * <p> This class is NOT intended to be sub-classed by clients.
 * @since 1.0
 * @author maamria
 *
 * @param <E> the type of expressions this matcher works with
 */
public abstract class ExpressionMatcher<E extends Expression> implements IExpressionMatcher {

	protected MatchingFactory matchingFactory;
	protected Class<E> type;
	
	protected ExpressionMatcher(Class<E> type){
		this.type = type;
		this.matchingFactory = MatchingFactory.getInstance();
	}

	public boolean match(Expression form, Expression pattern,
			IBinding existingBinding) {
		E eForm = getExpression(form);
		E ePattern = getExpression(pattern);
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
	protected abstract E getExpression(Expression e);
	
	/**
	 * Returns the type of expressions handled by this matcher.
	 * @return the type of expressions
	 */
	public Class<E> getType(){
		return type;
	}

}
