/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core;

/**
 * Common protocol for a result of a deployment operation.
 * 
 * @author maamria
 *
 */
public interface IDeploymentResult {
	
	/**
	 * Returns whether the deployment operation has succeeded.
	 * @return whether the deployment operation has succeeded
	 */
	public boolean succeeded();
	
	/**
	 * Returns the error message if the deployment has not succeeded. This method is
	 * required to return a <code>null</code> value if deployment indeed succeeded.
	 * @return the error message
	 */
	public String getErrorMessage();

}
