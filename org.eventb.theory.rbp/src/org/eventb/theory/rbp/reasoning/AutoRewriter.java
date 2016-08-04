package org.eventb.theory.rbp.reasoning;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
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
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IFormulaRewriter;
import org.eventb.core.ast.ISpecialization;
import org.eventb.core.ast.ITypeEnvironment;
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
import org.eventb.core.ast.extensions.pm.Matcher;
import org.eventb.theory.core.IGeneralRule;
import org.eventb.theory.core.ISCRewriteRule;
import org.eventb.theory.internal.rbp.reasoners.input.IPRMetadata;
import org.eventb.theory.rbp.rulebase.BaseManager;
import org.eventb.theory.rbp.rulebase.IPOContext;
import org.eventb.theory.rbp.rulebase.basis.IDeployedRewriteRule;
import org.eventb.theory.rbp.utils.ProverUtilities;

/**
 * <p>An implementation of a rewrite rule automatic rewriter.</p>
 * @author maamria
 * @author htson
 */
public class AutoRewriter implements IFormulaRewriter{
	
	private Set<Expression> instantiations;
	String ruleDescription;
	private IPRMetadata prMetadata;
	private IPOContext context;
	
	
	public AutoRewriter(IPOContext context, IPRMetadata prMetadata){
		this.context = context;
		this.prMetadata = prMetadata;
		this.instantiations = new LinkedHashSet<Expression>();
	}
	
	/**
	 * <p>Applies automatic expression rewrite rules to the given expression.</p>
	 * @param original to rewrite
	 * @return the rewritten expression
	 */
	protected Expression applyExpressionRewrites(Expression original){
		Expression expression = ((Expression) applyRule(original));
		if(original.equals(expression)){
			return original;
		}
		// return rewritten
		return expression;
	}

	/**
	 * <p>Applies automatic predicate rewrite rules to the given predicate.</p>
	 * @param original to rewrite
	 * @return the rewritten predicate
	 */
	protected Predicate applyPredicateRewrites(Predicate original){
		Predicate pred = ((Predicate) applyRule(original));
		if(original.equals(pred)){
			return original;
		}
		// return rewritten
		return pred;
	}
	
	/**
	 * Returns the formula resulting from applying all the (applicable) 
	 * automatic unconditional rules.
	 * @param original the formula to rewrite
	 * @return the rewritten formula
	 */
	private Formula<?> applyRule(Formula<?> original){
		IGeneralRule rule = getRule(original);
		if (rule == null) 
			return original;
		
		Formula<?> ruleLhs = null;
		Formula<?> ruleRhs = null;
		FormulaFactory factory = context.getFormulaFactory();
		
		if (rule instanceof IDeployedRewriteRule) {
			ruleLhs = ((IDeployedRewriteRule) rule).getLeftHandSide();
			ruleRhs = ((IDeployedRewriteRule) rule).getRightHandSides().get(0).getRHSFormula();
			ruleLhs = ruleLhs.translate(factory);
			ruleRhs = ruleRhs.translate(factory);
			ruleDescription = ((IDeployedRewriteRule) rule).getDescription();
		}
		else { //if (rule instanceof ISCRewriteRule) {
			try {
				ITypeEnvironment typeEnvironment = ProverUtilities
						.makeTypeEnvironment(factory, (ISCRewriteRule) rule);
				ruleLhs = ((ISCRewriteRule) rule).getSCFormula(factory,
						typeEnvironment);
				ruleRhs = Arrays.asList(((ISCRewriteRule) rule).getRuleRHSs())
						.get(0).getSCFormula(factory, typeEnvironment);
			} catch (CoreException e) {
				e.printStackTrace();
				return original;
			}
			;
		}
		
		ISpecialization specialization = Matcher.match(original, ruleLhs);
		if (specialization == null) {
			return original;
		}
		// since rule is unconditional
		if (!ProverUtilities.canBeSpecialized(specialization, ruleRhs)) {
			return original;
		}

		Set<Expression> expressions = ProverUtilities
				.getInstantiatingExpressions(specialization);
		instantiations.addAll(expressions);
		Formula<?> boundRhs = ruleRhs.specialize(specialization);
		return boundRhs;
	}
	
	/**
	 * Returns the list of rewrite rules to apply.
	 * 
	 * <p> Override this method to specify different list of rules.
	 * @param original
	 * @return
	 */
	protected IGeneralRule getRule(Formula<?> original){
		BaseManager manager = BaseManager.getDefault();
		String projectName = prMetadata.getProjectName();
		String theoryName = prMetadata.getTheoryName();
		String ruleName = prMetadata.getRuleName();
		IGeneralRule rule = manager.getRewriteRule(true, projectName, ruleName,
				theoryName, original.getClass(), context);
		return rule;
	}
	
	@Override
	public Expression rewrite(AssociativeExpression expression) {
		return applyExpressionRewrites(expression);
	}
	
	@Override
	public Predicate rewrite(AssociativePredicate predicate) {
		return applyPredicateRewrites(predicate);
	}
	
	@Override
	public Expression rewrite(AtomicExpression expression) {
		return applyExpressionRewrites(expression);
	}

	@Override
	public Expression rewrite(BinaryExpression expression) {
		return applyExpressionRewrites(expression);
	}

	@Override
	public Predicate rewrite(BinaryPredicate predicate) {
		return applyPredicateRewrites(predicate);
	}

	@Override
	public Expression rewrite(BoolExpression expression) {
		return applyExpressionRewrites(expression);
	}

	@Override
	public Expression rewrite(BoundIdentifier identifier) {
		// DO NOT REWRITE IDENTIFIER
		return identifier;
	}

	@Override
	public Expression rewrite(FreeIdentifier identifier) {
		// DO NOT REWRITE IDENTIFIER
		return identifier;
	}

	@Override
	public Expression rewrite(IntegerLiteral literal) {
		return applyExpressionRewrites(literal);
	}

	@Override
	public Predicate rewrite(LiteralPredicate predicate) {
		return applyPredicateRewrites(predicate);
	}

	@Override
	public Predicate rewrite(MultiplePredicate predicate) {
		return applyPredicateRewrites(predicate);
	}

	@Override
	public Expression rewrite(QuantifiedExpression expression) {
		return applyExpressionRewrites(expression);
	}

	@Override
	public Predicate rewrite(QuantifiedPredicate predicate) {
		return applyPredicateRewrites(predicate);
	}

	@Override
	public Predicate rewrite(RelationalPredicate predicate) {
		return applyPredicateRewrites(predicate);
	}

	@Override
	public Expression rewrite(SetExtension expression) {
		return applyExpressionRewrites(expression);
	}

	@Override
	public Predicate rewrite(SimplePredicate predicate) {
		return applyPredicateRewrites(predicate);
	}
	
	@Override
	public Predicate rewrite(ExtendedPredicate predicate) {
		return applyPredicateRewrites(predicate);
	}
	
	@Override
	public Expression rewrite(ExtendedExpression expression) {
		return applyExpressionRewrites(expression);
	}

	@Override
	public Expression rewrite(UnaryExpression expression) {
		return applyExpressionRewrites(expression);
	}

	@Override
	public Predicate rewrite(UnaryPredicate predicate) {
		return applyPredicateRewrites(predicate);
	}

	@Override
	public boolean autoFlatteningMode() {
		return false;
	}
	
	@Override
	public void enteringQuantifier(int nbOfDeclarations) {
		// nothing to do
	}

	@Override
	public void leavingQuantifier(int nbOfDeclarations) {
		// nothing to do
	}

	/**
	 * @return
	 */
	public Set<Expression> getInstantiations() {
		return instantiations;
	}

	/**
	 * @return
	 */
	public String getDescription() {
		return ruleDescription;
	}
}
