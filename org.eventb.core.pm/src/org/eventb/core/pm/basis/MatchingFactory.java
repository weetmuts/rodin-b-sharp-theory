package org.eventb.core.pm.basis;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.BoolExpression;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.ExtendedPredicate;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.MultiplePredicate;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.ast.UnaryPredicate;
import org.eventb.core.internal.pm.ExpressionPatternMatchersRegistry;
import org.eventb.core.internal.pm.PredicatePatternMatchersRegistry;
import org.eventb.core.pm.basis.engine.Binding;
import org.eventb.core.pm.basis.engine.MatchingUtilities;
import org.eventb.core.pm.matchers.exp.AssociativeExpressionMatcher;
import org.eventb.core.pm.matchers.exp.AtomicExpressionMatcher;
import org.eventb.core.pm.matchers.exp.BinaryExpressionMatcher;
import org.eventb.core.pm.matchers.exp.BoolExpressionMatcher;
import org.eventb.core.pm.matchers.exp.BoundIdentifierMatcher;
import org.eventb.core.pm.matchers.exp.IntegerLiteralMatcher;
import org.eventb.core.pm.matchers.exp.QuantifiedExpressionMatcher;
import org.eventb.core.pm.matchers.exp.SetExtensionMatcher;
import org.eventb.core.pm.matchers.exp.UnaryExpressionMatcher;
import org.eventb.core.pm.matchers.pred.AssociativePredicateMatcher;
import org.eventb.core.pm.matchers.pred.BinaryPredicateMatcher;
import org.eventb.core.pm.matchers.pred.LiteralPredicateMatcher;
import org.eventb.core.pm.matchers.pred.MultiplePredicateMatcher;
import org.eventb.core.pm.matchers.pred.QuantifiedPredicateMatcher;
import org.eventb.core.pm.matchers.pred.RelationalPredicateMatcher;
import org.eventb.core.pm.matchers.pred.SimplePredicateMatcher;
import org.eventb.core.pm.matchers.pred.UnaryPredicateMatcher;

/**
 * A matching factory that has the following capabilities:
 * <p>1- <u> Return a matcher</u> for a given formula class.
 * <p>2-<u> Create a fresh binding </u> when starting a matching process.
 * @since 1.0
 * @author maamria
 *
 */
public final class MatchingFactory {

	/**
	 * The matchers database
	 */
	private static final Map<Class<? extends Expression>, IExpressionMatcher> EXPRESSION_MATCHERS = new LinkedHashMap<Class<? extends Expression>, IExpressionMatcher>();
	private static final Map<Class<? extends Predicate>, IPredicateMatcher> PREDICATE_MATCHERS = new LinkedHashMap<Class<? extends Predicate>, IPredicateMatcher>();
	
	/**
	 * Load expression matchers.
	 */
	static {
		EXPRESSION_MATCHERS.put(AssociativeExpression.class, new AssociativeExpressionMatcher());
		EXPRESSION_MATCHERS.put(AtomicExpression.class, new AtomicExpressionMatcher());
		EXPRESSION_MATCHERS.put(BinaryExpression.class, new BinaryExpressionMatcher());
		EXPRESSION_MATCHERS.put(BoolExpression.class, new BoolExpressionMatcher());
		EXPRESSION_MATCHERS.put(BoundIdentifier.class, new BoundIdentifierMatcher());
		EXPRESSION_MATCHERS.put(IntegerLiteral.class, new IntegerLiteralMatcher());
		EXPRESSION_MATCHERS.put(QuantifiedExpression.class, new QuantifiedExpressionMatcher());
		EXPRESSION_MATCHERS.put(SetExtension.class, new SetExtensionMatcher());
		EXPRESSION_MATCHERS.put(UnaryExpression.class, new UnaryExpressionMatcher());
	}
	
	/**
	 * Load predicate matchers.
	 */
	static {
		PREDICATE_MATCHERS.put(AssociativePredicate.class, new AssociativePredicateMatcher());
		PREDICATE_MATCHERS.put(BinaryPredicate.class, new BinaryPredicateMatcher());
		PREDICATE_MATCHERS.put(LiteralPredicate.class, new LiteralPredicateMatcher());
		PREDICATE_MATCHERS.put(MultiplePredicate.class, new MultiplePredicateMatcher());
		PREDICATE_MATCHERS.put(QuantifiedPredicate.class, new QuantifiedPredicateMatcher());
		PREDICATE_MATCHERS.put(RelationalPredicate.class, new RelationalPredicateMatcher());
		PREDICATE_MATCHERS.put(SimplePredicate.class, new SimplePredicateMatcher());
		PREDICATE_MATCHERS.put(UnaryPredicate.class, new UnaryPredicateMatcher() );
	}
	
	private static MatchingFactory instance;
	
	private ExpressionPatternMatchersRegistry expressionPatternMatchersRegistry;
	private PredicatePatternMatchersRegistry predicatePatternMatchersRegistry;
	
	/**
	 * Private constructor.
	 */
	private MatchingFactory(){
		expressionPatternMatchersRegistry = ExpressionPatternMatchersRegistry.getMatchersRegistry();
		predicatePatternMatchersRegistry = PredicatePatternMatchersRegistry.getMatchersRegistry();
	}
	
	/**
	 * <p> Matches <code>form</code> and <code>pattern</code> to augment the existing binding.</p>
	 * @param formula the formula
	 * @param pattern the pattern
	 * @param initialBinding must not be <code>null</code>
	 * @return whether the matching succeeded
	 */
	public final boolean match(Formula<?> formula, Formula<?> pattern, IBinding initialBinding){
		if(initialBinding == null){
			throw new IllegalArgumentException("Matching started without a binding object.");
		}
		if(!MatchingUtilities.sameClass(formula, pattern)){
			return false;
		}
		
		if(formula instanceof Expression){
			IExpressionMatcher expMatcher = null;
			if (formula instanceof ExtendedExpression){
				ExtendedExpression extendedExpression = (ExtendedExpression) formula;
				expMatcher = expressionPatternMatchersRegistry.getMatcher(extendedExpression.getExtension().getClass());
			}
			else {
				 expMatcher = getExpressionMatcher(((Expression)formula).getClass());
			}
			if (expMatcher == null){
				return false;
			}
			return expMatcher.match((Expression)formula, (Expression)pattern, initialBinding);
		}
		else {
			IPredicateMatcher predMatcher = null;
			if (formula instanceof ExtendedPredicate){
				ExtendedPredicate extendedPredicate = (ExtendedPredicate) formula;
				predMatcher = predicatePatternMatchersRegistry.getMatcher(extendedPredicate.getExtension().getClass());
			}
			else {
				predMatcher = getPredicateMatcher(((Predicate)formula).getClass());
			}
			if (predMatcher == null){
				return false;
			}
			return predMatcher.match((Predicate)formula, (Predicate) pattern, initialBinding);
		}
	}
	
	/**
	 * Returns an empty binding to start a matching process.
	 * @param isPartialMatchAcceptable whether a partial match is acceptable
	 * @param factory the formula factory
	 * @return an empty binding
	 */
	public final IBinding createBinding(boolean isPartialMatchAcceptable, FormulaFactory factory){
		return new Binding(isPartialMatchAcceptable, factory);
	}
	
	private IExpressionMatcher getExpressionMatcher(Class<? extends Expression> clazz){
		return EXPRESSION_MATCHERS.get(clazz);
	}
	
	private IPredicateMatcher getPredicateMatcher(Class<? extends Predicate> clazz){
		return PREDICATE_MATCHERS.get(clazz);
	}
	
	public static MatchingFactory getInstance(){
		if (instance == null){
			instance = new MatchingFactory();
		}
		return instance;
	}
	
	
}
