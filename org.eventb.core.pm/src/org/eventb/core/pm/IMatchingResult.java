/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.pm;

import java.util.Map;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.PredicateVariable;
import org.eventb.core.pm.basis.AssociativeExpressionComplement;
import org.eventb.core.pm.basis.AssociativePredicateComplement;

/**
 * Common protocol for a matching process result.
 * 
 * <p> Matching results are the output of matching processes. They hold useful information that can be used
 * to create new formulae or text based on predefined patterns.
 * 
 * @see SimpleBinder
 * @see ComplexBinder
 * 
 * @author maamria
 * 
 * @since 1.0
 *
 */
public interface IMatchingResult {
	
	/**
	 * Returns the expression mappings of the matching process.
	 * @return expression mappings, or <code>null</code> if matching failed
	 */
	public Map<FreeIdentifier, Expression> getExpressionMappings();
	
	/**
	 * Returns the predicate mappings of the matching process.
	 * @return predicate mappings, or <code>null</code> if matching failed
	 */
	public Map<PredicateVariable, Predicate> getPredicateMappings();
	
	/**
	 * Returns an object containing information about unmatched expressions.
	 * @return the associative complement
	 */
	public AssociativeExpressionComplement getAssociativeExpressionComplement();
	
	/**
	 * Returns an object containing information about unmatched predicates.
	 * @return the associative complement
	 */
	public AssociativePredicateComplement getAssociativePredicateComplement();
	
}
