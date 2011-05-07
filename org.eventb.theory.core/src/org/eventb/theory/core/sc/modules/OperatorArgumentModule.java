/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.sc.modules;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.IIdentifierElement;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Type;
import org.eventb.core.sc.GraphProblem;
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
import org.eventb.theory.core.TheoryAttributes;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.sc.TheoryGraphProblem;
import org.eventb.theory.core.sc.states.IOperatorInformation;
import org.eventb.theory.core.sc.states.TheorySymbolFactory;
import org.eventb.theory.internal.core.util.CoreUtilities;
import org.eventb.theory.internal.core.util.MathExtensionsUtilities;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;

/**
 * 
 * @author maamria
 * 
 */
@SuppressWarnings("restriction")
public class OperatorArgumentModule extends IdentifierModule {

	private final IModuleType<OperatorArgumentModule> MODULE_TYPE = SCCore
			.getModuleType(TheoryPlugin.PLUGIN_ID + ".operatorArgumentModule");

	/**
	 * TODO this should not be needed. Need to request change to implementation
	 * of symbol tables.
	 */
	private List<IIdentifierSymbolInfo> insertionOrderedSymbols;

	@Override
	public void process(IRodinElement element, IInternalElement target,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		INewOperatorDefinition operatorDefinition = (INewOperatorDefinition) element;
		ISCNewOperatorDefinition scOperatorDefinition = (ISCNewOperatorDefinition) target;
		IOperatorArgument[] arguments = operatorDefinition
				.getOperatorArguments();
		IOperatorInformation operatorInformation = (IOperatorInformation) repository
				.getState(IOperatorInformation.STATE_TYPE);
		insertionOrderedSymbols = new ArrayList<IIdentifierSymbolInfo>();
		fetchSymbols(arguments, target, repository, monitor);
		for (IIdentifierSymbolInfo symbolInfo : insertionOrderedSymbols) {
			if (symbolInfo == null) {
				operatorInformation.setHasError();
			} else if (symbolInfo.getSymbolType() == ISCOperatorArgument.ELEMENT_TYPE
					&& symbolInfo.isPersistent()) {
				Type type = symbolInfo.getType();
				if (type == null) { // identifier could not be typed
					symbolInfo.createUntypedErrorMarker(this);
					symbolInfo.setError();
					operatorInformation.setHasError();
				}
				if (!symbolInfo.hasError()) {
					if (scOperatorDefinition != null) {
						operatorInformation.addOperatorArgument(
								symbolInfo.getSymbol(), type);
						symbolInfo.createSCElement(scOperatorDefinition, null);
					} else {
						operatorInformation.setHasError();
					}
				} else {
					operatorInformation.setHasError();
				}
				symbolInfo.makeImmutable();
			}
		}
	}

	@Override
	public IModuleType<?> getModuleType() {
		// TODO Auto-generated method stub
		return MODULE_TYPE;
	}

	@Override
	protected IIdentifierSymbolInfo createIdentifierSymbolInfo(String name,
			IIdentifierElement element) {
		INewOperatorDefinition opDef = (INewOperatorDefinition) element
				.getParent();
		return TheorySymbolFactory.getInstance().makeLocalOperatorArgument(
				name, true, element,
				opDef.getAncestor(ITheoryRoot.ELEMENT_TYPE).getComponentName());
	}

	// needed order of insertion
	protected void fetchSymbols(IIdentifierElement[] elements,
			IInternalElement target, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
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
			if (!ok || !checkAndType((IOperatorArgument)element, newSymbolInfo))
				continue;
			typeIdentifierSymbol(newSymbolInfo, typeEnvironment);
			insertionOrderedSymbols.add(newSymbolInfo);
			monitor.worked(1);
		}
	}
	
	protected void typeIdentifierSymbol(IIdentifierSymbolInfo newSymbolInfo,
			final ITypeEnvironment environment) throws CoreException {
		environment.addName(newSymbolInfo.getSymbol(), newSymbolInfo.getType());
	}

	/**
	 * Checks and types the identifier present in the given operator argument. 
	 * 
	 * <p> As a side effect, this method sets the type of the given symbol.
	 * @param operatorArgument the operator argument
	 * @param identifierSymbolInfo the symbol info
	 * @return whether the argument has successfully been typed
	 * @throws CoreException
	 */
	protected boolean checkAndType(IOperatorArgument operatorArgument, IIdentifierSymbolInfo identifierSymbolInfo)
			throws CoreException {
		if (!operatorArgument.hasType() || operatorArgument.getType().equals("")) {
			createProblemMarker(operatorArgument, TheoryAttributes.TYPE_ATTRIBUTE,
					TheoryGraphProblem.TypeAttrMissingForOpArgError,
					operatorArgument.getIdentifierString());
			return false;
		}
		Type type = CoreUtilities.parseTypeExpression(operatorArgument, factory, this);
		if (type == null) {
			return false;
		}
		if (!checkTypeParameters(type, operatorArgument)) {
			return false;
		}
		identifierSymbolInfo.setType(type);
		return true;
	}

	/**
	 * Returns whether the given type of the argument uses only the type parameters declared in the enclosing theory.
	 * @param type the type of the argument
	 * @param operatorArgument the operator argument
	 * @return whether the given type of the argument uses only the type parameters
	 * @throws CoreException
	 */
	protected boolean checkTypeParameters(Type type, IOperatorArgument operatorArgument)
			throws CoreException {
		FreeIdentifier[] idents = type.toExpression(factory).getSyntacticallyFreeIdentifiers();
		List<String> givenSets = MathExtensionsUtilities.getGivenSetsNames(typeEnvironment);
		for (FreeIdentifier ident : idents) {
			if (!typeEnvironment.contains(ident.getName())) {
				createProblemMarker(operatorArgument, TheoryAttributes.TYPE_ATTRIBUTE,
						GraphProblem.UndeclaredFreeIdentifierError,
						ident.getName());
				return false;
			} else if (!givenSets.contains(ident.getName())) {
				createProblemMarker(operatorArgument, TheoryAttributes.TYPE_ATTRIBUTE,
						TheoryGraphProblem.IdentIsNotTypeParError,
						ident.getName());
				return false;
			}
		}
		return true;
	}

}
