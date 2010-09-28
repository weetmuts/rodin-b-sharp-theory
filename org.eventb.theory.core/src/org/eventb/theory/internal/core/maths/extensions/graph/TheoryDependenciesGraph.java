/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.internal.core.maths.extensions.graph;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
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
public class TheoryDependenciesGraph<E extends 
IFormulaExtensionsSource<E>> extends DependenciesGraph<E>{

	@Override
	protected Set<E> getEdgesOut(E e) {
		try {
			return new LinkedHashSet<E>(Arrays.asList(e.getRelatedSources()));
		} catch (CoreException e1) {
			e1.printStackTrace();
		}
		return new LinkedHashSet<E>();
	}

	@Override
	protected DependencyNode<E> getNode(E element,
			SortedSet<DependencyNode<E>> connected) {
		return new DependencyNode<E>(element, connected) {

			@Override
			protected int compare(E e1, E e2) {
				if(e1 instanceof ISCTheoryRoot){
					ISCTheoryRoot tTheory = (ISCTheoryRoot) e1;
					ISCTheoryRoot oTheory = (ISCTheoryRoot) e2;
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
				}
				else if(e1 instanceof IDeployedTheoryRoot){
					IDeployedTheoryRoot tTheory = (IDeployedTheoryRoot) e1;
					IDeployedTheoryRoot oTheory = (IDeployedTheoryRoot) e2;
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
				}
				return 0;
			}

			@Override
			protected String toString(E e) {
				return e.getElementName();
			}
		};
	}

	@Override
	protected String getElementName(E element) {
		return element.getElementName();
	}

}
