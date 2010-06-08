package ac.soton.eventb.prover.engine;

import org.eventb.core.ast.Formula;

/**
 * Common protocol for a matching complement of an associative formula.
 * 
 * @author Issam Maamria
 *
 * @param <F> the class of the formula
 */
public interface AssociativeComplement<F extends Formula<F>> {

	/**
	 * Returns the tag of the associative formula.
	 * @return the tag
	 */
	public int getTag();
	
	/**
	 * Returns the formula to append.
	 * @return the formula to append
	 */
	public F getToAppend();
	
	/**
	 * Returns the formula to prepend.
	 * @return the formula to prepend
	 */
	public F getToPrepend();
	
}
