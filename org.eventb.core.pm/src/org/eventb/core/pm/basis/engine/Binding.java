package org.eventb.core.pm.basis.engine;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eventb.core.ast.BooleanType;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.IntegerType;
import org.eventb.core.ast.ParametricType;
import org.eventb.core.ast.PowerSetType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.PredicateVariable;
import org.eventb.core.ast.ProductType;
import org.eventb.core.ast.Type;
import org.eventb.core.pm.basis.AssociativeExpressionComplement;
import org.eventb.core.pm.basis.AssociativePredicateComplement;
import org.eventb.core.pm.basis.IBinding;

/**
 * <p>
 * An implementation of a binding.
 * </p>
 * 
 * <p>
 * In order to create a new binding object, call {@link MatchingFactory.createBinding()}.
 * </p>
 * 
 * @see IBinding
 * 
 * @since 1.0
 * 
 * @author maamria
 * 
 */
public final class Binding implements IBinding {

	// mappings stores
	private Map<FreeIdentifier, Expression> binding;
	private Map<FreeIdentifier, Type> typeParametersInstantiations;
	private Map<PredicateVariable, Predicate> predBinding;
	// factory used
	final private FormulaFactory factory;
	// state information
	private boolean isImmutable = false;
	// type environment generated if the matching process is a success
	final private ITypeEnvironment typeEnvironment;
	// matching information
	private boolean isPartialMatchAcceptable;
	private AssociativeExpressionComplement expComplement;
	private AssociativePredicateComplement predComplement;

	public Binding(
			boolean isPartialMatchAcceptable, FormulaFactory factory) {
		this.isPartialMatchAcceptable = isPartialMatchAcceptable;
		binding = new HashMap<FreeIdentifier, Expression>();
		typeParametersInstantiations = new HashMap<FreeIdentifier, Type>();
		predBinding = new HashMap<PredicateVariable, Predicate>();
		this.factory = factory;
		typeEnvironment = factory.makeTypeEnvironment();
	}

	public boolean isImmutable() {
		return isImmutable;
	}

	public String toString() {
		return "Binding: " + binding;
	}

	public boolean isBindingInsertable(IBinding binding) {
		// mutable binding are not insertable
		if (!binding.isImmutable())
			return false;
		// cannot insert into an immutable binding
		if (isImmutable)
			return false;
		Map<FreeIdentifier, Expression> identMap = ((Binding) binding).binding;
		Map<PredicateVariable, Predicate> predMap = ((Binding) binding).predBinding;
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

	public boolean putExpressionMapping(FreeIdentifier ident, Expression e) {
		if (isImmutable)
			throw new UnsupportedOperationException(
					"Trying to add a mapping after the matching process finished.");
		if (!condition1_CanUnifyTypes(e.getType(), ident.getType())
				|| !condition2_IdentifierIsGivenType(e, ident)
				|| (binding.get(ident) != null && !e.equals(binding.get(ident)))) {
			return false;
		}
		binding.put(ident, e);
		return true;
	}

	public boolean insertBinding(IBinding another) {
		if (!another.isImmutable())
			throw new IllegalArgumentException(
					"Trying to add mappings from a mutable binding.");
		if (isImmutable)
			throw new UnsupportedOperationException(
					"Trying to add mappings after the matching process finished.");
		Binding anotherBinding = (Binding) another;
		for (FreeIdentifier ident : anotherBinding.binding.keySet()) {
			if (!putExpressionMapping(ident, anotherBinding.binding.get(ident))) {
				return false;
			}
		}
		for (PredicateVariable var : anotherBinding.predBinding.keySet()) {
			if (!putPredicateMapping(var,
					anotherBinding.predBinding.get(var))) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public boolean isPartialMatchAcceptable() {
		return isPartialMatchAcceptable;
	}

	public void makeImmutable() {
		isImmutable = true;
		for (FreeIdentifier ident : typeParametersInstantiations.keySet()) {
			binding.put(ident, typeParametersInstantiations.get(ident)
					.toExpression(factory));
		}
		for (FreeIdentifier ident : binding.keySet()) {
			Type newType = binding.get(ident).getType();
			typeEnvironment.addName(ident.getName(), newType);
		}

	}

	public Map<FreeIdentifier, Expression> getExpressionMappings() {
		if (!isImmutable)
			throw new UnsupportedOperationException(
					"Trying to access mappings while still calculating the binding.");
		Map<FreeIdentifier, Expression> finalBinding = new HashMap<FreeIdentifier, Expression>();
		for (FreeIdentifier ident : binding.keySet()) {
			Expression exp = binding.get(ident);
			Type newType = exp.getType();
			FreeIdentifier newIdent = factory.makeFreeIdentifier(
					ident.getName(), null, newType);
			finalBinding.put(newIdent, exp);
		}
		return finalBinding;
	}

	public ITypeEnvironment getTypeEnvironment() {
		if (!isImmutable)
			throw new UnsupportedOperationException(
					"Trying to access type environment while still calculating the binding.");
		return typeEnvironment.clone();
	}

	public void setAssociativeExpressionComplement(
			AssociativeExpressionComplement comp) {
		this.expComplement = comp;
	}

	public AssociativeExpressionComplement getAssociativeExpressionComplement() {
		return expComplement;
	}

	public Map<PredicateVariable, Predicate> getPredicateMappings() {
		if (!isImmutable)
			throw new UnsupportedOperationException(
					"Trying to access mappings while still calculating the binding.");
		return Collections.unmodifiableMap(predBinding);
	}

	public boolean putPredicateMapping(PredicateVariable var, Predicate p) {
		if (isImmutable)
			throw new UnsupportedOperationException(
					"Trying to add a mapping after the matching process finished.");
		if (predBinding.get(var) != null && !p.equals(predBinding.get(var))) {
			return false;
		}
		predBinding.put(var, p);
		return true;
	}

	public AssociativePredicateComplement getAssociativePredicateComplement() {
		return predComplement;
	}

	public void setAssociativePredicateComplement(
			AssociativePredicateComplement comp) {
		this.predComplement = comp;
	}

	@Override
	public FormulaFactory getFormulaFactory() {
		return factory;
	}

	public boolean canUnifyTypes(Type expressionType, Type patternType) {
		if (isImmutable) {
			return false;
		}
		if (patternType instanceof IntegerType) {
			return expressionType instanceof IntegerType;
		} else if (patternType instanceof BooleanType) {
			return expressionType instanceof BooleanType;
		} else if (patternType instanceof GivenType) {
			return putTypeMapping(factory.makeFreeIdentifier(
					((GivenType) patternType).getName(), null, patternType
							.toExpression(factory).getType()), expressionType);
		} else if (patternType instanceof PowerSetType) {
			if (expressionType instanceof PowerSetType) {
				Type pBase = patternType.getBaseType();
				Type fBase = expressionType.getBaseType();
				return canUnifyTypes(fBase, pBase);
			}
		} else if (patternType instanceof ProductType) {
			if (expressionType instanceof ProductType) {
				Type pLeft = ((ProductType) patternType).getLeft();
				Type fLeft = ((ProductType) expressionType).getLeft();

				Type pRight = ((ProductType) patternType).getRight();
				Type fRight = ((ProductType) expressionType).getRight();

				return canUnifyTypes(fLeft, pLeft)
						&& canUnifyTypes(fRight, pRight);
			}
		} else if (patternType instanceof ParametricType) {
			if (expressionType instanceof ParametricType) {

				ParametricType patParametricType = (ParametricType) patternType;
				ParametricType expParametricType = (ParametricType) expressionType;
				if (!patParametricType.getExprExtension().equals(
						expParametricType.getExprExtension())) {
					return false;
				}
				Type[] patTypes = patParametricType.getTypeParameters();
				Type[] expTypes = expParametricType.getTypeParameters();
				boolean ok = true;
				for (int i = 0; i < patTypes.length; i++) {
					ok &= canUnifyTypes(expTypes[i], patTypes[i]);
					if (!ok) {
						return false;
					}
				}
				return true;
			}
		}
		// unification not possible
		return false;
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
		// if there is a binding for ident that is different from the type expression
		if(binding.get(ident) != null && !binding.get(ident).equals(type.toExpression(factory))){
			return false;
		}
		// if there is a type instant. for ident that is different from type
		if (typeParametersInstantiations.get(ident) != null
				&& !typeParametersInstantiations.get(ident).equals(type)) {
			return false;
		}
		// all OK
		typeParametersInstantiations.put(ident, type);
		return true;
	}

	// ////////////////////////////////////////////////////////////////////////////////
	// ////////////////////////////////////////////////////////////////////////////////
	// ////////////////////////////////////////////////////////////////////////////////
	// //// Conditions for inserting a new expression mapping
	/**
	 * Returns whether the types of the expression and the identifier are
	 * compatible.
	 * 
	 * @param expressionType
	 *            the type of the expression
	 * @param identifierType
	 *            the type of the identifier pattern
	 * @return whether the two types are compatible
	 */
	protected boolean condition1_CanUnifyTypes(Type expressionType,
			Type identifierType) {
		return canUnifyTypes(expressionType, identifierType);
	}

	/**
	 * Checks the condition when the identifier is a given type in which case
	 * the expression has to be a type expression.
	 * 
	 * @param expression
	 * @param identifier
	 *            the
	 * @return whether the condition is met
	 */
	protected boolean condition2_IdentifierIsGivenType(Expression expression,
			FreeIdentifier identifier) {
		Set<GivenType> allPGivenTypes = identifier.getGivenTypes();
		if (isIdentAGivenType(identifier, allPGivenTypes)) {
			return expression.isATypeExpression();
		}
		return true;
	}

	// ////////////////////////////////////////////////////////////////////////////////
	// ////////////////////////////////////////////////////////////////////////////////
	// ////////////////////////////////////////////////////////////////////////////////

	/**
	 * Checks whether the identifier is a given type.
	 * 
	 * @param i
	 *            the identifier
	 * @param types
	 *            the set of given types
	 * @return whether the identifier is a given type
	 */
	protected boolean isIdentAGivenType(FreeIdentifier i, Set<GivenType> types) {
		for (GivenType gt : types) {
			if (i.equals(gt.toExpression(factory))) {
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
		if (isImmutable
				|| !condition1_CanUnifyTypes(e.getType(), ident.getType())
				|| !condition2_IdentifierIsGivenType(e, ident)
				|| (binding.get(ident) != null && !e.equals(binding.get(ident)))) {
			return false;
		}

		return true;
	}

	protected boolean isPredicateMappingInsertable(PredicateVariable var,
			Predicate p) {
		if (isImmutable
				|| (predBinding.get(var) != null && !p.equals(predBinding
						.get(var))))
			return false;

		return true;
	}
}
