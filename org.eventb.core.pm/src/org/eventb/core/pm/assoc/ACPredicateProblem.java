/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.pm.assoc;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.PredicateVariable;
import org.eventb.core.pm.AssociativePredicateComplement;
import org.eventb.core.pm.IBinding;
import org.eventb.core.pm.basis.engine.MatchingUtilities;

/**
 * 
 * @author maamria
 *
 */
public class ACPredicateProblem extends ACProblem<Predicate> {

	public ACPredicateProblem(int tag, Predicate[] formulae, Predicate[] patterns, IBinding existingBinding) {
		super(tag, formulae, patterns, existingBinding);
	}

	@Override
	protected void putVariableMapping(IndexedFormula<Predicate> var, IndexedFormula<Predicate> indexedFormula, IBinding initialBinding) {
		initialBinding.putPredicateMapping((PredicateVariable) var.getFormula(), indexedFormula.getFormula());
	}

	@Override
	protected void addAssociativeComplement(List<IndexedFormula<Predicate>> formulae, IBinding binding) {
		List<Predicate> list = new ArrayList<Predicate>();
		for (IndexedFormula<Predicate> formula : formulae){
			list.add(formula.getFormula());
		}
		Predicate comp = MatchingUtilities.makeAssociativePredicate(
					tag, binding.getFormulaFactory(), list.toArray(new Predicate[list.size()]));
		binding.setAssociativePredicateComplement(new AssociativePredicateComplement(tag, null, comp));
	}
}
