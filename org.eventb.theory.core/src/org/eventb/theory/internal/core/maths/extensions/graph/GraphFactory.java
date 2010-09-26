/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.internal.core.maths.extensions.graph;

import java.util.SortedSet;

import org.eclipse.core.runtime.CoreException;
import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.core.IFormulaExtensionsSource;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.TheoryCoreFacade;

/**
 * @author maamria
 * 
 */
public class GraphFactory {

	private static GraphFactory factory;

	private GraphFactory() {

	}

	public <E extends IFormulaExtensionsSource<E>> ITheoryGraph<E> getGraph(
			E[] sources) throws CoreException {
		ITheoryGraph<E> graph = new TheoryGraph<E>() {

			@Override
			protected Node<E> getNode(E source, SortedSet<Node<E>> connected) {
				return getTheoryNode(source, connected);

			}
		};
		for (E source : sources) {
			graph.addVertex(source);
		}

		return graph;
	}

	public <E extends IFormulaExtensionsSource<E>> Node<E> getTheoryNode(E source,
			SortedSet<Node<E>> connected) {
		if (source instanceof IDeployedTheoryRoot) {
			return new Node<E>(source, connected) {

				@Override
				protected int compare(E thisTheory, E otherTheory) {
					IDeployedTheoryRoot tTheory = (IDeployedTheoryRoot) thisTheory;
					IDeployedTheoryRoot oTheory = (IDeployedTheoryRoot) otherTheory;
					try {
						if (TheoryCoreFacade.doesTheoryUseTheory(tTheory,
								oTheory)) {
							return 1;
						} else if (TheoryCoreFacade.doesTheoryUseTheory(
								oTheory, tTheory)) {
							return -1;
						}
					} catch (CoreException e) {
						e.printStackTrace();
					}
					return 0;
				}
			};
		} else {
			return new Node<E>(source, connected) {

				@Override
				protected int compare(E thisTheory, E otherTheory) {
					ISCTheoryRoot tTheory = (ISCTheoryRoot) thisTheory;
					ISCTheoryRoot oTheory = (ISCTheoryRoot) otherTheory;
					try {
						if (TheoryCoreFacade.doesSCTheoryImportSCTheory(
								tTheory, oTheory)) {
							return 1;
						} else if (TheoryCoreFacade.doesSCTheoryImportSCTheory(
								oTheory, tTheory)) {
							return -1;
						}
					} catch (CoreException e) {
						e.printStackTrace();
					}
					return 0;
				}

			};
		}
	}

	public static GraphFactory getFactory() {
		if (factory == null) {
			factory = new GraphFactory();
		}
		return factory;
	}

}
