package ac.soton.eventb.prover.engine;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;

import ac.soton.eventb.prover.utils.ProverUtilities;

/**
 * A simple binder is implemented as a singeleton.
 * 
 * @author maamria
 * 
 */
public class SimpleBinder {

	private static SimpleBinder simpleBinder;

	private FormulaFactory factory;

	private SimpleBinder() {
		factory = FormulaFactory.getDefault();
	}

	public Formula<?> bind(Formula<?> pattern, IBinding binding, boolean isPatternRHS){
		Formula<?> rhs = ProverUtilities.parseAndTypeFormulaString(pattern
				.toString(), ProverUtilities.isExpression(pattern), factory);
		rhs.typeCheck(binding.getTypeEnvironment());
		Formula<?> formula = rhs.substituteFreeIdents(binding
				.getMappings(), FormulaFactory.getDefault());
		if(isPatternRHS){
			AssociativeExpressionComplement comp;
			if ((comp = binding.getAssociativeExpressionComplement()) != null) {
				// if ac, then use toAppend
				Expression e1 = comp.getToAppend();
				Expression e2 = comp.getToPrepend();
				int tag = comp.getTag();
				return ProverUtilities.makeAssociativeExpression(
						tag, new Expression[]{e1, (Expression)formula, e2}, factory);
				
			}
		}
		return formula;
	}

	/**
	 * Returns the singeleton binder instance.
	 * 
	 * @return the binder instance
	 */
	public static SimpleBinder getDefault() {
		if (simpleBinder == null)
			simpleBinder = new SimpleBinder();
		return simpleBinder;
	}
}
