/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.basis;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.basis.SCExpressionElement;
import org.eventb.theory.core.ISCRecursiveDefinitionCase;
import org.eventb.theory.core.TheoryAttributes;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * 
 * @author maamria
 *
 */
public class SCRecursiveDefinitionCase extends SCExpressionElement implements
		ISCRecursiveDefinitionCase {

	public SCRecursiveDefinitionCase(String name, IRodinElement parent) {
		super(name, parent);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean hasSCFormula() throws RodinDBException {
		return hasAttribute(TheoryAttributes.FORMULA_ATTRIBUTE);
	}

	// TODO check if necessary to make this method raise an exception instead of returning null when failed
	@Override
	public Formula<?> getSCFormula(FormulaFactory ff,
			ITypeEnvironment typeEnvironment) throws RodinDBException {
		String form = getAttributeValue(TheoryAttributes.FORMULA_ATTRIBUTE);
		Formula<?> formula = TheoryElement.parseFormula(form, ff, false);
		if (formula == null) {
			return null;
		}
		ITypeCheckResult result = formula.typeCheck(typeEnvironment);
		if (result.hasProblem()) {
			return null;
		}
		return formula;
	}

	@Override
	public void setSCFormula(Formula<?> formula, IProgressMonitor monitor)
			throws RodinDBException {
		setAttributeValue(TheoryAttributes.FORMULA_ATTRIBUTE ,formula.toStringWithTypes(), monitor);
	}

	@Override
	public IInternalElementType<? extends IInternalElement> getElementType() {
		// TODO Auto-generated method stub
		return ELEMENT_TYPE;
	}

	

}