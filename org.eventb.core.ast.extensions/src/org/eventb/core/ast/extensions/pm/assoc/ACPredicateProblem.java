/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.ast.extensions.pm.assoc;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.PredicateVariable;
import org.eventb.core.ast.extensions.maths.AstUtilities;
import org.eventb.core.ast.extensions.pm.IBinding;
import org.eventb.core.ast.extensions.pm.engine.AssociativePredicateComplement;
import org.eventb.core.internal.ast.extensions.pm.assoc.IndexedFormula;

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
	protected boolean mapVariables(List<IndexedFormula<Predicate>> usedUpFormulae, IBinding initialBinding) {
		int sizeOfVariables = variables.size();
		if (sizeOfVariables > 0) {
			List<IndexedFormula<Predicate>> availableFormulae = new ArrayList<IndexedFormula<Predicate>>();
			availableFormulae.addAll(indexedFormulae);
			availableFormulae.removeAll(usedUpFormulae);
			// we cannot solve if not enough formulae to draw from
			if (availableFormulae.size() < sizeOfVariables) {
				return false;
			}
			List<IndexedFormula<Predicate>> remainingVars = new ArrayList<IndexedFormula<Predicate>>();
			for (IndexedFormula<Predicate> indexedVariable : variables){
				PredicateVariable predicateVariable = (PredicateVariable) indexedVariable.getFormula();
				Predicate currentMapping = existingBinding.getCurrentMapping(predicateVariable);
				if(currentMapping != null){
					IndexedFormula<Predicate> indexedFormula = null;
					if((indexedFormula = getMatch(availableFormulae, currentMapping)) == null){
						return false;
					}
					usedUpFormulae.add(indexedFormula);
				}
				else {
					remainingVars.add(indexedVariable);
				}
			}
			// remove used up formulae again
			availableFormulae.removeAll(usedUpFormulae);
			if(remainingVars.size() > availableFormulae.size()){
				return false;
			}
			if(remainingVars.isEmpty()){
				return true;
			}
			int sizeOfRemainingVars = remainingVars.size();
			for (int i = 0; i < sizeOfRemainingVars - 1; i++) {
				IndexedFormula<Predicate> var = remainingVars.get(i);
				Predicate formula = availableFormulae.get(i).getFormula();
				if (!initialBinding.putPredicateMapping((PredicateVariable) var.getFormula(), 
						formula)){
					return false;
				}
				usedUpFormulae.add(availableFormulae.get(i));
			}
			// remove used up formulae again
			availableFormulae.removeAll(usedUpFormulae);
			IndexedFormula<Predicate> lastVar = remainingVars.get(sizeOfRemainingVars-1);
			List<Predicate> remainingPreds = getFormulae(availableFormulae);
			if (!initialBinding.putPredicateMapping((PredicateVariable) lastVar.getFormula(), 
					AstUtilities.makeAssociativePredicate(
							tag, existingBinding.getFormulaFactory(), remainingPreds.toArray(new Predicate[remainingPreds.size()])))){
				return false;
			}
			usedUpFormulae.addAll(availableFormulae);
		}
		return true;
	}
	
	@Override
	protected void addAssociativeComplement(List<IndexedFormula<Predicate>> formulae, IBinding binding) {
		List<Predicate> list = new ArrayList<Predicate>();
		for (IndexedFormula<Predicate> formula : formulae){
			list.add(formula.getFormula());
		}
		Predicate comp = AstUtilities.makeAssociativePredicate(
					tag, binding.getFormulaFactory(), list.toArray(new Predicate[list.size()]));
		binding.setAssociativePredicateComplement(new AssociativePredicateComplement(tag, null, comp));
	}
}
