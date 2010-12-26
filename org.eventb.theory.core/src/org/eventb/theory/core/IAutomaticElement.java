package org.eventb.theory.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for an element (proof rules) that can be attributed to be automatic.
 * <p>
 * Rewrite and inference rules can be tagged to be automatic, and therefore, 
 * available to the automatic prover.
 * </p>
 * This interface is not intended to be implemented by clients.
 * 
 * @author maamria
 *
 */
public interface IAutomaticElement extends IInternalElement {
	/**
	 * <p>
	 * Tests whether the rule automatic attribute is present.
	 * </p>
	 * @return whether the rule is automatic
	 * @throws RodinDBException
	 */
	public boolean hasAutomatic() throws RodinDBException;

	/**
	 * 
	 * <p>Gets the value of the automatic attribute of a rule.</p>
	 * 
	 * @return whether the rule is automatic.
	 * @throws RodinDBException
	 */
	public boolean isAutomatic() throws RodinDBException;

	/**
	 * <p> Sets the automatic attribute of the rule to the specified value of <code>auto</code></p>
	 * @param auto the new value
	 * @param pm the progress monitor
	 * @throws RodinDBException
	 */
	public void setAutomatic(boolean auto, IProgressMonitor pm)
			throws RodinDBException;
}
