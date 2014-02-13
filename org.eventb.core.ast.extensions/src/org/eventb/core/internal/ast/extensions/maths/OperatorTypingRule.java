/*******************************************************************************
 * Copyright (c) 2010, 2014 University of Southampton and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University of Southampton - initial API and implementation
 *******************************************************************************/
package org.eventb.core.internal.ast.extensions.maths;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eventb.core.ast.BooleanType;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.IntegerType;
import org.eventb.core.ast.LanguageVersion;
import org.eventb.core.ast.ParametricType;
import org.eventb.core.ast.PowerSetType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.ProductType;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.core.ast.extension.IExtendedFormula;
import org.eventb.core.ast.extension.IPredicateExtension;
import org.eventb.core.ast.extension.ITypeMediator;
import org.eventb.core.ast.extension.IWDMediator;
import org.eventb.core.ast.extensions.maths.AstUtilities;
import org.eventb.core.ast.extensions.wd.YMediator;

/**
 * 
 * A basic implementation of a typing rule for operators.
 * 
 * @param <F>
 *            the type of the operator (Predicate or Expression)
 * 
 * @since 1.0
 * 
 * @author maamria
 * 
 * @see IPredicateExtension
 * @see IExpressionExtension
 */
public abstract class OperatorTypingRule {

	protected List<OperatorArgument> operatorArguments;
	protected int arity = 0;
	protected Set<GivenType> typeParameters;
	protected Predicate wdPredicate;
	/**
	 * This is the D based WD predicate of this operator.
	 */
	protected Predicate dWDPredicate;
	
	/**
	 * Creates a basic typing rule with the supplied arguments, the given well-definedness predicate.
	 * 
	 * <p> A formula factory must be supplied, and it is used to handle all operations related to 
	 * type checking instances of the concerned operator.
	 * @param operatorArguments the list of operator arguments, must not be <code>null</code>
	 * @param wdPredicate the well-definedness predicate, must not be <code>null</code>
	 * @param dWDPredicate the D well-definedness predicate, must not be <code>null</code>
	 */
	public OperatorTypingRule(List<OperatorArgument> operatorArguments, Predicate wdPredicate, Predicate dWDPredicate) {
		AstUtilities.ensureNotNull(operatorArguments, wdPredicate, dWDPredicate);
		this.operatorArguments = operatorArguments;
		this.arity = operatorArguments.size();
		this.typeParameters = new HashSet<GivenType>();
		for (OperatorArgument operatorArgument : operatorArguments){
			addTypeParameters(AstUtilities.getGivenTypes(operatorArgument.getArgumentType()));
		}
		this.wdPredicate = wdPredicate;
		this.dWDPredicate = dWDPredicate;
	}

	public int getArity() {
		return arity;
	}
	
	public Predicate getWDPredicate(IExtendedFormula formula,
			IWDMediator wdMediator) {
		FormulaFactory factory = wdMediator.getFormulaFactory();
		Formula<?> unflattened = AstUtilities.unflatten(formula, factory);
		Map<FreeIdentifier, Expression> allSubs = getOverallSubstitutions(
				((IExtendedFormula) unflattened).getChildExpressions(), factory);
		if (allSubs == null) {
			return null;
		}
		Predicate wdToUse = wdPredicate;
		// if the call is to generate a D WD condition
		if(wdMediator instanceof YMediator){
			wdToUse = dWDPredicate;
		}
		String rawWD = wdToUse.toString();
		Predicate pred = factory
				.parsePredicate(rawWD, LanguageVersion.V2, null)
				.getParsedPredicate();
		ITypeEnvironment typeEnvironment = generateOverallTypeEnvironment(allSubs, factory);
		pred.typeCheck(typeEnvironment);
		Predicate actWDPred = pred.substituteFreeIdents(allSubs, factory);
		Predicate actWDPredWD = actWDPred.getWDPredicate(factory);
		return AstUtilities.conjunctPredicates(new Predicate[] {
				actWDPredWD, actWDPred }, factory);
	}

	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (o == null || !(o instanceof OperatorTypingRule)) {
			return false;
		}
		OperatorTypingRule rule = (OperatorTypingRule) o;
		return operatorArguments.equals(rule.operatorArguments)
				&& arity == rule.arity
				&& typeParameters.equals(rule.typeParameters)
				&& wdPredicate.equals(rule.wdPredicate)
				&& (dWDPredicate == null ? rule.dWDPredicate == null : dWDPredicate.equals(rule.dWDPredicate));
	}

	public int hashCode() {
		return 97 * (operatorArguments.hashCode() + arity
				+ typeParameters.hashCode() + wdPredicate.hashCode()) + 
				(dWDPredicate == null ? 0 : dWDPredicate.hashCode());
	}

	public String toString() {
		return toString(operatorArguments);
	}

	private void addTypeParameters(List<GivenType> types) {
		for (GivenType type : types) {
			if (!typeParameters.contains(type)) {
				typeParameters.add(type);
			}
		}
	}

	/**
	 * Unifies the argument type with the type of the actual type (i.e., after
	 * extended expression is created from this extension). The unification
	 * stops at the level where the argument type is a given type. It also
	 * terminates at the level where the argument type is an instance of a
	 * pre-defined Event-B type.
	 * 
	 * @param argumentType
	 *            the type of the argument
	 * @param actualType
	 *            the type of a potential instance of this argument
	 * @param calculatedInstantiations
	 *            the gathered instantiations
	 * @return whether the type unification has succeeded
	 */
	protected boolean unifyTypes(Type argumentType, Type actualType,
			Map<GivenType, Type> calculatedInstantiations) {
		if (argumentType instanceof PowerSetType) {
			if (!(actualType instanceof PowerSetType)) {
				return false;
			}
			Type argumentBaseType = argumentType.getBaseType();
			Type actualBaseType = actualType.getBaseType();
			return unifyTypes(argumentBaseType, actualBaseType,
					calculatedInstantiations);
		} else if (argumentType instanceof BooleanType) {
			if (!(actualType instanceof BooleanType)) {
				return false;
			}
		} else if (argumentType instanceof GivenType) {
			if (calculatedInstantiations.get(argumentType) != null
					&& !calculatedInstantiations.get(argumentType).equals(
							actualType)) {
				return false;
			}
			calculatedInstantiations.put((GivenType) argumentType, actualType);
			return true;
		} else if (argumentType instanceof IntegerType) {
			if (!(actualType instanceof IntegerType)) {
				return false;
			}
		} else if (argumentType instanceof ProductType) {
			if (!(actualType instanceof ProductType)) {
				return false;
			}
			return unifyTypes(((ProductType) argumentType).getLeft(),
					((ProductType) actualType).getLeft(),
					calculatedInstantiations)
					&& unifyTypes(((ProductType) argumentType).getRight(),
							((ProductType) actualType).getRight(),
							calculatedInstantiations);
		} else if (argumentType instanceof ParametricType) {
			if (!(actualType instanceof ParametricType)) {
				return false;
			}
			ParametricType argParType = (ParametricType) argumentType;
			ParametricType actParType = (ParametricType) actualType;
			if (!argParType.getExprExtension().equals(
					actParType.getExprExtension())) {
				return false;
			}
			Type[] argTypes = argParType.getTypeParameters();
			Type[] actTypes = actParType.getTypeParameters();
			if (argTypes.length != actTypes.length) {
				return false;
			}
			boolean ok = true;
			for (int i = 0; i < argTypes.length; i++) {
				ok &= unifyTypes(argTypes[i], actTypes[i],
						calculatedInstantiations);
			}
			return ok;
		}
		return true;
	}

	protected Map<FreeIdentifier, Expression> getOverallSubstitutions(Expression[] childrenExpressions, FormulaFactory factory) {
		Type[] childrenTypes = AstUtilities
				.getTypes(childrenExpressions);
		Map<FreeIdentifier, Expression> initial = getTypeSubstitutions(childrenTypes, factory);
		if (initial != null) {
			for (OperatorArgument arg : operatorArguments) {
				initial.put(factory.makeFreeIdentifier(arg.getArgumentName(),
						null, childrenTypes[arg.getIndex()]),
						childrenExpressions[arg.getIndex()]);
			}
		}
		return initial;
	}

	protected Map<FreeIdentifier, Expression> getTypeSubstitutions(Type[] childrenTypes, FormulaFactory factory) {
		Map<FreeIdentifier, Expression> subs = new HashMap<FreeIdentifier, Expression>();
		Map<GivenType, Type> instantiations = new HashMap<GivenType, Type>();
		for (int i = 0; i < childrenTypes.length; i++) {
			if (!isValidTypeInstantiation(i, childrenTypes[i], instantiations)) {
				return null;
			}
		}
		for (GivenType gType : instantiations.keySet()) {
			subs.put(factory.makeFreeIdentifier(gType.getName(), null,
					instantiations.get(gType).toExpression(factory).getType()),
					instantiations.get(gType).toExpression(factory));
		}
		return subs;
	}

	protected boolean isValidTypeInstantiation(int argumentIndex,
			Type proposedType, Map<GivenType, Type> calculatedInstantiations) {
		Type argumentType = operatorArguments.get(argumentIndex)
				.getArgumentType();
		if (argumentType == null) {
			return false;
		}
		return unifyTypes(argumentType, proposedType, calculatedInstantiations);
	}

	protected ITypeEnvironment generateTypeParametersTypeEnvironment(Map<FreeIdentifier, Expression> typeSubs, FormulaFactory factory) {
		ITypeEnvironment actualTypeEnvironment = factory.makeTypeEnvironment();
		for (FreeIdentifier ident : typeSubs.keySet()) {
			actualTypeEnvironment.addName(ident.getName(), typeSubs.get(ident)
					.getType());
		}
		return actualTypeEnvironment;
	}

	protected ITypeEnvironment generateOverallTypeEnvironment(Map<FreeIdentifier, Expression> allSubs, FormulaFactory factory) {
		ITypeEnvironment actualTypeEnvironment = factory.makeTypeEnvironment();
		for (FreeIdentifier ident : allSubs.keySet()) {
			actualTypeEnvironment.addName(ident.getName(), allSubs.get(ident)
					.getType());
		}
		return actualTypeEnvironment;
	}

	/**
	 * Constructs the type variable-based representation of the type
	 * <code>theoryType</code>. This representation is computed by replacing the
	 * given types in <code>theoryType</code> by their corresponding type
	 * variables in the map <code>parToTypeVarMap</code>.
	 * 
	 * <p>
	 * For example, POW(A**B) gets translated to POW('0**'1) where '0 and '1 are
	 * the type variables corresponding to A and B respectively.
	 * </p>
	 * 
	 * @param theoryType
	 *            the type used to define the extension
	 * @param typeParameterToTypeVariablesMap
	 *            the map between given types (type parameters in theories) to
	 *            type variables
	 * @param mediator
	 *            the mediator
	 * @return the constructed type
	 */
	protected Type constructPatternType(Type theoryType,
			Map<GivenType, Type> typeParameterToTypeVariablesMap,
			ITypeMediator mediator) {

		if (typeParameterToTypeVariablesMap.containsKey(theoryType)) {
			return typeParameterToTypeVariablesMap.get(theoryType);
		} else {
			if (theoryType instanceof PowerSetType) {
				return mediator.makePowerSetType(constructPatternType(
						theoryType.getBaseType(),
						typeParameterToTypeVariablesMap, mediator));
			} else if (theoryType instanceof ProductType) {
				return mediator.makeProductType(
						constructPatternType(
								((ProductType) theoryType).getLeft(),
								typeParameterToTypeVariablesMap, mediator),
						constructPatternType(
								((ProductType) theoryType).getRight(),
								typeParameterToTypeVariablesMap, mediator));
			} else if (theoryType instanceof ParametricType) {
				Type[] typePars = ((ParametricType) theoryType)
						.getTypeParameters();
				Type[] newTypePars = new Type[typePars.length];
				for (int i = 0; i < typePars.length; i++) {
					newTypePars[i] = constructPatternType(typePars[i],
							typeParameterToTypeVariablesMap, mediator);
				}
				final IExpressionExtension exprExtension = ((ParametricType) theoryType)
						.getExprExtension();
				return mediator.makeParametricType(Arrays.asList(newTypePars),
						(IExpressionExtension) exprExtension);
			}
		}
		return theoryType;
	}
	
	// utility
	private static <E> String toString(List<E> list) {
		String result = "";
		for (int i = 0; i < list.size(); i++) {
			result += list.get(i).toString();
			if (i < list.size() - 1) {
				result += ", ";
			}
		}
		return result;
	}

}
