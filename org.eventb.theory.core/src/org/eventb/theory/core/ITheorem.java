/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core;

import org.eventb.core.ICommentedElement;
import org.eventb.core.ILabeledElement;
import org.eventb.core.IPredicateElement;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;

/**
 * Common protocol for a theory theorem.
 * 
 * @author maamria
 *
 */
public interface ITheorem extends ILabeledElement, IPredicateElement, ICommentedElement{
	
	IInternalElementType<ITheorem> ELEMENT_TYPE =
		RodinCore.getInternalElementType(TheoryPlugin.PLUGIN_ID + ".theorem");

}