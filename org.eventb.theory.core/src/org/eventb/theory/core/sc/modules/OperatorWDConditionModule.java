/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
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
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.SCProcessorModule;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.theory.core.INewOperatorDefinition;
import org.eventb.theory.core.IOperatorWDCondition;
import org.eventb.theory.core.ISCNewOperatorDefinition;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.sc.states.IOperatorInformation;
import org.eventb.theory.internal.core.util.CoreUtilities;
import org.eventb.theory.internal.core.util.MathExtensionsUtilities;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;

/**
 * @author maamria
 *
 */
public class OperatorWDConditionModule extends SCProcessorModule{

	private final IModuleType<OperatorWDConditionModule> MODULE_TYPE = 
		SCCore.getModuleType(TheoryPlugin.PLUGIN_ID + ".operatorWDConditionModule");
	
	private FormulaFactory factory;
	private ITypeEnvironment typeEnvironment;
	private IOperatorInformation operatorInformation;

	@Override
	public void process(IRodinElement element, IInternalElement target,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		INewOperatorDefinition newOpDef = (INewOperatorDefinition) element;
		ISCNewOperatorDefinition scNewOpDef = (ISCNewOperatorDefinition) target;
		IOperatorWDCondition[] wdConds = newOpDef.getOperatorWDConditions();
		
		if(wdConds != null && wdConds.length > 0){
			Predicate wdPred  = processWdConditions(wdConds, repository, monitor);
			if(wdPred != null && !wdPred.equals(MathExtensionsUtilities.BTRUE)){
				if(target != null){
					Predicate wdPredWD = wdPred.getWDPredicate(factory);
					wdPred = MathExtensionsUtilities.conjunctPredicates(new Predicate[]{wdPredWD, wdPred}, factory);
					scNewOpDef.setPredicate(wdPred, monitor);
					operatorInformation.setWdCondition(wdPred);
				}
				else{
					operatorInformation.setHasError();
				}
			}
		}
	}

	protected Predicate processWdConditions(IOperatorWDCondition[] wds, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException{
		List<Predicate> wdPredicates = new ArrayList<Predicate>();
		initFilterModules(repository, monitor);
		for(IOperatorWDCondition wd : wds){
			if(!filterModules(wd, repository, monitor)){
				operatorInformation.setHasError();
				continue;
			}
			else {
				Predicate pred = CoreUtilities.parseAndCheckPredicate(wd, factory, typeEnvironment, this);
				if(!pred.equals(MathExtensionsUtilities.BTRUE))
					wdPredicates.add(pred);
			}
		}
		endFilterModules(repository, monitor);
		return MathExtensionsUtilities.conjunctPredicates(wdPredicates, factory);
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
		operatorInformation = (IOperatorInformation) repository.getState(IOperatorInformation.STATE_TYPE);
		
	}

	@Override
	public void endModule(IRodinElement element, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		factory = null;
		typeEnvironment = null;
		operatorInformation = null;
		super.endModule(element, repository, monitor);
	}

}
