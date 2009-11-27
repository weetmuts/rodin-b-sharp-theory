package ac.soton.eventb.prover.rewriter;

import java.util.List;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.BoolExpression;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IFormulaFilter;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.MultiplePredicate;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.ast.UnaryPredicate;

import ac.soton.eventb.prover.internal.rewriter.RuleBaseSelector;
import ac.soton.eventb.prover.internal.tactic.RewriteRuleApplicabilityInfo;

/**
 * <p>An implementation of formula filter for the rule-based prover.</p>
 * <p>It provides the filtering capability plus it keeps track of the set of rules that are applicable at the selected positions.</p>
 * @author maamria
 *
 */
public class RuleBaseFormulaFilter implements IFormulaFilter{

	/**
	 * The formula selector
	 */
	private RuleBaseSelector ruleBaseSelector;
	
	/**
	 * <p>Constructs a formula filter where <code>emptyInfos</code> will be used as a placeholder for applicable rules.</p>
	 * @param emptyInfos must not be null
	 */
	public RuleBaseFormulaFilter(List<RewriteRuleApplicabilityInfo> emptyInfos){
		ruleBaseSelector = new RuleBaseSelector(emptyInfos);
	}
	
	@Override
	public boolean select(AssociativeExpression expression) {
		return ruleBaseSelector.select(expression);
	}

	@Override
	public boolean select(AssociativePredicate predicate) {
		return ruleBaseSelector.select(predicate);
	}

	@Override
	public boolean select(AtomicExpression expression) {
		return ruleBaseSelector.select(expression);
	}

	@Override
	public boolean select(BinaryExpression expression) {
		return ruleBaseSelector.select(expression);
	}

	@Override
	public boolean select(BinaryPredicate predicate) {
		return ruleBaseSelector.select(predicate);
	}

	@Override
	public boolean select(BoolExpression expression) {
		return ruleBaseSelector.select(expression);
	}

	@Override
	public boolean select(BoundIdentDecl decl) {
		return false;
	}

	@Override
	public boolean select(BoundIdentifier identifier) {
		return false;
	}

	@Override
	public boolean select(FreeIdentifier identifier) {
		return false;
	}

	@Override
	public boolean select(IntegerLiteral literal) {
		return ruleBaseSelector.select(literal);
	}

	@Override
	public boolean select(LiteralPredicate predicate) {
		return ruleBaseSelector.select(predicate);
	}

	@Override
	public boolean select(MultiplePredicate predicate) {
		return ruleBaseSelector.select(predicate);
	}

	@Override
	public boolean select(QuantifiedExpression expression) {
		return ruleBaseSelector.select(expression);
	}

	@Override
	public boolean select(QuantifiedPredicate predicate) {
		return ruleBaseSelector.select(predicate);
	}

	@Override
	public boolean select(RelationalPredicate predicate) {
		return ruleBaseSelector.select(predicate);
	}

	@Override
	public boolean select(SetExtension expression) {
		return ruleBaseSelector.select(expression);
	}

	@Override
	public boolean select(SimplePredicate predicate) {
		return ruleBaseSelector.select(predicate);
	}

	@Override
	public boolean select(UnaryExpression expression) {
		return ruleBaseSelector.select(expression);
	}

	@Override
	public boolean select(UnaryPredicate predicate) {
		return ruleBaseSelector.select(predicate);
	}
}
