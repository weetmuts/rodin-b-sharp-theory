/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.maths.extensions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.ast.extension.IOperator;
import org.eventb.core.ast.extension.IOperatorGroup;
import org.eventb.core.ast.extension.IOperatorProperties.FormulaType;
import org.eventb.core.ast.extension.IOperatorProperties.Notation;

/**
 * Utilities class for obtaining information related to grammars.
 * 
 * @author maamria
 * 
 */
public class MathExtensionsUtilities {

	public static boolean checkOperatorID(String id, FormulaFactory ff) {
		return !populateOpIDs(ff).contains(id);
	}

	public static boolean checkOperatorSyntaxSymbol(String symbol,
			FormulaFactory ff) {
		return !populateOpSyntaxSymbols(ff).contains(symbol);
	}

	public static boolean checkGroupID(String id, FormulaFactory ff) {
		return !populateOperatorGroupIDs(ff).contains(id);
	}

	public static List<String> populateOpSyntaxSymbols(FormulaFactory ff) {
		List<String> result = new ArrayList<String>();
		Set<IOperatorGroup> groups = ff.getGrammarView().getGroups();
		for (IOperatorGroup g : groups) {
			for (IOperator op : g.getOperators()) {
				result.add(op.getSyntaxSymbol());
			}
		}
		return result;
	}

	public static List<String> populateOpIDs(FormulaFactory ff) {
		List<String> result = new ArrayList<String>();
		Set<IOperatorGroup> groups = ff.getGrammarView().getGroups();
		for (IOperatorGroup g : groups) {
			for (IOperator op : g.getOperators()) {
				result.add(op.getId());
			}
		}
		return result;
	}

	public static List<String> populateOperatorGroupIDs(FormulaFactory ff) {
		List<String> result = new ArrayList<String>();
		Set<IOperatorGroup> groups = ff.getGrammarView().getGroups();
		for (IOperatorGroup g : groups) {
			result.add(g.getId());
		}
		return result;
	}

	public static IFormulaExtension getFormulaExtension(FormulaFactory factory,
			boolean isExpression, String operatorID, String syntax, FormulaType formulaType,
			Notation notation, boolean isAssociative, boolean isCommutative,
			Formula<?> directDefinition, Predicate wdCondition, 
			HashMap<String, Type> opArguments){
		if(isExpression){
			return new ExpressionOperatorExtension(factory, operatorID, syntax, 
					formulaType, notation, isAssociative, isCommutative, 
					directDefinition, wdCondition, opArguments);
		}
		else {
			return new PredicateOperatorExtension(factory, operatorID, syntax, 
					formulaType, notation, isCommutative, directDefinition, 
					wdCondition, opArguments);
		}
	}
	

}
