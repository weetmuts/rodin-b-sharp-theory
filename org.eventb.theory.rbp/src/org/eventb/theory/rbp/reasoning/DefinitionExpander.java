/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.reasoning;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.theory.rbp.rewriting.DefinitionExpansionManualApplyer;

/**
 * @author maamria
 * 
 */
public class DefinitionExpander {

	private DefinitionExpansionManualApplyer applyer;

	public void setFormulaFactory(FormulaFactory factory) {
		this.applyer = new DefinitionExpansionManualApplyer(factory);

	}

	/**
	 * Returns the antecedents resulting from expanding the definition of the
	 * given operator.
	 * <p>
	 * 
	 * @param pred
	 *            to which the rule was applicable
	 * @param position
	 * @param isGoal
	 * @return the antecedents or <code>null</code> if the rule is not found or
	 *         inapplicable
	 */
	public IAntecedent[] getAntecedents(Predicate pred, IPosition position,
			boolean isGoal) {
		return applyer.applyRule(pred, position, isGoal);
	}
}
