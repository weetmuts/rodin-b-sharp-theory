/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.maths.extensions;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eventb.core.IEventBRoot;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.extension.IFormulaExtensionProvider;
import org.eventb.theory.core.ISCTheoryPathRoot;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.internal.core.util.CoreUtilities;
import org.rodinp.core.IRodinFile;
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
		try {
			ISCTheoryPathRoot[] paths = root.getRodinProject().getRootElementsOfType(ISCTheoryPathRoot.ELEMENT_TYPE);
			if (paths.length == 1){
				return Collections.singleton(paths[0].getRodinFile());
			}
		} catch (RodinDBException e) {
			CoreUtilities.log(e, "error while getting theory path for project " + root.getRodinProject());
		}
		return new LinkedHashSet<IRodinFile>();
	}

	@Override
	public Set<IRodinFile> getProjectFiles(IEventBRoot root) {
		try {
			ISCTheoryPathRoot[] paths = root.getRodinProject().getRootElementsOfType(ISCTheoryPathRoot.ELEMENT_TYPE);
			if (paths.length == 1){
				return Collections.singleton(paths[0].getRodinFile());
			}
		} catch (RodinDBException e) {
			CoreUtilities.log(e, "error while getting theory path for project " + root.getRodinProject());
		}
		return new LinkedHashSet<IRodinFile>();
	}

}
