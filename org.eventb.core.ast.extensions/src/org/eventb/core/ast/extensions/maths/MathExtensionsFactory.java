/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.ast.extensions.maths;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.ast.extension.IOperatorProperties.FormulaType;
import org.eventb.core.ast.extension.IOperatorProperties.Notation;
import org.eventb.core.ast.extension.IPredicateExtension;
import org.eventb.core.internal.ast.extensions.maths.AxiomaticTypeExtension;
import org.eventb.core.internal.ast.extensions.maths.CompleteDatatypeExtension;
import org.eventb.core.internal.ast.extensions.maths.ExpressionOperatorExtension;
import org.eventb.core.internal.ast.extensions.maths.ExpressionOperatorTypingRule;
import org.eventb.core.internal.ast.extensions.maths.OperatorArgument;
import org.eventb.core.internal.ast.extensions.maths.OperatorTypingRule;
import org.eventb.core.internal.ast.extensions.maths.PredicateOperatorExtension;
import org.eventb.core.internal.ast.extensions.maths.PredicateOperatorTypingRule;
import org.eventb.core.internal.ast.extensions.maths.SimpleDatatypeExtension;

/**
 * Factory class for the different mathematical extensions contributed by the
 * Theory plug-in.
 * 
 * @since 1.0
 * 
 * @author maamria
 * 
 */
public final class MathExtensionsFactory {

	/**
	 * Creates an operator extension properties with the passed parameters.
	 * @param operatorID the operator ID
	 * @param syntax the operator syntax
	 * @param formulaType the operator formula type
	 * @param notation the operator notation
	 * @param groupID the operator group ID
	 * @return the operator properties 
	 */
	public static OperatorExtensionProperties getOperatorExtensionProperties(String operatorID, String syntax, 
			FormulaType formulaType, Notation notation, 
			String groupID){
		return new OperatorExtensionProperties(operatorID, syntax, formulaType, notation, groupID);
	}
	
	/**
	 * Returns the expression extension with the given properties and the operator typing rule.
	 * @param properties the operator properties, must not be <code>null</code>
	 * @param isCommutative whether the operator is commutative
	 * @param isAssociative whether the operator is associative
	 * @param operatorTypingRule the operator typing rule, must not be <code>null</code>
	 * @param source the source of the extension
	 * @return the expression operator extension
	 */
	public static  IExpressionExtension getExpressionExtension(OperatorExtensionProperties properties,
			boolean isCommutative, boolean isAssociative, Map<String, Type> operatorArguments, Type resultantType, 
			Predicate wdPredicate, Predicate dWDPredicate, Object source) {
		List<OperatorArgument> opArgs = new ArrayList<OperatorArgument>();
		int index = 0;
		for (String name : operatorArguments.keySet()){
			opArgs.add(new OperatorArgument(index++, name, operatorArguments.get(name)));
		}
		OperatorTypingRule operatorTypingRule = new ExpressionOperatorTypingRule(opArgs, wdPredicate, 
				dWDPredicate, resultantType, isAssociative);
		return new ExpressionOperatorExtension(properties, isCommutative, isAssociative, operatorTypingRule, 
				source);
	}
	
	/**
	 * Returns the predicate extension with the given properties and the operator typing rule.
	 * @param properties the operator syntactic properties
	 * @param isCommutative whether the operator is commutative
	 * @param operatorArguments the map of arguments, not that order is imported here
	 * @param wdPredicate the WD predicate 
	 * @param dWDPredicate the D-based WD predicate
	 * @param source the source of this extension if any
	 * @return the predicate extension
	 */
	
	public static  IPredicateExtension getPredicateExtension(OperatorExtensionProperties properties,
			boolean isCommutative, Map<String, Type> operatorArguments, Predicate wdPredicate, 
			Predicate dWDPredicate, Object source) {
		List<OperatorArgument> opArgs = new ArrayList<OperatorArgument>();
		int index = 0;
		for (String name : operatorArguments.keySet()){
			opArgs.add(new OperatorArgument(index++, name, operatorArguments.get(name)));
		}
		OperatorTypingRule operatorTypingRule = new PredicateOperatorTypingRule(opArgs,
				wdPredicate, dWDPredicate);
		return new PredicateOperatorExtension(properties, isCommutative, operatorTypingRule, source);
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
	public static Set<IFormulaExtension> getSimpleDatatypeExtensions(
			String identifier, String[] typeArguments, FormulaFactory factory) {
		return factory.makeDatatype(
				new SimpleDatatypeExtension(identifier, typeArguments)).getExtensions();
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
	public static Set<IFormulaExtension> getCompleteDatatypeExtensions(
			String identifier, String[] typeArguments,
			Map<String, Map<String, String>> constructors, FormulaFactory factory) {
		CompleteDatatypeExtension completeDtExt = new CompleteDatatypeExtension(identifier, 
				typeArguments, constructors);
		return factory.makeDatatype(completeDtExt).getExtensions();
	}
	
	/**
	 * Returns the axiomatic type extension with the name <code>typeName</code>.
	 * @param typeName the name of the type, e.g., REAL
	 * @param id the id of the operator
	 * @param origin the origin of the extension
	 * @return the axiomatic type extension
	 */
	public static IExpressionExtension getAxiomaticTypeExtension(String typeName, String id, Object origin){
		return new AxiomaticTypeExtension(typeName, id, origin);
	}
}
