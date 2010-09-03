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
import org.eventb.core.EventBAttributes;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Type;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.SCProcessorModule;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.theory.core.IConstructorArgument;
import org.eventb.theory.core.IDatatypeConstructor;
import org.eventb.theory.core.ISCConstructorArgument;
import org.eventb.theory.core.ISCDatatypeConstructor;
import org.eventb.theory.core.TheoryAttributes;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.sc.TheoryGraphProblem;
import org.eventb.theory.internal.core.sc.states.DatatypeTable;
import org.eventb.theory.internal.core.sc.states.DatatypeTable.ERROR_CODE;
import org.eventb.theory.internal.core.util.CoreUtilities;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;

/**
 * @author maamria
 * 
 */
public class DatatypeDestructorModule extends SCProcessorModule {

	IModuleType<DatatypeDestructorModule> MODULE_TYPE = SCCore
			.getModuleType(TheoryPlugin.PLUGIN_ID + ".datatypeDestructorModule");

	private FormulaFactory factory;
	private ITypeEnvironment typeEnvironment;
	private DatatypeTable datatypeTable;

	@Override
	public void process(IRodinElement element, IInternalElement target,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		IDatatypeConstructor cons = (IDatatypeConstructor) element;
		ISCDatatypeConstructor scCons = (ISCDatatypeConstructor) target;
		IConstructorArgument[] consArgs = cons.getConstructorArguments();
		processDestructors(consArgs, cons, scCons, repository, monitor);
	}

	/**
	 * @param consArgs
	 * @param cons
	 * @param scCons
	 * @param repository
	 * @param monitor
	 */
	private void processDestructors(IConstructorArgument[] consArgs,
			IDatatypeConstructor cons, ISCDatatypeConstructor scCons,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		if (consArgs != null && consArgs.length != 0) {
			for (IConstructorArgument consArg : consArgs) {
				if (!consArg.hasIdentifierString()) {
					createProblemMarker(consArg,
							EventBAttributes.IDENTIFIER_ATTRIBUTE,
							TheoryGraphProblem.MissingDestructorNameError);
					datatypeTable.setErrorProne();
					continue;
				}
				String name = consArg.getIdentifierString();
				ERROR_CODE error = datatypeTable.isNameOk(name);
				if (error != null) {
					createProblemMarker(consArg,
							EventBAttributes.IDENTIFIER_ATTRIBUTE,
							CoreUtilities.getAppropriateProblemForCode(error),
							name);
					datatypeTable.setErrorProne();
					continue;
				}
				FreeIdentifier ident = CoreUtilities.parseIdentifier(name,
						consArg, EventBAttributes.IDENTIFIER_ATTRIBUTE,
						factory, this);

				if (ident != null) {
					if (typeEnvironment.contains(name)) {
						createProblemMarker(
								consArg,
								EventBAttributes.IDENTIFIER_ATTRIBUTE,
								TheoryGraphProblem.DestructorNameAlreadyATypeParError,
								name);
						datatypeTable.setErrorProne();
						continue;
					}
					if (!consArg.hasType()) {
						createProblemMarker(consArg,
								TheoryAttributes.TYPE_ATTRIBUTE,
								TheoryGraphProblem.MissingDestructorTypeError, name);
						datatypeTable.setErrorProne();
						continue;
					}
					Type type = CoreUtilities.parseTypeExpression(consArg, factory,
							this);
					initFilterModules(repository, monitor);
					if (type != null && filterModules(consArg, repository, monitor)) {
						ISCConstructorArgument scConsArg = scCons
								.getConstructorArgument(name);
						scConsArg.create(null, monitor);
						scConsArg.setType(type, monitor);
						datatypeTable.addDestructor(name, type);
					} else {
						datatypeTable.setErrorProne();
					}
					endFilterModules(repository, monitor);
				}
				else{
					datatypeTable.setErrorProne();
				}
				
			}
			
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
		typeEnvironment = repository.getTypeEnvironment();
		datatypeTable = (DatatypeTable) repository
				.getState(DatatypeTable.STATE_TYPE);

	}

	@Override
	public void endModule(IRodinElement element, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		factory = null;
		typeEnvironment = null;
		datatypeTable = null;
		super.endModule(element, repository, monitor);
	}

}
