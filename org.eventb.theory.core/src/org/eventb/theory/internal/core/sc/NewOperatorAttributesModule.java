/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.internal.core.sc;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Type;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.SCProcessorModule;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.theory.core.INewOperatorDefinition;
import org.eventb.theory.core.ISCNewOperatorDefinition;
import org.eventb.theory.core.TheoryAttributes;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.sc.TheoryGraphProblem;
import org.eventb.theory.internal.core.sc.states.IOperatorInformation;
import org.eventb.theory.internal.core.util.CoreUtilities;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;

/**
 * @author maamria
 * 
 */
public class NewOperatorAttributesModule extends SCProcessorModule {

	IModuleType<NewOperatorAttributesModule> MODULE_TYPE = SCCore
			.getModuleType(TheoryPlugin.PLUGIN_ID
					+ ".newOperatorAttributesModule");
	
	private ITypeEnvironment typeEnvironment;
	private IOperatorInformation operatorInformation;

	@Override
	public void process(IRodinElement element, IInternalElement target,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		if (target != null) {
			INewOperatorDefinition opDef = (INewOperatorDefinition) element;
			ISCNewOperatorDefinition scNewOpDef = (ISCNewOperatorDefinition) target;
			String opID = opDef.getLabel();
			
			List<String> args = CoreUtilities.getOperatorArguments(typeEnvironment);
			
			if (!opDef.hasAssociativeAttribute()) {
				// warn
				createProblemMarker(opDef,
						TheoryAttributes.ASSOCIATIVE_ATTRIBUTE,
						TheoryGraphProblem.OperatorAssocMissingWarning, opID);
			} else{
				boolean isAssos = opDef.isAssociative();
				if(isAssos){
					if(checkAssociativity(args))
						scNewOpDef.setAssociative(isAssos, monitor);
					else{
						createProblemMarker(opDef,
								TheoryAttributes.ASSOCIATIVE_ATTRIBUTE,
								TheoryGraphProblem.OperatorCannotBeAssosWarning, opID);
					}
				}
			}
			if (!opDef.hasCommutativeAttribute()) {
				// warn
				createProblemMarker(opDef,
						TheoryAttributes.COMMUTATIVE_ATTRIBUTE,
						TheoryGraphProblem.OperatorCommutMissingWarning, opID);
			} else{
				boolean isCommut = opDef.isCommutative();
				if(isCommut){
					if(checkCommutativity(args))
						scNewOpDef.setCommutative(isCommut, monitor);
					else{
						createProblemMarker(opDef,
								TheoryAttributes.COMMUTATIVE_ATTRIBUTE,
								TheoryGraphProblem.OperatorCannotBeCommutWarning, opID);
					}
				}
			}
		}

	}

	@Override
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	protected boolean checkAssociativity(List<String> args) {
		boolean ok = operatorInformation.isExpressionOperator();
		ok &= (args.size() == 2);
		Type type = null;
		for(String arg : args){
			if(type == null){
				type = typeEnvironment.getType(arg);
			}
			ok &= (type.equals(typeEnvironment.getType(arg)));
		}
		ok &= (type.equals(operatorInformation.getResultantType()));
		return ok;
	}

	/**
	 * An operator can be commutative if it can have two arguments of the same type.
	 * @param args the operator arguments
	 * @return whether this operator can be commutative
	 */
	protected boolean checkCommutativity(List<String> args) {
		boolean ok = (args.size() == 2);
		Type type = null;
		for(String arg : args){
			if(type == null){
				type = typeEnvironment.getType(arg);
			}
			ok &= (type.equals(typeEnvironment.getType(arg)));
		}
		return ok;
	}

	@Override
	public void initModule(IRodinElement element,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		super.initModule(element, repository, monitor);
		typeEnvironment = repository.getTypeEnvironment();
		operatorInformation = (IOperatorInformation) repository
				.getState(IOperatorInformation.STATE_TYPE);

	}

	@Override
	public void endModule(IRodinElement element, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		typeEnvironment = null;
		operatorInformation = null;
		super.endModule(element, repository, monitor);
	}

}
