/*******************************************************************************
 * Copyright (c) 2006, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *******************************************************************************/
package ac.soton.eventb.ruleBase.theory.core.sc.states;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ILabeledElement;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;

import ac.soton.eventb.ruleBase.theory.core.sc.symbolTable.ISymbolInfo;

/**
 * Common protocol for labeled elements stored in a label symbol table.
 * <p>
 * Clients that need to contribute symbols to a label symbol table,
 * {@link ILabelSymbolTable}, must implement this interface.
 * </p>
 * 
 * @see ILabelSymbolTable
 * @see IMachineLabelSymbolTable
 * @see IEventLabelSymbolTable
 * 
 * @author Stefan Hallerstede
 * 
 */
public interface ILabelSymbolInfo
		extends
		ISymbolInfo<ILabeledElement, IInternalElementType<? extends ILabeledElement>> {

	/**
	 * Create a statically checked element for this symbol with the specified
	 * parent.
	 * 
	 * @param parent
	 *            the parent of the element to create
	 * @param elementName
	 *            the element name to use for the new element
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress reporting
	 *            is not desired
	 * @return the created statically checked identifier element
	 * @throws CoreException
	 *             if there was a problem creating the element
	 */
	ILabeledElement createSCElement(IInternalElement parent, String elementName,
			IProgressMonitor monitor) throws CoreException;

}
