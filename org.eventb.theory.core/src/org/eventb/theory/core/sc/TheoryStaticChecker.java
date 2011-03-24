/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.sc;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.sc.StaticChecker;
import org.eventb.theory.core.ITheoryRoot;
import org.eventb.theory.core.DB_TCFacade;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinCore;
import org.rodinp.core.builder.IGraph;

/**
 * @author maamria
 *
 */
public class TheoryStaticChecker extends StaticChecker {

	
	public void extract(IFile file, IGraph graph, IProgressMonitor monitor)
			throws CoreException {
		try {
			monitor.beginTask(Messages.bind(Messages.build_extracting, file
					.getName()), 1);
			IRodinFile source = RodinCore.valueOf(file);
			if (DB_TCFacade.doesDeployedTheoryExist(source.getRoot().getElementName(), source.getRodinProject())){
				return;
			}
			ITheoryRoot root = (ITheoryRoot) source.getRoot();
			IRodinFile target = root.getSCTheoryRoot().getRodinFile();
			graph.addTarget(target.getResource());
			graph.addToolDependency(source.getResource(), target.getResource(),
					true);

		} finally {
			monitor.done();
		}

	}
}