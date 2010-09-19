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
import org.eventb.core.IEventBRoot;
import org.eventb.core.IPRRoot;
import org.eventb.core.IPSRoot;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.internal.core.ast.extension.Cond;
import org.eventb.theory.core.IDeployedTheoryEntry;
import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.core.IExtensionsSourceEntry;
import org.eventb.theory.core.IFormulaExtensionsSource;
import org.eventb.theory.core.IGeneratedRootEntry;
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
public class ProjectDependenciesManager implements IProjectDependenciesManager{
	
	private IRodinProject project;
	
	private IProjectMetaDependencies dependenciesFile;
	
	private final static Cond cond = Cond.getCond();
	
	public ProjectDependenciesManager(IRodinProject project)
	throws CoreException{
		this.project = project;
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
		IEventBRoot alternativeRoot = root;
		if(root instanceof IPSRoot || root instanceof IPRRoot){
			alternativeRoot = root.getPORoot();
		}
		String fileName = alternativeRoot.getRodinFile().getElementName();
		IGeneratedRootEntry entry = dependenciesFile.getRootEntry(fileName);
		Set<IFormulaExtension> extensions = new LinkedHashSet<IFormulaExtension>();
		extensions.add(cond);
		FormulaFactory localFactory = FormulaFactory.getInstance(extensions);
		if(entry.exists()){
			IExtensionsSourceEntry[] sources = entry.getSourceEntries();
			for(IExtensionsSourceEntry sourceEntry : sources){
				IFormulaExtensionsSource source = sourceEntry.getExtensionsSource();
				if(source.exists()){
					DefinitionTransformer<IFormulaExtensionsSource> transformer = new TheoryTransformer();
					Set<IFormulaExtension> added = transformer.transform(source, localFactory, localFactory.makeTypeEnvironment());
					localFactory = localFactory.withExtensions(added);
					extensions.addAll(added);
				}
			}
		}
		else{
			entry.create(null, null);
			entry.setSource(alternativeRoot, null);
			IDeployedTheoryRoot[] deployedRoots = TheoryCoreFacade.getDeployedTheories(project);
			List<String> processedTheories = new ArrayList<String>();
			for (IDeployedTheoryRoot depl : deployedRoots){
				if(processedTheories.contains(depl.getComponentName())){
					continue;
				}
				IExtensionsSourceEntry sEntry =entry.getSourceEntry(depl.getElementName());
				sEntry.create(null, null);
				sEntry.setExtensionsSource(depl, null);
				DefinitionTransformer<IFormulaExtensionsSource> transformer =
					new TheoryTransformer();
				extensions.addAll(transformer.transform(depl, 
						localFactory, localFactory.makeTypeEnvironment()));
				processedTheories.addAll(transformer.getProcessedDependencies());
				localFactory = localFactory.withExtensions(extensions);
			}
		}
		extensions.remove(cond);
		
		return extensions;
	}

	@Override
	public void setFormulaFactory(IEventBRoot root) throws CoreException {
		// TODO Auto-generated method stub
		IEventBRoot alternativeRoot = root;
		if(root instanceof IPSRoot || root instanceof IPRRoot){
			alternativeRoot = root.getPORoot();
		}
		String fileName = alternativeRoot.getRodinFile().getElementName();
		IDeployedTheoryRoot[] depls = TheoryCoreFacade.getDeployedTheories(project);
		IGeneratedRootEntry entry = dependenciesFile.getRootEntry(fileName);
		if(!entry.exists()){
			entry.create(null, null);
			entry.setGeneratedRoot(alternativeRoot, null);
			
			for(IDeployedTheoryRoot depl : depls){
				IExtensionsSourceEntry sEntry = 
					entry.getSourceEntry(depl.getRodinFile().getElementName());
				sEntry.create(null, null);
				sEntry.setExtensionsSource(depl, null);
			}	
		}
		else {
			for(IDeployedTheoryRoot depl : depls){
				IExtensionsSourceEntry sEntry = 
					entry.getSourceEntry(depl.getRodinFile().getElementName());
				if(sEntry.exists() && sEntry.getExtensionsSource().equals(depl)){
					continue;
				}
				sEntry.create(null, null);
				sEntry.setExtensionsSource(depl, null);
			}	
		}
		
	}

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

	@Override
	public void addGeneratedFileEntry(IEventBRoot eventBRoot)
			throws CoreException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addExtensionSource(IEventBRoot generatedRoot,
			IFormulaExtensionsSource source) throws CoreException {
		// TODO Auto-generated method stub
		
	}
	


}
