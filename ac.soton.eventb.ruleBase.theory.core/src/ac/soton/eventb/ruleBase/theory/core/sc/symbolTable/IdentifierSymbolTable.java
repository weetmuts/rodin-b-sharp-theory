/*******************************************************************************
 * Copyright (c) 2006-2008 ETH Zurich, 2008 University of Southampton
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package ac.soton.eventb.ruleBase.theory.core.sc.symbolTable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.ISCIdentifierElement;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.tool.IStateType;
import org.rodinp.core.IInternalElementType;

import ac.soton.eventb.ruleBase.theory.core.sc.states.IIdentifierSymbolInfo;
import ac.soton.eventb.ruleBase.theory.core.sc.states.IIdentifierSymbolTable;

/**
 * @author Stefan Hallerstede
 * 
 */
public class IdentifierSymbolTable
		extends
		SymbolTable<ISCIdentifierElement, IInternalElementType<? extends ISCIdentifierElement>, IIdentifierSymbolInfo>
		implements IIdentifierSymbolTable {

	private final Set<FreeIdentifier> freeIdentifiers;

	public IdentifierSymbolTable(int identSize) {
		super(identSize);
		freeIdentifiers = new HashSet<FreeIdentifier>(identSize);
	}

	public IStateType<?> getStateType() {
		return STATE_TYPE;
	}

	public Collection<FreeIdentifier> getFreeIdentifiers() {
		return freeIdentifiers;
	}

	public IIdentifierSymbolTable getParentTable() {
		return null;
	}

	@Override
	public void putSymbolInfo(IIdentifierSymbolInfo symbolInfo)
			throws CoreException {
		super.putSymbolInfo(symbolInfo);
		freeIdentifiers.add(FormulaFactory.getDefault().makeFreeIdentifier(
				symbolInfo.getSymbol(), null));
	}

	public IIdentifierSymbolInfo getSymbolInfoFromTop(String symbol) {
		return getSymbolInfo(symbol);
	}

	public Collection<IIdentifierSymbolInfo> getSymbolInfosFromTop() {
		return Collections.unmodifiableSet(tableValues);
	}

}
