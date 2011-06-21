/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.pm;

import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.pm.basis.IBinding;
import org.eventb.core.pm.basis.MatchingFactory;

/**
 * 
 * @author maamria
 * 
 */
public class Matcher {

	private FormulaFactory factory;
	private MatchingFactory matchingFactory;

	public Matcher(FormulaFactory factory) {
		this.factory = factory;
		this.matchingFactory = MatchingFactory.getInstance();
	}

	/**
	 * Matches the formula and the pattern and produces a matching result.
	 * <p> The matching process can be instructed to produce partial matches. This is relevant when matching
	 * two associative expressions (or predicates).
	 * @param form the formula
	 * @param pattern the pattern
	 * @param acceptPartialMatch whether to accept a partial match
	 * @return the matching result, or <code>null</code> if matching failed
	 */
	public IMatchingResult match(Formula<?> form, Formula<?> pattern, boolean acceptPartialMatch) {
		IBinding initialBinding = matchingFactory.createBinding(acceptPartialMatch, factory);
		if (matchingFactory.match(form, pattern, initialBinding)){
			return initialBinding;
		}
		return null;
	}

}
