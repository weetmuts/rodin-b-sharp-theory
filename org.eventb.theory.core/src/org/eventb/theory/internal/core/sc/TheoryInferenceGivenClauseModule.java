/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.internal.core.sc;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.EventBAttributes;
import org.eventb.core.ast.Predicate;
import org.eventb.core.sc.SCCore;
import org.eventb.core.tool.IModuleType;
import org.eventb.theory.core.IGiven;
import org.eventb.theory.core.IInferenceRule;
import org.eventb.theory.core.ISCGiven;
import org.eventb.theory.core.ISCInferenceRule;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.sc.TheoryGraphProblem;
import org.eventb.theory.internal.core.util.CoreUtilities;

/**
 * @author maamria
 *
 */
public class TheoryInferenceGivenClauseModule extends TheoryInferenceClausesModule<IGiven, ISCGiven>{

protected static final String GIV_NAME_PREFIX = "givc";
	
	public static final IModuleType<TheoryInferenceGivenClauseModule> MODULE_TYPE = SCCore
		.getModuleType(TheoryPlugin.PLUGIN_ID
			+ ".theoryInferenceGivenClauseModule");
	
	@Override
	public IModuleType<?> getModuleType() {
		// TODO Auto-generated method stub
		return MODULE_TYPE;
	}

	@Override
	protected IGiven[] getClauses(IInferenceRule rule) throws CoreException {
		// TODO Auto-generated method stub
		return rule.getGivens();
	}

	@Override
	protected void addIdentifiers(Predicate predicate) {
		inferenceIdentifiers.addGivenIdentifiers(predicate.getFreeIdentifiers());
		
	}

	@Override
	protected ISCGiven getSCClause(ISCInferenceRule parent, String name) {
		ISCGiven scGiven = parent.getGiven(name);
		return scGiven;
	}


	@Override
	protected String getPrefix() {
		// TODO Auto-generated method stub
		return GIV_NAME_PREFIX;
	}

	@Override
	protected boolean checkPredicate(Predicate predicate, IGiven clause) 
	throws CoreException{
		if(predicate.equals(CoreUtilities.BTRUE)){
			createProblemMarker(clause, EventBAttributes.PREDICATE_ATTRIBUTE,TheoryGraphProblem.InferenceGivenBTRUEPredWarn);
		}
		return true;
	}

}
