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
import org.eventb.core.IIdentifierElement;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Type;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.state.IIdentifierSymbolInfo;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.internal.core.sc.modules.IdentifierModule;
import org.eventb.theory.core.IMetavariable;
import org.eventb.theory.core.IProofRulesBlock;
import org.eventb.theory.core.ISCMetavariable;
import org.eventb.theory.core.ISCProofRulesBlock;
import org.eventb.theory.core.ITheoryRoot;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.internal.core.sc.states.TheorySymbolFactory;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;

/**
 * @author maamria
 * 
 */
@SuppressWarnings("restriction")
public class MetavariableModule extends IdentifierModule {

	IModuleType<MetavariableModule> MODULE_TYPE = SCCore
			.getModuleType(TheoryPlugin.PLUGIN_ID + ".metavariableModule");

	@Override
	public void process(IRodinElement element, IInternalElement target,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		IProofRulesBlock rulesBlock = (IProofRulesBlock) element;
		ISCProofRulesBlock scRulesBlock = (ISCProofRulesBlock) target;
		IMetavariable[] metavars = rulesBlock.getMetavariables();
		fetchSymbols(metavars, scRulesBlock, repository, monitor);
		for (IIdentifierSymbolInfo symbolInfo : identifierSymbolTable
				.getSymbolInfosFromTop()) {
			if (symbolInfo.getSymbolType() == ISCMetavariable.ELEMENT_TYPE
					&& symbolInfo.isPersistent()) {
				Type type = symbolInfo.getType();
				if (type == null) { // identifier could not be typed
					symbolInfo.createUntypedErrorMarker(this);
					symbolInfo.setError();
				} else if (!symbolInfo.hasError()) {
					if (target != null) {
						symbolInfo.createSCElement(target, null);
					}

				}
			}
		}
	}

	@Override
	public IModuleType<?> getModuleType() {
		// TODO Auto-generated method stub
		return MODULE_TYPE;
	}

	protected void typeIdentifierSymbol(IIdentifierSymbolInfo newSymbolInfo,
			final ITypeEnvironment environment) throws CoreException {
		environment.addName(newSymbolInfo.getSymbol(), newSymbolInfo.getType());
	}
	
	
	@Override
	protected IIdentifierSymbolInfo createIdentifierSymbolInfo(String name,
			IIdentifierElement element) {
		// TODO Auto-generated method stub
		return TheorySymbolFactory.getInstance().makeLocalMetavariable(
				name,
				true,
				element,
				element.getAncestor(ITheoryRoot.ELEMENT_TYPE)
						.getComponentName());
	}

}
