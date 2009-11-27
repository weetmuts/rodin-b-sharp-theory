/*******************************************************************************
 * Copyright (c) 2007 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package ac.soton.eventb.ruleBase.theory.ui.editor.markers;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IRodinElement;

/**
 * @author htson
 *         <p>
 *         The "supposed" interface for markers registry. This is supposed to be
 *         implemented using the RodinDB.
 *         </p>
 */
public interface IMarkerRegistry {

	/**
	 * Get the list of markers attached to a rodin element for a paricular
	 * attribute type.
	 * 
	 * @param element
	 *            a Rodin element, this must not be <code>null</code>.
	 * @param attributeType
	 *            an attribute type, this must not be <code>null</code>
	 * @return an array of markers associated with the input element for input
	 *         attribute type.
	 * @throws CoreException
	 *             if some problems occur
	 */
	public abstract IMarker[] getAttributeMarkers(IRodinElement element,
			IAttributeType attributeType) throws CoreException;

	/**
	 * Get the list of markers attached to a rodin element.
	 * 
	 * @param element
	 *            a Rodin element, this must not be <code>null</code>.
	 * @return an array of markers associated with the input element.
	 * @throws CoreException
	 *             if some problems occur
	 */
	public abstract IMarker[] getMarkers(IRodinElement element)
			throws CoreException;

	/**
	 * Get the maximum severity for markers for a particular element.
	 * 
	 * @param element
	 *            a Rodin element, this must not be <code>null</code>.
	 * 
	 * @return Return the maximum severity of the markers found. Return -1 if no
	 *         markers found.
	 * @throws CoreException
	 *             if some problems occur
	 */
	public abstract int getMaxMarkerSeverity(IRodinElement element)
			throws CoreException;

	/**
	 * Get the maximum severity for markers for an attribute type for a
	 * particular element.
	 * 
	 * @param element
	 *            a Rodin element, this must not be <code>null</code>.
	 * @param attributeType
	 *            an attribute type, this must not be <code>null</code>
	 * @return Return the maximum severity of the markers found. Return -1 if no
	 *         markers found.
	 * @throws CoreException
	 *             if some problems occur
	 */
	public abstract int getMaxMarkerSeverity(IRodinElement element,
			IAttributeType attributeType) throws CoreException;

}
