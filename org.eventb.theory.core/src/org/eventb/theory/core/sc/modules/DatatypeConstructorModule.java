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
import org.eventb.core.EventBAttributes;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.SCProcessorModule;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.theory.core.IDatatypeConstructor;
import org.eventb.theory.core.IDatatypeDefinition;
import org.eventb.theory.core.ISCDatatypeConstructor;
import org.eventb.theory.core.ISCDatatypeDefinition;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.sc.TheoryGraphProblem;
import org.eventb.theory.core.sc.states.DatatypeTable;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;

/**
 * 
 * @author maamria
 * 
 */
public class DatatypeConstructorModule extends SCProcessorModule {

	private final IModuleType<DatatypeConstructorModule> MODULE_TYPE = SCCore.getModuleType(TheoryPlugin.PLUGIN_ID
			+ ".datatypeConstructorModule");

	private DatatypeTable datatypeTable;

	@Override
	public void initModule(IRodinElement element, ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		super.initModule(element, repository, monitor);
		datatypeTable = (DatatypeTable) repository.getState(DatatypeTable.STATE_TYPE);
	}

	@Override
	public void process(IRodinElement element, IInternalElement target, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		IDatatypeDefinition datatypeDefinition = (IDatatypeDefinition) element;
		ISCDatatypeDefinition scDefinition = (ISCDatatypeDefinition) target;
		IDatatypeConstructor[] constructors = datatypeDefinition.getDatatypeConstructors();
		if (constructors.length < 1) {
			createProblemMarker(datatypeDefinition, EventBAttributes.IDENTIFIER_ATTRIBUTE,
					TheoryGraphProblem.DatatypeHasNoConsError, datatypeDefinition.getIdentifierString());
			datatypeTable.setErrorProne();
			return;
		}
		processConstructors(constructors, datatypeDefinition, scDefinition, repository, monitor);
		monitor.worked(1);

	}

	@Override
	public void endModule(IRodinElement element, ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		datatypeTable = null;
		super.endModule(element, repository, monitor);
	}

	@Override
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	/**
	 * Processes the supplied constructors and creates their statically checked
	 * counterparts where appropriate.
	 * <p>
	 * Potential issues that might arise include:
	 * <li>Constructor name is missing;</li>
	 * <li>Name clash exists.</li>
	 * <p>
	 * 
	 * @param constructors
	 *            the array of datatype constructors
	 * @param datatypeDefinition
	 *            the datatype definition
	 * @param scDefinition
	 *            the target SC datatype definition
	 * @param repository
	 *            the state repository
	 * @param monitor
	 *            the progress monitor
	 * @throws CoreException
	 */
	protected void processConstructors(IDatatypeConstructor[] constructors, IDatatypeDefinition datatypeDefinition,
			ISCDatatypeDefinition scDefinition, ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		FormulaFactory factory = repository.getFormulaFactory();
		ITypeEnvironment typeEnvironment = repository.getTypeEnvironment();
		for (IDatatypeConstructor cons : constructors) {
			if (!checkConstructorName(cons, factory, typeEnvironment, datatypeTable)) {
				datatypeTable.setErrorProne();
				continue;
			}
			ISCDatatypeConstructor scCons = ModulesUtils.createSCIdentifierElement(ISCDatatypeConstructor.ELEMENT_TYPE,
					cons, scDefinition, monitor);
			scCons.setSource(cons, monitor);
			datatypeTable.addConstructor(cons.getIdentifierString());
			// Run child modules
			{
				initProcessorModules(cons, repository, monitor);
				processModules(cons, scCons, repository, monitor);
				endProcessorModules(cons, repository, monitor);
			}
		}

	}
	// check constructor name/identifier
	private boolean checkConstructorName(IDatatypeConstructor constructor, FormulaFactory factory,
			ITypeEnvironment typeEnvironment, DatatypeTable datatypeTable) throws CoreException {
		if (!constructor.hasIdentifierString() || constructor.getIdentifierString().equals("")) {
			createProblemMarker(constructor, EventBAttributes.IDENTIFIER_ATTRIBUTE,
					TheoryGraphProblem.MissingDatatypeNameError);
			return false;
		}
		String name = constructor.getIdentifierString();
		String error = datatypeTable.checkName(name);
		if (error != null) {
			createProblemMarker(constructor, EventBAttributes.IDENTIFIER_ATTRIBUTE,
					ModulesUtils.getAppropriateProblemForCode(error), name);
			return false;
		}
		FreeIdentifier ident = ModulesUtils.parseIdentifier(name, constructor, EventBAttributes.IDENTIFIER_ATTRIBUTE,
				factory, this);
		if (ident != null && typeEnvironment.contains(ident.getName())) {
			createProblemMarker(constructor, EventBAttributes.IDENTIFIER_ATTRIBUTE,
					TheoryGraphProblem.ConstructorNameAlreadyATypeParError, ident.getName());
			return false;
		}
		else if (ident == null){
			return false;
		}
		return true;
	}
}
