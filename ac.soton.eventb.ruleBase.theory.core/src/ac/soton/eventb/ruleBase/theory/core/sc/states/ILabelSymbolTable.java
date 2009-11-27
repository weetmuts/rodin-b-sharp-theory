/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package ac.soton.eventb.ruleBase.theory.core.sc.states;

import org.eventb.core.ILabeledElement;
import org.eventb.core.sc.state.ISCState;
import org.rodinp.core.IInternalElementType;

import ac.soton.eventb.ruleBase.theory.core.sc.symbolTable.ISymbolTable;

/**
 * Common protocol for symbol tables of labeled elements.
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @see IContextLabelSymbolTable
 * @see IMachineLabelSymbolTable
 * @see IEventLabelSymbolTable
 * 
 * @author Stefan Hallerstede
 * 
 */
public interface ILabelSymbolTable
		extends
		ISymbolTable<ILabeledElement, IInternalElementType<? extends ILabeledElement>, ILabelSymbolInfo>,
		ISCState {

	// marker class for labeled element symbols
}
