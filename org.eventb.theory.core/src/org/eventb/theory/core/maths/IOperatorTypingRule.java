/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.maths;

import java.util.List;

import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.extension.IExtendedFormula;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.ast.extension.IWDMediator;

/**
 * @author maamria
 *
 */
public interface IOperatorTypingRule<E extends IFormulaExtension> {
	
	public void addOperatorArgument(OperatorArgument arg);
	
	public int getArity();
	
	public void addTypeParameters(List<GivenType> types);
	
	public void setWDPredicate(Predicate wdPredicate);
	
	public Predicate getWDPredicate(IExtendedFormula formula, IWDMediator wdMediator);

}
