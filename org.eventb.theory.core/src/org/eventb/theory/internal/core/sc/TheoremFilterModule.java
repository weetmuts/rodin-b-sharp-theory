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
import org.eventb.theory.internal.core.sc.states.TheoryLabelSymbolTable;
import org.eventb.theory.internal.core.util.CoreUtilities;
import org.rodinp.core.IRodinElement;

/**
 * @author maamria
 *
 */
public class TheoremFilterModule extends SCFilterModule{

	IModuleType<TheoremFilterModule> MODULE_TYPE = 
		SCCore.getModuleType(TheoryPlugin.PLUGIN_ID + ".theoremFilterModule");

	private ITypeEnvironment typeEnvironment;
	private FormulaFactory factory;
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
		
		Predicate thmPredicate = CoreUtilities.parseAndCheckPredicate(thm, factory, typeEnvironment, this);
		if(thmPredicate != null){
			ILabelSymbolInfo info = labelSymbolTable.getSymbolInfo(label);
			info.setAttributeValue(EventBAttributes.PREDICATE_ATTRIBUTE, thmPredicate.toString());
			return true;
		}
		return false;
	}

	/**
	 * May not be needed after all.
	 * @param thm
	 * @param pred
	 * @return
	 * @throws CoreException
	 */
	protected boolean checkAgainstTypeParameters(ITheorem thm, Predicate pred)
	throws CoreException{
		FreeIdentifier[] idents = pred.getFreeIdentifiers();
		for(FreeIdentifier ident : idents){
			if(!CoreUtilities.isGivenSet(typeEnvironment, ident.getName())){
				createProblemMarker(thm, EventBAttributes.PREDICATE_ATTRIBUTE, 
						ident.getSourceLocation().getStart(), ident.getSourceLocation().getEnd(), 
						TheoryGraphProblem.TheoremNonTypeParOccurError);
				return false;
			}
		}
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
		factory = repository.getFormulaFactory();
		typeEnvironment = repository.getTypeEnvironment();
		labelSymbolTable =(ILabelSymbolTable) repository.getState(TheoryLabelSymbolTable.STATE_TYPE);
	}

	@Override
	public void endModule(
			ISCStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		factory = null;
		typeEnvironment = null;
		labelSymbolTable = null;
		super.endModule(repository, monitor);
	}

}
