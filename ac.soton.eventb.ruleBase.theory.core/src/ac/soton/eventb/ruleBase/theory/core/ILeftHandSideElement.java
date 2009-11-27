package ac.soton.eventb.ruleBase.theory.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for an internal element that has a left hand side theory formula.
 * <p>Theory formulas can either be an expression or a predicate.</p>
 * <p>This interface is not intended to be implemented by clients.</p>
 * @author maamria
 *
 */

public interface ILeftHandSideElement extends IInternalElement {
	/**
	 * <p>Returns the value of lhs attribute.</p>
	 * @return lhs value
	 * @throws RodinDBException
	 */
	String getLHSString() throws RodinDBException;
	/**
	 * <p>Returns whether the lhs attribute is set.</p>
	 * @return whether the element lhs attribute is present
	 * @throws RodinDBException
	 */
	boolean hasLHSString() throws RodinDBException;
	/**
	 * <p>Sets the lhs to the new value <code>form</code>.</p>
	 * @param form the new lhs
	 * @param pm the progress monitor
	 * @throws RodinDBException
	 */
	void setLHSString(String form, IProgressMonitor pm) throws RodinDBException;
}
