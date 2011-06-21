/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.reasoning;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.pm.Matcher;
import org.eventb.core.pm.SimpleBinder;
import org.eventb.theory.rbp.rulebase.BaseManager;
import org.eventb.theory.rbp.rulebase.IPOContext;

/**
 * Common implementation of proof rules applyer.
 * @since 1.0
 * @author maamria
 *
 */
public class AbstractRulesApplyer {

	protected Matcher finder;
	protected SimpleBinder simpleBinder;
	protected BaseManager manager;
	protected FormulaFactory factory;
	
	protected IPOContext context;
	
	protected AbstractRulesApplyer(FormulaFactory factory, IPOContext context){
		this.manager = BaseManager.getDefault();
		this.factory = factory;
		this.finder = new Matcher(factory);
		this.simpleBinder = new SimpleBinder(factory);
		this.context = context;
	}
}
