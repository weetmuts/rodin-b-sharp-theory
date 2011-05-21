/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.rulebase;

import org.eventb.core.IEventBRoot;

/**
 * Common protocol for proof obligation contextual information.
 * @author maamria
 * @since 1.0
 *
 */
public interface IPOContext {
	
	public IEventBRoot getParentRoot();
	
	public boolean isTheoryRelated();
	
	public boolean inMathExtensions();
	
}
