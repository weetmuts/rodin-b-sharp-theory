package ac.soton.eventb.prover.engine;

import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;

import ac.soton.eventb.prover.utils.GeneralUtilities;


/**
 * A simple binder is implemented as a singeleton.
 * @author maamria
 *
 */
public class SimpleBinder {

	private static SimpleBinder simpleBinder;
	
	private FormulaFactory factory;
	
	private SimpleBinder(){
		factory = FormulaFactory.getDefault();
	}
	
	/**
	 * <p>Returns the formula resulting from applying <code>binding</code>
	 * to <code>pattern</code>.</p>
	 * @param pattern the pattern formula
	 * @param binding
	 * @return the new formula
	 */
	public Formula<?> applyBinding(Formula<?> pattern, IBinding binding){
		Formula<?> rhs = 
			GeneralUtilities.parseAndTypeFormulaString(pattern.toString(), 
			GeneralUtilities.isExpression(pattern), 
			factory);
		rhs.typeCheck(binding.getTypeEnvironment());
		return rhs.substituteFreeIdents(binding.getMappings(), FormulaFactory.getDefault());
	}
	
	/**
	 * Returns the singeleton binder instance.
	 * @return the binder instance
	 */
	public static SimpleBinder getDefault(){
		if(simpleBinder == null)
			simpleBinder =  new SimpleBinder();
		return simpleBinder;
	}
}
