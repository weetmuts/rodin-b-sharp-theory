/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.reasoning;

import org.eventb.core.ast.extensions.pm.Matcher;
import org.eventb.theory.rbp.rulebase.BaseManager;
import org.eventb.theory.rbp.rulebase.IPOContext;

/**
 * Common implementation of proof rules applyer.
 * 
 * <p> This class is intended to provide fields that are common to rule applyers.
 * 
 * @since 1.0
 * @author maamria
 *
 */
public abstract class AbstractRulesApplyer {

	protected Matcher finder;
	protected BaseManager manager;
	protected IPOContext context;
	
	protected AbstractRulesApplyer(IPOContext context){
		this.manager = BaseManager.getDefault();
		this.finder = new Matcher(context.getFormulaFactory());
		this.context = context;
	}
}
