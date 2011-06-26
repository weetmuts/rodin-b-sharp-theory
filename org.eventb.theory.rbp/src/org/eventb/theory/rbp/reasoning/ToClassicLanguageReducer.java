/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.reasoning;

import java.util.List;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.Predicate;
import org.eventb.theory.rbp.reasoners.ReduceToClassicLanguageReasoner;
import org.eventb.theory.rbp.rulebase.IPOContext;
import org.eventb.theory.rbp.rulebase.basis.IDeployedRewriteRule;

public class ToClassicLanguageReducer extends AutoRewriter{

	public ToClassicLanguageReducer(IPOContext context) {
		super(context);
	}
	
	protected List<IDeployedRewriteRule> getRules(Formula<?> original){
		List<IDeployedRewriteRule> rules = (original instanceof Expression ? 
				manager.getDefinitionalExpressionRules(((Expression)original).getClass(), context):
				manager.getDefinitionalPredicateRules(((Predicate)original).getClass(), context));
		return rules;
	}
	
	protected void addUsedTheory(String name){
		if(!ReduceToClassicLanguageReasoner.usedReduceTheories.contains(name))
			ReduceToClassicLanguageReasoner.usedReduceTheories.add(name);
	}

}
