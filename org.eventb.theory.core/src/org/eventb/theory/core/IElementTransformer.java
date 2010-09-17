/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;

/**
 * Common interface for an internal element transformer.
 * 
 * @author maamria
 *
 *	@param E the type of the element
 *	@param R the result of the transformation
 */
public interface IElementTransformer<E extends IInternalElement, R> {

	/**
	 * Returns the type of the element being transformed.
	 * @return type of the transformed element
	 * @throws CoreException
	 */
	public IInternalElementType<E> getElementType() throws CoreException;
	
	/**
	 * Returns the result of transformation of the given element.
	 * @param element the internal element
	 * @param factory the formula factory
	 * @param typeEnvironment the type environment
	 * @return reasult of transformation or <code>null</code>
	 * @throws CoreException
	 */
	public R transform(E element, final FormulaFactory factory, ITypeEnvironment typeEnvironment) throws CoreException;
	
}
