/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.maths.extensions;

import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.theory.core.IExtensionElement;

/**
 * @author maamria
 *
 */
public interface IDefinitionTransformer<E extends IExtensionElement> {
	
	Set<IFormulaExtension> transform(E definition, FormulaFactory factory, ITypeEnvironment typeEnvironment) throws CoreException;

}
