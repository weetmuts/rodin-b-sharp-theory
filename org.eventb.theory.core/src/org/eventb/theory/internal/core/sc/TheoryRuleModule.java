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
import org.eventb.core.ILabeledElement;
import org.eventb.core.sc.state.ILabelSymbolInfo;
import org.eventb.core.sc.state.ILabelSymbolTable;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.internal.core.sc.modules.LabeledElementModule;
import org.eventb.theory.core.IProofRulesBlock;
import org.eventb.theory.core.IRule;
import org.eventb.theory.core.ISCProofRulesBlock;
import org.eventb.theory.core.ISCRule;
import org.eventb.theory.core.sc.Messages;
import org.eventb.theory.internal.core.sc.states.TheoryAccuracyInfo;
import org.eventb.theory.internal.core.sc.states.TheoryLabelSymbolTable;
import org.eventb.theory.internal.core.util.CoreUtilities;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;

/**
 * @author maamria
 * 
 */
@SuppressWarnings("restriction")
public abstract class TheoryRuleModule<R extends IRule, S extends ISCRule>
		extends LabeledElementModule {

	protected final static int LABEL_SYMTAB_SIZE = 2047;

	protected TheoryAccuracyInfo accuracyInfo;
	protected R[] rules;


	@Override
	public void initModule(IRodinElement element,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		super.initModule(element, repository, monitor);
		rules = getRuleElements(element);
		accuracyInfo = (TheoryAccuracyInfo) repository
				.getState(TheoryAccuracyInfo.STATE_TYPE);

	}

	public void process(IRodinElement element, IInternalElement target,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		IProofRulesBlock block = (IProofRulesBlock) element;
		monitor.subTask(Messages.bind(getMessage()));
		ILabelSymbolInfo[] symbolInfos = fetchRules(block.getParent()
				.getElementName(), repository, monitor);
		S[] scRules = createSCRulesArray();
		commitRules((ISCProofRulesBlock) target, scRules, symbolInfos, monitor);
		processRules(scRules, repository, symbolInfos, monitor);

	}

	@Override
	public void endModule(IRodinElement element, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		accuracyInfo = null;
		rules = null;
		super.endModule(element, repository, monitor);
	}

	protected abstract String getMessage();
	
	protected abstract S[] createSCRulesArray();
	
	@Override
	protected ILabelSymbolInfo createLabelSymbolInfo(String symbol,
			ILabeledElement element, String component) throws CoreException {
		return makeLocalRule(symbol, element, component);
	}

	protected abstract ILabelSymbolInfo makeLocalRule(String symbol,
			ILabeledElement element, String component) throws CoreException;
	
	@Override
	protected ILabelSymbolTable getLabelSymbolTableFromRepository(
			ISCStateRepository repository) throws CoreException {
		return (ILabelSymbolTable) repository
				.getState(TheoryLabelSymbolTable.STATE_TYPE);
	}

	protected abstract R[] getRuleElements(IRodinElement element) throws CoreException ;

	protected abstract ILabelSymbolInfo[] fetchRules(String theoryName,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException;

	protected void commitRules(ISCProofRulesBlock target,
			S[] scRules, ILabelSymbolInfo[] symbolInfos,
			IProgressMonitor monitor) throws CoreException {
		int index = CoreUtilities.SC_STARTING_INDEX;
		for (int i = 0; i < rules.length; i++) {
			if (symbolInfos[i] != null && !symbolInfos[i].hasError()) {
				scRules[i] = createSCRule(target, index++, symbolInfos[i],
						monitor);
			}
		}
	}

	// create an empty sc element
	protected S createSCRule(ISCProofRulesBlock target, int index,
			ILabelSymbolInfo symbolInfo, IProgressMonitor monitor)
			throws CoreException {
		ILabeledElement scRule = symbolInfo.createSCElement(target,
				getPrefix() + index, monitor);
		return cast(scRule);
	}

	protected abstract String getPrefix();
	
	protected abstract S cast(ILabeledElement scRule);
	
	protected abstract void processRules(S[] scRules,
			ISCStateRepository repository, ILabelSymbolInfo[] infos,
			IProgressMonitor monitor) throws CoreException ;

}
