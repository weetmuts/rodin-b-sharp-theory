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
import org.eventb.core.sc.SCFilterModule;
import org.eventb.core.sc.state.ILabelSymbolInfo;
import org.eventb.core.sc.state.ILabelSymbolTable;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.theory.core.IRule;
import org.eventb.theory.core.TheoryAttributes;
import org.eventb.theory.core.sc.TheoryGraphProblem;
import org.eventb.theory.core.sc.states.TheoryLabelSymbolTable;
import org.rodinp.core.IRodinElement;

/**
 * 
 * @author maamria
 * 
 */
public abstract class RuleFilterModule<R extends IRule> extends SCFilterModule {

	private ILabelSymbolTable labelSymbolTable;

	public void initModule(ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		super.initModule(repository, monitor);
		labelSymbolTable = (TheoryLabelSymbolTable) repository
				.getState(TheoryLabelSymbolTable.STATE_TYPE);
	}

	@Override
	public boolean accept(IRodinElement element, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		R rule = getRule(element);
		final String label = rule.getLabel();
		final ILabelSymbolInfo symbolInfo = labelSymbolTable
				.getSymbolInfo(label);
		if (symbolInfo == null) {
			throw new IllegalStateException("No defined symbol for: " + label);
		}
		// Check automatic attribute
		if (!checkAutomaticAttribute(rule, symbolInfo, repository, monitor)) {
			return false;
		}
		// Check interactive and description attribute
		if (!checkInteractiveAttribute(rule, symbolInfo, repository, monitor)) {
			return false;
		}
		if (!furtherCheck(rule, symbolInfo, repository, monitor)){
			return false;
		}
		// all OK
		return true;
	}

	public void endModule(ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		labelSymbolTable = null;
		super.endModule(repository, monitor);
	}
	
	protected abstract R getRule(IRodinElement element);
	
	protected abstract boolean furtherCheck(R rule,
			ILabelSymbolInfo symbolInfo, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException;

	private boolean checkInteractiveAttribute(R rule,
			ILabelSymbolInfo symbolInfo, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		String desc = rule.getParent().getElementName() + "." + rule.getLabel();
		boolean isInter = false;
		// warning interactive status needs to be defined
		if (!rule.hasInteractive()) {
			createProblemMarker(rule, TheoryAttributes.INTERACTIVE_ATTRIBUTE,
					TheoryGraphProblem.InterUndefWarning);
			// default is interactive
			isInter = true;
		} else {
			isInter = rule.isInteractive();
		}
		if (isInter) {
			// check description
			if (!rule.hasDescription()
					|| (rule.hasDescription() && rule.getDescription().equals(
							""))) {
				createProblemMarker(rule, TheoryAttributes.DESC_ATTRIBUTE,
						TheoryGraphProblem.DescNotSupplied, rule.getLabel());
			} else {
				desc = rule.getDescription();
			}
		}
		symbolInfo.setAttributeValue(TheoryAttributes.INTERACTIVE_ATTRIBUTE,
				isInter);
		symbolInfo.setAttributeValue(TheoryAttributes.DESC_ATTRIBUTE, desc);
		return true;
	}

	private boolean checkAutomaticAttribute(R rule,
			ILabelSymbolInfo symbolInfo, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		boolean isAuto = false;
		// warning auto status needs to be defined
		if (!rule.hasAutomatic()) {
			createProblemMarker(rule, TheoryAttributes.AUTOMATIC_ATTRIBUTE,
					TheoryGraphProblem.AutoUndefWarning);
			// default is manual
			isAuto = false;
		} else {
			isAuto = rule.isAutomatic();
		}
		symbolInfo.setAttributeValue(TheoryAttributes.AUTOMATIC_ATTRIBUTE,
				isAuto);
		return true;
	}
}
