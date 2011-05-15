/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.pog.modules;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IPORoot;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.pog.POGCore;
import org.eventb.core.pog.POGProcessorModule;
import org.eventb.core.pog.state.IPOGStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.theory.core.ISCNewOperatorDefinition;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.rodinp.core.IRodinElement;

/**
 * @author maamria
 * 
 */
public class OperatorExtensionPOGModule extends POGProcessorModule {

	private final IModuleType<OperatorExtensionPOGModule> MODULE_TYPE = 
		POGCore.getModuleType(TheoryPlugin.PLUGIN_ID + ".operatorExtensionPOGModule"); 
	
	protected FormulaFactory factory;
	protected ITypeEnvironment typeEnvironment;
	protected IPORoot target;

	protected static final String OPERATOR_WD_PO = "Operator Well-Definedness Preservation";
	protected static final String OPERATOR_WD_POSTFIX = "/Op-WD";
	protected static final String OPERATOR_COMMUT_PO = "Operator Commutativity";
	protected static final String OPERATOR_COMMUT_POSTFIX = "/Op-COMMUT";
	protected static final String OPERATOR_ASSOC_PO = "Operator Associativity";
	protected static final String OPERATOR_ASSOC_POSTFIX = "/Op-ASSOC";
	
	@Override
	public void initModule(IRodinElement element,
			IPOGStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		super.initModule(element, repository, monitor);
		factory = repository.getFormulaFactory();
		typeEnvironment = repository.getTypeEnvironment();
		target = repository.getTarget();
		
	}

	@Override
	public void endModule(IRodinElement element,
			IPOGStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		typeEnvironment = null;
		factory = null;
		target = null;
		super.endModule(element, repository, monitor);
	}
	
	protected void generateCorrespondingPOs(IFormulaExtension extension,
			ISCNewOperatorDefinition definition, IProgressMonitor monitor) throws CoreException {
		
	}

	@Override
	public void process(IRodinElement element, IPOGStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IModuleType<?> getModuleType() {
		// TODO Auto-generated method stub
		return MODULE_TYPE;
	}

}
