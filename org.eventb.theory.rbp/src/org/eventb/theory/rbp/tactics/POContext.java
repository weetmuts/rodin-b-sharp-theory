/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.tactics;

import org.eventb.core.IEventBRoot;
import org.eventb.core.IPSStatus;
import org.eventb.theory.core.DatabaseUtilities;

/**
 * 
 * @author maamria
 *
 */
public class POContext {

	private IPSStatus psStatus;
	
	public POContext(IPSStatus psStatus){
		this.psStatus = psStatus;
	}
	
	public IEventBRoot getParentRoot(){
		return (IEventBRoot) psStatus.getRoot();
	}
	
	public boolean isTheoryRelated(){
		return DatabaseUtilities.originatedFromTheory(psStatus.getRodinFile());
	}
	
	public boolean inMathExtensions(){
		return DatabaseUtilities.isMathExtensionsProject(psStatus.getRodinProject());
	}
	
}
