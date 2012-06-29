/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.ast.maths;

import org.eventb.core.ast.extension.IFormulaExtension;

/**
 * Common protocol for an operator extension. Additional methods are added for convenience when
 * checking the operator properties.
 * 
 * @since 1.0
 * 
 * @author maamria
 *
 */

public interface IOperatorExtension extends IFormulaExtension{
	
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
}
