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

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.ast.extension.IOperatorProperties.FormulaType;
import org.eventb.core.ast.extension.IOperatorProperties.Notation;
import org.eventb.core.ast.extension.IPredicateExtension;
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
 * @since 1.0
 * 
 * @author maamria
 * 
 */
public final class MathExtensionsFactory {

	private static MathExtensionsFactory instance;

	private MathExtensionsFactory() {}

	/**
	 * Creates an operator extension properties with the passed parameters.
	 * @param operatorID the operator ID
	 * @param syntax the operator syntax
	 * @param formulaType the operator formula type
	 * @param notation the operator notation
	 * @param groupID the operator group ID
	 * @return the operator properties 
	 */
	public OperatorExtensionProperties getOperatorExtensionProperties(String operatorID, String syntax, 
			FormulaType formulaType, Notation notation, 
			String groupID){
		return new OperatorExtensionProperties(operatorID, syntax, formulaType, notation, groupID);
	}
	
	/**
	 * Returns the formula extension with the given properties and the operator typing rule.
	 * @param properties the operator properties, must not be <code>null</code>
	 * @param isCommutative whether the operator is commutative
	 * @param isAssociative whether the operator is associative
	 * @param operatorTypingRule the operator typing rule, must not be <code>null</code>
	 * @param source the source of the extension
	 * @return the expression operator extension
	 */
	public  IExpressionExtension getFormulaExtension(OperatorExtensionProperties properties,
			boolean isCommutative, boolean isAssociative,
			ExpressionOperatorTypingRule operatorTypingRule,
			Object source) {
		return new ExpressionOperatorExtension(properties, isCommutative, isAssociative, operatorTypingRule, 
				source);
	}
	
	/**
	 * Returns the formula extension with the given properties and the operator typing rule.
	 * @param properties the operator properties, must not be <code>null</code>
	 * @param isCommutative whether the operator is commutative
	 * @param operatorTypingRule the operator typing rule, must not be <code>null</code>
	 * @param source the source of the extension
	 * @return the predicate operator extension
	 */
	public  IPredicateExtension getFormulaExtension(OperatorExtensionProperties properties,
			boolean isCommutative, 
			PredicateOperatorTypingRule operatorTypingRule,
			Object source) {
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
	public Set<IFormulaExtension> getSimpleDatatypeExtensions(
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
	public Set<IFormulaExtension> getCompleteDatatypeExtensions(
			String identifier, String[] typeArguments,
			Map<String, Map<String, Type>> constructors, FormulaFactory factory) {
		CompleteDatatypeExtension completeDtExt = new CompleteDatatypeExtension(identifier, 
				typeArguments, constructors);
		return factory.makeDatatype(completeDtExt).getExtensions();
	}
	
	/**
	 * Returns a typing rule with the operator that has the specified arguments. The operator
	 * is deemed a predicate operator if <code>resultantType</code> is <code>null</code>.
	 * @param operatorArguments the operator arguments
	 * @param resultantType the resultant type
	 * @param wdPredicate the WD condition
	 * @param dWDPredicate the D WD predicate
	 * @param isAssociative
	 * @return the appropriate typing rule
	 */
	public ExpressionOperatorTypingRule getTypingRule(List<IOperatorArgument> operatorArguments, Type resultantType, 
			Predicate wdPredicate, Predicate dWDPredicate,boolean isAssociative){
		ExpressionOperatorTypingRule typingRule  = 
			new ExpressionOperatorTypingRule(operatorArguments, wdPredicate, dWDPredicate, resultantType, isAssociative);
		
		return typingRule;
	}
	
	/**
	 * Returns a typing rule with the operator that has the specified arguments. The operator
	 * is deemed a predicate operator if <code>resultantType</code> is <code>null</code>.
	 * @param operatorArguments the operator arguments
	 * @param wdPredicate the WD condition
	 * @param dWDPredicate the D WD predicate 
	 * @return the appropriate typing rule
	 */
	public PredicateOperatorTypingRule getTypingRule(List<IOperatorArgument> operatorArguments, Predicate wdPredicate, 
			Predicate dWDPredicate){
		PredicateOperatorTypingRule typingRule  = 
			new PredicateOperatorTypingRule(operatorArguments, wdPredicate, dWDPredicate);
		return typingRule;
	}
	
	public static MathExtensionsFactory getDefault(){
		if (instance == null){
			instance = new MathExtensionsFactory();
		}
		return instance;
	}
}
