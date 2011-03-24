/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.internal.core.sc;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.SCProcessorModule;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.theory.core.DB_TCFacade;
import org.eventb.theory.core.IImportTheory;
import org.eventb.theory.core.ISCImportTheory;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.ITheoryRoot;
import org.eventb.theory.core.TheoryAttributes;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.sc.Messages;
import org.eventb.theory.core.sc.TheoryGraphProblem;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;

/**
 * 
 * @author maamria
 *
 */
public class ImportTheoryModule extends SCProcessorModule {

	IModuleType<ImportTheoryModule> MODULE_TYPE = SCCore
		.getModuleType(TheoryPlugin.PLUGIN_ID + ".importTheoryModule"); //$NON-NLS-1$

	@Override
	public void process(IRodinElement element, IInternalElement target,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		IRodinFile file = (IRodinFile) element;
		ITheoryRoot root = (ITheoryRoot) file.getRoot();
		IImportTheory[] importTheories = root.getImportTheories();
		if(importTheories.length == 0){
			return;
		}
		monitor.subTask(Messages.bind(Messages.progress_TheoryImportTheories));
		ISCTheoryRoot targetRoot = (ISCTheoryRoot) target;
		processImports(importTheories, targetRoot, repository, monitor);
	}

	/**
	 * Processes the theory imports.
	 * @param importTheories the imports
	 * @param targetRoot the SC theory target
	 * @param repository the state repository
	 * @param monitor the progress monitor
	 * @throws CoreException
	 */
	protected void processImports(IImportTheory[] importTheories,
			ISCTheoryRoot targetRoot, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException{
		Set<ISCTheoryRoot> importedTheories = new HashSet<ISCTheoryRoot>();
		for (IImportTheory importTheory : importTheories){
			// missing attribute
			if (!importTheory.hasImportTheory()){
				createProblemMarker(importTheory, TheoryAttributes.IMPORT_THEORY_ATTRIBUTE, 
						TheoryGraphProblem.ImportTheoryAttrMissing);
				continue;
			}
			ISCTheoryRoot importRoot = importTheory.getImportTheory();
			// target does not exist
			if(!importRoot.exists()){
				createProblemMarker(importTheory, TheoryAttributes.IMPORT_THEORY_ATTRIBUTE, 
						TheoryGraphProblem.ImportTheoryNotExist, importRoot.getComponentName());
				continue;
			}
			// circularity
			if(DB_TCFacade.doesTheoryImportTheory(importRoot, targetRoot)){
				createProblemMarker(importTheory, TheoryAttributes.IMPORT_THEORY_ATTRIBUTE, 
						TheoryGraphProblem.ImportDepCircularity, importRoot.getComponentName(), 
						targetRoot.getComponentName());
				continue;
			}
			// redundancy
			if (importedTheories.contains(importRoot)){
				createProblemMarker(importTheory, TheoryAttributes.IMPORT_THEORY_ATTRIBUTE, 
						TheoryGraphProblem.RedundantImportWarn, importRoot.getComponentName(), 
						targetRoot.getComponentName());
				continue;
			}
			// create the SC counterpart
			ISCImportTheory scImport = targetRoot.getImportTheory(importTheory.getElementName());
			scImport.create(null, monitor);
			scImport.setSource(importTheory, monitor);
			scImport.setImportTheory(importRoot, monitor);
			// add to the set
			importedTheories.add(importRoot);
		}
		// need to patch up formula factory
	}

	@Override
	public IModuleType<?> getModuleType() {
		// TODO Auto-generated method stub
		return MODULE_TYPE;
	}

}
