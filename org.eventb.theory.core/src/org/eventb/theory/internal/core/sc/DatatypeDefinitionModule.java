/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.internal.core.sc;

import java.util.ArrayList;

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
import org.eventb.theory.internal.core.sc.states.AddedTypeExpression;
import org.eventb.theory.internal.core.sc.states.DatatypeTable;
import org.eventb.theory.internal.core.sc.states.TheoryAccuracyInfo;
import org.eventb.theory.internal.core.sc.states.DatatypeTable.ERROR_CODE;
import org.eventb.theory.internal.core.sc.states.ReferencedTypes;
import org.eventb.theory.internal.core.util.CoreUtilities;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;

/**
 * @author maamria
 *
 */
public class DatatypeDefinitionModule extends SCProcessorModule{

	IModuleType<DatatypeDefinitionModule> MODULE_TYPE = 
		SCCore.getModuleType(TheoryPlugin.PLUGIN_ID + ".datatypeDefinitionModule");
	
	private DatatypeTable datatypeTable;
	
	private ITypeEnvironment typeEnvironment;
	private FormulaFactory factory;
	private Type typeExpression;
	private TheoryAccuracyInfo theoryAccuracyInfo;

	@Override
	public void process(IRodinElement element, IInternalElement target,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		
		IRodinFile file = (IRodinFile) element;
		ITheoryRoot root = (ITheoryRoot) file.getRoot();
		ISCTheoryRoot targetRoot = (ISCTheoryRoot) target;
		IDatatypeDefinition[] dtdef = root.getDatatypeDefinitions();
		monitor.subTask(Messages.progress_TheoryDatatypes);
		monitor.worked(1);
		processDatatypes(dtdef, targetRoot, repository, monitor);
		monitor.worked(2);
	}

	/**
	 * Checks that:<br>
	 * -ident is not missing <br>
	 * -ident is not already used as a name for datatype related constructs<br>
	 * -ident is a valid identifier
	 * -ident is not a name of a declared type parameter.
	 * @param dtdef datatype definitions
	 * @param targetRoot
	 * @param repository 
	 * @param monitor
	 */
	private void processDatatypes(IDatatypeDefinition[] dtdef,
			ISCTheoryRoot targetRoot,
			ISCStateRepository repository, IProgressMonitor monitor) throws CoreException{
		boolean hasError = false;
		if(dtdef != null && dtdef.length > 0){
			for (IDatatypeDefinition dtd : dtdef){
				if(!dtd.hasIdentifierString()){
					createProblemMarker(dtd, EventBAttributes.IDENTIFIER_ATTRIBUTE, 
							TheoryGraphProblem.MissingDatatypeNameError);
					continue;
				}
				String name = dtd.getIdentifierString();
				ERROR_CODE error = datatypeTable.isNameOk(name);
				if(error != null){
					createProblemMarker(dtd, EventBAttributes.IDENTIFIER_ATTRIBUTE, 
							CoreUtilities.getAppropriateProblemForCode(error), name);
					continue;
				}
				FreeIdentifier ident = CoreUtilities.parseIdentifier(name, 
						dtd, EventBAttributes.IDENTIFIER_ATTRIBUTE, 
						factory, this);
				if(ident != null){
					if(typeEnvironment.contains(ident.getName())){
						createProblemMarker(dtd, EventBAttributes.IDENTIFIER_ATTRIBUTE, 
								TheoryGraphProblem.DatatypeNameAlreadyATypeParError, 
								ident.getName());
						continue;
					}
					ISCDatatypeDefinition scDtd = CoreUtilities.createSCIdentifierElement(ISCDatatypeDefinition.ELEMENT_TYPE, dtd, targetRoot, monitor);
					scDtd.setSource(dtd, monitor);
					ITypeArgument typeArgs[] = dtd.getTypeArguments();
					
					ReferencedTypes referencedTypes = new ReferencedTypes();
					FormulaFactory decoy = processTypeArguments(typeArgs, scDtd, referencedTypes, monitor);
					
					repository.setFormulaFactory(decoy);
					factory = decoy;
					
					repository.setState(new AddedTypeExpression(typeExpression));
					repository.setState(referencedTypes);
					
					initProcessorModules(dtd, repository, monitor);
					processModules(dtd, scDtd, repository, monitor);
					endProcessorModules(dtd, repository, monitor);
					
					initFilterModules(repository, monitor);
					if(!filterModules(dtd, repository, monitor)){
						repository.setFormulaFactory(datatypeTable.reset());
						factory = repository.getFormulaFactory();
						theoryAccuracyInfo.setNotAccurate();
						scDtd.setHasError(true, monitor);
						continue;
					}
					endFilterModules(repository, monitor);
					hasError = datatypeTable.isErrorProne();
					
					scDtd.setHasError(hasError, monitor);
					
					repository.setFormulaFactory(datatypeTable.augmentFormulaFactory());
					factory = repository.getFormulaFactory();
					
					repository.removeState(AddedTypeExpression.STATE_TYPE);
					repository.removeState(ReferencedTypes.STATE_TYPE);
				}
				else {
					theoryAccuracyInfo.setNotAccurate();
				}
			}
			
		}
		
	}

	/**
	 * Checks that:<br>
	 * -given type attribute is not missing<br>
	 * -given type is indeed defined in the type parameters<br>
	 * -given type is not redundant i.e., already refered to
	 * @param typeArgs
	 * @param scDtd
	 * @param referencedTypes 
	 * @param monitor
	 */
	private FormulaFactory processTypeArguments(ITypeArgument[] typeArgs,
			ISCDatatypeDefinition scDtd, ReferencedTypes referencedTypes, IProgressMonitor monitor) throws CoreException{
		ArrayList<String> argsList = new ArrayList<String>();
		boolean hasError = false;
		if(typeArgs != null && typeArgs.length > 0){
			for (ITypeArgument typeArg : typeArgs){
				if (!typeArg.hasGivenType()){
					createProblemMarker(typeArg, TheoryAttributes.GIVEN_TYPE_ATTRIBUTE, 
							TheoryGraphProblem.TypeArgMissingError, scDtd.getElementName());
					continue;
				}
				String type = typeArg.getGivenType();
				if(!typeEnvironment.contains(type)){
					createProblemMarker(typeArg, TheoryAttributes.GIVEN_TYPE_ATTRIBUTE, 
							TheoryGraphProblem.TypeArgNotDefinedError, typeArg.getGivenType());
					hasError = true;
					continue;
				}
				if(argsList.contains(type)){
					createProblemMarker(typeArg, TheoryAttributes.GIVEN_TYPE_ATTRIBUTE, 
							TheoryGraphProblem.TypeArgRedundWarn, type);
					hasError = true;
					continue;
				}
				ISCTypeArgument scArg = scDtd.getTypeArgument(type);
				scArg.create(null, monitor);
				scArg.setSCGivenType(factory.makeGivenType(type), monitor);
				argsList.add(type);
				referencedTypes.addReferencedType(type);
				
			}

		}
		String name = scDtd.getIdentifierString();
		datatypeTable.addDatatype(name, argsList.toArray(new String[argsList.size()]));
		if(hasError)
			datatypeTable.setErrorProne();
		FormulaFactory ff = datatypeTable.augmentDecoyFormulaFactory();
		typeExpression =CoreUtilities.createTypeExpression(name, argsList, ff);
		return ff;
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
		datatypeTable = (DatatypeTable) repository.getState(DatatypeTable.STATE_TYPE);
		theoryAccuracyInfo = (TheoryAccuracyInfo) repository.getState(TheoryAccuracyInfo.STATE_TYPE);
		
	}

	@Override
	public void endModule(IRodinElement element, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		factory = null;
		typeEnvironment = null;
		datatypeTable = null;
		theoryAccuracyInfo = null;
		super.endModule(element, repository, monitor);
	}

}
