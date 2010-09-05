/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.internal.core.sc;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.SCFilterModule;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.theory.core.IOperatorWDCondition;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.sc.TheoryGraphProblem;
import org.eventb.theory.internal.core.sc.states.OperatorInformation;
import org.eventb.theory.internal.core.util.CoreUtilities;
import org.rodinp.core.IRodinElement;

/**
 * @author maamria
 *
 */
public class OperatorWDConditionFilterModule extends SCFilterModule{

	IModuleType<OperatorWDConditionFilterModule> MODULE_TYPE = 
		SCCore.getModuleType(TheoryPlugin.PLUGIN_ID + ".operatorWDConditionFilterModule");
	
	private FormulaFactory factory;
	private ITypeEnvironment typeEnvironment;
	private OperatorInformation operatorInformation;

	@Override
	public boolean accept(IRodinElement element, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		IOperatorWDCondition wdCond = (IOperatorWDCondition) element;
		if(!wdCond.hasPredicateString()){
			createProblemMarker(wdCond, EventBAttributes.PREDICATE_ATTRIBUTE, TheoryGraphProblem.WDPredUndefError);
			return false;
		}
		Predicate wd = CoreUtilities.parseAndCheckPredicate(wdCond, factory, typeEnvironment, this);
		if(wd == null || !checkAgainstReferencedIdentifiers(wdCond, wd)){
			return false;
		}
		return true;
	}
	
	protected boolean checkAgainstReferencedIdentifiers(IOperatorWDCondition wdCond, Predicate wd)
	throws CoreException{
		FreeIdentifier[] idents = wd.getFreeIdentifiers();
		List<String> notAllowed = new ArrayList<String>();
		for(FreeIdentifier ident : idents){
			if(!operatorInformation.isAllowedIdentifier(ident)){
				notAllowed.add(ident.getName());
			}
		}
		if(notAllowed.size() != 0){
			createProblemMarker(wdCond, EventBAttributes.PREDICATE_ATTRIBUTE, 
					TheoryGraphProblem.OpCannotReferToTheseTypes, CoreUtilities.toString(notAllowed));
			return false;
		}
		return true;
	}

	@Override
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}
	
	@Override
	public void initModule(ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		super.initModule(repository, monitor);
		factory = repository.getFormulaFactory();
		typeEnvironment = repository.getTypeEnvironment();
		operatorInformation = (OperatorInformation) repository.getState(OperatorInformation.STATE_TYPE);
	}

	@Override
	public void endModule(ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		factory = null;
		typeEnvironment = null;
		operatorInformation = null;
		super.endModule(repository, monitor);
	}

}
