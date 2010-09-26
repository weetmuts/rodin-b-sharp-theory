/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.internal.core.maths.extensions;

import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.theory.core.maths.MathExtensionsFactory;
import org.rodinp.core.IInternalElement;

/**
 * Common protocol for a definition element transformer.
 * 
 * @author maamria
 *
 */
public abstract class DefinitionTransformer<E extends IInternalElement> {
	
	protected MathExtensionsFactory extensionsFactory;
	
	protected DefinitionTransformer(){
		extensionsFactory = MathExtensionsFactory.getExtensionsFactory();
	}
	
	/**
	 * Returns the set of mathematical extensions contained in the definition element.
	 * @param definition the defintional element
	 * @param factory the formula factory
	 * @param typeEnvironment the type environment
	 * @return the formula extensions
	 * @throws CoreException
	 */
	public abstract Set<IFormulaExtension> transform(E definition,
			final FormulaFactory factory, ITypeEnvironment typeEnvironment) throws CoreException;
	
}
