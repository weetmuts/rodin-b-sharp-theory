/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.maths;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.ast.extension.IOperatorProperties.FormulaType;
import org.eventb.core.ast.extension.IOperatorProperties.Notation;
import org.eventb.theory.internal.core.maths.CompleteDatatypeExtension;
import org.eventb.theory.internal.core.maths.ExpressionOperatorExtension;
import org.eventb.theory.internal.core.maths.ExpressionOperatorTypingRule;
import org.eventb.theory.internal.core.maths.PredicateOperatorExtension;
import org.eventb.theory.internal.core.maths.PredicateOperatorTypingRule;
import org.eventb.theory.internal.core.maths.SimpleDatatypeExtension;

/**
 * Factory class for the different mathematical extensions provided by the
 * Theory Core plug-in.
 * 
 * @author maamria
 * 
 */
public final class MathExtensionsFactory {
	/**
	 * The singleton instance
	 */
	protected static MathExtensionsFactory factory;

	private MathExtensionsFactory() {
		// do nothing
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
	 * @param isCommutative
	 *            whether the operator is commutative
	 * @param isAssociative
	 *            whether the operator is associative
	 * @param operatorTypingRule
	 *            the typing rule
	 * @param source
	 * 			  the source
	 * @return the new formula extension
	 */
	public IFormulaExtension getFormulaExtension(String operatorID,
			String syntax, FormulaType formulaType, Notation notation,String groupID,
			boolean isCommutative, boolean isAssociative,
			Formula<?> directDefinition,  IOperatorTypingRule operatorTypingRule,
			Object source) {

		if(formulaType.equals(FormulaType.EXPRESSION)){
			return new ExpressionOperatorExtension(operatorID, syntax,
					formulaType, notation, groupID, isCommutative, isAssociative, 
					operatorTypingRule, (Expression)directDefinition, source);
		}
		else {
			return new PredicateOperatorExtension(operatorID, syntax, formulaType,
					notation,groupID, isCommutative, operatorTypingRule, 
					(Predicate)directDefinition, source);
		}
	}
	
	/**
	 * Returns a simple datatype extension with the given details.
	 * 
	 * @param identifier
	 *            the name of the datatype
	 * @param typeArguments
	 *            the type arguments of this datatype
	 * @param factory
	 *            the formula factory
	 * @return the set of resulting extensions
	 */
	public Set<IFormulaExtension> getSimpleDatatypeExtensions(
			String identifier, String[] typeArguments, FormulaFactory factory) {
		return factory.makeDatatype(
				new SimpleDatatypeExtension(identifier, typeArguments))
				.getExtensions();
	}

	/**
	 * Returns a complete datatype extension with the given details.
	 * 
	 * @param identifier
	 *            the name of the datatype
	 * @param typeArguments
	 *            the type arguments of this datatype
	 * @param constructors
	 *            the constructors of this datatype
	 * @param factory
	 *            the formula factory
	 * @return the set of resulting extensions
	 */
	public Set<IFormulaExtension> getCompleteDatatypeExtensions(
			String identifier, String[] typeArguments,
			Map<String, Map<String, Type>> constructors, FormulaFactory factory) {
		return factory.makeDatatype(
				new CompleteDatatypeExtension(identifier, typeArguments,
						constructors)).getExtensions();
	}
	
	/**
	 * Returns a typing rule with the operator that has the specified arguments. The operator
	 * is deemed a predicate operator if <code>resultantType</code> is <code>null</code>.
	 * @param typeParameters the type parameters
	 * @param operatorArguments the operator arguments
	 * @param resultantType the type of the operator if it is an expression, or <code>null</code>
	 * @param wdPredicate the WD condition
	 * @return the appopriate typing rule
	 */
	public IOperatorTypingRule getTypingRule(List<GivenType> typeParameters,
			List<IOperatorArgument> operatorArguments, Type resultantType, Predicate wdPredicate){
		IOperatorTypingRule typingRule = null;
		if(resultantType == null){
			typingRule = new PredicateOperatorTypingRule(wdPredicate);
		}
		else {
			typingRule = new ExpressionOperatorTypingRule(resultantType, wdPredicate);
		}
		typingRule.addTypeParameters(typeParameters);
		for (IOperatorArgument arg : operatorArguments) {
			typingRule.addOperatorArgument(arg);
		}
		return typingRule;
	}

	/**
	 * Returns the singleton instance of this factory.
	 * 
	 * @return the extensions factory
	 */
	public static MathExtensionsFactory getExtensionsFactory() {
		if (factory == null) {
			factory = new MathExtensionsFactory();
		}
		return factory;
	}

}
