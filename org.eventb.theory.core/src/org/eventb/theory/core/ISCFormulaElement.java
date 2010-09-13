/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

/**
 * @author maamria
 *
 */
public interface ISCFormulaElement extends IInternalElement{
	
	boolean hasSCFormula() throws RodinDBException;
	
	Formula<?> getSCFormula(FormulaFactory ff, ITypeEnvironment typeEnvironment) throws RodinDBException;
	
	void setSCFormula(Formula<?> formula, IProgressMonitor monitor) throws RodinDBException;

}
