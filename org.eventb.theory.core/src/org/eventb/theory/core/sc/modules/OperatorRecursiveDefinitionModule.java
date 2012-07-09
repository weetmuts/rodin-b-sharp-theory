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
import org.eventb.core.ast.extensions.maths.AstUtilities;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.SCProcessorModule;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.theory.core.INewOperatorDefinition;
import org.eventb.theory.core.IRecursiveOperatorDefinition;
import org.eventb.theory.core.ISCNewOperatorDefinition;
import org.eventb.theory.core.ISCRecursiveOperatorDefinition;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.sc.states.IOperatorInformation;
import org.eventb.theory.core.sc.states.RecursiveDefinitionInfo;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;

/**
 * 
 * @author maamria
 * 
 */
public class OperatorRecursiveDefinitionModule extends SCProcessorModule {

	private static final IModuleType<OperatorRecursiveDefinitionModule> MODULE_TYPE = SCCore
			.getModuleType(TheoryPlugin.PLUGIN_ID
					+ ".operatorRecursiveDefinitionModule");

	private IOperatorInformation operatorInformation;
	
	@Override
	public void process(IRodinElement element, IInternalElement target,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		INewOperatorDefinition newOperatorDefinition = (INewOperatorDefinition) element;
		ISCNewOperatorDefinition scNewOperatorDefinition = (ISCNewOperatorDefinition) target;
		IRecursiveOperatorDefinition[] definitions = newOperatorDefinition
				.getRecursiveOperatorDefinitions();
		if (definitions.length == 1 && !operatorInformation.hasError()) {
			RecursiveDefinitionInfo recursiveDefinitionInfo = new RecursiveDefinitionInfo();
			repository.setState(recursiveDefinitionInfo);
			IRecursiveOperatorDefinition recursiveOperatorDefinition = definitions[0];
			process(recursiveOperatorDefinition, newOperatorDefinition,
					scNewOperatorDefinition, recursiveDefinitionInfo,
					repository, monitor);
			repository.removeState(RecursiveDefinitionInfo.STATE_TYPE);
		}
	}

	protected void process(
			IRecursiveOperatorDefinition recursiveOperatorDefinition,
			INewOperatorDefinition parent, ISCNewOperatorDefinition target,
			RecursiveDefinitionInfo recursiveDefinitionInfo,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		boolean error = false;
		initFilterModules(repository, monitor);
		if (!filterModules(recursiveOperatorDefinition, repository, monitor)) {
			operatorInformation.setHasError();
			error = true;
		}
		endFilterModules(repository, monitor);
		if (!error) {
			ISCRecursiveOperatorDefinition scDefinition = createSCDefinition(
					recursiveOperatorDefinition, target,
					recursiveDefinitionInfo, monitor);
			// processor modules
			{
				initProcessorModules(recursiveOperatorDefinition, repository,
						monitor);
				processModules(recursiveOperatorDefinition, scDefinition,
						repository, monitor);
				endProcessorModules(recursiveOperatorDefinition, repository,
						monitor);
			}
			if (!recursiveDefinitionInfo.isAccurate()) {
				operatorInformation.setHasError();
			} 
			else {
				target.setWDCondition(AstUtilities.BTRUE, monitor);
				operatorInformation.setD_WDCondition(AstUtilities.BTRUE);
			}
		}
	}

	protected ISCRecursiveOperatorDefinition createSCDefinition(
			IRecursiveOperatorDefinition source,
			ISCNewOperatorDefinition target, RecursiveDefinitionInfo info,
			IProgressMonitor monitor) throws CoreException {
		ISCRecursiveOperatorDefinition scDefinition = target
				.getRecursiveOperatorDefinition(source.getElementName());
		scDefinition.create(null, monitor);
		scDefinition.setSource(source, monitor);
		scDefinition.setInductiveArgument(
				info.getInductiveArgument().getName(), monitor);
		return scDefinition;
	}

	@Override
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
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
}
