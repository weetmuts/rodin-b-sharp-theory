/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.internal.core.sc;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Type;
import org.eventb.core.sc.GraphProblem;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.SCFilterModule;
import org.eventb.core.sc.state.IIdentifierSymbolInfo;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.internal.core.sc.symbolTable.IdentifierSymbolTable;
import org.eventb.theory.core.IMetavariable;
import org.eventb.theory.core.TheoryAttributes;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.sc.TheoryGraphProblem;
import org.eventb.theory.internal.core.util.CoreUtilities;
import org.eventb.theory.internal.core.util.MathExtensionsUtilities;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author maamria
 * 
 */
@SuppressWarnings("restriction")
public class MetavariableFilterModule extends SCFilterModule {

	IModuleType<MetavariableFilterModule> MODULE_TYPE = SCCore
			.getModuleType(TheoryPlugin.PLUGIN_ID
					+ ".metavariableFilterModule");

	private IdentifierSymbolTable identifierSymbolTable;
	private FormulaFactory factory;
	private ITypeEnvironment typeEnvironment;

	@Override
	public boolean accept(IRodinElement element, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		IMetavariable var = (IMetavariable) element;
		String name = var.getIdentifierString();

		IIdentifierSymbolInfo symbolInfo = identifierSymbolTable
				.getSymbolInfo(name);

		if (!var.hasType() || var.getType().equals("")) {
			createProblemMarker(var, TheoryAttributes.TYPE_ATTRIBUTE,
					TheoryGraphProblem.TypeAttrMissingForOpArgError, name);
			return false;
		}
		Type type = CoreUtilities.parseTypeExpression(var, factory, this);
		if (type == null) {
			return false;
		}
		if (!checkTypeParameters(type, var)) {
			return false;
		}
		symbolInfo.setType(type);
		return true;
	}

	/**
	 * @param type
	 * @param name
	 * @param opArg
	 * @param typeEnvironment
	 */
	private boolean checkTypeParameters(Type type, IMetavariable var)
			throws RodinDBException {
		FreeIdentifier[] idents = type.toExpression(factory)
				.getSyntacticallyFreeIdentifiers();
		List<String> givenSets = MathExtensionsUtilities
				.getGivenSetsNames(typeEnvironment);
		for (FreeIdentifier ident : idents) {

			if (!typeEnvironment.contains(ident.getName())) {
				createProblemMarker(var, TheoryAttributes.TYPE_ATTRIBUTE,
						GraphProblem.UndeclaredFreeIdentifierError,
						ident.getName());
				return false;
			} else if (!givenSets.contains(ident.getName())) {
				createProblemMarker(var,

				TheoryAttributes.TYPE_ATTRIBUTE,
						TheoryGraphProblem.IdentIsNotTypeParError,
						ident.getName());
				return false;
			}

		}
		return true;

	}

	@Override
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}
	
	@Override
	public void initModule(ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		super.initModule(repository, monitor);
		identifierSymbolTable = (IdentifierSymbolTable) repository
				.getState(IdentifierSymbolTable.STATE_TYPE);
		factory = repository.getFormulaFactory();
		typeEnvironment = repository.getTypeEnvironment();
	}

	@Override
	public void endModule(ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		identifierSymbolTable = null;
		factory = null;
		typeEnvironment = null;
		super.endModule(repository, monitor);
	}
}
