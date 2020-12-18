/*******************************************************************************
 * Copyright (c) 2010, 2020 University of Southampton and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.sc.modules;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.ast.Predicate;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.SCFilterModule;
import org.eventb.core.sc.state.ILabelSymbolInfo;
import org.eventb.core.sc.state.ILabelSymbolTable;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.theory.core.ITheorem;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.sc.TheoryGraphProblem;
import org.eventb.theory.core.sc.states.TheoremsLabelSymbolTable;
import org.eventb.theory.core.util.CoreUtilities;
import org.rodinp.core.IRodinElement;

/**
 * @author maamria
 *
 */
public class TheoremFilterModule extends SCFilterModule{

	IModuleType<TheoremFilterModule> MODULE_TYPE = 
		SCCore.getModuleType(TheoryPlugin.PLUGIN_ID + ".theoremFilterModule");

	private ILabelSymbolTable labelSymbolTable;

	@Override
	public boolean accept(IRodinElement element, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		ITheorem thm = (ITheorem) element;
		String label = thm.getLabel();
		if(!thm.hasPredicateString() || thm.getPredicateString().equals("")){
			createProblemMarker(thm, EventBAttributes.PREDICATE_ATTRIBUTE, 
					TheoryGraphProblem.TheoremPredMissingError, label);
			return false;
		}
		
		Predicate thmPredicate = CoreUtilities.parseAndCheckPredicate(thm, repository.getFormulaFactory(), repository.getTypeEnvironment(), this);
		if(thmPredicate == null || !CoreUtilities.checkAgainstTypeParameters(thm, thmPredicate, repository.getTypeEnvironment(), this)){
			return false;
		}
		ILabelSymbolInfo info = labelSymbolTable.getSymbolInfo(label);
		// FIXED Bug used toStringWithTypes() instead of toString() to avoid losing type information
		info.setAttributeValue(EventBAttributes.PREDICATE_ATTRIBUTE, thmPredicate.toStringWithTypes());
		return true;
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
		labelSymbolTable =(ILabelSymbolTable) repository.getState(TheoremsLabelSymbolTable.STATE_TYPE);
	}

	@Override
	public void endModule(
			ISCStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		labelSymbolTable = null;
		super.endModule(repository, monitor);
	}

}
