/*******************************************************************************
 * Copyright (c) 2011, 2013 University of Southampton and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Soton - initial API and implementation
 *     Systerel - dependency on checked theory path
 *******************************************************************************/
package org.eventb.theory.core.maths.extensions;

import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static org.eventb.theory.internal.core.util.CoreUtilities.log;

import java.util.Set;

import org.eventb.core.IEventBRoot;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.extension.IFormulaExtensionProvider;
import org.eventb.theory.core.ISCTheoryPathRoot;
import org.eventb.theory.core.ITheoryPathRoot;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

/**
 * 
 * @author maamria
 *
 */
public class TheoryFormulaExtensionProvider implements IFormulaExtensionProvider {

	private final String PROVIDER_ID = TheoryPlugin.PLUGIN_ID
		+ ".theoryExtensionsProvider";
	
	private WorkspaceExtensionsManager manager;
	
	public TheoryFormulaExtensionProvider(){
		manager = new WorkspaceExtensionsManager();
	}
	
	@Override
	public String getId() {
		return PROVIDER_ID;
	}

	@Override
	public Set<IFormulaExtension> getFormulaExtensions(IEventBRoot root) {
		return manager.getFormulaExtensions(root);
	}

	@Override
	public void setFormulaFactory(IEventBRoot root, FormulaFactory ff) {
		// nothing to do
	}

	@Override
	public Set<IRodinFile> getCommonFiles(IEventBRoot root) {
		return emptySet();
	}

	@Override
	public Set<IRodinFile> getProjectFiles(IEventBRoot root) {
		final IRodinProject prj = root.getRodinProject();
		final ISCTheoryPathRoot pathRoot = findSCTheoryPathRoot(prj);
		if (pathRoot == null) {
			return emptySet();
		}
		return singleton(pathRoot.getRodinFile());
	}

	/**
	 * Returns the checked theory path of the given project. We do not search
	 * directly the checked file, as it might not exist yet (e.g., just after a
	 * project clean), but rather the unchecked file that we convert eventually.
	 * 
	 * @param prj
	 *            some Rodin project
	 * @return the checked theory path of the given project
	 */
	private ISCTheoryPathRoot findSCTheoryPathRoot(IRodinProject prj) {
		final ITheoryPathRoot[] paths;
		try {
			paths = prj.getRootElementsOfType(ITheoryPathRoot.ELEMENT_TYPE);
		} catch (RodinDBException e) {
			log(e, "error while getting theory path for project " + prj);
			return null;
		}
		if (paths.length != 1) {
			log(null, "Several theory paths in project " + prj);
			return null;
		}
		return paths[0].getSCTheoryPathRoot();
	}

}
