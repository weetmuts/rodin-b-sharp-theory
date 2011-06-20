/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.pm.basis.engine;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;

/**
 * 
 * @author maamria
 *
 */
public class MatchingUtilities {

	/**
	 * <p>
	 * Checks whether two objects are of the same class.
	 * </p>
	 * 
	 * @param o1 the first object
	 * @param o2 the second object
	 * @return whether the two objects are of the same class
	 */
	public static boolean sameClass(Object o1, Object o2) {
		return o1.getClass().equals(o2.getClass());
	}
	
	/**
	 * Literal predicate true.
	 */
	public static final Predicate BTRUE = FormulaFactory.getDefault()
			.makeLiteralPredicate(Formula.BTRUE, null);

	/**
	 * Make sure tag is for an associative expression.
	 * <p>
	 * This method checks whether the operator is ac.
	 * 
	 * @param tag
	 * @return
	 */
	public static boolean isAssociativeCommutative(int tag) {
		if (tag == AssociativeExpression.BCOMP
				|| tag == AssociativeExpression.FCOMP) {
			return false;
		}
		return true;
	}
	
}
