/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.maths.extensions.dependencies;

import java.util.Comparator;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eventb.theory.core.DatabaseUtilities;
import org.eventb.theory.core.IDeployedTheoryRoot;
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
				try {
					if (DatabaseUtilities.doesTheoryUseTheory(root1, root2)){
						return 1;
					}
					else if(DatabaseUtilities.doesTheoryUseTheory(root2, root1)){
						return -1;
					}
					else if(root1.getComponentName().equals(root2.getComponentName())){
						return 0;
					}
				} catch (CoreException e) {
					e.printStackTrace();
				}
				return 1;
			}
			
		};
	}

	@Override
	protected IDeployedTheoryRoot[] getEdgesOut(IDeployedTheoryRoot element) {
		// TODO Auto-generated method stub
		List<IDeployedTheoryRoot> imported;
		try {
			imported = DatabaseUtilities.getUsedTheories(element);
			return imported.toArray(new IDeployedTheoryRoot[imported.size()]);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			CoreUtilities.log(e, "Error getting imported theories of " +element.getComponentName());
		}
		return new IDeployedTheoryRoot[0];
	}

}
