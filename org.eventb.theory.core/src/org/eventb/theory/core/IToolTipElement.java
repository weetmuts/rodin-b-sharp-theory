package org.eventb.theory.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;


/**
 * <p>Common protocol for internal elements that can have a tool tip.</p>
 * <p>This interface is not intended to be implemented by clients.</p>
 * 
 * @see IRewriteRule
 * @see IInferenceRule
 * 
 * @author maamria
 *
 */
public interface IToolTipElement extends IInternalElement{
	/**
	 * <p>Returns whether the tool tip attribute is set.</p>
	 * @return whether the attribute is set
	 * @throws RodinDBException
	 */
	boolean hasToolTip() throws RodinDBException;
	/**
	 * <p>Returns the tool tip.</p>
	 * @return the tool tip
	 * @throws RodinDBException
	 */
	String getToolTip() throws RodinDBException;
	/**
	 * <p>Sets the tool tip to the new value <code>newToolTip</code>.</p>
	 * @param newToolTip
	 * @param monitor the progress monitor
	 * @throws RodinDBException
	 */
	void setToolTip(String newToolTip, IProgressMonitor monitor) throws RodinDBException;
	
}
