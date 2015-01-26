/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.ast.extensions.pm.engine;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eventb.core.ast.BooleanType;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.IntegerType;
import org.eventb.core.ast.ParametricType;
import org.eventb.core.ast.PowerSetType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.PredicateVariable;
import org.eventb.core.ast.ProductType;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extensions.pm.IBinding;
import org.eventb.core.internal.ast.extensions.AstExtensionsPlugin;

/**
 * <p>
 * An implementation of a binding.
 * <p>
 * Call {@link MatchingFactory.createBinding()} to create a fresh binding.
 * 
 * <p> Equality and hashcode are not overridden in this implementation.
 * 
 * @see IBinding
 * @since 1.0
 * @author maamria
 * 
 */
public class Binding implements IBinding {

	/**
	 * The formula to match.
	 */
	private Formula<?> formula;
	/**
	 * The pattern to match against.
	 */
	private Formula<?> pattern;

	/**
	 * The mapping storage for identifiers, type and predicate variables.
	 */
	private Map<FreeIdentifier, Expression> binding;
	private Map<FreeIdentifier, Type> typeParametersInstantiations;
	private Map<PredicateVariable, Predicate> predicateBinding;

	/**
	 * The formula factory used
	 */
	private FormulaFactory factory;

	/**
	 * The type environment generated if the matching process is a success.
	 */
	private ITypeEnvironmentBuilder typeEnvironment;

	/**
	 * Other matching information.
	 */
	private boolean isPartialMatchAcceptable;
	private AssociativeExpressionComplement expressionComplement;
	private AssociativePredicateComplement predicateComplement;

	/**
	 * State of binding information.
	 */
	private boolean isImmutable = false;

	/**
	 * Creates a binding that will contain match information between
	 * <code>formula</code> and <code>pattern</code>. It is possible to specify
	 * whether a complete or partial match is acceptable. This is relevant when
	 * matching associative formulae.
	 * 
	 * @param formula
	 *            the formula to match
	 * @param pattern
	 *            the pattern to match against
	 * @param isPartialMatchAcceptable
	 *            whether a partial match is acceptable
	 * @param factory
	 *            the formula factory
	 * 
	 * @throws IllegalArgumentException
	 *             if formula or pattern are <code>null</code> or not type
	 *             checked
	 */
	public Binding(Formula<?> formula, Formula<?> pattern, boolean isPartialMatchAcceptable, FormulaFactory factory) {
		if (formula == null || pattern == null) {
			IllegalArgumentException ex = new IllegalArgumentException("formula and pattern should not be null");
			AstExtensionsPlugin.log(ex, ex.getMessage());
			throw ex;
		}
		if (!formula.isTypeChecked() || !pattern.isTypeChecked()) {
			IllegalArgumentException ex = new IllegalArgumentException("formula and pattern should be type checked ["
					+ formula + "   |   " + pattern + "]");
			AstExtensionsPlugin.log(ex, ex.getMessage());
			throw ex;
		}
		this.formula = formula;
		this.pattern = pattern;
		this.isPartialMatchAcceptable = isPartialMatchAcceptable;
		this.factory = factory;
		binding = new HashMap<FreeIdentifier, Expression>();
		typeParametersInstantiations = new HashMap<FreeIdentifier, Type>();
		predicateBinding = new HashMap<PredicateVariable, Predicate>();
		typeEnvironment = factory.makeTypeEnvironment();
	}

	/**
	 * Creates a binding that can be used as an accumulator of other bindings.
	 * 
	 * @param acceptPartialMatch
	 *            whether to accept partial match
	 * @param factory
	 *            the formula factory
	 */
	public Binding(boolean isPartialMatchAcceptable, FormulaFactory factory) {
		this.isPartialMatchAcceptable = isPartialMatchAcceptable;
		this.factory = factory;
		binding = new HashMap<FreeIdentifier, Expression>();
		typeParametersInstantiations = new HashMap<FreeIdentifier, Type>();
		predicateBinding = new HashMap<PredicateVariable, Predicate>();
		typeEnvironment = factory.makeTypeEnvironment();
		//formula = FormulaFactory.
		//pattern = getPattern();
	}

	/**
	 * Returns the formula to match.
	 * @return the formula
	 */
	public Formula<?> getFormula() {
		return formula;
	}

	/**
	 * Returns the formula against which to match.
	 * @return the pattern
	 */
	public Formula<?> getPattern() {
		return pattern;
	}

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
	public boolean putExpressionMapping(FreeIdentifier identifier, Expression e) {
		checkMutable();
		if (!e.isTypeChecked() || !identifier.isTypeChecked()){
			throw new IllegalArgumentException("expression/identifier should be type check before insertion to binding");
		}
		if (!isMappingInsertable(identifier, e)) {
			return false;
		}
		// now unify types and add information
		// TODO this is a rather cumbersome fix as type unification is ran twice
		unifyTypes(e.getType(), identifier.getType(), true);
		binding.put(identifier, e);
		return true;
	}

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
	public boolean putPredicateMapping(PredicateVariable variable, Predicate p) {
		checkMutable();
		if (!p.isTypeChecked()){
			throw new IllegalArgumentException("predicate should be type check before insertion to binding");
		}
		if (!isPredicateMappingInsertable(variable, p)) {
			return false;
		}
		predicateBinding.put(variable, p);
		return true;
	}

	/**
	 * Returns the predicate mapped to the given variable.
	 * @param variable the predicate variable
	 * @return the mapped predicate, or <code>null</code> if not mapped
	 * @throws UnsupportedOperationException if this binding is immutable
	 */
	public Predicate getCurrentMapping(PredicateVariable variable) {
		checkMutable();
		return predicateBinding.get(variable);
	}

	/**
	 * Returns the expression mapped to the given identifier.
	 * @param identifier the free identifier
	 * @return the mapped expression, or <code>null</code> if not mapped
	 * @throws UnsupportedOperationException if this binding is immutable
	 */
	public Expression getCurrentMapping(FreeIdentifier identifier) {
		checkMutable();
		return binding.get(identifier);
	}

	@Override
	public boolean isBindingInsertable(IBinding ibinding) {
		final Binding binding = (Binding) ibinding;
		// cannot insert into an immutable binding
		if (isImmutable)
			return false;
		// mutable binding are not insertable
		if (!binding.isImmutable())
			return false;
		Map<FreeIdentifier, Expression> identMap = ((Binding) binding).binding;
		Map<PredicateVariable, Predicate> predMap = ((Binding) binding).predicateBinding;
		for (FreeIdentifier ident : identMap.keySet()) {
			if (!isMappingInsertable(ident, identMap.get(ident))) {
				return false;
			}
		}
		for (PredicateVariable var : predMap.keySet()) {
			if (!isPredicateMappingInsertable(var, predMap.get(var))) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean insertBinding(IBinding ianother) {
		checkMutable();
		final Binding another = (Binding) ianother;
		if (!another.isImmutable())
			throw new UnsupportedOperationException("Trying to add mappings from a mutable binding.");
		// add each of the mappings
		Binding anotherBinding = (Binding) another;
		for (FreeIdentifier ident : anotherBinding.binding.keySet()) {
			if (!putExpressionMapping(ident, anotherBinding.binding.get(ident))) {
				return false;
			}
		}
		for (PredicateVariable var : anotherBinding.predicateBinding.keySet()) {
			if (!putPredicateMapping(var, anotherBinding.predicateBinding.get(var))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks whether two types (an instance and a pattern) can be considered as matchable.
	 * <p> If the two types are matchable and <code>augmentBinding</code> is set to <code>true</code>, 
	 * the binding will be augmented with any inferred information (i.e., mappings between type parameters and type expressions).
	 * @param expressionType the type of the instance
	 * @param patternType the type of the pattern
	 * @param augmentBinding whether to add any type unification data to the binding (e.g., given type to a type expression mapping)
	 * @return whether the two types are unifyable
	 */
	public boolean unifyTypes(Type expressionType, Type patternType, boolean augmentBinding) {
		if (isImmutable) {
			return false;
		}
		if (patternType instanceof IntegerType) {
			return expressionType instanceof IntegerType;
		} else if (patternType instanceof BooleanType) {
			return expressionType instanceof BooleanType;
		} else if (patternType instanceof GivenType) {
			// if requested to augment binding add the following type mapping
			if (augmentBinding){
				return putTypeMapping(
						factory.makeFreeIdentifier(((GivenType) patternType).getName(), null,
								patternType.toExpression().getType()), expressionType);
			}
			else{
				return true;
			}
		} else if (patternType instanceof PowerSetType) {
			if (expressionType instanceof PowerSetType) {
				Type pBase = patternType.getBaseType();
				Type fBase = expressionType.getBaseType();
				return unifyTypes(fBase, pBase, augmentBinding);
			}
		} else if (patternType instanceof ProductType) {
			if (expressionType instanceof ProductType) {
				Type pLeft = ((ProductType) patternType).getLeft();
				Type fLeft = ((ProductType) expressionType).getLeft();

				Type pRight = ((ProductType) patternType).getRight();
				Type fRight = ((ProductType) expressionType).getRight();

				return unifyTypes(fLeft, pLeft, augmentBinding) && unifyTypes(fRight, pRight, augmentBinding);
			}
		} else if (patternType instanceof ParametricType) {
			if (expressionType instanceof ParametricType) {

				ParametricType patParametricType = (ParametricType) patternType;
				ParametricType expParametricType = (ParametricType) expressionType;
				if (!patParametricType.getExprExtension().equals(expParametricType.getExprExtension())) {
					return false;
				}
				Type[] patTypes = patParametricType.getTypeParameters();
				Type[] expTypes = expParametricType.getTypeParameters();
				boolean ok = true;
				for (int i = 0; i < patTypes.length; i++) {
					ok &= unifyTypes(expTypes[i], patTypes[i], augmentBinding);
					if (!ok) {
						return false;
					}
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns whether a partial match is acceptable.
	 * <p> A partial match is acceptable in the case of matching associative expressions/predicates.
	 * For example, matching <code>(3+2+1)</code> against <code>(2+1)</code> produces a partial match with <code>3</code>
	 * being left out. <code> 3</code> in this case should be added as an associative complement.
	 * @return whether a partial match is acceptable
	 */
	public boolean isPartialMatchAcceptable() {
		return isPartialMatchAcceptable;
	}

	/**
	 * Keeps track of the expressions that are unmatched in the case where a partial match is acceptable.
	 * <p> The binding must be mutable.
	 * @param comp the associative complement object
	 * @throws UnsupportedOperationException if this binding is immutable
	 */
	public void setAssociativeExpressionComplement(AssociativeExpressionComplement comp) {
		checkMutable();
		this.expressionComplement = comp;
	}

	/**
	 * Keeps track of the predicates that are unmatched in the case where a partial match is acceptable.
	 * <p> The binding must be mutable.
	 * @param comp the associative complement object
	 * @throws UnsupportedOperationException if this binding is immutable
	 */
	public void setAssociativePredicateComplement(AssociativePredicateComplement comp) {
		checkMutable();
		this.predicateComplement = comp;
	}

	/**
	 * Returns an object containing information about unmatched expressions.
	 * @return the associative complement
	 * @throws UnsupportedOperationException if this binding is mutable
	 */
	public AssociativeExpressionComplement getAssociativeExpressionComplement() {
		checkImmutable();
		return expressionComplement;
	}

	/**
	 * Returns an object containing information about unmatched predicates.
	 * @return the associative complement
	 * @throws UnsupportedOperationException if this binding is mutable
	 */
	public AssociativePredicateComplement getAssociativePredicateComplement() {
		checkImmutable();
		return predicateComplement;
	}

	/**
	 * Returns whether this binding is immutable.
	 * <p> The binding should stay mutable for as long as the matching process.
	 * It should be made immutable when the matching finishes.
	 * @return whether this binding is immutable
	 */
	public boolean isImmutable() {
		return isImmutable;
	}

	@Override
	public void makeImmutable() {
		isImmutable = true;
		for (FreeIdentifier ident : typeParametersInstantiations.keySet()) {
			Type type = typeParametersInstantiations.get(ident);
			Expression expression = type.toExpression();
			binding.put(ident, expression);
		}
		for (FreeIdentifier ident : binding.keySet()) {
			Expression expression = binding.get(ident);
			Type newType = expression.getType();
			typeEnvironment.addName(ident.getName(), newType);
		}
	}

	/**
	 * Returns the type environment assigning types to the pattern free variables that are compatible with their matches in the matched formula.
	 * <p>For example, this type environment is used to typecheck the right hand sides of a rewrite rule.
	 * <p>This method should be called on an immutable binding (i.e., when matching has finished).</p>
	 * 
	 * @return the type environment
	 * @throws UnsupportedOperationException if this binding is mutable
	 */
	public ITypeEnvironment getTypeEnvironment() {
		checkImmutable();
		return typeEnvironment.makeSnapshot();
	}

	@Override
	public Binding clone() {
		// copy all state information
		Binding newBinding = new Binding(formula, pattern, isPartialMatchAcceptable, factory);
		newBinding.binding = new LinkedHashMap<FreeIdentifier, Expression>(binding);
		newBinding.typeParametersInstantiations = new LinkedHashMap<FreeIdentifier, Type>(typeParametersInstantiations);
		newBinding.predicateBinding = new LinkedHashMap<PredicateVariable, Predicate>(predicateBinding);
		if (expressionComplement != null)
			newBinding.expressionComplement = new AssociativeExpressionComplement(expressionComplement);
		if (predicateComplement != null)
			newBinding.predicateComplement = new AssociativePredicateComplement(predicateComplement);
		return newBinding;
	}

	@Override
	public Map<FreeIdentifier, Expression> getExpressionMappings() {
		checkImmutable();
		Map<FreeIdentifier, Expression> finalBinding = new HashMap<FreeIdentifier, Expression>();
		for (FreeIdentifier ident : binding.keySet()) {
			Expression exp = binding.get(ident);
			Type newType = exp.getType();
			FreeIdentifier newIdent = factory.makeFreeIdentifier(ident.getName(), null, newType);
			finalBinding.put(newIdent, exp);
		}
		return finalBinding;
	}

	/**
	 * Returns the predicate mappings of the matching process.
	 * @return predicate mappings
	 * @throws UnsupportedOperationException if this binding is mutable
	 */
	public Map<PredicateVariable, Predicate> getPredicateMappings() {
		checkImmutable();
		return Collections.unmodifiableMap(predicateBinding);
	}

	/**
	 * Returns the formula factory used by this binding.
	 * <p> One formula factory should be used across an entire matching process to ensure consistency
	 * of mathematical extensions used.
	 * @return the formula factory
	 */
	public FormulaFactory getFormulaFactory() {
		return factory;
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Expression binding : [" + binding + "]  ");
		builder.append("Predicate binding : [" + predicateBinding + "]  ");
		builder.append((isPartialMatchAcceptable ? "" : "Not ") + "Accepting Partial Match.");
		return builder.toString();
	}

	/**
	 * Utilities.
	 */

	protected void checkMutable() throws UnsupportedOperationException {
		if (isImmutable) {
			throw new UnsupportedOperationException("Matching process has finished.");
		}
	}

	protected void checkImmutable() throws UnsupportedOperationException {
		if (!isImmutable) {
			throw new UnsupportedOperationException("Matching process has not finished.");
		}
	}

	/**
	 * Adds the mapping of the type specified by the given free identifier and
	 * the supplied type.
	 * 
	 * @param ident
	 *            the type identifier
	 * @param type
	 *            the type
	 * @return whether the mapping has been inserted
	 */
	protected boolean putTypeMapping(FreeIdentifier ident, Type type) {
		// if there is a binding for ident that is different from the type
		// expression
		if (binding.get(ident) != null && !binding.get(ident).equals(type.toExpression())) {
			return false;
		}
		// if there is a type instant. for ident that is different from type
		if (typeParametersInstantiations.get(ident) != null && !typeParametersInstantiations.get(ident).equals(type)) {
			return false;
		}
		// add mapping
		typeParametersInstantiations.put(ident, type);
		return true;
	}

	/**
	 * Returns whether the types of the expression and the identifier are
	 * compatible.
	 * 
	 * <p>
	 * Corresponds to condition (2).
	 * 
	 * @param expressionType
	 *            the type of the expression
	 * @param identifierType
	 *            the type of the identifier pattern
	 * @return whether the two types are compatible
	 */
	protected boolean condition_CanUnifyTypes(Type expressionType, Type identifierType) {
		return unifyTypes(expressionType, identifierType, false);
	}

	/**
	 * Checks the condition when the identifier is a given type in which case
	 * the expression has to be a type expression.
	 * 
	 * <p>
	 * Corresponds to condition (3).
	 * 
	 * @param expression
	 * @param identifier
	 *            the
	 * @return whether the condition is met
	 */
	protected boolean condition_IdentifierIsGivenType(Expression expression, FreeIdentifier identifier) {
		if (isIdentAGivenType(identifier)) {
			return expression.isATypeExpression();
		}
		return true;
	}

	/**
	 * Checks whether the identifier is a given type.
	 * 
	 * @param identifier
	 *            the identifier
	 * @param types
	 *            the set of given types
	 * @return whether the identifier is a given type
	 */
	protected boolean isIdentAGivenType(FreeIdentifier identifier) {
		for (GivenType gt : identifier.getGivenTypes()) {
			if (identifier.equals(gt.toExpression())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks whether an individual mapping is insertable.
	 * 
	 * @param ident
	 *            the identifier
	 * @param e
	 *            the expression
	 * @return whether an individual mapping is insertable
	 */
	protected boolean isMappingInsertable(FreeIdentifier ident, Expression e) {
		if (isImmutable || !ident.isTypeChecked() || !e.isTypeChecked()
				|| !condition_CanUnifyTypes(e.getType(), ident.getType())
				|| !condition_IdentifierIsGivenType(e, ident)
				|| (binding.get(ident) != null && !e.equals(binding.get(ident)))) {
			return false;
		}

		return true;
	}

	/**
	 * Checks whether an individual mapping is insertable.
	 * 
	 * @param var
	 *            the predicate variable
	 * @param p
	 *            the predicate
	 * @return whether an individual mapping is insertable
	 */
	protected boolean isPredicateMappingInsertable(PredicateVariable var, Predicate p) {
		if (isImmutable || (predicateBinding.get(var) != null && !p.equals(predicateBinding.get(var))))
			return false;

		return true;
	}

}
