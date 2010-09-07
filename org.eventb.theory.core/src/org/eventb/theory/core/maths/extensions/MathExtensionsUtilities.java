/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.maths.extensions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.ParametricType;
import org.eventb.core.ast.PowerSetType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.ProductType;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.ast.extension.IOperator;
import org.eventb.core.ast.extension.IOperatorGroup;
import org.eventb.core.ast.extension.ITypeMediator;
import org.eventb.core.ast.extension.IOperatorProperties.FormulaType;
import org.eventb.core.ast.extension.IOperatorProperties.Notation;
import org.eventb.theory.core.maths.OperatorArgument;

/**
 * Utilities class for obtaining information related to grammars and operators plus other utilities.
 * 
 * @author maamria
 * 
 */
public class MathExtensionsUtilities {

	/**
	 * Checks whether an operator with the given ID is already in the given factory.
	 * @param id the ID of the new operator
	 * @param ff the formula factory
	 * @return whether an operator with the given ID already exists
	 */
	public static boolean checkOperatorID(String id, FormulaFactory ff) {
		return !populateOpIDs(ff).contains(id);
	}

	/**
	 * Checks whether an operator with the given syntax symbol is already in the given factory.
	 * @param symbol the syntax symbol of the new operator
	 * @param ff the formula factory
	 * @return whether an operator with the given symbol already exists
	 */
	public static boolean checkOperatorSyntaxSymbol(String symbol,
			FormulaFactory ff) {
		return !populateOpSyntaxSymbols(ff).contains(symbol);
	}

	/**
	 * Checks whether a group with the given ID is already in the given factory.
	 * @param id the ID of the new group
	 * @param ff the formula factory
	 * @return whether a group with the given ID already exists
	 */
	public static boolean checkGroupID(String id, FormulaFactory ff) {
		return !populateOperatorGroupIDs(ff).contains(id);
	}

	static List<String> populateOpSyntaxSymbols(FormulaFactory ff) {
		List<String> result = new ArrayList<String>();
		Set<IOperatorGroup> groups = ff.getGrammarView().getGroups();
		for (IOperatorGroup g : groups) {
			for (IOperator op : g.getOperators()) {
				result.add(op.getSyntaxSymbol());
			}
		}
		return result;
	}

	static List<String> populateOpIDs(FormulaFactory ff) {
		List<String> result = new ArrayList<String>();
		Set<IOperatorGroup> groups = ff.getGrammarView().getGroups();
		for (IOperatorGroup g : groups) {
			for (IOperator op : g.getOperators()) {
				result.add(op.getId());
			}
		}
		return result;
	}

	static List<String> populateOperatorGroupIDs(FormulaFactory ff) {
		List<String> result = new ArrayList<String>();
		Set<IOperatorGroup> groups = ff.getGrammarView().getGroups();
		for (IOperatorGroup g : groups) {
			result.add(g.getId());
		}
		return result;
	}

	/**
	 * Returns the formula extension corresponding to the supplied details.
	 * @param isExpression whether this an expression extension
	 * @param operatorID the operator ID
	 * @param syntax the syntax symbol
	 * @param formulaType the formula type
	 * @param notation the notation
	 * @param isAssociative whether the operator is associative
	 * @param isCommutative whether the operator is commutative
	 * @param directDefinition the direct definition of the operator
	 * @param wdCondition the pattern well-definedness condition
	 * @param opArguments the arguments of the new operator
	 * @return the new formula extension
	 */
	public static IFormulaExtension getFormulaExtension(
			boolean isExpression, String operatorID, String syntax,
			FormulaType formulaType, Notation notation, boolean isAssociative,
			boolean isCommutative, Formula<?> directDefinition,
			Predicate wdCondition, HashMap<String, OperatorArgument> opArguments,
			List<GivenType> typeParameters) {
		if (isExpression) {
			return new ExpressionOperatorExtension(
					operatorID, syntax, formulaType, notation, 
					isAssociative, isCommutative, (Expression) directDefinition, 
					wdCondition, opArguments, typeParameters);
		} else {
			return new PredicateOperatorExtension(
					operatorID, syntax, formulaType, notation, isCommutative, 
					(Predicate) directDefinition, wdCondition, opArguments,
					typeParameters);
		}
	}

	/**
	 * Returns the type parameters occurring in the given type.
	 * @param type the type
	 * @param factory the formula factory
	 * @return the list of occurring types parameters
	 */
	public static List<Type> getTypeParametersInType(Type type, FormulaFactory factory) {
		FreeIdentifier[] idents = type.toExpression(factory).getFreeIdentifiers();
		List<Type> types = new ArrayList<Type>();
		for (FreeIdentifier ident : idents) {
			types.add(factory.makeGivenType(ident.getName()));
		}
		return types;
	}

	public static Type constructPatternTypeFor(Type theoryType,
			Map<Type, Type> parToTypeVarMap, ITypeMediator mediator) {

		if (parToTypeVarMap.containsKey(theoryType)) {
			return parToTypeVarMap.get(theoryType);
		} else {
			if (theoryType instanceof PowerSetType) {
				return mediator.makePowerSetType(constructPatternTypeFor(
						theoryType.getBaseType(), parToTypeVarMap, mediator));
			} else if (theoryType instanceof ProductType) {
				return mediator.makeProductType(
						constructPatternTypeFor(
								((ProductType) theoryType).getLeft(),
								parToTypeVarMap, mediator),
						constructPatternTypeFor(
								((ProductType) theoryType).getRight(),
								parToTypeVarMap, mediator));
			} else if(theoryType instanceof ParametricType){
				Type[] typePars = ((ParametricType) theoryType).getTypeParameters();
				Type[] newTypePars = new Type[typePars.length];
				for(int i = 0 ; i < typePars.length ; i++){
					newTypePars[i] = constructPatternTypeFor(typePars[i], 
							parToTypeVarMap, mediator);
				}
				return mediator.makeParametricType(
						Arrays.asList(newTypePars), 
						((ParametricType) theoryType).getExprExtension());
			}
		}
		return theoryType;
	}
	
	/**
	 * Returns the types of the given expressions.
	 * @param exps the expressions
	 * @return the corresponding types
	 */
	public static Type[] getTypes(Expression[] exps){
		Type[] types = new Type[exps.length];
		for(int i = 0 ; i < types.length ; i++){
			types[i] = exps[i].getType();
		}
		return types;
	}
	
	/**
	 * Creates a sorted list of the given element type.
	 * @param <E> the type of the elements
	 * @param collection the original collection
	 * @return the sorted list
	 */
	public static <E extends Comparable<E>> List<E> getSortedList(Collection<E> collection){
		List<E> list = new ArrayList<E>();
		for(E item : collection){
			list.add(item);
		}
		Collections.sort(list);
		return list;
	}
}
