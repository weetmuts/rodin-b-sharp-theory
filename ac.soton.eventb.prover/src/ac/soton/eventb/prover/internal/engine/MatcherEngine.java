package ac.soton.eventb.prover.internal.engine;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.Predicate;

import ac.soton.eventb.prover.engine.IBinding;
import ac.soton.eventb.prover.utils.ProverUtilities;

/**
 * 
 * @author maamria
 *
 */
public class MatcherEngine {

	private static MatcherEngine instance;
	
	private IExpressionMatcher expMatcher;
	private IPredicateMatcher predMatcher;
	
	private MatcherEngine(){}
	
	public static MatcherEngine getDefault(){
		if(instance == null)
			instance =  new MatcherEngine();
		return instance;
	}

	/**
	 * <p> Matches <code>form</code> and <code>pattern</code> to augment the exisiting binding.</p>
	 * @param form
	 * @param pattern
	 * @param initialBinding must not be <code>null</code>
	 * @return whether the matching succeeded
	 */
	public boolean match(Formula<?> form, Formula<?> pattern, IBinding initialBinding){
		if(!ProverUtilities.sameClass(form, pattern)){
			return false;
		}
		assert initialBinding != null;
		if(form instanceof Expression){
			expMatcher = MatcherFactory.getExpressionMatcher(((Expression)form).getClass());
			return expMatcher.match((Expression)form, (Expression)pattern, initialBinding);
		}
		else {
			predMatcher = MatcherFactory.getPredicateMatcher(((Predicate)form).getClass());
			return predMatcher.match((Predicate)form, (Predicate) pattern, initialBinding);
		}
	}
}
