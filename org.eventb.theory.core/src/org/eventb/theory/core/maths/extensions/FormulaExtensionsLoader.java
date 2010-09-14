/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.maths.extensions;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IEventBProject;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.internal.core.ast.extension.Cond;
import org.eventb.theory.core.ISCTheoryRoot;

/**
 * @author maamria
 *
 */
@SuppressWarnings("restriction")
public class FormulaExtensionsLoader {
	
	private IEventBProject project;
	
	public FormulaExtensionsLoader(IEventBProject project){
		this.project = project;
	}
	
	public Set<IFormulaExtension> getFormulaExtensions() throws CoreException{
		ISCTheoryRoot[] theoryRoots = project.getRodinProject().
			getRootElementsOfType(ISCTheoryRoot.ELEMENT_TYPE);
		Set<IFormulaExtension> extensions = new LinkedHashSet<IFormulaExtension>();
		IFormulaExtension cond = Cond.getCond();
		extensions.add(cond);
		FormulaFactory factory = FormulaFactory.getInstance(extensions);
		for(ISCTheoryRoot root : theoryRoots){
			TheoryProcessor processor = new TheoryProcessor(root, factory);
			processor.initialise();
			processor.processExtensions();
			extensions.addAll(processor.getExtensions());
		}
		extensions.remove(cond);
		return new HashSet<IFormulaExtension>();
	}

}
