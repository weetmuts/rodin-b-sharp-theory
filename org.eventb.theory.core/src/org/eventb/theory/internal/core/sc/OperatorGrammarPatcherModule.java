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
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.SCProcessorModule;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.theory.core.INewOperatorDefinition;
import org.eventb.theory.core.ISCNewOperatorDefinition;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.TheoryAttributes;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.sc.TheoryGraphProblem;
import org.eventb.theory.internal.core.sc.states.IOperatorInformation;
import org.eventb.theory.internal.core.util.GeneralUtilities;
import org.eventb.theory.internal.core.util.MathExtensionsUtilities;
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
		ISCNewOperatorDefinition scNewOperatorDefinition = (ISCNewOperatorDefinition) target;
		boolean opHasError = false;
		if(!operatorInformation.hasError()){
			String syntax = operatorInformation.getSyntax();
			if(MathExtensionsUtilities.checkOperatorSyntaxSymbol(syntax, factory)){
				IFormulaExtension formulaExtension = operatorInformation.getExtension(scNewOperatorDefinition, factory);
				FormulaFactory newFactory = factory.withExtensions(
						GeneralUtilities.singletonSet(formulaExtension));
				repository.setFormulaFactory(newFactory);
				factory = repository.getFormulaFactory();
				scNewOperatorDefinition.setHasError(false, monitor);
				scNewOperatorDefinition.setOperatorGroup(formulaExtension.getGroupId(), monitor);
				operatorInformation.generateDefinitionalRule(newFactory, target.getAncestor(ISCTheoryRoot.ELEMENT_TYPE));
			}
			else {
				createProblemMarker((INewOperatorDefinition) element,TheoryAttributes.SYNTAX_SYMBOL_ATTRIBUTE, 
						TheoryGraphProblem.OperatorWithSameSynJustBeenAdded, syntax);
				operatorInformation.setHasError();
				opHasError = true;
				
			}
		}
		else {
			opHasError = true;
		}
		scNewOperatorDefinition.setHasError(opHasError, monitor);
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
