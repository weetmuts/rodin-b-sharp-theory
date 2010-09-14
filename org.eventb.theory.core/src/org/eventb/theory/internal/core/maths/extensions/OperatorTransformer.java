/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.internal.core.maths.extensions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.ast.extension.IOperatorProperties.FormulaType;
import org.eventb.core.ast.extension.IOperatorProperties.Notation;
import org.eventb.theory.core.IElementTransformer;
import org.eventb.theory.core.ISCDirectOperatorDefinition;
import org.eventb.theory.core.ISCNewOperatorDefinition;
import org.eventb.theory.core.ISCOperatorArgument;
import org.eventb.theory.core.maths.MathExtensionsFacilitator;
import org.eventb.theory.internal.core.maths.IOperatorArgument;
import org.eventb.theory.internal.core.maths.OperatorArgument;
import org.rodinp.core.IInternalElementType;

/**
 * @author maamria
 *
 */
public class OperatorTransformer implements IElementTransformer<ISCNewOperatorDefinition, Set<IFormulaExtension>> {
	
	@Override
	public Set<IFormulaExtension> transform(
			ISCNewOperatorDefinition definition, FormulaFactory factory, ITypeEnvironment typeEnvironment)
			throws CoreException {
		if(definition.hasError()){
			return null;
		}
		String operatorID = definition.getLabel();
		String syntax = definition.getSyntaxSymbol();
		FormulaType formulaType = definition.getFormulaType();
		Notation notation = definition.getNotationType();
		boolean isAssociative = definition.isAssociative();
		boolean isCommutative =definition.isCommutative();
		
		ISCOperatorArgument[] scOperatorArguments = definition.getOperatorArguments();
		List<IOperatorArgument> operatorArguments = new ArrayList<IOperatorArgument>();
		List<GivenType> typeParameters = new ArrayList<GivenType>();
		int index = 0;
		for(ISCOperatorArgument arg : scOperatorArguments){
			OperatorArgument opArg = new OperatorArgument(index++, arg.getIdentifierString(), arg.getType(factory));
			operatorArguments.add(opArg);
			for (GivenType t : opArg.getGivenTypes(factory, typeEnvironment)){
				if(!typeParameters.contains(t)){
					typeParameters.add(t);
				}
			}
		}
		Collections.sort(operatorArguments);
		ITypeEnvironment tempTypeEnvironment = MathExtensionsFacilitator.getTypeEnvironmentForFactory(typeEnvironment, factory);
		for(IOperatorArgument arg : operatorArguments){
			tempTypeEnvironment.addName(arg.getArgumentName(), arg.getArgumentType());
		}
		Predicate wdCondition = definition.getPredicate(factory, tempTypeEnvironment);
		ISCDirectOperatorDefinition scDirecDefinition = definition.getDirectOperatorDefinitions()[0];
		Formula<?> directDefinition = scDirecDefinition.getSCFormula(factory, tempTypeEnvironment);
		
		IFormulaExtension extension = MathExtensionsFacilitator.
				getFormulaExtension(operatorID, syntax, 
						formulaType, notation, isAssociative, isCommutative, 
						directDefinition, wdCondition, operatorArguments, typeParameters, definition);
		return MathExtensionsFacilitator.singletonExtension(extension);
	}

	@Override
	public IInternalElementType<ISCNewOperatorDefinition> getElementType()
			throws CoreException {
		// TODO Auto-generated method stub
		return ISCNewOperatorDefinition.ELEMENT_TYPE;
	}

}