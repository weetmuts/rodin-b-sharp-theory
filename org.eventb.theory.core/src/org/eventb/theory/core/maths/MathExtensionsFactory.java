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
import org.eventb.theory.internal.core.maths.IOperatorArgument;

/**
 * Factory class for the different mathematical extensions provided by the Theory Core plug-in.
 * 
 * @author maamria
 *
 */
public final class MathExtensionsFactory {
	/**
	 * The singleton instance
	 */
	protected static MathExtensionsFactory factory;
	
	private MathExtensionsFactory(){
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
	public IFormulaExtension getFormulaExtension(
			String operatorID, String syntax, FormulaType formulaType,
			Notation notation, boolean isAssociative, boolean isCommutative,
			Formula<?> directDefinition, Predicate wdCondition,
			List<IOperatorArgument> opArguments,
			List<GivenType> typeParameters, Object source) {
		
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
	
	/**
	 * Returns a simple datatype extension with the given details.
	 * @param identifier the name of the datatype
	 * @param typeArguments the type arguments of this datatype
	 * @param factory the formula factory 
	 * @return the set of resulting extensions
	 */
	public Set<IFormulaExtension> getSimpleDatatypeExtensions(String identifier, String[] typeArguments, FormulaFactory factory){
		return factory.makeDatatype(new SimpleDatatypeExtension(identifier, typeArguments)).getExtensions();
	}
	
	/**
	 * Returns a complete datatype extension with the given details.
	 * @param identifier the name of the datatype
	 * @param typeArguments the type arguments of this datatype
	 * @param constructors the constructors of this datatype
	 * @param factory the formula factory 
	 * @return the set of resulting extensions
	 */
	public Set<IFormulaExtension> getCompleteDatatypeExtensions(String identifier, String[] typeArguments, Map<String, Map<String, Type>> constructors, FormulaFactory factory){
		return factory.makeDatatype(new CompleteDatatypeExtension(
				identifier, typeArguments, constructors)).getExtensions();
	}
	
	/**
	 * Returns the singleton instance of this factory.
	 * @return the extensions factory
	 */
	public static MathExtensionsFactory getExtensionsFactory(){
		if(factory == null){
			factory =  new MathExtensionsFactory();
		}
		return factory;
	}

}
