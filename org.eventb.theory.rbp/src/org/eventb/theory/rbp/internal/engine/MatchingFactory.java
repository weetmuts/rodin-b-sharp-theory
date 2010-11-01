package org.eventb.theory.rbp.internal.engine;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.theory.rbp.engine.IBinding;
import org.eventb.theory.rbp.engine.IExpressionMatcher;
import org.eventb.theory.rbp.engine.IPredicateMatcher;
import org.eventb.theory.rbp.utils.ProverUtilities;

/**
 * An implementation of a matching factory that has the following capabilities:
 * <p>1-<u>Return a matcher</u> for a given formula class.
 * <p>2-<u>Create a fresh binding</u> when starting a matching process.
 * @since 1.0
 * @author maamria
 *
 */
public final class MatchingFactory {

	/**
	 * <p>Returns the appropriate matcher for the specific class of expression specified by <code>clazz</code>.</p>
	 * @param clazz the class of the formula
	 * @return the matcher
	 */
	protected static IExpressionMatcher getExpressionMatcher(Class<? extends Expression> clazz){
		return ExpressionPatternMatchersRegistry.getMatchersRegistry().getMatcher(clazz);
		
	}
	/**
	 * <p>Returns the appropriate matcher for the specific class of predicate specified by <code>clazz</code>.</p>
	 * @param clazz the class of the formula
	 * @return the matcher
	 */
	protected static IPredicateMatcher getPredicateMatcher(Class<? extends Predicate> clazz){
		return PredicatePatternMatchersRegistry.getMatchersRegistry().getMatcher(clazz);
	}
	
	/**
	 * <p> Matches <code>form</code> and <code>pattern</code> to augment the exisiting binding.</p>
	 * @param form
	 * @param pattern
	 * @param initialBinding must not be <code>null</code>
	 * @return whether the matching succeeded
	 */
	public final static boolean match(Formula<?> form, Formula<?> pattern, IBinding initialBinding){
		if(initialBinding == null){
			throw new IllegalArgumentException("Matching started without a binding object.");
		}
		if(!ProverUtilities.sameClass(form, pattern)){
			return false;
		}
		
		if(form instanceof Expression){
			IExpressionMatcher expMatcher = getExpressionMatcher(((Expression)form).getClass());
			return expMatcher.match((Expression)form, (Expression)pattern, initialBinding);
		}
		else {
			IPredicateMatcher predMatcher = getPredicateMatcher(((Predicate)form).getClass());
			return predMatcher.match((Predicate)form, (Predicate) pattern, initialBinding);
		}
	}
	
	/**
	 * Returns an empty binding to start a matching process.
	 * @param formula the formula
	 * @param pattern the pattern against which to match the formula
	 * @param isPartialMatchAcceptable whether a partial match is acceptable
	 * @param factory the formula factory
	 * @return an empty binding
	 */
	public final static IBinding createBinding(Formula<?> formula, Formula<?> pattern, 
			boolean isPartialMatchAcceptable, FormulaFactory factory){
		return new Binding(formula, pattern, isPartialMatchAcceptable, factory);
	}
}
