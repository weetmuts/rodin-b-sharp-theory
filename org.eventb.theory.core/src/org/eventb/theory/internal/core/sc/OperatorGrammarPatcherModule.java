/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.internal.core.sc;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.SCProcessorModule;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.internal.core.sc.states.IOperatorInformation;
import org.eventb.theory.internal.core.util.CoreUtilities;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;

/**
 * @author maamria
 *
 */
public class OperatorGrammarPatcherModule extends SCProcessorModule{

	IModuleType<OperatorGrammarPatcherModule> MODULE_TYPE = SCCore
			.getModuleType(TheoryPlugin.PLUGIN_ID
					+ ".operatorGrammarPatcherModule");
	
	private FormulaFactory factory;
	private IOperatorInformation operatorInformation;
	
	@Override
	public void process(IRodinElement element, IInternalElement target,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		if(!operatorInformation.hasError()){
			FormulaFactory newFactory = factory.withExtensions(
					CoreUtilities.singletonSet(operatorInformation.getExtension(factory)));
			repository.setFormulaFactory(newFactory);
			factory = repository.getFormulaFactory();
		}
		
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
		factory = repository.getFormulaFactory();
		operatorInformation = (IOperatorInformation) repository
				.getState(IOperatorInformation.STATE_TYPE);
	}

	@Override
	public void endModule(IRodinElement element, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		factory = null;
		operatorInformation = null;
		super.endModule(element, repository, monitor);
	}
}
