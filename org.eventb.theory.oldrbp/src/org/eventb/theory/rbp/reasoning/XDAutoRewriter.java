/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.reasoning;

import java.util.List;

import org.eventb.core.ast.Formula;
import org.eventb.theory.core.IGeneralRule;
import org.eventb.theory.rbp.rulebase.IPOContext;

public class XDAutoRewriter extends AutoRewriter{

	public XDAutoRewriter(IPOContext context) {
		super(context);
	}
	
	protected List<IGeneralRule> getRules(Formula<?> original){
		List<IGeneralRule> rules = manager.getDefinitionalRules(original.getClass(), context);
		return rules;
	}

}
