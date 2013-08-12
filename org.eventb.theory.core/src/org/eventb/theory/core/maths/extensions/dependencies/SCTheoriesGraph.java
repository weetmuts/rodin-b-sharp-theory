/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.maths.extensions.dependencies;

import static org.eventb.theory.core.TheoryHierarchyHelper.getImportedTheories;

import java.util.Comparator;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.TheoryHierarchyHelper;
import org.eventb.theory.internal.core.util.CoreUtilities;

public class SCTheoriesGraph extends DependenciesGraph<ISCTheoryRoot>{

	@Override
	protected Comparator<DependencyNode<ISCTheoryRoot>> getPartialOrder() {
		return new Comparator<DependencyNode<ISCTheoryRoot>>(){

			@Override
			public int compare(DependencyNode<ISCTheoryRoot> node1,
					DependencyNode<ISCTheoryRoot> node2) {
				ISCTheoryRoot root1 = node1.element;
				ISCTheoryRoot root2 = node2.element;
				return TheoryHierarchyHelper.getSCTheoryDependencyComparator().compare(root1, root2);
			}
			
		};
	}

	@Override
	protected ISCTheoryRoot[] getEdgesOut(ISCTheoryRoot element) {
		
		try {
			if (element instanceof IDeployedTheoryRoot) {
				final Set<IDeployedTheoryRoot> imported = getImportedTheories((IDeployedTheoryRoot) element);
				if (imported != null) {
					return imported.toArray(new ISCTheoryRoot[imported.size()]);
				}
			}
			else {
				final Set<IDeployedTheoryRoot> imported = getImportedTheories(element);
				return imported.toArray(new ISCTheoryRoot[imported.size()]);
			}
				
			
			
		} catch (CoreException e) {
			CoreUtilities.log(e, "Error getting imported theories of " +element.getComponentName());
		}
		return new ISCTheoryRoot[0];
	}

}
