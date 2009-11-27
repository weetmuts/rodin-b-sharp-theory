package ac.soton.eventb.ruleBase.theory.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for an internal element that has a right hand side theory formula.
 * <p>Theory formulas can either be an expression or a predicate.</p>
 * <p>This interface is not intended to be implemented by clients.</p>
 * @author maamria
 *
 */
public interface IRightHandSideElement extends IInternalElement {

	/**
	 * <p>Returns the value of lhs attribute.</p>
	 * @return rhs value
	 * @throws RodinDBException
	 */
	String getRHSString() throws RodinDBException;

	/**
	 * <p>Returns whether the rhs attribute is set.</p>
	 * @return whether the element lhs attribute is present
	 * @throws RodinDBException
	 */
	boolean hasRHSString() throws RodinDBException;
	/**
	 * <p>Sets the rhs to the new value <code>form</code>.</p>
	 * @param form the new rhs
	 * @param pm the progress monitor
	 * @throws RodinDBException
	 */
	void setRHSString(String form, IProgressMonitor pm) throws RodinDBException;
}
