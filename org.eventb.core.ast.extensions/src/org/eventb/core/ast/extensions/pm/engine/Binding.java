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
import org.eventb.core.ast.extensions.plugin.AstExtensionsPlugin;
import org.eventb.core.ast.extensions.pm.IBinding;

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

	@Override
	public Formula<?> getFormula() {
		return formula;
	}

	@Override
	public Formula<?> getPattern() {
		return pattern;
	}

	@Override
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

	@Override
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

	@Override
	public Predicate getCurrentMapping(PredicateVariable variable) {
		checkMutable();
		return predicateBinding.get(variable);
	}

	@Override
	public Expression getCurrentMapping(FreeIdentifier identifier) {
		checkMutable();
		return binding.get(identifier);
	}

	@Override
	public boolean isBindingInsertable(IBinding binding) {
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
	public boolean insertBinding(IBinding another) {
		checkMutable();
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

	@Override
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

	@Override
	public boolean isPartialMatchAcceptable() {
		return isPartialMatchAcceptable;
	}

	@Override
	public void setAssociativeExpressionComplement(AssociativeExpressionComplement comp) {
		checkMutable();
		this.expressionComplement = comp;
	}

	@Override
	public void setAssociativePredicateComplement(AssociativePredicateComplement comp) {
		checkMutable();
		this.predicateComplement = comp;
	}

	@Override
	public AssociativeExpressionComplement getAssociativeExpressionComplement() {
		checkImmutable();
		return expressionComplement;
	}

	@Override
	public AssociativePredicateComplement getAssociativePredicateComplement() {
		checkImmutable();
		return predicateComplement;
	}

	@Override
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

	@Override
	public ITypeEnvironment getTypeEnvironment() {
		checkImmutable();
		return typeEnvironment.makeSnapshot();
	}

	@Override
	public IBinding clone() {
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

	@Override
	public Map<PredicateVariable, Predicate> getPredicateMappings() {
		checkImmutable();
		return Collections.unmodifiableMap(predicateBinding);
	}

	@Override
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

	@Override
	public void reset() {
		binding = new HashMap<FreeIdentifier, Expression>();
		typeParametersInstantiations = new HashMap<FreeIdentifier, Type>();
		predicateBinding = new HashMap<PredicateVariable, Predicate>();
		typeEnvironment = factory.makeTypeEnvironment();
		expressionComplement = null;
		predicateComplement = null;
		isImmutable = false;
	}
}
