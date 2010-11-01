/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eventb.core.IPSStatus;
import org.eventb.core.seqprover.IConfidence;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

public class TheoryCoreFacadeGeneral {

	/**
	 * Returns a string representation of the given list of objects.
	 * 
	 * @param list
	 *            the list of strings
	 * @return the representing string
	 */
	public static <E> String toString(List<E> list) {
		String result = "";
		for (int i = 0; i < list.size(); i++) {
			result += list.get(i).toString();
			if (i < list.size() - 1) {
				result += ", ";
			}
		}
		return result;
	}

	/**
	 * Returns a set representation of the array of the given elements.
	 * 
	 * @param <E>
	 *            the type of elements
	 * @param elements
	 *            the actual elements
	 * @return the set
	 */
	public static <E extends IInternalElement> Set<E> getSet(E[] elements) {
		Set<E> set = new LinkedHashSet<E>();
		set.addAll(Arrays.asList(elements));
		return set;
	}

	/**
	 * Returns a new core exception.
	 * 
	 * @param message
	 *            the message
	 * @return the core exception
	 */
	public static CoreException newCoreException(String message) {
		return new CoreException(new Status(IStatus.ERROR,
				TheoryPlugin.PLUGIN_ID, message));
	}

	/**
	 * Returns a new rodin DB exception.
	 * 
	 * @param message
	 *            the message
	 * @return the rodin DB exception
	 */
	public static RodinDBException newDBException(String message) {
		return new RodinDBException(newCoreException(message));
	}

	/**
	 * Returns whether the given proof status is of discharged status.
	 * 
	 * @param status
	 *            proof status
	 * @return whether status PO has been discharged
	 * @throws RodinDBException
	 */
	public static boolean isDischarged(IPSStatus status)
			throws RodinDBException {
		return (status.getConfidence() > IConfidence.REVIEWED_MAX)
				&& (!status.isBroken());
	}

	/**
	 * Returns whether the given proof status is of reviewed status.
	 * 
	 * @param status
	 *            proof status
	 * @return whether status PO has been reviewed
	 * @throws RodinDBException
	 */
	public static boolean isReviewed(IPSStatus status) throws RodinDBException {
		return (status.getConfidence() > IConfidence.PENDING)
				&& (status.getConfidence() <= IConfidence.REVIEWED_MAX);
	}
}
