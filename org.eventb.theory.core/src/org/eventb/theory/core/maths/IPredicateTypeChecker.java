/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.maths;

import org.eventb.core.ast.ExtendedPredicate;
import org.eventb.core.ast.extension.ITypeCheckMediator;

/**
 * @author maamria
 *
 */
public interface IPredicateTypeChecker {

	public void typeCheck(ExtendedPredicate predicate, ITypeCheckMediator tcMediator);
}
