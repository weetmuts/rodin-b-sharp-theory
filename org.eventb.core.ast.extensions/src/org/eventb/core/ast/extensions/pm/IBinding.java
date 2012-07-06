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
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.PredicateVariable;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extensions.pm.engine.AssociativeExpressionComplement;
import org.eventb.core.ast.extensions.pm.engine.AssociativePredicateComplement;

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
	 * Returns the formula to match.
	 * @return the formula
	 */
	public Formula<?> getFormula();
	
	/**
	 * Returns the formula against which to match.
	 * @return the pattern
	 */
	public Formula<?> getPattern();
	
	/**
	 * Adds the mapping between <code>identifier</code> and <code>e</code> to the binding if conditions to do so are met.
	 * <p> The four conditions for adding an expression mapping are :
	 * 	<ol>
	 * 		<li> The binding has to be mutable.
	 * 		<li> The types of <code>identifier</code> and <code>e</code> are unifyable.
	 * 		<li> If <code>identifier</code> is a given type (i.e., a type parameter), then 
	 * 			<code>e</code> must be a type expression.
	 * 		<li> Either <code>identifier</code> does not have an entry in the binding, or it has one and it is equal to 
	 * 			<code>e</code>.
	 * 	</ol>
	 * <p>Returns whether the mapping has been successfully added.</p>
	 * @param identifier the free identifier
	 * @param e the expression
	 * @return whether the mapping has been added
	 * @throws IllegalArgumentException if <code>identifier</code> or <code>e</code> are not type checked
	 * @throws UnsupportedOperationException if this binding is immutable
	 */
	public boolean putExpressionMapping(FreeIdentifier identifier, Expression e);
	
	/**
	 * Adds the mapping between <code>variable</code> and <code>p</code> to the binding if conditions to do so are met.
	 * <p> The two conditions for adding an expression mapping are :
	 * 	<ol>
	 * 		<li> The binding has to be mutable.
	 * 		<li> Either <code>variable</code> does not have an entry in the binding, or it has one and it is equal to 
	 * 			<code>p</code>.
	 * 	</ol>
	 * <p>Returns whether the mapping has been successfully added.</p>
	 * @param variable the predicate variable
	 * @param p the predicate
	 * @return whether the mapping has been added
	 * @throws IllegalArgumentException if <code>p</code> is not type checked
	 * @throws UnsupportedOperationException if this binding is immutable
	 */
	public boolean putPredicateMapping(PredicateVariable variable, Predicate p);
	
	/**
	 * Returns the predicate mapped to the given variable.
	 * @param variable the predicate variable
	 * @return the mapped predicate, or <code>null</code> if not mapped
	 * @throws UnsupportedOperationException if this binding is immutable
	 */
	public Predicate getCurrentMapping(PredicateVariable variable);
	
	/**
	 * Returns the expression mapped to the given identifier.
	 * @param identifier the free identifier
	 * @return the mapped expression, or <code>null</code> if not mapped
	 * @throws UnsupportedOperationException if this binding is immutable
	 */
	public Expression getCurrentMapping(FreeIdentifier identifier);
	
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
	 * Checks whether two types (an instance and a pattern) can be considered as matchable.
	 * <p> If the two types are matchable and <code>augmentBinding</code> is set to <code>true</code>, 
	 * the binding will be augmented with any inferred information (i.e., mappings between type parameters and type expressions).
	 * @param expressionType the type of the instance
	 * @param patternType the type of the pattern
	 * @param augmentBinding whether to add any type unification data to the binding (e.g., given type to a type expression mapping)
	 * @return whether the two types are unifyable
	 */
	public boolean unifyTypes(Type expressionType, Type patternType, boolean augmentBinding);
	
	/**
	 * Returns whether a partial match is acceptable.
	 * <p> A partial match is acceptable in the case of matching associative expressions/predicates.
	 * For example, matching <code>(3+2+1)</code> against <code>(2+1)</code> produces a partial match with <code>3</code>
	 * being left out. <code> 3</code> in this case should be added as an associative complement.
	 * @return whether a partial match is acceptable
	 */
	public boolean isPartialMatchAcceptable();
	
	/**
	 * Keeps track of the expressions that are unmatched in the case where a partial match is acceptable.
	 * <p> The binding must be mutable.
	 * @param comp the associative complement object
	 * @throws UnsupportedOperationException if this binding is immutable
	 */
	public void setAssociativeExpressionComplement(AssociativeExpressionComplement comp);
	
	/**
	 * Keeps track of the predicates that are unmatched in the case where a partial match is acceptable.
	 * <p> The binding must be mutable.
	 * @param comp the associative complement object
	 * @throws UnsupportedOperationException if this binding is immutable
	 */
	public void setAssociativePredicateComplement(AssociativePredicateComplement comp);
	
	/**
	 * Returns an object containing information about unmatched expressions.
	 * @return the associative complement
	 * @throws UnsupportedOperationException if this binding is mutable
	 */
	public AssociativeExpressionComplement getAssociativeExpressionComplement();
	
	/**
	 * Returns an object containing information about unmatched predicates.
	 * @return the associative complement
	 * @throws UnsupportedOperationException if this binding is mutable
	 */
	public AssociativePredicateComplement getAssociativePredicateComplement();
	
	/**
	 * Returns a deep clone of this binding.
	 * 
	 * @return a deep clone
	 */
	public IBinding clone();
	
	/**
	 * Returns whether this binding is immutable.
	 * <p> The binding should stay mutable for as long as the matching process.
	 * It should be made immutable when the matching finishes.
	 * @return whether this binding is immutable
	 */
	public boolean isImmutable();
	
	/**
	 * Makes the binding immutable. Therefore, new mappings cannot be added.
	 * <p>When the matching process has finished, call this method to generate the appropriate
	 * information to apply the resultant binding (e.g., type environment).</p>
	 */
	public void makeImmutable();
	
	/**
	 * Returns the type environment assigning types to the pattern free variables that are compatible with their matches in the matched formula.
	 * <p>For example, this type environment is used to typecheck the right hand sides of a rewrite rule.
	 * <p>This method should be called on an immutable binding (i.e., when matching has finished).</p>
	 * 
	 * @return the type environment
	 * @throws UnsupportedOperationException if this binding is mutable
	 */
	public ITypeEnvironment getTypeEnvironment();
	
	/**
	 * Returns the expression mappings of the matching process.
	 * @return expression mappings
	 * @throws UnsupportedOperationException if this binding is mutable
	 */
	public Map<FreeIdentifier, Expression> getExpressionMappings();
	
	/**
	 * Returns the predicate mappings of the matching process.
	 * @return predicate mappings
	 * @throws UnsupportedOperationException if this binding is mutable
	 */
	public Map<PredicateVariable, Predicate> getPredicateMappings();
	
	/**
	 * Returns the formula factory used by this binding.
	 * <p> One formula factory should be used across an entire matching process to ensure consistency
	 * of mathematical extensions used.
	 * @return the formula factory
	 */
	public FormulaFactory getFormulaFactory();
	
	/**
	 * This should reset any state held as part of the binding.
	 */
	public void reset();
	
}
