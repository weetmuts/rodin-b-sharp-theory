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
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.SCFilterModule;
import org.eventb.core.sc.state.ILabelSymbolInfo;
import org.eventb.core.sc.state.ILabelSymbolTable;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.theory.core.IInferenceRule;
import org.eventb.theory.core.TheoryAttributes;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.sc.TheoryGraphProblem;
import org.eventb.theory.internal.core.sc.states.TheoryLabelSymbolTable;
import org.rodinp.core.IRodinElement;

/**
 * @author maamria
 * 
 */
public class TheoryInferenceRuleAttributesModule extends SCFilterModule {

	public static final IModuleType<TheoryInferenceRuleAttributesModule> MODULE_TYPE = SCCore
			.getModuleType(TheoryPlugin.PLUGIN_ID
					+ ".theoryInferenceRuleAttributesModule");

	private ILabelSymbolTable labelSymbolTable;

	public void initModule(ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		super.initModule(repository, monitor);
		labelSymbolTable = getLabelSymbolTable(repository);
	}

	public boolean accept(IRodinElement element, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		IInferenceRule rule = (IInferenceRule) element;
		String toolTip = rule.getParent().getElementName()+"."+rule.getLabel();
		String desc = rule.getParent().getElementName()+"."+rule.getLabel();
		final String label = rule.getLabel();
		boolean isAuto = false;
		// warning auto status needs to be defined
		if (!rule.hasAutomatic()) {
			createProblemMarker(rule,
					TheoryAttributes.AUTOMATIC_ATTRIBUTE,
					TheoryGraphProblem.AutoUndefWarning);
			// default is manual
			isAuto = false;
		} else {
			isAuto = rule.isAutomatic();
		}

		boolean isInter = true;
		if (!rule.hasInteractive()) {
			createProblemMarker(rule,
					TheoryAttributes.INTERACTIVE_ATTRIBUTE,
					TheoryGraphProblem.InterUndefWarning);
			// default is interactive
			isInter = true;
		} else {
			isInter = rule.isInteractive();
		}
		if(isInter){
			// check the tool tip
			if(!rule.hasToolTip() || (rule.hasToolTip() && rule.getToolTip().equals(""))){
				createProblemMarker(rule, TheoryAttributes.TOOL_TIP_ATTRIBUTE, 
						TheoryGraphProblem.ToolTipNotSupplied, rule.getLabel());
			}
			else{
				toolTip = rule.getToolTip();
			}
			// check desc
			if(!rule.hasDescription() || (rule.hasDescription() && rule.getDescription().equals(""))){
				createProblemMarker(rule, 
						TheoryAttributes.DESC_ATTRIBUTE , 
						TheoryGraphProblem.DescNotSupplied, rule.getLabel());
			}
			else {
				desc = rule.getDescription();
			}
		}
		checkAndSetSymbolInfo(label, isAuto, isInter, toolTip, desc);
		if(rule.getInfers().length == 0){
			createProblemMarker(rule, EventBAttributes.LABEL_ATTRIBUTE, TheoryGraphProblem.RuleNoInfersError,  label);
			return false;
		}
		return true;
	}

	public void endModule(ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		labelSymbolTable = null;
		super.endModule(repository, monitor);
	}

	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	private void checkAndSetSymbolInfo(String label, boolean isAuto, boolean isInter, String toolTip, String desc) {
		final ILabelSymbolInfo symbolInfo = labelSymbolTable
				.getSymbolInfo(label);
		if (symbolInfo == null) {
			throw new IllegalStateException("No defined symbol for: " + label);
		}
		symbolInfo.setAttributeValue(TheoryAttributes.AUTOMATIC_ATTRIBUTE,
				isAuto);
		symbolInfo.setAttributeValue(TheoryAttributes.INTERACTIVE_ATTRIBUTE,
				isInter);
		symbolInfo.setAttributeValue(TheoryAttributes.TOOL_TIP_ATTRIBUTE,
				toolTip);
		symbolInfo.setAttributeValue(TheoryAttributes.DESC_ATTRIBUTE,
				desc);

	}

	private TheoryLabelSymbolTable getLabelSymbolTable(
			ISCStateRepository repository) throws CoreException {
		return (TheoryLabelSymbolTable) repository
				.getState(TheoryLabelSymbolTable.STATE_TYPE);
	}
}
