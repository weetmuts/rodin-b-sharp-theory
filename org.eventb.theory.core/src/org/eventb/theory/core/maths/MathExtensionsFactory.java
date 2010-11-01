/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.maths;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.GivenType;
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
	/**
	 * The singleton instance
	 */
	protected static MathExtensionsFactory factory;
	
	protected Map<CompleteDatatypeExtension, Set<IFormulaExtension>> processedDatatypes;

	private MathExtensionsFactory() {
		processedDatatypes = new LinkedHashMap<CompleteDatatypeExtension, Set<IFormulaExtension>>();
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
	public  IExpressionExtension getFormulaExtension(String operatorID,
			String syntax, FormulaType formulaType, Notation notation,String groupID,
			boolean isCommutative, boolean isAssociative,
			Expression directDefinition,  ExpressionOperatorTypingRule operatorTypingRule,
			Object source) {
		return new ExpressionOperatorExtension(operatorID, syntax, formulaType, 
				notation, groupID, isCommutative, isAssociative, operatorTypingRule, 
				directDefinition, source);
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
	 * @param operatorTypingRule
	 *            the typing rule
	 * @param source
	 * 			  the source
	 * @return the new formula extension
	 */
	public  IPredicateExtension getFormulaExtension(String operatorID,
			String syntax, FormulaType formulaType, Notation notation,String groupID,
			boolean isCommutative, Predicate directDefinition,  
			PredicateOperatorTypingRule operatorTypingRule,
			Object source) {
		return new PredicateOperatorExtension(operatorID, syntax, formulaType, notation, 
				groupID, isCommutative, operatorTypingRule, directDefinition, source);
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
		CompleteDatatypeExtension completeDtExt = new CompleteDatatypeExtension(identifier, 
				typeArguments, constructors);
		if(!processedDatatypes.containsKey(completeDtExt)){
			processedDatatypes.put(completeDtExt, factory.makeDatatype(completeDtExt).getExtensions());
		}
		return processedDatatypes.get(completeDtExt);
	}
	
	/**
	 * Returns a typing rule with the operator that has the specified arguments. The operator
	 * is deemed a predicate operator if <code>resultantType</code> is <code>null</code>.
	 * @param typeParameters the type parameters
	 * @param operatorArguments the operator arguments
	 * @param directDefinition the direct definition
	 * @param wdPredicate the WD condition
	 * @param isAssociative
	 * @return the appopriate typing rule
	 */
	public ExpressionOperatorTypingRule getTypingRule(List<GivenType> typeParameters,
			Collection<IOperatorArgument> operatorArguments, Expression directDefinition, 
			Predicate wdPredicate, boolean isAssociative){
		ExpressionOperatorTypingRule typingRule  = 
			new ExpressionOperatorTypingRule((Expression) directDefinition, wdPredicate, isAssociative);
		typingRule.addTypeParameters(typeParameters);
		for (IOperatorArgument arg : operatorArguments) {
			typingRule.addOperatorArgument(arg);
		}
		return typingRule;
	}
	
	/**
	 * Returns a typing rule with the operator that has the specified arguments. The operator
	 * is deemed a predicate operator if <code>resultantType</code> is <code>null</code>.
	 * @param typeParameters the type parameters
	 * @param operatorArguments the operator arguments
	 * @param directDefinition the direct definition
	 * @param wdPredicate the WD condition
	 * @return the appopriate typing rule
	 */
	public PredicateOperatorTypingRule getTypingRule(List<GivenType> typeParameters,
			Collection<IOperatorArgument> operatorArguments, Predicate directDefinition, 
			Predicate wdPredicate){
		PredicateOperatorTypingRule typingRule  = 
			new PredicateOperatorTypingRule((Predicate) directDefinition, wdPredicate);
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
