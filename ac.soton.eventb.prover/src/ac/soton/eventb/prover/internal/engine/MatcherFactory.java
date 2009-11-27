package ac.soton.eventb.prover.internal.engine;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Predicate;

/**
 * <p>This class enables a mechanism by which concrete matchers for specific type of formulas are provided.</p>
 * @author maamria
 * @see IExpressionMatcher
 * @see IPredicateMatcher
 */
public class MatcherFactory {

	/**
	 * <p>Returns the appropriate matcher for the specific class of expression specified by <code>clazz</code>.</p>
	 * @param clazz the class of the formula
	 * @return the matcher
	 */
	public static  IExpressionMatcher getExpressionMatcher(Class<? extends Expression> clazz){
		return MatchersDatabase.EXP_MATCHERS.get(clazz);
		
	}
	/**
	 * <p>Returns the appropriate matcher for the specific class of predicate specified by <code>clazz</code>.</p>
	 * @param clazz the class of the formula
	 * @return the matcher
	 */
	public static IPredicateMatcher getPredicateMatcher(Class<? extends Predicate> clazz){
		return MatchersDatabase.PRED_MATCHERS.get(clazz);
	}
}
