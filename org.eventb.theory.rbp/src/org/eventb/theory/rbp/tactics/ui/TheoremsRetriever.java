/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.tactics.ui;

import java.util.List;
import java.util.Map;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.theory.core.IExtensionRulesSource;
import org.eventb.theory.rbp.rulebase.BaseManager;
import org.eventb.theory.rbp.rulebase.IPOContext;
import org.eventb.theory.rbp.rulebase.basis.IDeployedTheorem;

/**
 * 
 * @author maamria
 *
 */
public class TheoremsRetriever {

	private IPOContext poContext;
	private BaseManager baseManager;
	private FormulaFactory factory;

	public TheoremsRetriever(IPOContext poContext, FormulaFactory factory){
		this.poContext = poContext;
		this.factory = factory;
		baseManager = BaseManager.getDefault();
	}
	
	public Map<IExtensionRulesSource, List<IDeployedTheorem>> getTheorems(){
		return baseManager.getTheorems(poContext, factory);
	}
	
}
