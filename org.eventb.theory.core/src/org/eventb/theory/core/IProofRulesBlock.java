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
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for an internal element that can be used to group together a collection of proof rules.
 * <p> A proof block may defined metavariables, rewrite rules and inference rules.
 * 
 * @see IMetavariable
 * @see IRewriteRule
 * @see IInferenceRule
 * 
 * @author maamria
 *
 */
public interface IProofRulesBlock extends ILabeledElement, ICommentedElement{
	
	public IInternalElementType<IProofRulesBlock> ELEMENT_TYPE = 
		RodinCore.getInternalElementType(TheoryPlugin.PLUGIN_ID + ".proofRulesBlock");

	
	IMetavariable getMetavariable(String name);
	
	IMetavariable[] getMetavariables() throws RodinDBException;
	
	IRewriteRule getRewriteRule(String name);
	
	IRewriteRule[] getRewriteRules() throws RodinDBException;
	
	IInferenceRule getInferenceRule(String name);
	
	IInferenceRule[] getInferenceRules() throws RodinDBException;
	
}
