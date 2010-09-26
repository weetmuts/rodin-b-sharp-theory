/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.maths.extensions;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.TheoryCoreFacade;
import org.eventb.theory.internal.core.maths.extensions.TheoryTransformer;
import org.eventb.theory.internal.core.maths.extensions.graph.ITheoryGraph;
import org.eventb.theory.internal.core.maths.extensions.graph.ProjectGraph;
import org.eventb.theory.internal.core.util.MathExtensionsUtilities;
import org.rodinp.core.IRodinProject;

/**
 * An implementation of a formula extensions loader tailored to a specific project.
 * 
 * @author maamria
 *
 */
public class FormulaExtensionsLoader {
	
	private IRodinProject project;
	private List<String> execluded;
	private FormulaFactory factory;
	
	/**
	 * Create a formula extension loader for the given project.
	 * @param project the project for which to load extensions
	 * @param execluded any execluded theories
	 */
	public FormulaExtensionsLoader(IRodinProject project, List<String> execluded){
		this.project = project;
		this.execluded = execluded;
	}
	
	/**
	 * Returns all deployed formula extensions for the Event-B project <code>project</code>.
	 * @return deployed formula extensions
	 * @throws CoreException
	 */
	public Set<IFormulaExtension> getFormulaExtensions() throws CoreException{
		IDeployedTheoryRoot[] theoryRoots = TheoryCoreFacade.getDeployedTheories(project);
		Set<IFormulaExtension> extensions = new LinkedHashSet<IFormulaExtension>();
		extensions.add(MathExtensionsUtilities.COND);
		factory = FormulaFactory.getInstance(extensions);
		// the project graph for deployed theories
		ITheoryGraph<IDeployedTheoryRoot> graph = 
			new ProjectGraph<IDeployedTheoryRoot>(theoryRoots).getGraph();
		List<IDeployedTheoryRoot> execludedRoots = getDeployedRoots();
		graph.remove(execludedRoots);
		Set<IDeployedTheoryRoot> newSetDepl = graph.getElements();
		theoryRoots = newSetDepl.toArray(new IDeployedTheoryRoot[newSetDepl.size()]);
		for(IDeployedTheoryRoot root : theoryRoots){
			TheoryTransformer transformer = new TheoryTransformer();
			Set<IFormulaExtension> theoryExtns = transformer.transform(root, factory, factory.makeTypeEnvironment());
			extensions.addAll(theoryExtns);
			factory = factory.withExtensions(extensions);
		}
		extensions.remove(MathExtensionsUtilities.COND);
		return extensions;
	}
	
	
	protected List<IDeployedTheoryRoot> getDeployedRoots() {
		List<IDeployedTheoryRoot> list = new ArrayList<IDeployedTheoryRoot>();
		for(String execlu : execluded){
			IDeployedTheoryRoot dep = TheoryCoreFacade.getDeployedTheory(execlu, project);
			if(dep.exists())
				list.add(dep);
		}
		return list;
	}

	public Set<IFormulaExtension> getAdditionalExtensions(ISCTheoryRoot[] roots)
	throws CoreException{
		Set<IFormulaExtension> extensions = new LinkedHashSet<IFormulaExtension>();
		extensions.add(MathExtensionsUtilities.COND);
		factory = factory.withExtensions(extensions);
		for (ISCTheoryRoot scTheoryRoot : roots){
			TheoryTransformer transformer = new TheoryTransformer();
			extensions.addAll(transformer.transform(scTheoryRoot, factory, factory.makeTypeEnvironment()));
			factory = factory.withExtensions(extensions);
		}
		extensions.remove(MathExtensionsUtilities.COND);
		return extensions;
	}

}
