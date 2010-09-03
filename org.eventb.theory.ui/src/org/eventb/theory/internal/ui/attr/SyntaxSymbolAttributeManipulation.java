/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.internal.ui.attr;

import static org.eventb.theory.core.TheoryAttributes.SYNTAX_SYMBOL_ATTRIBUTE;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.internal.ui.eventbeditor.manipulation.AbstractAttributeManipulation;
import org.eventb.theory.core.ISyntaxSymbolElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author maamria
 *
 */
public class SyntaxSymbolAttributeManipulation extends AbstractAttributeManipulation{

		
		public String[] getPossibleValues(IRodinElement element,
				IProgressMonitor monitor) {
			logCantGetPossibleValues(SYNTAX_SYMBOL_ATTRIBUTE);
			return null;
		}

		
		public String getValue(IRodinElement element, IProgressMonitor monitor)
				throws RodinDBException {
			return asSyntaxSymbolElement(element).getSyntaxSymbol();
		}

		
		public boolean hasValue(IRodinElement element, IProgressMonitor monitor)
				throws RodinDBException {
			return asSyntaxSymbolElement(element).hasSyntaxSymbol();
		}

		
		public void removeAttribute(IRodinElement element, IProgressMonitor monitor)
				throws RodinDBException {
			logCantRemove(SYNTAX_SYMBOL_ATTRIBUTE);

		}

		
		public void setDefaultValue(IRodinElement element, IProgressMonitor monitor)
				throws RodinDBException {
			asSyntaxSymbolElement(element).setSyntaxSymbol("ChangeMe", monitor);

		}

		
		public void setValue(IRodinElement element, String value,
				IProgressMonitor monitor) throws RodinDBException {
			asSyntaxSymbolElement(element).setSyntaxSymbol(value, monitor);

		}

		private ISyntaxSymbolElement asSyntaxSymbolElement(IRodinElement element){
			assert element instanceof ISyntaxSymbolElement;
			return (ISyntaxSymbolElement) element;
		}
	
}
