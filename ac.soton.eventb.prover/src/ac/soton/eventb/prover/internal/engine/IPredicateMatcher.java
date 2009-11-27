package ac.soton.eventb.prover.internal.engine;

import org.eventb.core.ast.Predicate;

import ac.soton.eventb.prover.engine.IBinding;

/**
 * <p>Common protocol for a predicate matcher.</p>
 * @author maamria
 */
public interface IPredicateMatcher {

	/**
	 * <p>Augments the given <code>existingBinding</code> with new binding as it traverses <code>pattern</code> and <code>form</code>.</p>
	 * @param form the original formula
	 * @param pattern
	 * @param existingBinding
	 * @return whether the matching succeeded
	 */
	public  boolean match(Predicate form, Predicate pattern, IBinding existingBinding);
}
