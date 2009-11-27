package ac.soton.eventb.ruleBase.theory.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

/**
 * <p>Common interface for internal elements that can have a type.</p>
 * <p>This interface is not intended to be implemented by clients.</p>
 * @author maamria
 *
 */
public interface ITypingElement extends IInternalElement{
	/**
	 * <p>Returns whether the typing attribute of this element is set.</p>
	 * @return whether the typing attribute is set
	 * @throws RodinDBException
	 */
	boolean hasTypingString() throws RodinDBException;
	/**
	 * <p>Returns the typing attribute value of this element.</p>
	 * @return the typing attribute
	 * @throws RodinDBException
	 */
	String getTypingString() throws RodinDBException;
	/**
	 * <p>Sets the typing attribute to the new value <code>expression</code>.</p>
	 * @param expression
	 * @param monitor the progress monitor
	 * @throws RodinDBException
	 */
	void setTypingString(String expression, IProgressMonitor monitor) throws RodinDBException;
	
}
