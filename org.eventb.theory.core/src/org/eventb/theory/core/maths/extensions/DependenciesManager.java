/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.maths.extensions;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IEventBRoot;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.internal.core.ast.extension.Cond;
import org.eventb.theory.core.IDeployedTheoryEntry;
import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.core.IFormulaExtensionsSource;
import org.eventb.theory.core.IProjectMetaDependencies;
import org.eventb.theory.core.ISCTheoryEntry;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.TheoryCoreFacade;
import org.eventb.theory.internal.core.maths.extensions.DefinitionTransformer;
import org.eventb.theory.internal.core.maths.extensions.TheoryTransformer;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;

/**
 * @author maamria
 *
 */
@SuppressWarnings("restriction")
public class DependenciesManager implements IProjectDependenciesManager
{

private IRodinProject project;
	
	private IProjectMetaDependencies dependenciesFile;
	
	private final static Cond cond = Cond.getCond();
	
	Map<String, Set<IFormulaExtension>> deployedExtensions;
	Map<String, Set<IFormulaExtension>> scTheoriesExtensions;
	
	public DependenciesManager(IRodinProject project)
	throws CoreException{
		this.project = project;
		this.deployedExtensions = new LinkedHashMap<String, Set<IFormulaExtension>>();
		this.scTheoriesExtensions = new LinkedHashMap<String, Set<IFormulaExtension>>();
		FormulaFactory localFactory = FormulaFactory.getInstance(extensions)
		String projectName = project.getElementName();
		IRodinFile file = project.getRodinFile(projectName +"."+TheoryCoreFacade.BPDF_FILE_EXTENSION);
		if(!file.exists()){
			file.create(true, null);
		}
		this.dependenciesFile = (IProjectMetaDependencies) file.getRoot();
		IDeployedTheoryRoot[] deployedRoots = TheoryCoreFacade.getDeployedTheories(project);
		ISCTheoryRoot[] scRoots = TheoryCoreFacade.getSCTheoryRoots(project);
		for (IDeployedTheoryRoot depl : deployedRoots){
			IDeployedTheoryEntry entry = dependenciesFile.getDeployedTheoryEntry(depl.getRodinFile().getElementName());
			if(entry.exists()){
				continue;
			}
			entry.create(null, null);
			entry.setSource(depl, null);
			entry.setModified(false, null);
			DefinitionTransformer<IFormulaExtensionsSource> transformer = new TheoryTransformer();
			Set<IFormulaExtension> extensions = transformer.transform(depl, factory, typeEnvironment)
		}
		for (ISCTheoryRoot root : scRoots){
			ISCTheoryEntry entry = dependenciesFile.getTheoryEntry(root.getRodinFile().getElementName());
			if(entry.exists()){
				continue;
			}
			entry.create(null, null);
			entry.setSource(root, null);
			entry.setModified(false, null);
		}
	}
	
	@Override
	public void elementChanged(ElementChangedEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Set<IFormulaExtension> getFormulaExtensions(IEventBRoot root)
			throws CoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eventb.theory.core.maths.extensions.IProjectDependenciesManager#setFormulaFactory(org.eventb.core.IEventBRoot)
	 */
	@Override
	public void setFormulaFactory(IEventBRoot root) throws CoreException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.eventb.theory.core.maths.extensions.IProjectDependenciesManager#addDeployedTheoryEntry(org.eventb.theory.core.IDeployedTheoryRoot)
	 */
	@Override
	public void addDeployedTheoryEntry(IDeployedTheoryRoot theoryRoot)
			throws CoreException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.eventb.theory.core.maths.extensions.IProjectDependenciesManager#addSCTheoryEntry(org.eventb.theory.core.ISCTheoryRoot)
	 */
	@Override
	public void addSCTheoryEntry(ISCTheoryRoot theoryRoot) throws CoreException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.eventb.theory.core.maths.extensions.IProjectDependenciesManager#deployedTheoryChanged(org.eventb.theory.core.IDeployedTheoryRoot)
	 */
	@Override
	public void deployedTheoryChanged(IDeployedTheoryRoot theoryRoot)
			throws CoreException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.eventb.theory.core.maths.extensions.IProjectDependenciesManager#scTheoryChanged(org.eventb.theory.core.ISCTheoryRoot)
	 */
	@Override
	public void scTheoryChanged(ISCTheoryRoot theoryRoot) throws CoreException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.eventb.theory.core.maths.extensions.IProjectDependenciesManager#addGeneratedFileEntry(org.eventb.core.IEventBRoot)
	 */
	@Override
	public void addGeneratedFileEntry(IEventBRoot eventBRoot)
			throws CoreException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.eventb.theory.core.maths.extensions.IProjectDependenciesManager#addExtensionSource(org.eventb.core.IEventBRoot, org.eventb.theory.core.IFormulaExtensionsSource)
	 */
	@Override
	public void addExtensionSource(IEventBRoot generatedRoot,
			IFormulaExtensionsSource source) throws CoreException {
		// TODO Auto-generated method stub
		
	}

}
