/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.sc.modules;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.state.ILabelSymbolInfo;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.theory.core.IRewriteRule;
import org.eventb.theory.core.IRewriteRuleRightHandSide;
import org.eventb.theory.core.TheoryAttributes;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.sc.TheoryGraphProblem;
import org.rodinp.core.IRodinElement;

/**
 * 
 * @author maamria
 * 
 */
public class RewriteRuleFilterModule extends RuleFilterModule<IRewriteRule> {

	private final IModuleType<RewriteRuleFilterModule> MODULE_TYPE = SCCore
			.getModuleType(TheoryPlugin.PLUGIN_ID
					+ ".rewriteRuleFilterModule");

	@Override
	public IModuleType<?> getModuleType() {
		// TODO Auto-generated method stub
		return MODULE_TYPE;
	}

	@Override
	protected IRewriteRule getRule(IRodinElement element) {
		// TODO Auto-generated method stub
		return (IRewriteRule) element;
	}

	@Override
	protected boolean furtherCheck(IRewriteRule rule,
			ILabelSymbolInfo symbolInfo, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		// Check complete attribute
		if (!checkCompleteAttribute(rule, symbolInfo, repository, monitor)) {
			return false;
		}
		// Check rule right hand sides number
		IRewriteRuleRightHandSide[] ruleHandSides = rule.getRuleRHSs();
		if(ruleHandSides.length < 1){
			createProblemMarker(rule, TheoryGraphProblem.RuleNoRhsError, rule.getLabel());
			return false;
		}
		return false;
	}
	
	private boolean checkCompleteAttribute(IRewriteRule rule, ILabelSymbolInfo symbolInfo,
			ISCStateRepository repository, IProgressMonitor monitor) throws CoreException {
		boolean isComp = false;
		if (!rule.hasComplete()) {
			createProblemMarker(rule, TheoryAttributes.COMPLETE_ATTRIBUTE,
					TheoryGraphProblem.CompleteUndefWarning);
			// default is incomplete
			isComp = false;
		} else {
			isComp = rule.isComplete();
		}
		symbolInfo.setAttributeValue(TheoryAttributes.COMPLETE_ATTRIBUTE,
				isComp);
		return true;
	}
}
