/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.pm.assoc;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.PredicateVariable;
import org.eventb.core.pm.IBinding;

/**
 * 
 * @author maamria
 *
 */
public class ACPredicateProblem extends ACProblem<Predicate> {

	public ACPredicateProblem(int tag, Predicate[] formulae, Predicate[] patterns, FormulaFactory factory) {
		super(tag, formulae, patterns, factory);
	}

	@Override
	protected void putVariableMapping(IndexedFormula<Predicate> var, IndexedFormula<Predicate> indexedFormula, IBinding initialBinding) {
		initialBinding.putPredicateMapping((PredicateVariable) var.getFormula(), indexedFormula.getFormula());
	}
}
