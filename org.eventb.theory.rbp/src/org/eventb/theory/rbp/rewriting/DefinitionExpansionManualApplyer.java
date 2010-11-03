/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.rewriting;

import java.util.Collections;

import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.extension.IExtendedFormula;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.theory.core.AST_TCFacade;
import org.eventb.theory.rbp.reasoning.AbstractRulesApplyer;

/**
 * An implementation of a definition expander.
 * @since 1.0
 * @author maamria
 *
 */
public class DefinitionExpansionManualApplyer extends AbstractRulesApplyer{

	public DefinitionExpansionManualApplyer(FormulaFactory factory) {
		super(factory);
	}

	/**
	 * Returns the antecedents resulting from expanding the operator definition at the given position.
	 * <p>
	 * @param pred the predicate
	 * @param position the position at which to rewrite
	 * @param isGoal whether the given predicate is the goal or a hypothesis.
	 * @return the antecedents or <code>null</code> if the rule was not found or is inapplicable.
	 */
	@SuppressWarnings("unchecked")
	public IAntecedent[] applyRule(Predicate pred, IPosition position, boolean isGoal){
		// get the subformula
		Formula<?> formula = pred.getSubFormula(position);
		if(formula == null) {
			return null;
		}
		if(formula instanceof IExtendedFormula){
			IExtendedFormula eform = (IExtendedFormula) formula;
			IFormulaExtension extension = eform.getExtension();
			if(AST_TCFacade.isATheoryExtension(extension)){
				Predicate newPred = pred.rewriteSubFormula(position,
						AST_TCFacade.delegateDefinitionExpansion(formula, factory), 
						factory);
				Predicate goal = (isGoal ? newPred : null);
				IAntecedent[] ants = new IAntecedent[1];
				ants[0] = ProverFactory.makeAntecedent(goal,
						isGoal ? Collections.EMPTY_SET: Collections.singleton(newPred), 
								isGoal ? null : ProverFactory.makeHideHypAction(Collections.singleton(pred)));
				return ants;
			}
		}
		return null;
	}
}
