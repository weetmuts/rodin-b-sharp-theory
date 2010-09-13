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
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.SCProcessorModule;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.theory.core.IDatatypeDefinition;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.sc.TheoryGraphProblem;
import org.eventb.theory.internal.core.sc.states.AddedTypeExpression;
import org.eventb.theory.internal.core.sc.states.DatatypeTable;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;

/**
 * @author maamria
 *
 */
public class DatatypeDefinitionCheckerModule extends SCProcessorModule{

	IModuleType<DatatypeDefinitionCheckerModule> MODULE_TYPE = 
		SCCore.getModuleType(TheoryPlugin.PLUGIN_ID + ".datatypeDefinitionCheckerModule");
	
	private DatatypeTable datatypeTable;
	private AddedTypeExpression addedTypeExpression;
	
	@Override
	public void process(IRodinElement element, IInternalElement target,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		IDatatypeDefinition dtd = (IDatatypeDefinition) element;
		if(dtd.getDatatypeConstructors().length < 1){
			createProblemMarker(dtd, EventBAttributes.IDENTIFIER_ATTRIBUTE, 
					TheoryGraphProblem.DatatypeHasNoConsError, dtd.getIdentifierString());
			datatypeTable.setErrorProne();
		}
		if(!datatypeTable.datatypeHasBaseConstructor(addedTypeExpression.getType())){
			createProblemMarker(dtd, EventBAttributes.IDENTIFIER_ATTRIBUTE, 
					TheoryGraphProblem.DatatypeHasNoBaseConsError, dtd.getIdentifierString());
			datatypeTable.setErrorProne();
		}
		
	}

	@Override
	public IModuleType<?> getModuleType() {
		// TODO Auto-generated method stub
		return MODULE_TYPE;
	}
	
	@Override
	public void initModule(IRodinElement element,
			ISCStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		super.initModule(element, repository, monitor);
		datatypeTable = (DatatypeTable) repository.getState(DatatypeTable.STATE_TYPE);
		addedTypeExpression = (AddedTypeExpression) repository.getState(AddedTypeExpression.STATE_TYPE);
	}
	
	@Override
	public void endModule(IRodinElement element,
			ISCStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		datatypeTable = null;
		addedTypeExpression = null;
		super.endModule(element, repository, monitor);
	}


}
