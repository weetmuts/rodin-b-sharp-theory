package ac.soton.eventb.prover.internal.engine;

import org.eventb.core.ast.Expression;

import ac.soton.eventb.prover.engine.IBinding;

/**
 * <p>Common protocol for an expression matcher.</p>
 * TODO there should not be a distnction between the two...
 * @author maamria
 */
public interface IExpressionMatcher {
	
	/**
	 * <p>Augments the given <code>existingBinding</code> with new binding as it traverses <code>pattern</code> and <code>form</code>.</p>
	 * <p> Callers should ensure that at runtime <code>form</code> and <code>pattern</code> are of the same class.<p>
	 * @param form the original formula
	 * @param pattern
	 * @param existingBinding
	 * @return whether the matching succeeded
	 */
	public  boolean match(Expression form, Expression pattern, IBinding existingBinding);
}
