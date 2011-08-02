/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.reasoners;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.ast.IFormulaRewriter;
import org.eventb.theory.rbp.plugin.RbPPlugin;
import org.eventb.theory.rbp.reasoning.ToClassicLanguageReducer;
import org.eventb.theory.rbp.rulebase.IPOContext;

/**
 * 
 * @author maamria
 * 
 */
public class ReduceToClassicLanguageReasoner extends AutoRewriteReasoner {

	private static final String REASONER_ID = RbPPlugin.PLUGIN_ID + ".reduceToClassicLanguageReasoner";
	
	private static final String DISPLAY_NAME = "RbP2Classic";
	
	public static List<String> usedReduceTheories = new ArrayList<String>();

	@Override
	public String getSignature() {
		return REASONER_ID;
	}

	@Override
	public String getReasonerID() {
		return REASONER_ID;
	}
	
	protected String getDisplayName() {
		return DISPLAY_NAME;
	}
	
	protected IFormulaRewriter getRewriter(IPOContext context){
		 return new ToClassicLanguageReducer(context);
	}
}
