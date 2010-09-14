/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.internal.core.maths;

import org.eventb.core.ast.ExtendedPredicate;
import org.eventb.core.ast.extension.ITypeCheckMediator;

/**
 * Common protocol for a predicate type checker.
 * 
 * @see IExpressionTypeChecker
 * 
 * @author maamria
 *
 */
public interface IPredicateTypeChecker {

	/**
	 * Type checks the extended predicate.
	 * 
	 * @param predicate the predicate to type check
	 * @param tcMediator the type check mediator
	 * 
	 */
	public void typeCheck(ExtendedPredicate predicate, ITypeCheckMediator tcMediator);
}
