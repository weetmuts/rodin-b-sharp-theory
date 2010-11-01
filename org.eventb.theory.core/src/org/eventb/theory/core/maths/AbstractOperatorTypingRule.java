/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.maths;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eventb.core.ast.BooleanType;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.ITypeCheckResult;
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
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.ast.extension.IPredicateExtension;
import org.eventb.core.ast.extension.IWDMediator;
import org.eventb.theory.core.TheoryCoreFacadeAST;
import org.eventb.theory.core.TheoryCoreFacadeGeneral;
import org.eventb.theory.internal.core.util.CoreUtilities;
import org.eventb.theory.internal.core.util.MathExtensionsUtilities;

/**
 * 
 * A basic implementation of a typing rule for operators.
 * 
 * @param <F> the type of the operator (Predicate or Expression)
 * 
 * @since 1.0
 * 
 * @author maamria
 * 
 * @see IPredicateExtension
 * @see IExpressionExtension
 */
public abstract class AbstractOperatorTypingRule<F extends Formula<F>> implements IOperatorTypingRule<F> {

	protected List<IOperatorArgument> argumentsTypes;
	protected int arity = 0;
	protected List<GivenType> typeParameters;
	protected F directDefinition;
	protected Predicate wdPredicate;
	protected boolean isAssociative;

	public AbstractOperatorTypingRule(F directDefinition,
			Predicate wdPredicate, boolean isAssociative) {
		this.argumentsTypes = new ArrayList<IOperatorArgument>();
		this.typeParameters = new ArrayList<GivenType>();
		this.directDefinition = directDefinition;
		this.wdPredicate = wdPredicate;
		this.isAssociative = isAssociative;
	}

	public void addOperatorArgument(IOperatorArgument arg) {
		argumentsTypes.add(arg);
		argumentsTypes = MathExtensionsUtilities.sort(argumentsTypes);
		arity++;
	}

	public boolean equals(Object o) {
		if(o == this)
			return true;
		if (o == null || !(o instanceof AbstractOperatorTypingRule)) {
			return false;
		}
		AbstractOperatorTypingRule<?> rule = (AbstractOperatorTypingRule<?>) o;
		return argumentsTypes.equals(rule.argumentsTypes)
				&& arity == rule.arity
				&& typeParameters.equals(rule.typeParameters)
				&& wdPredicate.equals(rule.wdPredicate)
				&& directDefinition.equals(rule.directDefinition);
	}

	public int hashCode() {
		return 97 * (argumentsTypes.hashCode() + arity
				+ typeParameters.hashCode() + wdPredicate.hashCode() + directDefinition
				.hashCode());
	}

	public String toString() {
		return TheoryCoreFacadeGeneral.toString(argumentsTypes);
	}

	public void addTypeParameters(List<GivenType> types) {
		for (GivenType type : types) {
			if (!typeParameters.contains(type)) {
				typeParameters.add(type);
			}
		}
	}

	public int getArity() {
		return arity;
	}

	@Override
	public Predicate getWDPredicate(IExtendedFormula formula,
			IWDMediator wdMediator) {
		FormulaFactory factory = wdMediator.getFormulaFactory();
		Expression[] childrenExprs = formula.getChildExpressions();
		IOperatorExtension<?> operatorExtension = (IOperatorExtension<?>) formula
				.getExtension();
		Formula<?> flattened = (Formula<?>) formula;
		if (operatorExtension.isAssociative()) {
			flattened = TheoryCoreFacadeAST.unflatten(operatorExtension, childrenExprs, factory);
		}

		Map<FreeIdentifier, Expression> allSubs = getOverallSubstitutions(
				((IExtendedFormula) flattened).getChildExpressions(), factory);
		if (allSubs == null) {
			return null;
		}
		String rawWD = wdPredicate.toString();
		Predicate pred = factory
				.parsePredicate(rawWD, LanguageVersion.V2, null)
				.getParsedPredicate();
		ITypeEnvironment typeEnvironment = generateOverallTypeEnvironment(
				allSubs, factory);
		pred.typeCheck(typeEnvironment);
		Predicate actWDPred = pred.substituteFreeIdents(allSubs, factory);
		Predicate actWDPredWD = actWDPred.getWDPredicate(factory);
		return CoreUtilities.conjunctPredicates(new Predicate[] { actWDPredWD,
				actWDPred }, factory);
	}

	@Override
	public F expandDefinition(IOperatorExtension<F> extension, F extendedFormula,
			FormulaFactory factory) {
		IExtendedFormula eForm = (IExtendedFormula) extendedFormula;
		IFormulaExtension formExtension =eForm.getExtension();
		if(formExtension.equals(extension)){
			IExtendedFormula extendedPredicate = (IExtendedFormula) extendedFormula;
			ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
			Expression[] exps = extendedPredicate.getChildExpressions();
			Map<FreeIdentifier, Expression> subs = 
				new LinkedHashMap<FreeIdentifier, Expression>();
			Map<GivenType, Type> typeSubs = 
				new LinkedHashMap<GivenType, Type>();
			int i = 0 ;
			for(IOperatorArgument arg : argumentsTypes){
				String argumentName = arg.getArgumentName();
				Type targetType = exps[i].getType();
				typeEnvironment.addName(argumentName, targetType);
				subs.put(factory.makeFreeIdentifier(argumentName, null, targetType), exps[i]);
				unifyTypes(arg.getArgumentType(), targetType, typeSubs);
				i++;
			}
			for (GivenType gType : typeSubs.keySet()){
				subs.put(factory.makeFreeIdentifier(gType.getName(), null, typeSubs.get(gType).toExpression(factory).getType()), 
						typeSubs.get(gType).toExpression(factory));
			}
			String raw = directDefinition.toString();
			F newForm = getParsedFormula(raw, factory);
			if(newForm == null){
				return extendedFormula;
			}
			ITypeCheckResult tcResult = newForm.typeCheck(typeEnvironment);
			if(tcResult.hasProblem()){
				return extendedFormula;
			}
			return newForm.substituteFreeIdents(subs, factory);
		}
		return extendedFormula;
	}

	@Override
	public List<IOperatorArgument> getOperatorArguments() {
		return argumentsTypes;
	}
	
	@Override
	public Predicate getWDPredicate() {
		return wdPredicate;
	}
	
	/**
	 * Returns the parsed formula from the given string.
	 * @param raw the string of the formula
	 * @param factory the formula factory
	 * @return the parsed formula or <code>null</code> if parsing has problems
	 */
	protected abstract F getParsedFormula(String raw, FormulaFactory factory);

	/**
	 * Unifies the argument type with the type of the actual type (i.e., after
	 * extended expression is created from this extension). The unification
	 * stops at the level where the ergument type is a given type. It also
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

	protected Map<FreeIdentifier, Expression> getOverallSubstitutions(
			Expression[] childrenExpressions, FormulaFactory factory) {
		Type[] childrenTypes = MathExtensionsUtilities
				.getTypes(childrenExpressions);
		Map<FreeIdentifier, Expression> initial = getTypeSubstitutions(
				childrenTypes, factory);
		if (initial != null) {
			for (IOperatorArgument arg : argumentsTypes) {
				initial.put(factory.makeFreeIdentifier(arg.getArgumentName(),
						null, childrenTypes[arg.getIndex()]),
						childrenExpressions[arg.getIndex()]);
			}
		}
		return initial;
	}

	protected Map<FreeIdentifier, Expression> getTypeSubstitutions(
			Type[] childrenTypes, FormulaFactory factory) {
		Map<FreeIdentifier, Expression> subs = new HashMap<FreeIdentifier, Expression>();
		Map<GivenType, Type> instantiations = new HashMap<GivenType, Type>();
		if (isAssociative) {
			if (!isValidTypeInstantiation(0, childrenTypes[0], instantiations)) {
				return null;
			}
		} else {
			for (int i = 0; i < childrenTypes.length; i++) {

				if (!isValidTypeInstantiation(i, childrenTypes[i],
						instantiations)) {
					return null;
				}
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
		Type argumentType = argumentsTypes.get(argumentIndex).getArgumentType();
		if (argumentType == null) {
			return false;
		}
		return unifyTypes(argumentType, proposedType, calculatedInstantiations);
	}

	protected ITypeEnvironment generateTypeParametersTypeEnvironment(
			Map<FreeIdentifier, Expression> typeSubs, FormulaFactory factory) {
		ITypeEnvironment actualTypeEnvironment = factory.makeTypeEnvironment();
		for (FreeIdentifier ident : typeSubs.keySet()) {
			actualTypeEnvironment.addName(ident.getName(), typeSubs.get(ident)
					.getType());
		}
		return actualTypeEnvironment;
	}

	protected ITypeEnvironment generateOverallTypeEnvironment(
			Map<FreeIdentifier, Expression> allSubs, FormulaFactory factory) {
		ITypeEnvironment actualTypeEnvironment = factory.makeTypeEnvironment();
		for (FreeIdentifier ident : allSubs.keySet()) {
			actualTypeEnvironment.addName(ident.getName(), allSubs.get(ident)
					.getType());
		}
		return actualTypeEnvironment;
	}

}
