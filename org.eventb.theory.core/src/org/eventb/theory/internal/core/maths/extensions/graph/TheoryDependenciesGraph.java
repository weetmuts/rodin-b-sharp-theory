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
import org.eventb.theory.core.DB_TCFacade;
import org.eventb.theory.internal.core.util.CoreUtilities;

/**
 * @author maamria
 * 
 */
public class TheoryDependenciesGraph extends DependenciesGraph<IDeployedTheoryRoot> {

	@Override
	protected Set<IDeployedTheoryRoot> getEdgesOut(IDeployedTheoryRoot e) {
		try {
			return new LinkedHashSet<IDeployedTheoryRoot>(Arrays.asList(e
					.getRelatedSources()));
		} catch (CoreException e1) {
			CoreUtilities.log(e1, "Error getting related sources of theory "+e.getComponentName() +".");
		}
		return new LinkedHashSet<IDeployedTheoryRoot>();
	}

	@Override
	protected DependencyNode<IDeployedTheoryRoot> getNode(
			IDeployedTheoryRoot element,
			SortedSet<DependencyNode<IDeployedTheoryRoot>> connected) {
		return new DependencyNode<IDeployedTheoryRoot>(element, connected) {

			@Override
			protected int compare(IDeployedTheoryRoot e1, IDeployedTheoryRoot e2) {

				try {
					if (DB_TCFacade.doesTheoryUseTheory(e1, e2)) {
						return 1;
					} else if (DB_TCFacade.doesTheoryUseTheory(e2, e1)) {
						return -1;
					}
				} catch (CoreException e) {
					e.printStackTrace();
				}
				if(e1.getComponentName().equals(e2.getComponentName())){
					return 0;
				}

				return 1;
			}

			@Override
			protected String toString(IDeployedTheoryRoot e) {
				return e.getElementName();
			}

		};
	}

	@Override
	protected String getElementName(IDeployedTheoryRoot element) {
		return element.getElementName();
	}
}
