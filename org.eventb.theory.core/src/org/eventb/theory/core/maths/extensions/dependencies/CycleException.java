/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.maths.extensions.dependencies;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eventb.theory.core.plugin.TheoryPlugin;

/**
 * A checked exception that is used to indicate a cycle exists in a DAG. 
 * 
 * @author maamria
 *
 */
public class CycleException extends CoreException{

	private static final long serialVersionUID = -3845379303201073089L;
	
	public CycleException(){
		super(new Status(IStatus.ERROR, TheoryPlugin.PLUGIN_ID, "Cycle exists in the dependency graph."));

	}
}
