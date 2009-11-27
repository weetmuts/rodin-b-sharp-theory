/*******************************************************************************
 * Copyright (c) 2006-2008 ETH Zurich, 2008 University of Southampton
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package ac.soton.eventb.ruleBase.theory.core.sc.symbolTable;

import java.util.Hashtable;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.runtime.CoreException;
import org.eventb.internal.core.Util;
import org.eventb.internal.core.tool.state.State;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;

/**
 * @author Stefan Hallerstede
 * 
 */
@SuppressWarnings("restriction")
public abstract class SymbolTable<E extends IInternalElement, T extends IInternalElementType<? extends E>, I extends ISymbolInfo<E, T>>
		extends State implements ISymbolTable<E, T, I> {

	private final Hashtable<String, I> table;

	// the tableValues variable is a cache that holds the value of
	// table.values()
	protected final Set<I> tableValues;

	public SymbolTable(int size) {
		table = new Hashtable<String, I>(size);
		tableValues = new TreeSet<I>();
	}

	public boolean containsKey(String symbol) {
		return table.containsKey(symbol);
	}

	public I getSymbolInfo(String symbol) {
		return table.get(symbol);
	}

	protected void throwSymbolConflict() throws CoreException {
		throw Util
				.newCoreException("Attempt to insert symbol into symbol table more than once");
	}

	public void putSymbolInfo(I symbolInfo) throws CoreException {

		String key = symbolInfo.getSymbol();

		I ocell = table.put(key, symbolInfo);
		if (ocell != null) {
			// revert to old symbol table and throw exception
			table.put(key, ocell);
			throwSymbolConflict();
		}
		tableValues.add(symbolInfo);
	}

	@Override
	public void makeImmutable() {
		for (I info : tableValues) {
			info.makeImmutable();
		}
		super.makeImmutable();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return table.toString();
	}

}
