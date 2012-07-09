/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.sc.modules;

import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.IOperatorProperties.FormulaType;
import org.eventb.core.ast.extension.IOperatorProperties.Notation;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.SCProcessorModule;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.theory.core.INewOperatorDefinition;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.sc.TheoryGraphProblem;
import org.eventb.theory.core.sc.states.IOperatorInformation;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;

/**
 * 
 * @author maamria
 *
 */
public class OperatorSyntacticPropertiesModule extends SCProcessorModule{

	private final IModuleType<OperatorSyntacticPropertiesModule> MODULE_TYPE = SCCore
		.getModuleType(TheoryPlugin.PLUGIN_ID + ".operatorSyntacticPropertiesModule");
	
	private IOperatorInformation operatorInformation;
	
	@Override
	public void process(IRodinElement element, IInternalElement target,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		INewOperatorDefinition operatorDefinition = (INewOperatorDefinition) element;
		Map<String, Type> operatorArguments = operatorInformation.getOperatorArguments();
		Notation notation = operatorDefinition.getNotationType();
		FormulaType formType = operatorDefinition.getFormulaType();
		if (!checkSyntacticProperties(operatorDefinition, formType, notation,
				operatorArguments)){
			operatorInformation.setHasError();
		}
	}
	
	protected boolean checkSyntacticProperties(
			INewOperatorDefinition operatorDefinition, FormulaType formType,
			Notation notation, Map<String, Type> operatorArguments) throws CoreException{
		int arity = operatorArguments.size();
		// Check notation
		// 1- Postfix not supported
		if (notation.equals(Notation.POSTFIX)) {
			createProblemMarker(operatorDefinition,
					EventBAttributes.LABEL_ATTRIBUTE,
					TheoryGraphProblem.OperatorCannotBePostfix);
			return false;
		}
		if (notation.equals(Notation.INFIX)) {
			// 2- Infix needs at least two arguments
			if (arity < 2) {
				createProblemMarker(
						operatorDefinition,
						EventBAttributes.LABEL_ATTRIBUTE,
						TheoryGraphProblem.OperatorExpInfixNeedsAtLeastTwoArgs);
				return false;
			}
		}
		// Check formula type
		if (formType.equals(FormulaType.PREDICATE)) {
			// 3- Infix predicates not supported
			if (notation.equals(Notation.INFIX)) {
				createProblemMarker(operatorDefinition,
						EventBAttributes.LABEL_ATTRIBUTE,
						TheoryGraphProblem.OperatorPredOnlyPrefix);
				return false;
			}
			// 4- Predicate operators need at least one argument
			if (arity < 1) {
				createProblemMarker(operatorDefinition,
						EventBAttributes.LABEL_ATTRIBUTE,
						TheoryGraphProblem.OperatorPredNeedOneOrMoreArgs);
				return false;
			}
		}
		return true;
	}

	@Override
	public void initModule(IRodinElement element,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		super.initModule(element, repository, monitor);
		operatorInformation = (IOperatorInformation) repository
				.getState(IOperatorInformation.STATE_TYPE);

	}

	@Override
	public void endModule(IRodinElement element, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		operatorInformation = null;
		super.endModule(element, repository, monitor);
	}

	@Override
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

}
