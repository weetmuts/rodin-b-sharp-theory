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
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.pm.basis.AssociativeExpressionComplement;
import org.eventb.core.pm.basis.AssociativePredicateComplement;
import org.eventb.core.pm.basis.IBinding;
import org.eventb.core.pm.basis.engine.MatchingUtilities;

/**
 * An implementation of a more structured binder that can be used when
 * associative matching is involved.
 * 
 * @since 1.0
 * @author maamria
 * 
 */
public class ComplexBinder extends SimpleBinder {

	public ComplexBinder(FormulaFactory factory) {
		super(factory);
	}

	/**
	 * Returns the formula resulting from binding the pattern to the binding of
	 * the given matching result.
	 * 
	 * @param pattern
	 *            the pattern
	 * @param result
	 *            the matching result
	 * @param includeComplement
	 *            whether associative complements should be considered
	 * @return the resultant formula
	 */
	public Formula<?> bind(Formula<?> pattern, IMatchingResult result, boolean includeComplement) {
		Formula<?> formula = super.bind(pattern, result);
		if (!includeComplement) {
			return formula;
		}
		IBinding binding = (IBinding) result;
		if (formula instanceof Expression) {
			AssociativeExpressionComplement comp = binding.getAssociativeExpressionComplement();
			if (comp != null) {
				Expression e1 = comp.getToAppend();
				Expression e2 = comp.getToPrepend();
				int tag = comp.getTag();
				// check here if we are dealing with extended expressions
				IFormulaExtension extension = factory.getExtension(tag);
				if (extension != null) {
					// here we return an extended expression instead
					return factory.makeExtendedExpression(
							(IExpressionExtension) extension, 
							MatchingUtilities.getExpressionArray(e1, (Expression) formula, e2), 
							new Predicate[0],
							null);
				}
				return MatchingUtilities.makeAssociativeExpression(tag, factory, e1, (Expression) formula, e2);
			}
		} else {
			AssociativePredicateComplement comp = binding.getAssociativePredicateComplement();
			if (comp != null) {
				Predicate e1 = comp.getToAppend();
				Predicate e2 = comp.getToPrepend();
				int tag = comp.getTag();
				return MatchingUtilities.makeAssociativePredicate(tag, factory, e1, (Predicate) formula, e2);
			}
		}
		return formula;
	}

}
