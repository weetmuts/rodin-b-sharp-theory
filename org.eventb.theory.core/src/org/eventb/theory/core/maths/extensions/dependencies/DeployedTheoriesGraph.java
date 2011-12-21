/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.maths.extensions.dependencies;

import java.util.Comparator;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.core.TheoryHierarchyHelper;
import org.eventb.theory.internal.core.util.CoreUtilities;

public class DeployedTheoriesGraph extends DependenciesGraph<IDeployedTheoryRoot>{

	@Override
	protected Comparator<DependencyNode<IDeployedTheoryRoot>> getPartialOrder() {
		return new Comparator<DependencyNode<IDeployedTheoryRoot>>(){

			@Override
			public int compare(DependencyNode<IDeployedTheoryRoot> node1,
					DependencyNode<IDeployedTheoryRoot> node2) {
				IDeployedTheoryRoot root1 = node1.element;
				IDeployedTheoryRoot root2 = node2.element;
				return TheoryHierarchyHelper.getDeployedTheoryDependencyComparator().compare(root1, root2);
			}
			
		};
	}

	@Override
	protected IDeployedTheoryRoot[] getEdgesOut(IDeployedTheoryRoot element) {
		Set<IDeployedTheoryRoot> imported;
		try {
			imported = TheoryHierarchyHelper.getImportedTheories(element);
			return imported.toArray(new IDeployedTheoryRoot[imported.size()]);
		} catch (CoreException e) {
			CoreUtilities.log(e, "Error getting imported theories of " +element.getComponentName());
		}
		return new IDeployedTheoryRoot[0];
	}

}
