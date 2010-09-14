/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.internal.core.maths.extensions;

import java.util.Set;

import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.theory.core.IElementTransformer;
import org.eventb.theory.core.maths.MathExtensionsFactory;
import org.rodinp.core.IInternalElement;

/**
 * @author maamria
 *
 */
public abstract class DefinitionTransformer<E extends IInternalElement> implements IElementTransformer<E, Set<IFormulaExtension>>{
	
	protected MathExtensionsFactory extensionsFactory;
	
	protected DefinitionTransformer(){
		extensionsFactory = MathExtensionsFactory.getExtensionsFactory();
	}

}
