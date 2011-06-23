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
import org.eventb.core.ast.Type;
import org.eventb.core.sc.GraphProblem;
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
import org.eventb.theory.core.sc.states.IDatatypeTable;
import org.eventb.theory.internal.core.util.CoreUtilities;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;

/**
 * 
 * @author maamria
 * 
 */
public class DatatypeDestructorModule extends SCProcessorModule {

	private final IModuleType<DatatypeDestructorModule> MODULE_TYPE = SCCore
			.getModuleType(TheoryPlugin.PLUGIN_ID + ".datatypeDestructorModule");

	@Override
	public void process(IRodinElement element, IInternalElement target,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		IDatatypeConstructor constructor = (IDatatypeConstructor) element;
		ISCDatatypeConstructor scConstructor = (ISCDatatypeConstructor) target;
		IConstructorArgument[] constructorArguments = constructor
				.getConstructorArguments();
		processDestructors(constructorArguments, constructor, scConstructor,
				repository, monitor);

	}

	@Override
	public IModuleType<?> getModuleType() {
		// TODO Auto-generated method stub
		return MODULE_TYPE;
	}

	/**
	 * Processes the supplied destructors and checks against the following potential issues:
	 * <p>
	 * 	<li>Missing destructor name;</li>
	 * 	<li>Name clash;</li>
	 * 	<li>Missing type for destructor;</li>
	 * 	<li>Any identifiers that should not be referenced from the type.</li>
	 * <p>
	 * @param constructorArguments the constructor arguments 
	 * @param constructor the datatype constructor
	 * @param scConstructor the SC constructor
	 * @param repository the state repository
	 * @param monitor the progress monitor
	 * @throws CoreException
	 */
	protected void processDestructors(
			IConstructorArgument[] constructorArguments,
			IDatatypeConstructor constructor,
			ISCDatatypeConstructor scConstructor,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		FormulaFactory factory = repository.getFormulaFactory();
		ITypeEnvironment typeEnvironment = repository.getTypeEnvironment();
		IDatatypeTable datatypeTable = (IDatatypeTable) repository
				.getState(IDatatypeTable.STATE_TYPE);
		for (IConstructorArgument consArg : constructorArguments) {
			if (!consArg.hasIdentifierString()) {
				createProblemMarker(consArg,
						EventBAttributes.IDENTIFIER_ATTRIBUTE,
						TheoryGraphProblem.MissingDestructorNameError);
				datatypeTable.setErrorProne();
				continue;
			}
			String name = consArg.getIdentifierString();
			String error = datatypeTable.checkName(name);
			if (error != null) {
				createProblemMarker(consArg,
						EventBAttributes.IDENTIFIER_ATTRIBUTE,
						ModulesUtils.getAppropriateProblemForCode(error), name);
				datatypeTable.setErrorProne();
				continue;
			}
			FreeIdentifier ident = ModulesUtils.parseIdentifier(name, consArg,
					EventBAttributes.IDENTIFIER_ATTRIBUTE, factory, this);

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
				if (!consArg.hasType() || consArg.getType().equals("")) {
					createProblemMarker(consArg,
							TheoryAttributes.TYPE_ATTRIBUTE,
							TheoryGraphProblem.MissingDestructorTypeError, name);
					datatypeTable.setErrorProne();
					continue;
				}
				Type type = CoreUtilities.parseTypeExpression(consArg, factory,
						this);
				if (type != null && checkTypeParameters(type, consArg, factory, typeEnvironment, datatypeTable)) {
					ISCConstructorArgument scConsArg = scConstructor
							.getConstructorArgument(name);
					scConsArg.create(null, monitor);
					scConsArg.setType(type, monitor);
					datatypeTable.addDestructor(name, type);
				} else {
					datatypeTable.setErrorProne();
				}
			} else {
				datatypeTable.setErrorProne();
			}

		}

	}

	/**
	 * Checks that the type of the given constructor argument refers only to referenced type of the datatype.
	 * @param type the type of the destructor
	 * @param consArg the constructor argument
	 * @param factory the formula factory
	 * @param typeEnvironment the type environment
	 * @param datatypeTable the datatype table
	 * @return whether the type of the destructor is acceptable
	 * @throws CoreException
	 */
	protected boolean checkTypeParameters(Type type, IConstructorArgument consArg,
			FormulaFactory factory, ITypeEnvironment typeEnvironment, IDatatypeTable datatypeTable)
			throws CoreException {
		FreeIdentifier[] idents = type.toExpression(factory)
				.getSyntacticallyFreeIdentifiers();
		boolean result = true;
		for (FreeIdentifier ident : idents) {
			if (!datatypeTable.isAllowedIdentifier(ident.toString())) {
				if (!typeEnvironment.contains(ident.getName())) {
					createProblemMarker(consArg,
							TheoryAttributes.TYPE_ATTRIBUTE,
							GraphProblem.UndeclaredFreeIdentifierError,
							ident.getName());
					result = false;
				} else {
					createProblemMarker(consArg,

					TheoryAttributes.TYPE_ATTRIBUTE,
							TheoryGraphProblem.TypeIsNotRefTypeError,
							ident.getName());
					result = false;
				}
			}
		}
		return result;

	}
}