/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.maths;

import org.eventb.core.ast.Formula;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.ast.extension.IOperatorProperties.Notation;

/**
 * Common protocol for an operator extension. Additional methods are added for convenience when
 * checking the operator properties.
 * 
 * @since 1.0
 * 
 * @param <F> the type of the formula
 * 
 * @author maamria
 *
 */

public interface IOperatorExtension<F extends Formula<F>> extends IFormulaExtension{
	
	/**
	 * Returns whether this operator is associative.
	 * @return operator associativity
	 */
	public boolean isAssociative();
	
	/**
	 * Returns whether this operator is commutative.
	 * @return operator commutativity
	 */
	public boolean isCommutative();
	
	/**
	 * Returns the notation of this operator.
	 * @return the notation
	 */
	public Notation getNotation();
	
}
