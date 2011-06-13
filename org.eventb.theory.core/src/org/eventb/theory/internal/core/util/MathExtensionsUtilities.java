/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.internal.core.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.LanguageVersion;
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
import org.eventb.theory.core.maths.IOperatorArgument;

/**
 * Facilities class for obtaining information related to grammars and operators
 * plus other utilities to do with AST and mathematical extensions.
 * 
 * @since 1.0
 * 
 * @author maamria
 * 
 */
public class MathExtensionsUtilities {

	/**
	 * Literal predicate true.
	 */
	public static final Predicate BTRUE = FormulaFactory.getDefault().makeLiteralPredicate(Formula.BTRUE, null);
	/**
	 * Cond extension
	 */
	public static final IFormulaExtension COND = FormulaFactory.getCond();
	/**
	 * Dummy theory group
	 */
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
	 * Returns whether the formula type is an expression type.
	 * 
	 * @param type
	 *            the formula type
	 * @return whether the type is an expression
	 */
	public static final boolean isExpressionOperator(FormulaType type) {
		return type.equals(FormulaType.EXPRESSION);
	}

	/**
	 * Returns the given types in <code>typeEnvironment</code>.
	 * 
	 * @param typeEnvironment
	 *            the type environment
	 * @return all given types
	 */
	public static List<String> getGivenSetsNames(
			ITypeEnvironment typeEnvironment) {
		List<String> result = new ArrayList<String>();
		for (String name : typeEnvironment.getNames()) {
			if (isGivenSet(typeEnvironment, name)) {
				result.add(name);
			}
		}
		return result;
	}

	/**
	 * Checks whether the name <code>name</code> is a given set in the given
	 * type environment.
	 * 
	 * @param typeEnvironment
	 *            the type environment
	 * @param name
	 *            the name
	 * @return whether <code>name</code> is a given set
	 */
	public static boolean isGivenSet(ITypeEnvironment typeEnvironment,
			String name) {
		Type type = typeEnvironment.getType(name);
		if (type == null) {
			return false;
		}
		final Type baseType = type.getBaseType();
		if (baseType instanceof GivenType) {
			GivenType givenType = (GivenType) baseType;
			return givenType.getName().equals(name);
		}
		return false;
	}

	/**
	 * Returns a predicate resulting from conjuncting the given predicates.
	 * 
	 * @param preds
	 *            the array of predicates
	 * @param ff
	 *            the formula factor
	 * @return the predicate
	 */
	public static Predicate conjunctPredicates(Predicate[] preds,
			FormulaFactory ff) {
		List<Predicate> pList = new ArrayList<Predicate>();
		for (Predicate p : preds) {
			if (!p.equals(BTRUE)) {
				pList.add(p);
			}
		}
		return conjunctPredicates(pList, ff);
	}

	/**
	 * Returns a predicate resulting from conjuncting the given predicates.
	 * 
	 * @param preds
	 *            the list of predicates
	 * @param ff
	 *            the formula factor
	 * @return the predicate
	 */
	public static Predicate conjunctPredicates(List<Predicate> preds,
			FormulaFactory ff) {
		while (preds.contains(BTRUE)) {
			preds.remove(BTRUE);
		}
		if (preds.size() == 0) {
			return BTRUE;
		}
		if (preds.size() == 1) {
			return preds.get(0);
		}
		return ff.makeAssociativePredicate(Formula.LAND, preds, null);
	}

	/**
	 * Returns the string type expression with the given name and type
	 * parameters e.g., List(A), Tree(A).
	 * 
	 * @param identifierString
	 *            the name of the type
	 * @param typeArguments
	 *            the list of type arguments
	 * @param the
	 *            formula factory tha knows about this datatype
	 * @return the type expression
	 */
	public static Type createTypeExpression(String identifierString,
			List<String> typeArguments, FormulaFactory ff) {
		String result = identifierString;
		if (typeArguments.size() != 0) {
			result += "(";
			for (int i = 0; i < typeArguments.size(); i++) {
				result += typeArguments.get(i);
				if (i < typeArguments.size() - 1) {
					result += ",";
				}
			}
			result += ")";
	
		}
		IParseResult parseResult = ff.parseType(result, LanguageVersion.V2);
		if(parseResult.hasProblem())
			return null;
		return parseResult.getParsedType();
	
	}

	/**
	 * Returns a formula factory with only one extension; the COND extension.
	 * @return the formula factory with COND
	 */
	public static FormulaFactory getFactoryWithCond(){
		return FormulaFactory.getInstance(singletonExtension(COND));
	}
	
	/**
	 * Returns an appropriate group for the operator with the supplied properties.
	 * <p>
	 * @param formulaType the formula type of the operator
	 * @param notation the notation of the operator
	 * @param arity the airty of the operator
	 * @return the appropriate group
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
		return GeneralUtilities.singletonSet(element);
	}

	/**
	 * Returns a sorted list of the given operator arguments. Operator arguments are sorted
	 * in ascending order by their index.
	 * @param args the operator arguments
	 * @return the sorted list of operator arguments
	 */
	public static List<IOperatorArgument> sort(Collection<IOperatorArgument> args){
		List<IOperatorArgument> list = new ArrayList<IOperatorArgument>(args);
		Collections.sort(list, new Comparator<IOperatorArgument>() {

			@Override
			public int compare(IOperatorArgument o1, IOperatorArgument o2) {
				return o1.getIndex() - o2.getIndex();
			}
		});
		return list;
	}
	
	/**
	 * Returns the given types occurring in <code>type</code>.
	 * @param type the type
	 * @return all given types
	 */
	public static List<GivenType> getGivenTypes(Type type){
		List<GivenType> list = new ArrayList<GivenType>();
		if (type instanceof GivenType){
			list.add((GivenType) type);
		}
		if (type instanceof ParametricType){
			ParametricType parametricType = (ParametricType) type;
			for (Type t : parametricType.getTypeParameters()){
				list.addAll(getGivenTypes(t));
			}
		}
		if (type instanceof PowerSetType){
			PowerSetType powerSetType = (PowerSetType) type;
			list.addAll(getGivenTypes(powerSetType.getBaseType()));
		}
		if (type instanceof ProductType){
			ProductType productType = (ProductType) type;
			list.addAll(getGivenTypes(productType.getLeft()));
			list.addAll(getGivenTypes(productType.getRight()));
		}
		return list;
	}
}
