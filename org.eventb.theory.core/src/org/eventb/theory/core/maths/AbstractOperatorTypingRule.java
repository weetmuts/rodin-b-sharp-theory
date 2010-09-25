/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.maths;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eventb.core.ast.BooleanType;
import org.eventb.core.ast.Expression;
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
import org.eventb.core.ast.extension.IWDMediator;
import org.eventb.theory.internal.core.util.CoreUtilities;
import org.eventb.theory.internal.core.util.MathExtensionsUtilities;

/**
 * 
 * A basic implementation of a typing rule for operators.
 * 
 * @author maamria
 * 
 * @see IPredicateExtension
 * @see IExpressionExtension
 */
public abstract class AbstractOperatorTypingRule
		implements IOperatorTypingRule {

	protected List<IOperatorArgument> argumentsTypes;
	protected int arity = 0;
	protected List<GivenType> typeParameters;
	protected Predicate wdPredicate;

	public AbstractOperatorTypingRule(Predicate wdPredicate) {
		this.argumentsTypes = new ArrayList<IOperatorArgument>();
		this.typeParameters = new ArrayList<GivenType>();
		this.wdPredicate = wdPredicate;
	}

	public void addOperatorArgument(IOperatorArgument arg) {
		argumentsTypes.add(arg);
		Collections.sort(argumentsTypes);
		arity++;
	}

	public void addTypeParameters(List<GivenType> types) {
		for (GivenType type : types) {
			if (!typeParameters.contains(type)) {
				typeParameters.add(type);
			}
		}
	}

	public void setWDPredicate(Predicate wdPredicate){
		this.wdPredicate = wdPredicate;
	}
	
	public int getArity() {
		return arity;
	}
	
	@Override
	public Predicate getWDPredicate(IExtendedFormula formula,
			IWDMediator wdMediator) {
		FormulaFactory factory = wdMediator.getFormulaFactory();
		Expression[] childrenExprs = formula.getChildExpressions();
		Map<FreeIdentifier, Expression> allSubs = getOverallSubstitutions(childrenExprs, factory);
		if(allSubs == null){
			return null;
		}
		String rawWD = wdPredicate.toString();
		Predicate pred = factory.parsePredicate(rawWD, LanguageVersion.V2, null).getParsedPredicate();
		ITypeEnvironment typeEnvironment = generateOverallTypeEnvironment(allSubs, factory);
		pred.typeCheck(typeEnvironment);
		Predicate actWDPred = pred.substituteFreeIdents(allSubs, factory);
		Predicate actWDPredWD = actWDPred.getWDPredicate(factory);
		return CoreUtilities.conjunctPredicates(new Predicate[]{actWDPredWD,actWDPred}, factory);
	}

	@Override
	public List<GivenType> getTypeParameters() {
		// TODO Auto-generated method stub
		return typeParameters;
	}

	@Override
	public List<IOperatorArgument> getOperatorArguments() {
		// TODO Auto-generated method stub
		return argumentsTypes;
	}

	/**
	 * Unifies the argument type with the type of the actual type (i.e., after extended expression is created from this extension).
	 * The unification stops at the level where the ergument type is a given type. It also terminates at the level where the argument type
	 * is an instance of a pre-defined Event-B type.
	 * @param argumentType the type of the argument
	 * @param actualType the type of a potential instance of this argument
	 * @param calculatedInstantiations the gathered instantiations
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
			return unifyTypes(((ProductType)argumentType).getLeft(), ((ProductType)actualType).getLeft(),
					calculatedInstantiations)
					&& unifyTypes(((ProductType)argumentType).getRight(),
							((ProductType)actualType).getRight(), calculatedInstantiations);
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
			Expression[] childrenExpressions, FormulaFactory factory){
		Type[] childrenTypes = MathExtensionsUtilities.getTypes(childrenExpressions);
		Map<FreeIdentifier, Expression> initial = getTypeSubstitutions(childrenTypes, factory);
		if(initial != null){
			for(IOperatorArgument arg : argumentsTypes){
				initial.put(factory.makeFreeIdentifier(arg.getArgumentName(), null, childrenTypes[arg.getIndex()]), 
						childrenExpressions[arg.getIndex()]);
			}
		}
		return initial;
	}
	
	protected Map<FreeIdentifier, Expression> getTypeSubstitutions(Type[] childrenTypes, FormulaFactory factory){
		Map<FreeIdentifier, Expression> subs = new HashMap<FreeIdentifier, Expression>();
		Map<GivenType, Type> instantiations = new HashMap<GivenType, Type>();
		for(int i = 0 ; i < childrenTypes.length; i++){
			if(!isValidTypeInstantiation(i, childrenTypes[i], instantiations)){
				return null;
			}
		}
		for(GivenType gType : instantiations.keySet()){
			subs.put(
					factory.makeFreeIdentifier(gType.getName(), 
							null, 
							instantiations.get(gType).toExpression(factory).getType()), 
					instantiations.get(gType).toExpression(factory));
		}
		return subs;
	}
	
	protected boolean isValidTypeInstantiation(
			int argumentIndex, Type proposedType, 
			Map<GivenType, Type> calculatedInstantiations){
		Type argumentType = argumentsTypes.get(argumentIndex).getArgumentType();
		if(argumentType == null){
			return false;
		}
		return unifyTypes(argumentType, proposedType, calculatedInstantiations);
	}
	
	protected ITypeEnvironment generateTypeParametersTypeEnvironment(Map<FreeIdentifier, Expression> typeSubs, FormulaFactory factory) {
		ITypeEnvironment actualTypeEnvironment = factory.makeTypeEnvironment();
		for (FreeIdentifier ident : typeSubs.keySet()) {
			actualTypeEnvironment.addName(ident.getName(), typeSubs.get(ident).getType());
		}
		return actualTypeEnvironment;
	}
	
	protected ITypeEnvironment generateOverallTypeEnvironment(Map<FreeIdentifier, Expression> allSubs, FormulaFactory factory) {
		ITypeEnvironment actualTypeEnvironment = factory.makeTypeEnvironment();
		for (FreeIdentifier ident : allSubs.keySet()) {
			actualTypeEnvironment.addName(ident.getName(), allSubs.get(ident).getType());
		}
		return actualTypeEnvironment;
	}

}
