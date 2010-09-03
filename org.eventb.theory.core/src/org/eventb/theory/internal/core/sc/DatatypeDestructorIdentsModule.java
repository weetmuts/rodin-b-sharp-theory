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
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Type;
import org.eventb.core.sc.GraphProblem;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.SCFilterModule;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.theory.core.IConstructorArgument;
import org.eventb.theory.core.TheoryAttributes;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.sc.TheoryGraphProblem;
import org.eventb.theory.internal.core.sc.states.ReferencedTypes;
import org.eventb.theory.internal.core.util.CoreUtilities;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author maamria
 *
 */
public class DatatypeDestructorIdentsModule  extends SCFilterModule{

	IModuleType<DatatypeDestructorIdentsModule> MODULE_TYPE = 
		SCCore.getModuleType(TheoryPlugin.PLUGIN_ID + ".datatypeDestructorIdentsModule");
	
	private ReferencedTypes referencedTypes;
	private ITypeEnvironment typeEnvironment;
	private FormulaFactory factory;
	
	@Override
	public boolean accept(IRodinElement element, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		IConstructorArgument consArg = (IConstructorArgument) element;
		Type type = CoreUtilities.parseTypeExpression(consArg, factory, this);
		return checkTypeParameters(type, consArg);
	}
	
	/**
	 * @param type
	 * @param consArg 
	 * @param typeEnvironment
	 */
	private boolean checkTypeParameters(Type type, IConstructorArgument consArg)
	throws RodinDBException{
		FreeIdentifier[] idents = type.toExpression(factory).getSyntacticallyFreeIdentifiers();
		boolean result = true;
		for(FreeIdentifier ident : idents){
			if(!referencedTypes.getReferencedTypes().contains(ident.toString())){
				if(!typeEnvironment.contains(ident.getName())){
					createProblemMarker(consArg, 
							TheoryAttributes.TYPE_ATTRIBUTE, 
							GraphProblem.UndeclaredFreeIdentifierError, 
							ident.getName());
					result = false;
				}
				else{
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

	@Override
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	@Override
	public void initModule(
			ISCStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		super.initModule(repository, monitor);
		referencedTypes = (ReferencedTypes) repository.getState(ReferencedTypes.STATE_TYPE);
		factory = repository.getFormulaFactory();
		typeEnvironment = repository.getTypeEnvironment();
	}
	
	@Override
	public void endModule(
			ISCStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		referencedTypes = null;
		factory = null;
		typeEnvironment = null;
		super.endModule(repository, monitor);
	}
}
