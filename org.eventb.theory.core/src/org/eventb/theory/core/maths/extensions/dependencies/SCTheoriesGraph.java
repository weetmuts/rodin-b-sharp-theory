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
import org.eventb.theory.core.DB_TCFacade;
import org.eventb.theory.core.ISCTheoryRoot;
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
				try {
					if (DB_TCFacade.doesTheoryImportTheory(root1, root2)){
						return 1;
					}
					else if(DB_TCFacade.doesTheoryImportTheory(root2, root1)){
						return -1;
					}
					else if(root1.getComponentName().equals(root2.getComponentName())){
						return 0;
					}
				} catch (CoreException e) {
					CoreUtilities.log(e, "Error comparing theories " + 
							root1.getComponentName() + " and " + root2.getComponentName());
				}
				return 1;
			}
			
		};
	}

	@Override
	protected ISCTheoryRoot[] getEdgesOut(ISCTheoryRoot element) {
		// TODO Auto-generated method stub
		List<ISCTheoryRoot> imported;
		try {
			imported = DB_TCFacade.getImportedTheories(element);
			return imported.toArray(new ISCTheoryRoot[imported.size()]);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			CoreUtilities.log(e, "Error getting imported theories of " +element.getComponentName());
		}
		return new ISCTheoryRoot[0];
	}

}
