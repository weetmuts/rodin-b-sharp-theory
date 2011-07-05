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
import org.eventb.theory.core.IDatatypeDefinition;
import org.eventb.theory.core.ISCDatatypeDefinition;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.ISCTypeArgument;
import org.eventb.theory.core.ITheoryRoot;
import org.eventb.theory.core.ITypeArgument;
import org.eventb.theory.core.TheoryAttributes;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.sc.Messages;
import org.eventb.theory.core.sc.TheoryGraphProblem;
import org.eventb.theory.core.sc.states.DatatypeTable;
import org.eventb.theory.core.sc.states.IDatatypeTable;
import org.eventb.theory.core.sc.states.TheoryAccuracyInfo;
import org.eventb.theory.internal.core.util.MathExtensionsUtilities;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;

/**
 * 
 * @author maamria
 * 
 */
public class DatatypeModule extends SCProcessorModule {

	private final IModuleType<DatatypeModule> MODULE_TYPE = SCCore
			.getModuleType(TheoryPlugin.PLUGIN_ID + ".datatypeModule");

	private TheoryAccuracyInfo theoryAccuracyInfo;

	@Override
	public void process(IRodinElement element, IInternalElement target,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		IRodinFile file = (IRodinFile) element;
		ITheoryRoot root = (ITheoryRoot) file.getRoot();
		ISCTheoryRoot targetRoot = (ISCTheoryRoot) target;
		IDatatypeDefinition[] dtdef = root.getDatatypeDefinitions();
		// set the datatype table state
		IDatatypeTable datatypeTable = new DatatypeTable(
				repository.getFormulaFactory());
		repository.setState(datatypeTable);
		if (dtdef.length != 0) {
			monitor.subTask(Messages.progress_TheoryDatatypes);
			monitor.worked(1);
			processDatatypes(dtdef, targetRoot, datatypeTable, repository,
					monitor);
			monitor.worked(2);
		}
	}

	@Override
	public void initModule(IRodinElement element,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		super.initModule(element, repository, monitor);
		theoryAccuracyInfo = (TheoryAccuracyInfo) repository
				.getState(TheoryAccuracyInfo.STATE_TYPE);

	}

	@Override
	public void endModule(IRodinElement element, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		theoryAccuracyInfo = null;
		super.endModule(element, repository, monitor);
	}

	@Override
	public IModuleType<?> getModuleType() {
		// TODO Auto-generated method stub
		return MODULE_TYPE;
	}

	/**
	 * Processes the datatype definitions and checks against any static errors that may be encountered.
	 * <p> Potential issues that might arise include:
	 * <li>Datatype name is missing;</li>
	 * <li>Name clash exists.</li>
	 * <p>
	 * @param datatypeDefinitions the datatype definitions
	 * @param targetRoot the target SC root
	 * @param datatypeTable the datatype table
	 * @param repository the state repository
	 * @param monitor the progress monitor
	 * @throws CoreException
	 */
	protected void processDatatypes(IDatatypeDefinition[] datatypeDefinitions,
			ISCTheoryRoot targetRoot, IDatatypeTable datatypeTable,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		boolean theoryAccurate = true;
		FormulaFactory factory = repository.getFormulaFactory();
		ITypeEnvironment typeEnvironment = repository.getTypeEnvironment();
		for (IDatatypeDefinition datatypeDefinition : datatypeDefinitions) {

			if (!datatypeDefinition.hasIdentifierString()) {
				createProblemMarker(datatypeDefinition,
						EventBAttributes.IDENTIFIER_ATTRIBUTE,
						TheoryGraphProblem.MissingDatatypeNameError);
				theoryAccurate = false;
				continue;
			}
			String name = datatypeDefinition.getIdentifierString();
			String error = datatypeTable.checkName(name);
			if (error != null) {
				createProblemMarker(datatypeDefinition,
						EventBAttributes.IDENTIFIER_ATTRIBUTE,
						ModulesUtils.getAppropriateProblemForCode(error),
						name);
				theoryAccurate = false;
				continue;
			}
			FreeIdentifier ident = ModulesUtils.parseIdentifier(name,
					datatypeDefinition, EventBAttributes.IDENTIFIER_ATTRIBUTE,
					factory, this);
			if (ident != null) {
				if (typeEnvironment.contains(ident.getName())) {
					createProblemMarker(
							datatypeDefinition,
							EventBAttributes.IDENTIFIER_ATTRIBUTE,
							TheoryGraphProblem.DatatypeNameAlreadyATypeParError,
							ident.getName());
					theoryAccurate = false;
					continue;
				}
				ISCDatatypeDefinition target = ModulesUtils
						.createSCIdentifierElement(
								ISCDatatypeDefinition.ELEMENT_TYPE,
								datatypeDefinition, targetRoot, monitor);
				target.setSource(datatypeDefinition, monitor);
				// process the type arguments
				List<String> typeArgyments = new ArrayList<String>();
				boolean faithful =processTypeArguments(datatypeDefinition, typeArgyments, target, 
						factory, typeEnvironment, datatypeTable, monitor);
				datatypeTable.addDatatype(name, typeArgyments.toArray(new String[typeArgyments.size()]));
				if(!faithful){
					datatypeTable.setErrorProne();
				}
				// create the decoy factory
				FormulaFactory decoy = datatypeTable.augmentDecoyFormulaFactory();
				// set the new factory and create an associated type environment
				repository.setFormulaFactory(decoy);
				factory = decoy;
				typeEnvironment = MathExtensionsUtilities.getTypeEnvironmentForFactory(typeEnvironment, factory);
				repository.setTypeEnvironment(typeEnvironment);
				
				// Run the child modules
				{
					initProcessorModules(datatypeDefinition, repository, monitor);
					processModules(datatypeDefinition, target, repository, monitor);
					endProcessorModules(datatypeDefinition, repository, monitor);
				}
				// check other properties of datatype definitions
				if(datatypeDefinition.getDatatypeConstructors().length < 1){
					createProblemMarker(datatypeDefinition, EventBAttributes.IDENTIFIER_ATTRIBUTE, 
							TheoryGraphProblem.DatatypeHasNoConsError, datatypeDefinition.getIdentifierString());
					datatypeTable.setErrorProne();
				}
				if(!datatypeTable.datatypeHasBaseConstructor()){
					createProblemMarker(datatypeDefinition, EventBAttributes.IDENTIFIER_ATTRIBUTE, 
							TheoryGraphProblem.DatatypeHasNoBaseConsError, datatypeDefinition.getIdentifierString());
					datatypeTable.setErrorProne();
				}
				if (datatypeTable.isErrorProne()) {
					repository.setFormulaFactory(datatypeTable.reset());
					factory = repository.getFormulaFactory();
					typeEnvironment = MathExtensionsUtilities.getTypeEnvironmentForFactory(typeEnvironment, factory);
					repository.setTypeEnvironment(typeEnvironment);
					target.setHasError(true, monitor);
					theoryAccurate = false;
					continue;
				}
				target.setHasError(false, monitor);
				// update the repository with the latest factory and the corresponding type environment
				repository.setFormulaFactory(datatypeTable.augmentFormulaFactory());
				factory = repository.getFormulaFactory();
				typeEnvironment = MathExtensionsUtilities.getTypeEnvironmentForFactory(typeEnvironment,factory);
				repository.setTypeEnvironment(typeEnvironment);
			}
			else {
				theoryAccurate = false;
			}
		}
		if (!theoryAccurate)
			theoryAccuracyInfo.setNotAccurate();
	}
	
	/**
	 * Processes the type arguments of the given datatype definition.
	 * @param datatypeDefinition the datatype definition
	 * @param toPopulate the list to populate with type arguments
	 * @param target the SC target datatype definition
	 * @param factory the formula factory
	 * @param typeEnvironment the type environment
	 * @param datatypeTable the datatype table
	 * @param monitor the progress monitor
	 * @return whether all type arguments have been processed faithfully
	 * @throws CoreException
	 */
	protected boolean processTypeArguments(IDatatypeDefinition datatypeDefinition, List<String> toPopulate,
			ISCDatatypeDefinition target, FormulaFactory factory, ITypeEnvironment typeEnvironment,
			IDatatypeTable datatypeTable, IProgressMonitor monitor) throws CoreException{
		ITypeArgument typeArgs[] = datatypeDefinition.getTypeArguments();
		// needed to check for redundancies
		boolean faithful = true;
		for (ITypeArgument typeArg : typeArgs) {
			if (!typeArg.hasGivenType()) {
				createProblemMarker(typeArg,
						TheoryAttributes.GIVEN_TYPE_ATTRIBUTE,
						TheoryGraphProblem.TypeArgMissingError,
						target.getElementName());
				faithful = false;
				continue;
			}
			String type = typeArg.getGivenType();
			if (!typeEnvironment.contains(type)) {
				createProblemMarker(typeArg,
						TheoryAttributes.GIVEN_TYPE_ATTRIBUTE,
						TheoryGraphProblem.TypeArgNotDefinedError,
						typeArg.getGivenType());
				faithful = false;
				continue;
			}
			if (toPopulate.contains(type)) {
				createProblemMarker(typeArg,
						TheoryAttributes.GIVEN_TYPE_ATTRIBUTE,
						TheoryGraphProblem.TypeArgRedundWarn, type);
				faithful = false;
				continue;
			}
			ISCTypeArgument scArg = target.getTypeArgument(type);
			scArg.create(null, monitor);
			scArg.setSCGivenType(factory.makeGivenType(type), monitor);
			toPopulate.add(type);
		}
		return faithful;
	}
}
