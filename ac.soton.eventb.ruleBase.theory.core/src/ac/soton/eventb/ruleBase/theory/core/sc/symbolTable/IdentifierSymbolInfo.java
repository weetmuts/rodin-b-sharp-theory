/*******************************************************************************
 * Copyright (c) 2006, 2009 ETH Zurich and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Soton - redesign of symbol table
 *     Systerel - separation of file and root element
 *******************************************************************************/
package ac.soton.eventb.ruleBase.theory.core.sc.symbolTable;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.ISCIdentifierElement;
import org.eventb.core.ast.Type;
import org.eventb.core.sc.IMarkerDisplay;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;

import ac.soton.eventb.ruleBase.theory.core.sc.states.IIdentifierSymbolInfo;

/**
 * @author Stefan Hallerstede
 * 
 */
class IdentifierSymbolInfo
		extends
		SymbolInfo<ISCIdentifierElement, IInternalElementType<? extends ISCIdentifierElement>, ITypedSymbolProblem>
		implements IIdentifierSymbolInfo {

	@Override
	protected void put(IAttributeType attType, Object value) {
		if (attType == EventBAttributes.IDENTIFIER_ATTRIBUTE
				|| attType == EventBAttributes.TYPE_ATTRIBUTE)
			throw new IllegalArgumentException("attribute cannot be set");
		super.put(attType, value);
	}

	public IdentifierSymbolInfo(String symbol,
			IInternalElementType<? extends ISCIdentifierElement> elementType,
			boolean persistent, IInternalElement problemElement,
			IAttributeType problemAttributeType, String component,
			ITypedSymbolProblem conflictProblem) {
		super(symbol, elementType, persistent, problemElement,
				problemAttributeType, component, conflictProblem);
	}

	private Type type;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.sc.IIdentifierSymbolInfo#getType()
	 */
	public final Type getType() {
		return type;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eventb.core.sc.IIdentifierSymbolInfo#setType(org.eventb.core.ast.
	 * Type)
	 */
	public final void setType(Type type) throws CoreException {
		assertMutable();
		this.type = type;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eventb.core.sc.IIdentifierSymbolInfo#issueUntypedErrorMarker(org.
	 * eventb.core.sc.IMarkerDisplay)
	 */
	public final void createUntypedErrorMarker(IMarkerDisplay markerDisplay)
			throws CoreException {

		markerDisplay.createProblemMarker(getProblemElement(),
				getProblemAttributeType(), getConflictProblem()
						.getUntypedError(), getSymbol());

	}

	public ISCIdentifierElement createSCElement(IInternalElement parent,
			IProgressMonitor monitor) throws CoreException {
		checkPersistence();
		ISCIdentifierElement element = parent.getInternalElement(
				getSymbolType(), getSymbol());
		element.create(null, monitor);
		createAttributes(element, monitor);
		element.setType(getType(), monitor);
		return element;
	}
}
