/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.sc.modules;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.ParametricType;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.SCFilterModule;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.theory.core.INewOperatorDefinition;
import org.eventb.theory.core.IRecursiveDefinitionCase;
import org.eventb.theory.core.IRecursiveOperatorDefinition;
import org.eventb.theory.core.TheoryAttributes;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.sc.TheoryGraphProblem;
import org.eventb.theory.core.sc.states.RecursiveDefinitionInfo;
import org.rodinp.core.IRodinElement;

/**
 * 
 * @author maamria
 * 
 */
public class OperatorRecursiveDefinitionFilterModule extends SCFilterModule {

	private final IModuleType<OperatorRecursiveDefinitionFilterModule> MODULE_TYPE = SCCore
			.getModuleType(TheoryPlugin.PLUGIN_ID
					+ ".operatorRecursiveDefinitionFilterModule");

	private FormulaFactory factory;
	private RecursiveDefinitionInfo recursiveDefinitionInfo;
	private ITypeEnvironment typeEnvironment;

	@Override
	public boolean accept(IRodinElement element, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		IRecursiveOperatorDefinition operatorDefinition = (IRecursiveOperatorDefinition) element;
		INewOperatorDefinition definition = operatorDefinition
				.getAncestor(INewOperatorDefinition.ELEMENT_TYPE);
		if (definition == null) {
			throw new IllegalStateException(
					"Illegal state : operator recursive definition has no parent ("
							+ operatorDefinition.getElementName());
		}
		if (!operatorDefinition.hasInductiveArgument()) {
			createProblemMarker(operatorDefinition,
					TheoryAttributes.INDUCTIVE_ARGUMENT_ATTRIBUTE,
					TheoryGraphProblem.InductiveArgMissing);
			return false;
		} else {
			String inductiveArgument = operatorDefinition
					.getInductiveArgument();
			if (!typeEnvironment.contains(inductiveArgument)
					|| !(typeEnvironment.getType(inductiveArgument) instanceof ParametricType)) {
				createProblemMarker(definition,
						TheoryAttributes.INDUCTIVE_ARGUMENT_ATTRIBUTE,
						TheoryGraphProblem.ArgumentNotExistOrNotParametric,
						inductiveArgument);
				return false;
			}
			FreeIdentifier ident = factory.makeFreeIdentifier(inductiveArgument, null, 
					typeEnvironment.getType(inductiveArgument));
			recursiveDefinitionInfo.setInductiveArgument(ident, factory);
			IRecursiveDefinitionCase[] cases = operatorDefinition
					.getRecursiveDefinitionCases();
			if (cases.length < 1) {
				createProblemMarker(operatorDefinition,
						TheoryGraphProblem.NoRecCasesError);
				return false;
			}

		}
		return true;
	}

	@Override
	public void initModule(ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		super.initModule(repository, monitor);
		typeEnvironment = repository.getTypeEnvironment();
		factory = repository.getFormulaFactory();
		recursiveDefinitionInfo = (RecursiveDefinitionInfo) repository.getState(RecursiveDefinitionInfo.STATE_TYPE);
	}

	@Override
	public void endModule(ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		typeEnvironment = null;
		recursiveDefinitionInfo = null;
		factory = null;
		super.endModule(repository, monitor);
	}

	@Override
	public IModuleType<?> getModuleType() {
		// TODO Auto-generated method stub
		return MODULE_TYPE;
	}
}
