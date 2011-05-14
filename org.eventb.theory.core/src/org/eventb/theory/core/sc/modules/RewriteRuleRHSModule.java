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
import org.eventb.core.ILabeledElement;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.state.ILabelSymbolInfo;
import org.eventb.core.sc.state.ILabelSymbolTable;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.internal.core.sc.modules.LabeledElementModule;
import org.eventb.theory.core.IRewriteRule;
import org.eventb.theory.core.IRewriteRuleRightHandSide;
import org.eventb.theory.core.ISCRewriteRule;
import org.eventb.theory.core.ISCRewriteRuleRightHandSide;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.sc.states.RewriteRuleLabelSymbolTable;
import org.eventb.theory.core.sc.states.TheorySymbolFactory;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;

/**
 * 
 * @author maamria
 *
 */
@SuppressWarnings("restriction")
public class RewriteRuleRHSModule extends LabeledElementModule {

	public static final IModuleType<RewriteRuleRHSModule> MODULE_TYPE = SCCore
		.getModuleType(TheoryPlugin.PLUGIN_ID
			+ ".rewriteRuleRHSModule");
	
	@Override
	public void process(IRodinElement element, IInternalElement target,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		IRewriteRule rewriteRule = (IRewriteRule) element;
		ISCRewriteRule scRewriteRule = (ISCRewriteRule) target;
		IRewriteRuleRightHandSide[] ruleRHSs = rewriteRule.getRuleRHSs();
		ILabelSymbolInfo[] symbolInfos = fetchRHSs(ruleRHSs, 
				element.getParent().getParent().getElementName(), repository, monitor);
		ISCRewriteRuleRightHandSide[] scRHSs = new ISCRewriteRuleRightHandSide[ruleRHSs.length];
		commitSides(ruleRHSs, scRewriteRule, scRHSs, symbolInfos, monitor);
	}

	@Override
	public IModuleType<?> getModuleType() {
		// TODO Auto-generated method stub
		return MODULE_TYPE;
	}
	
	private void commitSides(IRewriteRuleRightHandSide[] ruleRHSs,
			ISCRewriteRule scRewriteRule, ISCRewriteRuleRightHandSide[] scRHSs,
			ILabelSymbolInfo[] symbolInfos, IProgressMonitor monitor) throws CoreException{
		for (int i = 0; i < ruleRHSs.length; i++) {
			if (symbolInfos[i] != null && !symbolInfos[i].hasError()) {
				scRHSs[i] =(ISCRewriteRuleRightHandSide) symbolInfos[i].
						createSCElement(scRewriteRule, symbolInfos[i].getSymbol(), monitor);
			}
		}
	}

	private ILabelSymbolInfo[] fetchRHSs(IRewriteRuleRightHandSide[] ruleRHSs,
			String theoryName, ISCStateRepository repository,
			IProgressMonitor monitor)  throws CoreException{
		ILabelSymbolInfo[] symbolInfos = new ILabelSymbolInfo[ruleRHSs.length];
		initFilterModules(repository, monitor);
		for (int i = 0; i < ruleRHSs.length; i++) {
			symbolInfos[i] = fetchLabel(ruleRHSs[i], theoryName, monitor);
			if (symbolInfos[i] == null)
				continue;
			if (!filterModules(ruleRHSs[i], repository, monitor)) {
				symbolInfos[i].setError();
			}
		}
		endFilterModules(repository, monitor);
		return symbolInfos;
	}

	@Override
	protected ILabelSymbolTable getLabelSymbolTableFromRepository(
			ISCStateRepository repository) throws CoreException {
		// TODO Auto-generated method stub
		return (RewriteRuleLabelSymbolTable) repository.getState(RewriteRuleLabelSymbolTable.STATE_TYPE);
	}

	@Override
	protected ILabelSymbolInfo createLabelSymbolInfo(String symbol,
			ILabeledElement element, String component) throws CoreException {
		// TODO Auto-generated method stub
		return TheorySymbolFactory.getInstance().makeLocalRHS(symbol, true, element, component);
	}

}
