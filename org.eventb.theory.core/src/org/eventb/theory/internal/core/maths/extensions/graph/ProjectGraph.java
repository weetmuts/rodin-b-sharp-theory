/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.internal.core.maths.extensions.graph;

import org.eclipse.core.runtime.CoreException;
import org.eventb.theory.core.IFormulaExtensionsSource;

/**
 * @author maamria
 *
 */
public class ProjectGraph<E extends IFormulaExtensionsSource<E>> {
	
	E[] sources;
	ITheoryGraph<E> graph;
	
	public ProjectGraph(E[] sources) throws CoreException{
		this.sources = sources;
		this.graph = GraphFactory.getFactory().getGraph(sources);
	}
	
	public ITheoryGraph<E> getGraph(){
		return graph;
	}

}
