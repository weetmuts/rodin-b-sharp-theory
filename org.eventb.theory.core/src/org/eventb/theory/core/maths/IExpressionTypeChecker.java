/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.maths;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.ITypeCheckMediator;
import org.eventb.core.ast.extension.ITypeMediator;

/**
 * @author maamria
 *
 */
public interface IExpressionTypeChecker {

	public boolean verifyType(Type proposedType, Expression[] childExprs,
			Predicate[] childPreds);
	
	public Type typeCheck(ExtendedExpression expression,
			ITypeCheckMediator tcMediator);
	
	public abstract Type synthesizeType(Expression[] childExprs, Predicate[] childPreds,
			ITypeMediator mediator);
	
}
