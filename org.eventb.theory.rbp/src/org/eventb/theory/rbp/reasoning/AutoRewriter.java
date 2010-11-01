package org.eventb.theory.rbp.reasoning;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.BoolExpression;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.DefaultRewriter;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.ExtendedPredicate;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IFormulaRewriter;
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
import org.eventb.theory.rbp.rewriting.RewriteRuleAutoApplyer;

/**
 * <p>An implementation of a rewrite rule automatic rewriter.</p>
 * @author maamria
 * @see DefaultRewriter
 */
public class AutoRewriter implements IFormulaRewriter{
	
	private RewriteRuleAutoApplyer applyer ;
	private FormulaFactory factory;

	
	public void setFormulaFactory(FormulaFactory factory){
		this.factory = factory;
		this.applyer = new RewriteRuleAutoApplyer(factory);
		
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
		// TODO Auto-generated method stub
		return identifier;
	}

	@Override
	public Expression rewrite(FreeIdentifier identifier) {
		// TODO Auto-generated method stub
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

	/**
	 * <p>Applies automatic expression rewrite rules to the given expression.</p>
	 * @param original to rewrite
	 * @return the rewritten expression
	 */
	protected Expression applyExpressionRewrites(Expression original){
		Expression expression = ((Expression) applyer.applyRules(original));
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
		Predicate pred = ((Predicate) applyer.applyRules(original));
		if(original.equals(pred)){
			return original;
		}
		// return rewritten
		return pred;
	}

	@Override
	public boolean autoFlatteningMode() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public void enteringQuantifier(int nbOfDeclarations) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public FormulaFactory getFactory() {
		// TODO Auto-generated method stub
		return factory;
	}

	@Override
	public void leavingQuantifier(int nbOfDeclarations) {
		// TODO Auto-generated method stub
	}
}
