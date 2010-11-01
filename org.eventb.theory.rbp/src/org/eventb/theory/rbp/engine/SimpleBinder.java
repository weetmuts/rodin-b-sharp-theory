package org.eventb.theory.rbp.engine;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.theory.rbp.internal.engine.PredicateVariableSubstituter;
import org.eventb.theory.rbp.utils.ProverUtilities;

/**
 * A simple binder.
 * 
 * @since 1.0
 * 
 * @author maamria
 * 
 */
public class SimpleBinder {

	private FormulaFactory factory;

	public SimpleBinder(FormulaFactory factory) {
		this.factory = factory;
	}

	/**
	 * Returns the formula resulting from binding the pattern to the given binding.
	 * @param pattern the pattern
	 * @param binding the binding
	 * @param includeComplement whether associative complements should be considered
	 * @return
	 */
	public Formula<?> bind(Formula<?> pattern, IBinding binding,
			boolean includeComplement) {
		Formula<?> resultFormula = ProverUtilities.parseFormulaString(
				pattern.toString(), ProverUtilities.isExpression(pattern),
				factory);
		Formula<?> finalResultFormula = resultFormula
				.rewrite(new PredicateVariableSubstituter(binding
						.getPredicateMappings(), factory));
		finalResultFormula.typeCheck(binding.getTypeEnvironment());
		Formula<?> formula = finalResultFormula.substituteFreeIdents(
				binding.getMappings(), factory);

		if (includeComplement) {
			if (formula instanceof Expression) {
				AssociativeExpressionComplement comp;
				if ((comp = binding.getAssociativeExpressionComplement()) != null) {
					// if ac, then use toAppend
					Expression e1 = comp.getToAppend();
					Expression e2 = comp.getToPrepend();
					int tag = comp.getTag();
					return ProverUtilities.makeAssociativeExpression(tag,
							new Expression[] { e1, (Expression) formula, e2 },
							factory);

				}
			}
			else if(formula instanceof Predicate){
				AssociativePredicateComplement comp;
				if ((comp = binding.getAssociativePredicateComplement()) != null) {
					// if ac, then use toAppend
					Predicate e1 = comp.getToAppend();
					Predicate e2 = comp.getToPrepend();
					int tag = comp.getTag();
					return ProverUtilities.makeAssociativePredicate(tag,
							new Predicate[] { e1, (Predicate) formula, e2 },
							factory);

				}
			}
		}
		return formula;
	}

}
