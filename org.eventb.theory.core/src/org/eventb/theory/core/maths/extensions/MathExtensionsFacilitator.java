/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.maths.extensions;

import static org.eventb.core.ast.extension.IOperatorProperties.FormulaType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.ParametricType;
import org.eventb.core.ast.PowerSetType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.ProductType;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.ast.extension.IOperator;
import org.eventb.core.ast.extension.IOperatorGroup;
import org.eventb.core.ast.extension.IOperatorProperties.Notation;
import org.eventb.core.ast.extension.ITypeMediator;
import org.eventb.theory.core.maths.IOperatorArgument;
import org.rodinp.core.IRodinElement;

/**
 * Facilities class for obtaining information related to grammars and operators
 * plus other utilities.
 * 
 * @author maamria
 * 
 */
public class MathExtensionsFacilitator {

	/**
	 * Literal predicate true.
	 */
	public static final Predicate BTRUE = FormulaFactory.getDefault().makeLiteralPredicate(Formula.BTRUE, null);
	
	protected static final String DUMMY_OPERATOR_GROUP = "NEW THEORY GROUP";

	/**
	 * Operator groups used in <link>BMath</link>.
	 */
	public static final String RELOP_PRED = "Relational Operator Predicate";
	public static final String QUANTIFICATION = "Quantification";
	public static final String PAIR = "Pair";
	public static final String RELATION = "Set of Relations";
	public static final String BINOP = "Binary Operator";
	public static final String INTERVAL = "Interval";
	public static final String ARITHMETIC = "Arithmetic";
	public static final String UNARY_RELATION = "Unary Relation";
	public static final String FUNCTIONAL = "Functional";
	public static final String BRACE_SETS = "Brace Sets";
	public static final String QUANTIFIED_PRED = "Quantified";
	public static final String LOGIC_PRED = "Logic Predicate";
	public static final String INFIX_PRED = "Infix Predicate";
	public static final String NOT_PRED = "Not Predicate";
	public static final String ATOMIC_PRED = "Atomic Predicate";
	public static final String ATOMIC_EXPR = "Atomic Expression";
	public static final String BOUND_UNARY = "Bound Unary";
	public static final String BOOL_EXPR = "Bool";
	public static final String INFIX_SUBST = "Infix Substitution";

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
					group = BOUND_UNARY;
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
					group = BOUND_UNARY;
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
	 * 
	 * @param operatorID
	 *            the operator ID
	 * @param syntax
	 *            the syntax symbol
	 * @param formulaType
	 *            the formula type
	 * @param notation
	 *            the notation
	 * @param isAssociative
	 *            whether the operator is associative
	 * @param isCommutative
	 *            whether the operator is commutative
	 * @param directDefinition
	 *            the direct definition of the operator
	 * @param wdCondition
	 *            the pattern well-definedness condition
	 * @param opArguments
	 *            the arguments of the new operator
	 * @return the new formula extension
	 */
	public static IFormulaExtension getFormulaExtension(
			String operatorID, String syntax, FormulaType formulaType,
			Notation notation, boolean isAssociative, boolean isCommutative,
			Formula<?> directDefinition, Predicate wdCondition,
			List<IOperatorArgument> opArguments,
			List<GivenType> typeParameters, IRodinElement source) {
		if (formulaType.equals(FormulaType.EXPRESSION)) {
			return new ExpressionOperatorExtension(operatorID, syntax,
					formulaType, notation, isAssociative, isCommutative,
					(Expression) directDefinition, wdCondition, opArguments,
					typeParameters, source);
		} else {
			return new PredicateOperatorExtension(operatorID, syntax,
					formulaType, notation, isCommutative,
					(Predicate) directDefinition, wdCondition, opArguments,
					typeParameters, source);
		}
	}
	
	public static Set<IFormulaExtension> getSimpleDatatypeExtensions(String identifier, String[] typeArguments, FormulaFactory factory){
		return factory.makeDatatype(new SimpleDatatypeExtension(identifier, typeArguments)).getExtensions();
	}
	
	public static Set<IFormulaExtension> getCompleteDatatypeExtensions(String identifier, String[] typeArguments, Map<String, Map<String, Type>> constructors, FormulaFactory factory){
		return factory.makeDatatype(new CompleteDatatypeExtension(
				identifier, typeArguments, constructors)).getExtensions();
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
	 * @param parToTypeVarMap the map between given types (type parameters in theories) to type variables
	 * @param mediator the mediator
	 * @return the constructed type
	 */
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
			} else if (theoryType instanceof ParametricType) {
				Type[] typePars = ((ParametricType) theoryType)
						.getTypeParameters();
				Type[] newTypePars = new Type[typePars.length];
				for (int i = 0; i < typePars.length; i++) {
					newTypePars[i] = constructPatternTypeFor(typePars[i],
							parToTypeVarMap, mediator);
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
	 * Creates a sorted list of the given element type.
	 * 
	 * @param <E>
	 *            the type of the elements
	 * @param collection
	 *            the original collection
	 * @return the sorted list
	 */
	public static <E extends Comparable<E>> List<E> getSortedList(
			Collection<E> collection) {
		List<E> list = new ArrayList<E>();
		for (E item : collection) {
			list.add(item);
		}
		Collections.sort(list);
		return list;
	}

	public static ITypeEnvironment getTypeEnvironmentForFactory(
			ITypeEnvironment typeEnvironment, FormulaFactory factory){
		ITypeEnvironment newTypeEnvironment = factory.makeTypeEnvironment();
		for (String name : typeEnvironment.getNames()){
			newTypeEnvironment.addName(name, typeEnvironment.getType(name));
		}
		return newTypeEnvironment;
	}

	
}
