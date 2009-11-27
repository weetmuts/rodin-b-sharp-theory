package ac.soton.eventb.ruleBase.theory.deploy.deployer;

import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for internal elements that can be attributed to be sound.
 * <p>
 * @author maamria
 *
 */
public interface ISoundElement extends IInternalElement {

	/**
	 * Returns whether the element has the soundness attribute.
	 * <p>
	 * @return whether soundness attr is set
	 * @throws RodinDBException if a problem occurred
	 */
	boolean hasSound() throws RodinDBException;
	
	/**
	 * <p>Returns whether the element is sound.</p>
	 * @return whether the element is sound
	 * @throws RodinDBException if a problem occurred
	 */
	boolean isSound() throws RodinDBException;
	
	/**
	 * <p>Sets the soundness attribute of the internal element to the new value <code>isSound</code>.</p>
	 * @param isSound new value
	 * @param monitor the progress monitor
	 * @throws RodinDBException if a problem occurred
	 */
	void setSound(boolean isSound, IProgressMonitor monitor) throws RodinDBException;
}
