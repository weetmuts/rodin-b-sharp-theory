/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.internal.core.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.ParametricType;
import org.eventb.core.ast.PowerSetType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.ProductType;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.ast.extension.IOperator;
import org.eventb.core.ast.extension.IOperatorGroup;
import org.eventb.core.ast.extension.IOperatorProperties.FormulaType;
import org.eventb.core.ast.extension.IOperatorProperties.Notation;
import org.eventb.core.ast.extension.ITypeMediator;

/**
 * Facilities class for obtaining information related to grammars and operators
 * plus other utilities.
 * 
 * @author maamria
 * 
 */
public class MathExtensionsUtilities {

	/**
	 * Literal predicate true.
	 */
	public static final Predicate BTRUE = FormulaFactory.getDefault().makeLiteralPredicate(Formula.BTRUE, null);
	
	protected static final String DUMMY_OPERATOR_GROUP = "NEW THEORY GROUP";

	/**
	 * Operator groups used in <link>BMath</link>.
	 */
	private static final String AST_PREFIX = "org.eventb.core.ast."; //$NON-NLS-1$
	public static final String RELOP_PRED = AST_PREFIX + "relOp";
	public static final String QUANTIFICATION = AST_PREFIX + "quantification";
	public static final String PAIR = AST_PREFIX + "pair";
	public static final String RELATION = AST_PREFIX + "relation";
	public static final String BINOP = AST_PREFIX + "binOp";
	public static final String INTERVAL = AST_PREFIX + "interval";
	public static final String ARITHMETIC = AST_PREFIX + "arithmetic";
	public static final String UNARY_RELATION = AST_PREFIX + "unaryRelation";
	public static final String FUNCTIONAL = AST_PREFIX + "functional";
	public static final String BRACE_SETS = AST_PREFIX + "braceSets";
	public static final String QUANTIFIED_PRED = AST_PREFIX + "quantifiedPred";
	public static final String LOGIC_PRED = AST_PREFIX + "logicPred";
	public static final String INFIX_PRED = AST_PREFIX + "infixPred";
	public static final String NOT_PRED = AST_PREFIX + "notPred";
	public static final String ATOMIC_PRED = AST_PREFIX + "atomicPred";
	public static final String ATOMIC_EXPR = AST_PREFIX + "atomicExpr";
	public static final String CLOSED = AST_PREFIX + "closed";
	public static final String BOOL_EXPR = AST_PREFIX + "boolExpr";
	public static final String INFIX_SUBST = AST_PREFIX + "infixSubst";

	/**
	 * Returns an appropriate group for the operator with the supplied properties.
	 * <p>
	 * TODO this is only stop gap.
	 * @param formulaType
	 * @param notation
	 * @param arity
	 * @return
	 */
	public static String getGroupFor(FormulaType formulaType,
			Notation notation, int arity) {
		String group = DUMMY_OPERATOR_GROUP;
		switch (formulaType) {
		case EXPRESSION: {
			switch (notation) {
			case INFIX: {
				break;
			}
			case PREFIX: {
				if (arity > 0) {
					group = CLOSED;
				} else {
					group = ATOMIC_EXPR;
				}
				break;
			}
			case POSTFIX: {
				// leave as part of the dummy group TODO check this
			}
			}
			break;
		}
		case PREDICATE: {
			switch (notation) {
			case INFIX: {
				if (arity == 0) {
					group = ATOMIC_PRED;
				}
				// infix makes sense for ops with more than two args
				if (arity > 1) {
					group = INFIX_PRED;
				}
				break;
			}
			case PREFIX: {
				if (arity > 0) {
					group = CLOSED;
				} else {
					group = ATOMIC_PRED;
				}
				break;
			}
			case POSTFIX: {
				// leave as part of the dummy group TODO check this
			}
			}
		}
		}
		return group;
	}

	/**
	 * Checks whether an operator with the given ID is already in the given
	 * factory.
	 * 
	 * @param id
	 *            the ID of the new operator
	 * @param ff
	 *            the formula factory
	 * @return whether an operator with the given ID already exists
	 */
	public static boolean checkOperatorID(String id, FormulaFactory ff) {
		return !populateOpIDs(ff).contains(id);
	}

	/**
	 * Checks whether an operator with the given syntax symbol is already in the
	 * given factory.
	 * 
	 * @param symbol
	 *            the syntax symbol of the new operator
	 * @param ff
	 *            the formula factory
	 * @return whether an operator with the given symbol already exists
	 */
	public static boolean checkOperatorSyntaxSymbol(String symbol,
			FormulaFactory ff) {
		return !populateOpSyntaxSymbols(ff).contains(symbol);
	}

	/**
	 * Checks whether a group with the given ID is already in the given factory.
	 * 
	 * @param id
	 *            the ID of the new group
	 * @param ff
	 *            the formula factory
	 * @return whether a group with the given ID already exists
	 */
	public static boolean checkGroupID(String id, FormulaFactory ff) {
		return !populateOperatorGroupIDs(ff).contains(id);
	}

	/**
	 * Populates all syntax symbols of all operators recognised by the given formula
	 * factory.
	 * 
	 * @param ff the formula factory
	 * @return the list of all syntax symbols
	 */
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
	/**
	 * Populates all IDs of all operators recognised by the given formula
	 * factory.
	 * 
	 * @param ff the formula factory
	 * @return the list of all existing IDs
	 */
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
	/**
	 * Populates all groups of all operators recognised by the given formula
	 * factory.
	 * 
	 * @param ff the formula factory
	 * @return the list of all existing group IDs
	 */
	static List<String> populateOperatorGroupIDs(FormulaFactory ff) {
		List<String> result = new ArrayList<String>();
		Set<IOperatorGroup> groups = ff.getGrammarView().getGroups();
		for (IOperatorGroup g : groups) {
			result.add(g.getId());
		}
		return result;
	}

	
	/**
	 * Returns the type parameters occurring in the given type.
	 * 
	 * @param type
	 *            the type
	 * @param factory
	 *            the formula factory
	 * @return the list of occurring types parameters
	 */
	public static List<Type> getTypeParametersInType(Type type,
			FormulaFactory factory) {
		FreeIdentifier[] idents = type.toExpression(factory)
				.getFreeIdentifiers();
		List<Type> types = new ArrayList<Type>();
		for (FreeIdentifier ident : idents) {
			types.add(factory.makeGivenType(ident.getName()));
		}
		return types;
	}

	/**
	 * Constructs the type variable-based reprsentation of the type <code>theoryType</code>. This representation is computed
	 * by replacing the given types in <code>theoryType</code> by their corresponding type variables in the map
	 * <code>parToTypeVarMap</code>. 
	 * 
	 * <p>For example, POW(A**B) gets translated to POW('0**'1) where '0 and '1 are the type variables corresponding to A and B respectively.</p>
	 * @param theoryType the type used to define the extension
	 * @param typeParameterToTypeVariablesMap the map between given types (type parameters in theories) to type variables
	 * @param mediator the mediator
	 * @return the constructed type
	 */
	public static Type constructPatternType(Type theoryType,
			Map<Type, Type> typeParameterToTypeVariablesMap, ITypeMediator mediator) {

		if (typeParameterToTypeVariablesMap.containsKey(theoryType)) {
			return typeParameterToTypeVariablesMap.get(theoryType);
		} else {
			if (theoryType instanceof PowerSetType) {
				return mediator.makePowerSetType(constructPatternType(
						theoryType.getBaseType(), typeParameterToTypeVariablesMap, mediator));
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
				return mediator.makeParametricType(Arrays.asList(newTypePars),
						((ParametricType) theoryType).getExprExtension());
			}
		}
		return theoryType;
	}

	/**
	 * Returns the types of the given expressions.
	 * 
	 * @param exps
	 *            the expressions
	 * @return the corresponding types
	 */
	public static Type[] getTypes(Expression[] exps) {
		Type[] types = new Type[exps.length];
		for (int i = 0; i < types.length; i++) {
			types[i] = exps[i].getType();
		}
		return types;
	}

	/**
	 * Creates a type environment using the given factory with all the names occurring
	 * in the given type environment.
	 * 
	 * @param typeEnvironment the old type environment
	 * @param factory the formula factory
	 * @return the new type environment
	 */
	public static ITypeEnvironment getTypeEnvironmentForFactory(
			ITypeEnvironment typeEnvironment, FormulaFactory factory){
		ITypeEnvironment newTypeEnvironment = factory.makeTypeEnvironment();
		for (String name : typeEnvironment.getNames()){
			newTypeEnvironment.addName(name, typeEnvironment.getType(name));
		}
		return newTypeEnvironment;
	}

	/**
	 * Returns a singleton set containing one mathematical extension.
	 * @param element the mathematical extension
	 * @return a singleton set
	 */
	public static Set<IFormulaExtension> singletonExtension(IFormulaExtension element){
		Set<IFormulaExtension> set = new HashSet<IFormulaExtension>();
		set.add(element);
		return set;
	}

	
}
