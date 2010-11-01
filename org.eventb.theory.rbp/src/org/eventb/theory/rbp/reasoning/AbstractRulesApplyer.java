/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.reasoning;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.theory.rbp.base.IRuleBaseManager;
import org.eventb.theory.rbp.base.RuleBaseManager;
import org.eventb.theory.rbp.engine.MatchFinder;
import org.eventb.theory.rbp.engine.SimpleBinder;

/**
 * Common implementation of proof rules applyer.
 * @since 1.0
 * @author maamria
 *
 */
public class AbstractRulesApplyer {

	protected MatchFinder finder;
	protected SimpleBinder simpleBinder;
	protected IRuleBaseManager manager;
	protected FormulaFactory factory;
	
	protected AbstractRulesApplyer(FormulaFactory factory){
		this.manager = RuleBaseManager.getDefault();
		this.factory = factory;
		this.finder = new MatchFinder(factory);
		this.simpleBinder = new SimpleBinder(factory);
		
		
	}
}
