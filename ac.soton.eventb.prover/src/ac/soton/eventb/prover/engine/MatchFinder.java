package ac.soton.eventb.prover.engine;

import org.eventb.core.ast.Formula;

import ac.soton.eventb.prover.internal.engine.Binding;
import ac.soton.eventb.prover.internal.engine.MatcherEngine;

/**
 * <p>The match finder establishes whether two formulas (say a pattern and a formula) are matchable. </p>
 * If the pattern and the formula are matchable, a binding that when applied to the pattern will result in the formula is calculated.<p>
 * @author maamria
 *
 */
public class MatchFinder {

	private static MatchFinder instance;
	
	private MatcherEngine engine;
	
	private MatchFinder(){
		engine =  MatcherEngine.getDefault();
	}
	
	/**
	 * <p> This encapsulates the initial call to construct a binding and populate it.</p>
	 * @param form
	 * @param pattern
	 * @return the binding or <code>null</code> if the matching failed
	 */
	public IBinding calculateBindings(Formula<?> form, Formula<?> pattern){
		IBinding initialBinding = Binding.createBinding();
		
		if(!engine.match(form, pattern, initialBinding)){
			return null;
		}
		initialBinding.makeImmutable();
		return initialBinding;
	}
	/**
	 * Returns the singeleton finder instance.
	 * @return singeleton instance
	 */
	public static MatchFinder getDefault(){
		if(instance == null)
			instance = new MatchFinder();
		return instance;
	}
}
