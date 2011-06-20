/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.pm;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.core.pm.basis.ExpressionMatcher;

/**
 * 
 * @author maamria
 *
 */
public abstract class ExtendedExpressionMatcher<E extends IExpressionExtension> extends ExpressionMatcher<ExtendedExpression>{

	private Class<E> extensionClass;
	
	public ExtendedExpressionMatcher(Class<E> extensionClass) {
		super(ExtendedExpression.class);
		this.extensionClass = extensionClass;
	}

	@Override
	protected ExtendedExpression getExpression(Expression e) {
		return (ExtendedExpression) e;
	}
	
	public Class<E> getExtensionClass(){
		return extensionClass;
	}

}
