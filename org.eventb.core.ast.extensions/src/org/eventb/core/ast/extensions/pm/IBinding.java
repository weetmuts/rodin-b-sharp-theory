/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.ast.extensions.pm;

import java.util.Map;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FreeIdentifier;

/**
 * Common protocol for a binding responsible for collecting all information accumulated during a single matching process.
 * <p>
 * A matching process attempts to match a formula against a pattern (which itself is a formula). The distinction between the pattern
 * and the formula ensures that the correct way of matching is followed, as the process is always a one-way matching process.
 * <p>
 * Bindings can be mutable or immutable. A mutable binding can be augmented with new matching information, whereas an immutable binding
 * cannot. A binding should be kept mutable for as long as the matching process. After matching finishes, it should be made immutable.
 * <p>
 * The following functionalities should be available before the binding is made immutable:
 * 	<ol>
 * 		<li>Check insertability of a particular mapping/binding.
 * 		<li>Obtain the current mapping for a particular identifier/ predicate variable.
 * 		<li>Insert a predicate or an expression mapping.
 * 		<li>Set associative complements (predicate or expression).
 * 		<li>Make the binding immutable signalling the end of the matching process.
 * 	</ol>
 * <p>
 * The following functionalities should be available after the binding is make immutable:
 * <ol>
 * 		<li>Obtain the expression and predicate mappings stored in the binding.
 * 		<li>Obtain any stored associative complement.
 * 		<li>Obtain the type environment the contains the free identifiers/given types of the pattern mapped to the types
 * 	of their corresponding mapping.
 * </ol>
 * <p>
 * Two types of mappings are stored in a binding. Expression mappings are mappings between free identifiers in the pattern and expressions
 * in the formula. Predicate mappings are mappings between predicate variables in the pattern and predicates in the formula. 
 * <p>
 * In some cases where the pattern is an associative formula, a partial match can be obtained since formula are always flattened when rewriting.
 * This, however, should explicitly be requested.
 * <p>
 * Each binding is associated with a formula factory to ensure consistency of mathematical extensions used across a matching process.
 * <p> This interface is not intended to be implemented by clients.
 * @author maamria
 * @version 1.0
 *
 */
public interface IBinding extends Cloneable{
	
	/**
	 * Returns whether the specified binding is insertable in this binding.
	 * <p> The four conditions that must be checked in this case are :
	 * 	<ol>
	 * 		<li> This binding is mutable.
	 * 		<li> <code>binding</code> is immutable (i.e., result of a matching process that finished).
	 * 		<li> Each expression mapping in <code>binding</code> is insertable in this binding.
	 * 		<li> Each predicate mapping in <code>binding</code> is insertable in this binding.
	 * 	</ol>
	 * @param binding the other binding 
	 * @return whether the given binding is insertable 
	 */
	public boolean isBindingInsertable(IBinding binding);
	
	/**
	 * Adds all the mappings in <code>another</code> to the current binding if the conditions of 
	 * inserting mappings are met.
	 * <p> Typically a call to this method is preceded by a call to <code>isBindingInsertable(IBinding)</code> to ensure
	 * the insertion of the binding succeeds.
	 * <p> Returns whether the binding <code>another</code> has been successfully added 
	 * to the current binding</p>
	 * @param another the other binding
	 * @return whether the binding <code>another</code> has been inserted
	 * @throws UnsupportedOperationException if this binding is immutable or <code>another</code> is mutable
	 */
	public boolean insertBinding(IBinding another);
	
	/**
	 * Returns a deep clone of this binding.
	 * 
	 * @return a deep clone
	 */
	public IBinding clone();
	
	/**
	 * Makes the binding immutable. Therefore, new mappings cannot be added.
	 * <p>When the matching process has finished, call this method to generate the appropriate
	 * information to apply the resultant binding (e.g., type environment).</p>
	 */
	public void makeImmutable();
	
	/**
	 * Returns the expression mappings of the matching process.
	 * @return expression mappings
	 * @throws UnsupportedOperationException if this binding is mutable
	 */
	public Map<FreeIdentifier, Expression> getExpressionMappings();
	
}
