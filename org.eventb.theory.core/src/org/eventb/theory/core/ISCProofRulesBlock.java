/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core;

import org.eventb.core.ILabeledElement;
import org.eventb.core.ITraceableElement;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * @author maamria
 *
 */
public interface ISCProofRulesBlock extends ILabeledElement, ITraceableElement {

	public IInternalElementType<ISCProofRulesBlock> ELEMENT_TYPE = 
		RodinCore.getInternalElementType(TheoryPlugin.PLUGIN_ID + ".scProofRulesBlock");

	
	ISCMetavariable getMetavariable(String name);
	
	ISCMetavariable[] getMetavariables() throws RodinDBException;
	
	ISCRewriteRule getRewriteRule(String name);
	
	ISCRewriteRule[] getRewriteRules() throws RodinDBException;
	
	ISCInferenceRule getInferenceRule(String name);
	
	ISCInferenceRule[] getInferenceRules() throws RodinDBException;
}
