/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.maths.extensions;

import java.util.LinkedHashSet;
import java.util.Set;

import org.eventb.core.IEventBRoot;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.extension.IFormulaExtensionProvider;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.rodinp.core.IRodinFile;

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
		// nothing to supply
		return new LinkedHashSet<IRodinFile>();
	}

	@Override
	public Set<IRodinFile> getProjectFiles(IEventBRoot root) {
		// nothing to supply
		return new LinkedHashSet<IRodinFile>();
	}

}
