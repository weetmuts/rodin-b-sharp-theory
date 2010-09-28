/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.internal.core.maths.extensions.graph;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eventb.theory.core.IFormulaExtensionsSource;

/**
 * @author maamria
 *
 */
public class TheoryGraphFactory {

	private static TheoryGraphFactory factory;

	private TheoryGraphFactory() {

	}

	public <E extends IFormulaExtensionsSource<E>> TheoryDependenciesGraph<E> getGraph(
			E[] sources) throws CoreException {
		return getGraph(Arrays.asList(sources));
	}

	public <E extends IFormulaExtensionsSource<E>> TheoryDependenciesGraph<E> getGraph(
			List<E> sources) throws CoreException {
		TheoryDependenciesGraph<E> graph = new TheoryDependenciesGraph<E>();
		for (E source : sources) {
			graph.addVertex(source);
		}
		return graph;
	}

	public static TheoryGraphFactory getFactory() {
		if (factory == null) {
			factory = new TheoryGraphFactory();
		}
		return factory;
	}

}
