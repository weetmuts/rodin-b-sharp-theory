/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.sc.modules;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.IOperatorProperties.FormulaType;
import org.eventb.core.ast.extension.IOperatorProperties.Notation;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.SCProcessorModule;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.theory.core.INewOperatorDefinition;
import org.eventb.theory.core.TheoryAttributes;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.sc.TheoryGraphProblem;
import org.eventb.theory.core.sc.states.IOperatorInformation;
import org.eventb.theory.internal.core.util.MathExtensionsUtilities;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * 
 * @author maamria
 * 
 */
public class OperatorPropertiesModule extends SCProcessorModule {

	private final IModuleType<OperatorPropertiesModule> MODULE_TYPE = SCCore
			.getModuleType(TheoryPlugin.PLUGIN_ID + ".operatorPropertiesModule");

	private ITypeEnvironment typeEnvironment;
	private IOperatorInformation operatorInformation;

	@SuppressWarnings("restriction")
	@Override
	public void process(IRodinElement element, IInternalElement target,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		INewOperatorDefinition operatorDefinition = (INewOperatorDefinition) element;

		List<String> args = getOperatorArguments();
		// Check for allowable extension kinds
		boolean isCommutative = operatorDefinition.isCommutative();
		boolean isAssos = operatorDefinition.isAssociative();
		Notation notation = operatorDefinition.getNotationType();
		int arity = args.size();
		FormulaType formType = operatorDefinition.getFormulaType();

		if (!checkOperatorProperties(operatorDefinition, formType, notation,
				arity, isAssos, isCommutative, args)) {
			operatorInformation.setHasError();
		} else {
			operatorInformation.setAssociative(isAssos);
			operatorInformation.setCommutative(isCommutative);
		}
		if (operatorInformation.getWdCondition() == null) {
			operatorInformation.setHasError();
		}
		operatorInformation.makeImmutable();
	}

	@Override
	public void initModule(IRodinElement element,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		super.initModule(element, repository, monitor);
		typeEnvironment = repository.getTypeEnvironment();
		operatorInformation = (IOperatorInformation) repository
				.getState(IOperatorInformation.STATE_TYPE);

	}

	@Override
	public void endModule(IRodinElement element, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		typeEnvironment = null;
		operatorInformation = null;
		super.endModule(element, repository, monitor);
	}

	@Override
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	/**
	 * Checks the given operator properties against the requirements of the AST.
	 * <p>
	 * Returns whether the operator with the given properties can be handled by
	 * the current AST setup.
	 * 
	 * @param operatorDefinition
	 *            the new operator definition
	 * @param formType
	 *            the formula type
	 * @param notation
	 *            the notation
	 * @param arity
	 *            the arity
	 * @param isAssociative
	 *            whether the operator is marked to be associative
	 * @param isCommutative
	 *            whether the operator is marked to be commutative
	 * @param arguments
	 *            the arguments of the operator
	 * @return whether the operator properties are acceptable as per current AST
	 *         requirements
	 * @throws RodinDBException
	 */
	protected boolean checkOperatorProperties(
			INewOperatorDefinition operatorDefinition, FormulaType formType,
			Notation notation, int arity, boolean isAssociative,
			boolean isCommutative, List<String> arguments)
			throws CoreException {
		String opID = operatorDefinition.getLabel();
		// Check notation
		// 1- Postfix not supported
		if (notation.equals(Notation.POSTFIX)) {
			createProblemMarker(operatorDefinition,
					EventBAttributes.LABEL_ATTRIBUTE,
					TheoryGraphProblem.OperatorCannotBePostfix);
			return false;
		}
		// Check formula type
		if (formType.equals(FormulaType.PREDICATE)) {
			// 2- Infix predicates not supported
			if (notation.equals(Notation.INFIX)) {
				createProblemMarker(operatorDefinition,
						EventBAttributes.LABEL_ATTRIBUTE,
						TheoryGraphProblem.OperatorPredOnlyPrefix);
				return false;
			}
			// 3- Predicate operators need at least one argument
			if (arity < 1) {
				createProblemMarker(operatorDefinition,
						EventBAttributes.LABEL_ATTRIBUTE,
						TheoryGraphProblem.OperatorPredNeedOneOrMoreArgs);
				return false;
			}
		}
		// Issues with associativity
		if (isAssociative) {
			// 4- Predicate operators cannot be associative
			if (formType.equals(FormulaType.PREDICATE)) {
				createProblemMarker(operatorDefinition,
						EventBAttributes.LABEL_ATTRIBUTE,
						TheoryGraphProblem.OperatorPredCannotBeAssos);
				return false;
			} else {
				// 5- Associative and prefix not supported
				if (notation.equals(Notation.PREFIX)) {
					createProblemMarker(operatorDefinition,
							TheoryAttributes.ASSOCIATIVE_ATTRIBUTE,
							TheoryGraphProblem.OperatorExpPrefixCannotBeAssos);
					return false;
				} else if (notation.equals(Notation.INFIX)) {
					// 6- Infix needs at least two arguments
					if (arity < 2) {
						createProblemMarker(
								operatorDefinition,
								EventBAttributes.LABEL_ATTRIBUTE,
								TheoryGraphProblem.OperatorExpInfixNeedsAtLeastTwoArgs);
						return false;
					}
					// 7- Check actual associativity
					else if (!checkAssociativity(arguments)) {
						createProblemMarker(
								operatorDefinition,
								TheoryAttributes.ASSOCIATIVE_ATTRIBUTE,
								TheoryGraphProblem.OperatorCannotBeAssosWarning,
								opID);
						return false;
					}
				}
			}
		}
		// Issues with commutativity
		if (isCommutative) {
			// 8- Check actual commutativity
			if (!checkCommutativity(arguments)) {
				createProblemMarker(operatorDefinition,
						TheoryAttributes.COMMUTATIVE_ATTRIBUTE,
						TheoryGraphProblem.OperatorCannotBeCommutError, opID);
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns the argument of an operator. This method assumes that all given
	 * sets are theory type parameters, and all other names must be operator
	 * arguments.
	 * 
	 * @return list of operator arguments
	 */
	protected List<String> getOperatorArguments() {
		Set<String> allNames = typeEnvironment.clone().getNames();
		allNames.removeAll(MathExtensionsUtilities
				.getGivenSetsNames(typeEnvironment));
		return new ArrayList<String>(allNames);
	}

	/**
	 * An operator can be associative if it can have at least two arguments of
	 * the same type, which has to be the same as the resultant type.
	 * 
	 * @param args
	 *            the operator arguments
	 * @return whether this operator can be associative
	 */
	protected boolean checkAssociativity(List<String> args) throws CoreException{
		if (!operatorInformation.isExpressionOperator() || (args.size() != 2)) {
			return false;
		}
		Type type = null;
		for (String arg : args) {
			if (type == null) {
				type = typeEnvironment.getType(arg);
			}
			if (!type.equals(typeEnvironment.getType(arg))) {
				return false;
			}
		}
		if (!type.equals(operatorInformation.getResultantType())) {
			return false;
		}
		return true;
	}

	/**
	 * An operator can be commutative if it can have two arguments of the same
	 * type.
	 * 
	 * @param args
	 *            the operator arguments
	 * @return whether this operator can be commutative
	 */
	protected boolean checkCommutativity(List<String> args) {
		if (args.size() != 2) {
			return false;
		}
		Type type = null;
		for (String arg : args) {
			if (type == null) {
				type = typeEnvironment.getType(arg);
			}
			if (!(type.equals(typeEnvironment.getType(arg))))
				return false;
		}
		return true;
	}
}
