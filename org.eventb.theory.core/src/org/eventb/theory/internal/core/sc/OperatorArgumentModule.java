/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.internal.core.sc;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.IIdentifierElement;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Type;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.state.IIdentifierSymbolInfo;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.internal.core.sc.modules.IdentifierModule;
import org.eventb.theory.core.INewOperatorDefinition;
import org.eventb.theory.core.IOperatorArgument;
import org.eventb.theory.core.ISCNewOperatorDefinition;
import org.eventb.theory.core.ISCOperatorArgument;
import org.eventb.theory.core.ITheoryRoot;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.internal.core.sc.states.IOperatorInformation;
import org.eventb.theory.internal.core.sc.states.TheorySymbolFactory;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;

/**
 * @author maamria
 *
 */
@SuppressWarnings("restriction")
public class OperatorArgumentModule extends IdentifierModule {
	
	IModuleType<OperatorArgumentModule> MODULE_TYPE = 
		SCCore.getModuleType(TheoryPlugin.PLUGIN_ID + ".operatorArgumentModule");
	
	private List<IIdentifierSymbolInfo> insertionOrderedSymbols;
	
	@Override
	public void process(IRodinElement element, IInternalElement target,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		INewOperatorDefinition newOpDef = (INewOperatorDefinition) element;
		ISCNewOperatorDefinition scNewOpDef = (ISCNewOperatorDefinition) target;
		IOperatorArgument[] arguments = newOpDef.getOperatorArguments();
		IOperatorInformation operatorInformation = (IOperatorInformation) 
										repository.getState(IOperatorInformation.STATE_TYPE);
		insertionOrderedSymbols = new ArrayList<IIdentifierSymbolInfo>();
		fetchSymbols(arguments, scNewOpDef, repository, monitor);
		for (IIdentifierSymbolInfo symbolInfo : insertionOrderedSymbols) {
			if(symbolInfo == null){
				operatorInformation.setHasError();
			}
			else if (symbolInfo.getSymbolType() == ISCOperatorArgument.ELEMENT_TYPE && 
					symbolInfo.isPersistent()) {
				Type type = symbolInfo.getType();
				if (type == null) { // identifier could not be typed
					symbolInfo.createUntypedErrorMarker(this);
					symbolInfo.setError();
					operatorInformation.setHasError();
				} 
				if (!symbolInfo.hasError()) {
					if(scNewOpDef != null){
						operatorInformation.addOperatorArgument(symbolInfo.getSymbol(), type);
						symbolInfo.createSCElement(scNewOpDef, null);
					}
					else{
						operatorInformation.setHasError();
					}
				}
				else {
					operatorInformation.setHasError();
				}
				symbolInfo.makeImmutable();
			}
		}

	}

	protected void typeIdentifierSymbol(IIdentifierSymbolInfo newSymbolInfo,
			final ITypeEnvironment environment) throws CoreException {
		environment.addName(newSymbolInfo.getSymbol(), newSymbolInfo.getType());
	}
	
	@Override
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	@Override
	protected IIdentifierSymbolInfo createIdentifierSymbolInfo(String name,
			IIdentifierElement element) {
		INewOperatorDefinition opDef = (INewOperatorDefinition) element.getParent();
		return TheorySymbolFactory.getInstance().makeLocalOperatorArgument(name, true,
				element, opDef.getAncestor(ITheoryRoot.ELEMENT_TYPE).getComponentName());
	}
	
	/**
	 * Fetch identifiers from component, parse them and add them to the symbol
	 * table.
	 * 
	 * @param elements
	 *            the identifier elements to fetch
	 * @param target
	 *            the target static checked container
	 * @param repository
	 *            the state repository
	 * @throws CoreException
	 *             if there was a problem accessing the symbol table
	 */
	protected void fetchSymbols(IIdentifierElement[] elements,
			IInternalElement target, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {

		initFilterModules(repository, null);

		for (IIdentifierElement element : elements) {
			FreeIdentifier identifier = parseIdentifier(element, monitor);

			if (identifier == null)
				continue;
			String name = identifier.getName();

			IIdentifierSymbolInfo newSymbolInfo = createIdentifierSymbolInfo(
					name, element);
			newSymbolInfo.setAttributeValue(EventBAttributes.SOURCE_ATTRIBUTE,
					element);

			boolean ok = insertIdentifierSymbol(element, newSymbolInfo);

			if (!ok || !filterModules(element, repository, null))
				continue;

			typeIdentifierSymbol(newSymbolInfo, typeEnvironment);
			insertionOrderedSymbols.add(newSymbolInfo);
			monitor.worked(1);

		}

		endFilterModules(repository, null);
		
	}

}
