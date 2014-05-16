/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.sc.modules;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extensions.maths.AstUtilities;
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
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author maamria
 * 
 */
@SuppressWarnings("restriction")
public class MetavariableFilterModule extends SCFilterModule {

	private final IModuleType<MetavariableFilterModule> MODULE_TYPE = SCCore.getModuleType(TheoryPlugin.PLUGIN_ID
					+ ".metavariableFilterModule");

	private IdentifierSymbolTable identifierSymbolTable;

	@Override
	public boolean accept(IRodinElement element, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		IMetavariable var = (IMetavariable) element;
		String name = var.getIdentifierString();

		IIdentifierSymbolInfo symbolInfo = identifierSymbolTable
				.getSymbolInfo(name);

		if (!var.hasType() || var.getType().equals("")) {
			createProblemMarker(var, TheoryAttributes.TYPE_ATTRIBUTE,
					TheoryGraphProblem.TypeAttrMissingError, name);
			symbolInfo.setError();
			return false;
		}
		Type type = CoreUtilities.parseTypeExpression(var, repository.getFormulaFactory(), this);
		if (type == null) {
			symbolInfo.setError();
			return false;
		}
		if (!checkTypeParameters(type, var, repository.getFormulaFactory(), repository.getTypeEnvironment())) {
			symbolInfo.setError();
			return false;
		}
		symbolInfo.setType(type);
		return true;
	}

	/**
	 * Checks that the type of the given metavariable only refers to types defined as type parameters in the type environment.
	 * @param type the type of the metavariable
	 * @param var the metavariable element
	 * @param factory the formula factory
	 * @param typeEnvironment the type environment
	 * @return whether only type parameters are used to construct the type of the metavariable
	 * @throws RodinDBException
	 */
	protected boolean checkTypeParameters(Type type, IMetavariable var, FormulaFactory factory, ITypeEnvironment typeEnvironment)
			throws RodinDBException {
		FreeIdentifier[] idents = type.toExpression()
				.getSyntacticallyFreeIdentifiers();
		List<String> givenSets = AstUtilities
				.getGivenSetsNames(typeEnvironment);
		for (FreeIdentifier ident : idents) {
			// if not declared
			if (!typeEnvironment.contains(ident.getName())) {
				createProblemMarker(var, TheoryAttributes.TYPE_ATTRIBUTE,
						GraphProblem.UndeclaredFreeIdentifierError,
						ident.getName());
				return false;
			} 
			// if declared but not a type par
			if (!givenSets.contains(ident.getName())) {
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
	}

	@Override
	public void endModule(ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		identifierSymbolTable = null;
		super.endModule(repository, monitor);
	}
}
