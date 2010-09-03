/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.internal.core.sc.states;

import org.eventb.core.ILabeledElement;
import org.eventb.core.sc.state.ILabelSymbolInfo;
import org.eventb.core.sc.state.ISCState;
import org.eventb.internal.core.sc.symbolTable.SymbolTable;
import org.rodinp.core.IInternalElementType;

/**
 * @author maamria
 *
 */
@SuppressWarnings("restriction")
public abstract class AbstractTheoryLabelSymbolTable extends
	SymbolTable<ILabeledElement, IInternalElementType<? extends ILabeledElement>, ILabelSymbolInfo>
	implements
	ISCState{

	/**
	 * @param size
	 */
	public AbstractTheoryLabelSymbolTable(int size) {
		super(size);
	}

}
