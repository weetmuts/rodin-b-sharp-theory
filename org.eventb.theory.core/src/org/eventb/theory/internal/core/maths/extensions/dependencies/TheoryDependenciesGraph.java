/*******************************************************************************
 * Copyright (c) 2010-11 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.internal.core.maths.extensions.dependencies;

import java.util.Comparator;

import org.eclipse.core.runtime.CoreException;
import org.eventb.theory.core.DB_TCFacade;
import org.eventb.theory.core.IDeployedTheoryRoot;

/**
 * @author maamria
 *
 */
public class TheoryDependenciesGraph extends DependenciesGraph<IDeployedTheoryRoot>{

	@Override
	protected Comparator<Node<IDeployedTheoryRoot>> getPartialOrder() {
		return new Comparator<Node<IDeployedTheoryRoot>>() {
			@Override
			public int compare(Node<IDeployedTheoryRoot> node1, Node<IDeployedTheoryRoot> node2) {
				IDeployedTheoryRoot e1 = node1.element;
				IDeployedTheoryRoot e2 = node2.element;
				return TheoryDependenciesGraph.this.compare(e1, e2);
			}
		};
	}
	
	protected int compare(IDeployedTheoryRoot o1, IDeployedTheoryRoot o2) {
		try {
			if(DB_TCFacade.doesTheoryUseTheory(o1, o2)){
				return 1;
			}
			if(DB_TCFacade.doesTheoryUseTheory(o2, o1)){
				return -1;
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
		if(o1.getComponentName().equals(o2.getComponentName())){
			return 0;
		}
		return 1;
	}

	@Override
	protected IDeployedTheoryRoot[] getEdgesOut(IDeployedTheoryRoot element) {
		try {
			return element.getRelatedSources();
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return new IDeployedTheoryRoot[0];
	}

}
