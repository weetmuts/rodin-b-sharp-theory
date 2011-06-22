/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.reasoning;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pm.Matcher;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.theory.rbp.rulebase.BaseManager;
import org.eventb.theory.rbp.rulebase.IPOContext;
import org.eventb.ui.prover.ITacticApplication;

/**
 * @author maamria
 * 
 */
public class InferenceSelector {

	protected Matcher finder;
	protected BaseManager ruleBaseManager;
	protected IPOContext context;

	public InferenceSelector(FormulaFactory factory, IPOContext context) {
		ruleBaseManager = BaseManager.getDefault();
		finder = new Matcher(factory);
		this.context = context;
	}

	public List<ITacticApplication> select(Predicate predicate,
			IProverSequent sequent) {
		List<ITacticApplication> apps = new ArrayList<ITacticApplication>();
		return apps;
	}

}
