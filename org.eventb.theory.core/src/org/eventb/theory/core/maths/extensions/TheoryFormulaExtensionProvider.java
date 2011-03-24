/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.maths.extensions;

import java.util.Set;

import org.eventb.core.IEventBRoot;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.extension.IFormulaExtensionProvider;
import org.rodinp.core.IRodinFile;

public class TheoryFormulaExtensionProvider implements
		IFormulaExtensionProvider {

	public TheoryFormulaExtensionProvider() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<IFormulaExtension> getFormulaExtensions(IEventBRoot root) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setFormulaFactory(IEventBRoot root, FormulaFactory ff) {
		// TODO Auto-generated method stub

	}

	@Override
	public Set<IRodinFile> getCommonFiles(IEventBRoot root) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<IRodinFile> getProjectFiles(IEventBRoot root) {
		// TODO Auto-generated method stub
		return null;
	}

}
