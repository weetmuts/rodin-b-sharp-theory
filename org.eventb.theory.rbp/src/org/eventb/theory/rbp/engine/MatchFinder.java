package org.eventb.theory.rbp.engine;

import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.theory.rbp.internal.engine.MatchingFactory;


/**
 * <p>The match finder establishes whether two formulas (say a pattern and a formula) are matchable. </p>
 * If the pattern and the formula are matchable, a binding that when applied to the pattern will result in the formula is calculated.<p>
 * @author maamria
 *
 */
public class MatchFinder {

	private FormulaFactory factory;
	
	public MatchFinder(FormulaFactory factory){
		this.factory = factory;
	}
	
	/**
	 * <p> This encapsulates the initial call to construct a binding and populate it.</p>
	 * @param form
	 * @param pattern
	 * @return the binding or <code>null</code> if the matching failed
	 */
	public IBinding calculateBindings(Formula<?> form, Formula<?> pattern, boolean acceptPartialMatch){
		IBinding initialBinding = MatchingFactory.createBinding(form, pattern, acceptPartialMatch, factory);
		
		if(!MatchingFactory.match(form, pattern, initialBinding)){
			return null;
		}
		initialBinding.makeImmutable();
		return initialBinding;
	}
	
}
