/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.maths.extensions;

import java.util.HashMap;
import java.util.Map;

import org.eventb.core.ast.BooleanType;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.IntegerType;
import org.eventb.core.ast.InvalidExpressionException;
import org.eventb.core.ast.LanguageVersion;
import org.eventb.core.ast.ParametricType;
import org.eventb.core.ast.PowerSetType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.ProductType;
import org.eventb.core.ast.Type;
import org.eventb.theory.internal.core.util.CoreUtilities;

/**
 * @author maamria
 *
 */
public class PolymorphicTypeManipulation implements IPolymorphicTypeManipulation{
	private Map<String, Type> instantiations;
	private Map<String, Expression> argumentInstantiations;
	private Map<FreeIdentifier, Expression> overallInstantiations;
	private FormulaFactory factory;
	private Type definitionType;

	public PolymorphicTypeManipulation(FormulaFactory factory) {
		this.factory = factory;
		instantiations = new HashMap<String, Type>();
		argumentInstantiations = new HashMap<String, Expression>();
		overallInstantiations = new HashMap<FreeIdentifier, Expression>();
	}

	public boolean verifyType(Type proposedType)
			throws InvalidExpressionException {
		return synthesiseType(definitionType).equals(proposedType);
	}

	public Type synthesiseType(Type definitionType)
			throws InvalidExpressionException {
		this.definitionType = definitionType;
		if(definitionType == null){
			return null;
		}
		Expression typeExpression = definitionType.toExpression(factory);
		String rawTypeExp = typeExpression.toString();
		Expression exp = factory.parseExpression(rawTypeExp,
				LanguageVersion.V2, null).getParsedExpression();
		exp.typeCheck(generateActualTypeEnvironment());
		Expression actTypeExpression = exp.substituteFreeIdents(
				getTypeSubstitution(), factory);
		return actTypeExpression.toType();
	}

	protected void generateOverallSubstitutions(){
		ITypeEnvironment tEnvironment = generateActualTypeEnvironment();
		for (String name : tEnvironment.getNames()){
			if(CoreUtilities.isGivenSet(tEnvironment, name)){
				overallInstantiations.put(
						factory.makeFreeIdentifier(name, null, tEnvironment.getType(name)), 
						instantiations.get(name).toExpression(factory));
			}
			else {
				overallInstantiations.put(
						factory.makeFreeIdentifier(name, null, tEnvironment.getType(name)),
						argumentInstantiations.get(name));
			}
		}
	}
	
	public Predicate getWDPredicate(Predicate wdDefinitionPredicate) {
		generateOverallSubstitutions();
		String str = wdDefinitionPredicate.toString();
		Predicate pred = factory.parsePredicate(str,
				LanguageVersion.V2, null).getParsedPredicate();
		pred.typeCheck(generateActualTypeEnvironment());
		Predicate actWDPred = pred.substituteFreeIdents(overallInstantiations, factory);
		return actWDPred;
	}

	public Type getFinalType() throws InvalidExpressionException {
		return synthesiseType(definitionType);
	}

	public Map<FreeIdentifier, Expression> getTypeSubstitution() {
		Map<FreeIdentifier, Expression> map = new HashMap<FreeIdentifier, Expression>();
		for (String ident : instantiations.keySet()) {
			map.put(factory.makeFreeIdentifier(ident, null, instantiations
					.get(ident).toExpression(factory).getType()),
					instantiations.get(ident).toExpression(factory));
		}
		return map;
	}

	protected ITypeEnvironment generateActualTypeEnvironment() {
		ITypeEnvironment actualTypeEnvironment = factory
				.makeTypeEnvironment();
		for (String arg : argumentInstantiations.keySet()) {
			actualTypeEnvironment.addName(arg,
					argumentInstantiations.get(arg).getType());
		}
		for (String type : instantiations.keySet()) {
			actualTypeEnvironment.addName(type, instantiations.get(type)
					.toExpression(factory).getType());
		}
		return actualTypeEnvironment;
	}

	public boolean addArgumentMapping(String arg, Expression exp) {
		if (argumentInstantiations.containsKey(arg)) {
			return false;
		}
		argumentInstantiations.put(arg, exp);
		return true;
	}

	public boolean unifyTypes(Type argumentType, Type actualType) {
		if (argumentType instanceof PowerSetType) {
			if (!(actualType instanceof PowerSetType)) {
				return false;
			}
			Type argumentBaseType = argumentType.getBaseType();
			Type actualBaseType = actualType.getBaseType();
			return unifyTypes(argumentBaseType, actualBaseType);
		} else if (argumentType instanceof BooleanType) {
			if (!(actualType instanceof BooleanType)) {
				return false;
			}
		} else if (argumentType instanceof GivenType) {
			String name = ((GivenType) argumentType).getName();
			if (instantiations.get(name) != null
					&& !instantiations.get(name).equals(actualType)) {
				return false;
			}
			instantiations.put(name, actualType);
			return true;
		} else if (argumentType instanceof IntegerType) {
			if (!(actualType instanceof IntegerType)) {
				return false;
			}
		} else if (argumentType instanceof ProductType) {
			if (!(actualType instanceof ProductType)) {
				return false;
			}
			return unifyTypes(argumentType.getSource(),
					actualType.getSource())
					&& unifyTypes(argumentType.getTarget(),
							actualType.getTarget());
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
				ok &= unifyTypes(argTypes[i], actTypes[i]);
			}
			return ok;
		}
		return true;
	}

}