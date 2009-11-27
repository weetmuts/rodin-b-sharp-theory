package ac.soton.eventb.ruleBase.theory.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.RodinDBException;

/**
 * <p>Common protocol for internal elements that can have a description.</p>
 * <p>This interface is not intended to be implemented by clients.</p>
 * @author maamria
 *
 */
public interface IDescriptionElement {

	/**
	 * <p>Returns whether the description attribute is set.</p>
	 * @return whether the attribute is set
	 * @throws RodinDBException
	 */
	boolean hasDescription() throws RodinDBException;
	/**
	 * <p>Returns the description.</p>
	 * @return the description
	 * @throws RodinDBException
	 */
	String getDescription() throws RodinDBException;
	/**
	 * <p>Sets the Description to the new value <code>newDescription</code>.</p>
	 * @param newDescription
	 * @param monitor the progress monitor
	 * @throws RodinDBException
	 */
	void setDescription(String newDescription, IProgressMonitor monitor) throws RodinDBException;
}
