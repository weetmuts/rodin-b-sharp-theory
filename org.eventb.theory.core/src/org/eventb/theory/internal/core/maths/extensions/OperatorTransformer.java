/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.internal.core.maths.extensions;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.ast.extension.IOperatorProperties.FormulaType;
import org.eventb.core.ast.extension.IOperatorProperties.Notation;
import org.eventb.theory.core.ISCDirectOperatorDefinition;
import org.eventb.theory.core.ISCNewOperatorDefinition;
import org.eventb.theory.core.ISCOperatorArgument;
import org.eventb.theory.core.maths.IOperatorArgument;
import org.eventb.theory.internal.core.maths.OperatorArgument;
import org.eventb.theory.internal.core.util.MathExtensionsUtilities;

/**
 * @author maamria
 * 
 */
public class OperatorTransformer extends
		DefinitionTransformer<ISCNewOperatorDefinition> {

	@Override
	public Set<IFormulaExtension> transform(
			ISCNewOperatorDefinition definition, FormulaFactory factory,
			ITypeEnvironment typeEnvironment) {
		if (definition == null || !definition.exists()) {
			return EMPTY_EXT;
		}
		try {
			if (definition.hasHasErrorAttribute() && definition.hasError()) {
				return EMPTY_EXT;
			}
			String theoryName = definition.getParent().getElementName();
			String operatorID = theoryName + "." + definition.getLabel();
			String syntax = definition.getSyntaxSymbol();
			FormulaType formulaType = definition.getFormulaType();
			Notation notation = definition.getNotationType();
			String groupID = definition.getOperatorGroup();
			boolean isAssociative = definition.isAssociative();
			boolean isCommutative = definition.isCommutative();

			ISCOperatorArgument[] scOperatorArguments = definition
					.getOperatorArguments();
			List<IOperatorArgument> operatorArguments = new ArrayList<IOperatorArgument>();
			List<GivenType> typeParameters = new ArrayList<GivenType>();
			int index = 0;
			for (ISCOperatorArgument arg : scOperatorArguments) {
				OperatorArgument opArg = new OperatorArgument(index++,
						arg.getIdentifierString(), arg.getType(factory));
				operatorArguments.add(opArg);
				for (GivenType t : opArg
						.getGivenTypes(factory, typeEnvironment)) {
					if (!typeParameters.contains(t)) {
						typeParameters.add(t);
					}
				}
			}
			ITypeEnvironment tempTypeEnvironment = MathExtensionsUtilities
					.getTypeEnvironmentForFactory(typeEnvironment, factory);
			for (IOperatorArgument arg : operatorArguments) {
				tempTypeEnvironment.addName(arg.getArgumentName(),
						arg.getArgumentType());
			}
			Predicate wdCondition = definition.getPredicate(factory,
					tempTypeEnvironment);
			ISCDirectOperatorDefinition scDirecDefinition = definition
					.getDirectOperatorDefinitions()[0];
			Formula<?> directDefinition = scDirecDefinition.getSCFormula(
					factory, tempTypeEnvironment);
			IFormulaExtension extension = null;
			if (directDefinition instanceof Expression) {
				extension = extensionsFactory.getFormulaExtension(operatorID,
						syntax, formulaType, notation, groupID, isCommutative,
						isAssociative, (Expression) directDefinition,
						extensionsFactory.getTypingRule(typeParameters,
								operatorArguments,
								(Expression) directDefinition, wdCondition,
								isAssociative), definition);
			}
			else {
				extension = extensionsFactory.getFormulaExtension(operatorID,
						syntax, formulaType, notation, groupID, isCommutative,
						(Predicate) directDefinition,
						extensionsFactory.getTypingRule(typeParameters,
								operatorArguments,
								(Predicate) directDefinition, wdCondition), 
								definition);
			}
			return MathExtensionsUtilities.singletonExtension(extension);
		} catch (CoreException exception) {
			return EMPTY_EXT;
		}
	}

}
