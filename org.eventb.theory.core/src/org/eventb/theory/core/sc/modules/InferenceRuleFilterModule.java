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
import org.eventb.core.EventBAttributes;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.state.ILabelSymbolInfo;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.theory.core.IInfer;
import org.eventb.theory.core.IInferenceRule;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.sc.TheoryGraphProblem;
import org.rodinp.core.IRodinElement;

/**
 * 
 * @author maamria
 * 
 */
public class InferenceRuleFilterModule extends RuleFilterModule<IInferenceRule> {

	private final IModuleType<InferenceRuleFilterModule> MODULE_TYPE = SCCore
			.getModuleType(TheoryPlugin.PLUGIN_ID + ".inferenceRuleFilterModule");

	@Override
	public IModuleType<?> getModuleType() {
		// TODO Auto-generated method stub
		return MODULE_TYPE;
	}

	@Override
	protected IInferenceRule getRule(IRodinElement element) {
		// TODO Auto-generated method stub
		return (IInferenceRule) element;
	}

	@Override
	protected boolean furtherCheck(IInferenceRule rule,
			ILabelSymbolInfo symbolInfo, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		IInfer[] infers = rule.getInfers();
		// Rule must have at least one infer clause
		if (infers.length < 1){
			createProblemMarker(rule, EventBAttributes.LABEL_ATTRIBUTE, TheoryGraphProblem.RuleNoInfersError, rule.getLabel());
			return false;
		}
		return true;
	}

}
