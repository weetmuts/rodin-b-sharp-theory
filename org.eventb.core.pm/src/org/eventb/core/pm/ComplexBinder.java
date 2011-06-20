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

/**
 * An implementation of a more structured binder that can be used when associative matching is involved.
 * 
 * @since 1.0
 * @author maamria
 *
 */
public class ComplexBinder extends SimpleBinder{

	public ComplexBinder(FormulaFactory factory) {
		super(factory);
	}
	
	/**
	 * Returns the formula resulting from binding the pattern to the binding of the given matching result.
	 * @param pattern the pattern
	 * @param result the matching result
	 * @param includeComplement whether associative complements should be considered
	 * @return the resultant formula
	 */
	public Formula<?> bind(Formula<?> pattern, IMatchingResult result, boolean includeComplement) {
		return null;
	}

}
