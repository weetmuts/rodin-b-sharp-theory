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

import java.util.Arrays;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IEventBRoot;
import org.eventb.core.ILanguage;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.extension.IFormulaExtensionProvider;
import org.eventb.theory.core.ISCTheoryPathRoot;
import org.eventb.theory.core.ITheoryPathRoot;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.internal.core.util.CoreUtilities;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

/**
 * 
 * @author maamria
 *
 */
public class TheoryFormulaExtensionProvider implements IFormulaExtensionProvider {

	private static final String PROVIDER_ID = TheoryPlugin.PLUGIN_ID
		+ ".theoryExtensionsProvider";
	
	@Override
	public String getId() {
		return PROVIDER_ID;
	}

	@Override
	public Set<IFormulaExtension> getFormulaExtensions(IEventBRoot root) {
		try {
			return WorkspaceExtensionsManager.getInstance()
					.getFormulaExtensions(root);
			// else ignore paths
		} catch (CoreException e) {
			CoreUtilities
					.log(e, "Error while computing math extensions for "
							+ root.getPath());
		}
		return FormulaExtensionsLoader.EMPTY_EXT;
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
		if (paths.length == 0) {
			return null;
		}
		if (paths.length > 1) {
			log(null,
					"Several theory paths in project " + prj + ": "
							+ Arrays.asList(paths));
			return null;
		}
		return paths[0].getSCTheoryPathRoot();
	}

	@Override
	public Set<IRodinFile> getFactoryFiles(IEventBRoot root) {
		final IRodinProject prj = root.getRodinProject();
		final ISCTheoryPathRoot pathRoot = findSCTheoryPathRoot(prj);
		if (pathRoot == null) {
			return emptySet();
		}
		return singleton(pathRoot.getRodinFile());
	}

	@Override
	public FormulaFactory loadFormulaFactory(ILanguage element,
			IProgressMonitor monitor) throws CoreException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveFormulaFactory(ILanguage element, FormulaFactory factory,
			IProgressMonitor monitor) throws RodinDBException {
		// TODO Auto-generated method stub
		
	}

}
