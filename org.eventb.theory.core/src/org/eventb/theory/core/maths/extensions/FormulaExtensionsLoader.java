/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.maths.extensions;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IEventBProject;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.internal.core.ast.extension.Cond;
import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.internal.core.maths.extensions.TheoryTransformer;

/**
 * An implementation of a formula extensions loader tailored to a specific project.
 * 
 * @author maamria
 *
 */
@SuppressWarnings("restriction")
public class FormulaExtensionsLoader {
	
	private IEventBProject project;
	private List<String> execluded;
	private Set<IFormulaExtension> execludedExtensions;
	
	/**
	 * Create a formula extension loader for the given project.
	 * @param project the project for which to load extensions
	 * @param execluded any execluded theories
	 */
	public FormulaExtensionsLoader(IEventBProject project, List<String> execluded){
		this.project = project;
		this.execluded = execluded;
		this.execludedExtensions = new LinkedHashSet<IFormulaExtension>();
	}
	
	/**
	 * Returns all deployed formula extensions for the Event-B project <code>project</code>.
	 * @return deployed formula extensions
	 * @throws CoreException
	 */
	public Set<IFormulaExtension> getFormulaExtensions() throws CoreException{
		IDeployedTheoryRoot[] theoryRoots = project.getRodinProject().
			getRootElementsOfType(IDeployedTheoryRoot.ELEMENT_TYPE);
		Set<IFormulaExtension> extensions = new LinkedHashSet<IFormulaExtension>();
		IFormulaExtension cond = Cond.getCond();
		extensions.add(cond);
		FormulaFactory factory = FormulaFactory.getInstance(extensions);
		for(IDeployedTheoryRoot root : theoryRoots){
			TheoryTransformer transformer = new TheoryTransformer();
			Set<IFormulaExtension> theoryExtns = transformer.transform(root, factory, factory.makeTypeEnvironment());
			if(execluded.contains(root.getElementName())){
				execludedExtensions.addAll(theoryExtns);
			}
			extensions.addAll(theoryExtns);
		}
		extensions.remove(cond);
		return extensions;
	}
	
	public Set<IFormulaExtension> getExecludedExtensions(){
		return execludedExtensions;
	}

}
