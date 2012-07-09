package org.eventb.core.ast.extensions.pm;

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
import org.eventb.core.ast.extensions.pm.engine.Binding;
import org.eventb.core.ast.extensions.pm.engine.ExpressionMatcher;
import org.eventb.core.ast.extensions.pm.engine.PredicateMatcher;
import org.eventb.core.ast.extensions.pm.engine.exp.AssociativeExpressionMatcher;
import org.eventb.core.ast.extensions.pm.engine.exp.AtomicExpressionMatcher;
import org.eventb.core.ast.extensions.pm.engine.exp.BinaryExpressionMatcher;
import org.eventb.core.ast.extensions.pm.engine.exp.BoolExpressionMatcher;
import org.eventb.core.ast.extensions.pm.engine.exp.BoundIdentifierMatcher;
import org.eventb.core.ast.extensions.pm.engine.exp.DefaultExtendedExpressionMatcher;
import org.eventb.core.ast.extensions.pm.engine.exp.IntegerLiteralMatcher;
import org.eventb.core.ast.extensions.pm.engine.exp.QuantifiedExpressionMatcher;
import org.eventb.core.ast.extensions.pm.engine.exp.SetExtensionMatcher;
import org.eventb.core.ast.extensions.pm.engine.exp.UnaryExpressionMatcher;
import org.eventb.core.ast.extensions.pm.engine.pred.AssociativePredicateMatcher;
import org.eventb.core.ast.extensions.pm.engine.pred.BinaryPredicateMatcher;
import org.eventb.core.ast.extensions.pm.engine.pred.DefaultExtendedPredicateMatcher;
import org.eventb.core.ast.extensions.pm.engine.pred.LiteralPredicateMatcher;
import org.eventb.core.ast.extensions.pm.engine.pred.MultiplePredicateMatcher;
import org.eventb.core.ast.extensions.pm.engine.pred.QuantifiedPredicateMatcher;
import org.eventb.core.ast.extensions.pm.engine.pred.RelationalPredicateMatcher;
import org.eventb.core.ast.extensions.pm.engine.pred.SimplePredicateMatcher;
import org.eventb.core.ast.extensions.pm.engine.pred.UnaryPredicateMatcher;

/**
 * A matching factory that has the following capabilities:
 * <p>1- Return a matcher for a given formula class.
 * <p>2- Create a fresh binding when starting a matching process.
 * 
 * <p> Clients need not use this class directly. 
 * An instance is available as part of the matchers hierarchy class definition.
 * @since 1.0
 * @author maamria
 *
 */
public final class MatchingFactory {

	/**
	 * The matchers database
	 */
	private static final Map<Class<? extends Expression>, ExpressionMatcher<?>> EXPRESSION_MATCHERS = new LinkedHashMap<Class<? extends Expression>, ExpressionMatcher<?>>();
	private static final Map<Class<? extends Predicate>, PredicateMatcher<?>> PREDICATE_MATCHERS = new LinkedHashMap<Class<? extends Predicate>, PredicateMatcher<?>>();
	
	/**
	 * Load expression matchers.
	 */
	static{
		EXPRESSION_MATCHERS.put(AssociativeExpression.class, new AssociativeExpressionMatcher());
		EXPRESSION_MATCHERS.put(AtomicExpression.class, new AtomicExpressionMatcher());
		EXPRESSION_MATCHERS.put(BinaryExpression.class, new BinaryExpressionMatcher());
		EXPRESSION_MATCHERS.put(BoolExpression.class, new BoolExpressionMatcher());
		EXPRESSION_MATCHERS.put(BoundIdentifier.class, new BoundIdentifierMatcher());
		EXPRESSION_MATCHERS.put(IntegerLiteral.class, new IntegerLiteralMatcher());
		EXPRESSION_MATCHERS.put(QuantifiedExpression.class, new QuantifiedExpressionMatcher());
		EXPRESSION_MATCHERS.put(SetExtension.class, new SetExtensionMatcher());
		EXPRESSION_MATCHERS.put(UnaryExpression.class, new UnaryExpressionMatcher());
		EXPRESSION_MATCHERS.put(ExtendedExpression.class, new DefaultExtendedExpressionMatcher());
	}
	
	/**
	 * Load predicate matchers.
	 */
	static{
		PREDICATE_MATCHERS.put(AssociativePredicate.class, new AssociativePredicateMatcher());
		PREDICATE_MATCHERS.put(BinaryPredicate.class, new BinaryPredicateMatcher());
		PREDICATE_MATCHERS.put(LiteralPredicate.class, new LiteralPredicateMatcher());
		PREDICATE_MATCHERS.put(MultiplePredicate.class, new MultiplePredicateMatcher());
		PREDICATE_MATCHERS.put(QuantifiedPredicate.class, new QuantifiedPredicateMatcher());
		PREDICATE_MATCHERS.put(RelationalPredicate.class, new RelationalPredicateMatcher());
		PREDICATE_MATCHERS.put(SimplePredicate.class, new SimplePredicateMatcher());
		PREDICATE_MATCHERS.put(UnaryPredicate.class, new UnaryPredicateMatcher() );
		PREDICATE_MATCHERS.put(ExtendedPredicate.class, new DefaultExtendedPredicateMatcher() );
	}
	
	private static MatchingFactory instance;
	
	/**
	 * Private constructor.
	 */
	private MatchingFactory(){}
	
	/**
	 * <p> Matches <code>form</code> and <code>pattern</code> to augment the existing binding.</p>
	 * @param formula the formula
	 * @param pattern the pattern
	 * @param initialBinding must not be <code>null</code>
	 * @return whether the matching succeeded
	 */
	public final boolean match(Formula<?> formula, Formula<?> pattern, IBinding initialBinding){
		// initial binding cannot be null
		if(initialBinding == null){
			throw new IllegalArgumentException("Matching started without a binding object.");
		}
		// case Expression : use an expression matcher
		if(formula instanceof Expression){
			// get the matcher from the map
			ExpressionMatcher<?> expMatcher = getExpressionMatcher(((Expression)formula).getClass());
			if (expMatcher == null){
				return false;
			}
			// initiate matching process
			return expMatcher.match((Expression)formula, (Expression)pattern, initialBinding);
		}
		// case Predicate : use a predicate matcher
		else {
			// get the matcher from the map
			PredicateMatcher<?> predMatcher = getPredicateMatcher(((Predicate)formula).getClass());
			if (predMatcher == null){
				return false;
			}
			// initiate matching process
			return predMatcher.match((Predicate)formula, (Predicate) pattern, initialBinding);
		}
	}
	
	/**
	 * Returns an empty binding to start a matching process between <code>formula</code> and
	 * <code>pattern</code>.
	 * @param formula the formula
	 * @param pattern the pattern
	 * @param isPartialMatchAcceptable whether a partial match is acceptable
	 * @param factory the formula factory
	 * @return an empty binding
	 */
	public final IBinding createBinding(Formula<?> formula, Formula<?> pattern,
			boolean isPartialMatchAcceptable, FormulaFactory factory){
		return new Binding(formula, pattern, isPartialMatchAcceptable, factory);
	}
	
	/**
	 * Returns an empty binding that can be used to accumulate other bindings.
	 * <p> This binding is independent of the formula and pattern it matches if any.
	 * @param acceptPartialMatch whether to accept partial match
	 * @param factory the formula factory
	 * @return an empty binding
	 */
	public final IBinding createBinding(boolean acceptPartialMatch, FormulaFactory factory){
		return new Binding(acceptPartialMatch, factory);
	}
	
	private ExpressionMatcher<?> getExpressionMatcher(Class<? extends Expression> clazz){
		return EXPRESSION_MATCHERS.get(clazz);
	}
	
	private PredicateMatcher<?> getPredicateMatcher(Class<? extends Predicate> clazz){
		return PREDICATE_MATCHERS.get(clazz);
	}
	
	/**
	 * Returns the singleton instance of the factory.
	 * @return the singleton matching factory
	 */
	public static MatchingFactory getInstance(){
		if (instance == null){
			instance = new MatchingFactory();
		}
		return instance;
	}
	
	
}
