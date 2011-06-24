/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.pm;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.pm.basis.engine.MatchingUtilities;
import org.eventb.core.pm.basis.engine.PredicateVariableSubstituter;

/**
 * An implementation of a simple binder.
 * 
 * <p> This class is not intended to be extended by clients.
 * 
 * @since 1.0
 * @author maamria
 * 
 */
public class SimpleBinder {

	protected FormulaFactory factory;

	public SimpleBinder(FormulaFactory factory) {
		this.factory = factory;
	}

	/**
	 * Returns the formula resulting from binding the pattern by the given binding.
	 * 
	 * @param pattern
	 *            the pattern
	 * @param binding
	 *            the binding
	 * @return the resultant formula
	 */
	public Formula<?> bind(Formula<?> pattern, IBinding binding) {
		if (binding == null) {
			return null;
		}
		Formula<?> resultFormula = MatchingUtilities.parseFormula(pattern.toString(), pattern instanceof Expression, factory);
		Formula<?> finalResultFormula = resultFormula.rewrite(new PredicateVariableSubstituter(binding.getPredicateMappings(), factory));
		finalResultFormula.typeCheck(binding.getTypeEnvironment());
		Formula<?> formula = finalResultFormula.substituteFreeIdents(binding.getExpressionMappings(), factory);
		return formula;
	}
}
