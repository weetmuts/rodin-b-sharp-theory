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
import org.eventb.theory.internal.core.sc.states.DatatypeTable;
import org.eventb.theory.internal.core.sc.states.DatatypeTable.ERROR_CODE;
import org.eventb.theory.internal.core.util.CoreUtilities;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;

/**
 * @author maamria
 *
 */
public class DatatypeConstructorModule extends SCProcessorModule{

	IModuleType<DatatypeConstructorModule> MODULE_TYPE = 
		SCCore.getModuleType(TheoryPlugin.PLUGIN_ID + ".datatypeConstructorModule");
	
	private FormulaFactory factory;
	private ITypeEnvironment typeEnvironment;
	
	private DatatypeTable datatypeTable;
	private ISCDatatypeDefinition scDtd;
	
	@Override
	public void process(IRodinElement element, IInternalElement target,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		IDatatypeDefinition dtd = (IDatatypeDefinition) element;
		scDtd = (ISCDatatypeDefinition) target;
		IDatatypeConstructor[] dtConss = dtd.getDatatypeConstructors();
		processConstructors(dtConss,dtd, scDtd, repository, monitor);
		monitor.worked(1);
	}


	/**
	 * @param conss
	 * @param origin
	 * @param target
	 * @param repository
	 * @param monitor
	 */
	private void processConstructors(IDatatypeConstructor[] conss,
			IDatatypeDefinition origin, ISCDatatypeDefinition target, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException{
		for (IDatatypeConstructor cons : conss){
			if(!cons.hasIdentifierString()){
				createProblemMarker(cons, EventBAttributes.IDENTIFIER_ATTRIBUTE, 
						TheoryGraphProblem.MissingConstructorNameError);
				datatypeTable.setErrorProne();
				continue;
			}
			String name = cons.getIdentifierString();
			ERROR_CODE error = datatypeTable.isNameOk(name);
			if(error != null){
				createProblemMarker(cons, EventBAttributes.IDENTIFIER_ATTRIBUTE, 
						CoreUtilities.getAppropriateProblemForCode(error), name);
				datatypeTable.setErrorProne();
				continue;
			}
			FreeIdentifier ident = CoreUtilities.parseIdentifier(cons.getIdentifierString(), 
					cons, EventBAttributes.IDENTIFIER_ATTRIBUTE, 
					factory, this);
			if(ident != null){
				if(typeEnvironment.contains(ident.getName())){
					createProblemMarker(cons, EventBAttributes.IDENTIFIER_ATTRIBUTE, 
							TheoryGraphProblem.ConstructorNameAlreadyATypeParError, 
							ident.getName());
					datatypeTable.setErrorProne();
					continue;
				}
				ISCDatatypeConstructor scCons = 
					CoreUtilities.createSCIdentifierElement(ISCDatatypeConstructor.ELEMENT_TYPE, cons, target, monitor);
				scCons.setSource(cons, monitor);
				datatypeTable.addConstructor(name);
				
				initProcessorModules(cons, repository, monitor);
				processModules(cons, scCons, repository, monitor);
				endProcessorModules(cons, repository, monitor);
			}
			else
				datatypeTable.setErrorProne();
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
		datatypeTable = (DatatypeTable) repository.getState(DatatypeTable.STATE_TYPE);		
	}

	@Override
	public void endModule(IRodinElement element, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		scDtd.setHasError(datatypeTable.isErrorProne(), monitor);
		datatypeTable = null;
		typeEnvironment = null;
		factory =null;
		super.endModule(element, repository, monitor);
	}

}
