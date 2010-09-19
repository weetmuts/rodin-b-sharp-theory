/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.maths.extensions;

import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IEventBRoot;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.rodinp.core.IElementChangedListener;
import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.core.IFormulaExtensionsSource;
import org.eventb.theory.core.ISCTheoryRoot;

/**
 * @author maamria
 *
 */
public interface IProjectDependenciesManager extends IElementChangedListener{

	public Set<IFormulaExtension> getFormulaExtensions(IEventBRoot root) throws CoreException;
	
	public void setFormulaFactory(IEventBRoot root) throws CoreException;
	
	public void addDeployedTheoryEntry(IDeployedTheoryRoot theoryRoot) throws CoreException;
	
	public void addSCTheoryEntry(ISCTheoryRoot theoryRoot) throws CoreException;
	
	public void deployedTheoryChanged(IDeployedTheoryRoot theoryRoot) throws CoreException;
	
	public void scTheoryChanged(ISCTheoryRoot theoryRoot) throws CoreException;
	
	public void addGeneratedFileEntry(IEventBRoot eventBRoot) throws CoreException;
	
	public void addExtensionSource(IEventBRoot generatedRoot, IFormulaExtensionsSource source) throws CoreException;
	
	
	
}
