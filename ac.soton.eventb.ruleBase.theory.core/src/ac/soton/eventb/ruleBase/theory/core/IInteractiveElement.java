package ac.soton.eventb.ruleBase.theory.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for an element that can be attributed to be interactive.
 * <p>
 * Rewrite and inference rules can be tagged to be interactive, and therefore, 
 * available to the interactive prover.
 * </p>
 * <p>This interface is not intended to be implemented by clients.</p>
 * 
 * @author maamria
 *
 */
public interface IInteractiveElement extends IInternalElement{

	/**
	 * <p>Test whether the interactive attribute is set or not.</p>
	 * @return whether the interactive attribute is present
	 * @throws RodinDBException
	 */
	boolean hasInteractive() throws RodinDBException;
	/**
	 * <p>Returns whether the element is interactive.</p>
	 * @return whether the element is interactive
	 * @throws RodinDBException
	 */
	boolean isInteractive() throws RodinDBException;
	/**
	 * <p>Sets the interactive attribute to the new value <code>isInteractive</code>.</p>
	 * @param isInteractive the new value
	 * @param pm the progress monitor
	 * @throws RodinDBException
	 */
	void setInteractive(boolean isInteractive, IProgressMonitor pm)
		throws RodinDBException;
	
}
