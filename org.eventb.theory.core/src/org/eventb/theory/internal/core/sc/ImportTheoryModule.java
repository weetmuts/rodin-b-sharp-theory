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
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.extension.IFormulaExtension;
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
import org.eventb.theory.core.maths.extensions.FormulaExtensionsLoader;
import org.eventb.theory.core.maths.extensions.dependencies.SCTheoriesGraph;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.sc.Messages;
import org.eventb.theory.core.sc.TheoryGraphProblem;
import org.eventb.theory.internal.core.sc.states.DatatypeTable;
import org.eventb.theory.internal.core.util.MathExtensionsUtilities;
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

	private static final String IMPORT_NAME_PREFIX = "import";

	private Set<ISCTheoryRoot> importedTheories;

	@Override
	public void initModule(IRodinElement element,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		IRodinFile file = (IRodinFile) element;
		ITheoryRoot root = (ITheoryRoot) file.getRoot();
		IImportTheory[] importTheories = root.getImportTheories();
		importedTheories = new HashSet<ISCTheoryRoot>();
		if (importTheories.length != 0) {
			monitor.subTask(Messages
					.bind(Messages.progress_TheoryImportTheories));
			ISCTheoryRoot targetRoot = root.getSCTheoryRoot();
			processImports(importTheories, targetRoot, repository, monitor);
		}
		// datatype table state with appropriate formula factory
		final DatatypeTable datatypeTable = new DatatypeTable(
				repository.getFormulaFactory());
		repository.setState(datatypeTable);
	}

	@Override
	public void process(IRodinElement element, IInternalElement target,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		commitImports((ISCTheoryRoot) target, monitor);
	}

	/**
	 * Processes the theory imports.
	 * 
	 * @param importTheories
	 *            the imports
	 * @param targetRoot
	 *            the SC theory target
	 * @param repository
	 *            the state repository
	 * @param monitor
	 *            the progress monitor
	 * @throws CoreException
	 */
	protected void processImports(IImportTheory[] importTheories,
			ISCTheoryRoot targetRoot, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		for (IImportTheory importTheory : importTheories) {
			// missing attribute
			if (!importTheory.hasImportTheory()) {
				createProblemMarker(importTheory,
						TheoryAttributes.IMPORT_THEORY_ATTRIBUTE,
						TheoryGraphProblem.ImportTheoryAttrMissing);
				continue;
			}
			ISCTheoryRoot importRoot = importTheory.getImportTheory();
			// target does not exist
			if (!importRoot.exists()) {
				createProblemMarker(importTheory,
						TheoryAttributes.IMPORT_THEORY_ATTRIBUTE,
						TheoryGraphProblem.ImportTheoryNotExist,
						importRoot.getComponentName());
				continue;
			}
			// circularity
			if (DB_TCFacade.doesTheoryImportTheory(importRoot, targetRoot)) {
				createProblemMarker(importTheory,
						TheoryAttributes.IMPORT_THEORY_ATTRIBUTE,
						TheoryGraphProblem.ImportDepCircularity,
						importRoot.getComponentName(),
						targetRoot.getComponentName());
				continue;
			}
			// redundancy
			if (importedTheories.contains(importRoot)) {
				createProblemMarker(importTheory,
						TheoryAttributes.IMPORT_THEORY_ATTRIBUTE,
						TheoryGraphProblem.RedundantImportWarn,
						importRoot.getComponentName(),
						targetRoot.getComponentName());
				continue;
			}
			// add to the set
			importedTheories.add(importRoot);
		}
		// need to patch up formula factory
		SCTheoriesGraph graph = new SCTheoriesGraph();
		graph.setElements(importedTheories);
		FormulaFactory factory = repository.getFormulaFactory();
		ITypeEnvironment typeEnvironment = repository.getTypeEnvironment();
		
		for (ISCTheoryRoot root : graph.getElements()) {
			FormulaExtensionsLoader loader = new FormulaExtensionsLoader(root, factory);
			Set<IFormulaExtension> exts = loader.load();
			factory = factory.withExtensions(exts);
			typeEnvironment = MathExtensionsUtilities
					.getTypeEnvironmentForFactory(typeEnvironment, factory);
		}
		repository.setFormulaFactory(factory);
		repository.setTypeEnvironment(factory.makeTypeEnvironment());
	}

	/**
	 * Commits the theory imports to the target root.
	 * 
	 * @param targetRoot
	 *            the SC theory target
	 * @param monitor
	 *            the progress monitor
	 * @throws CoreException
	 */
	protected void commitImports(ISCTheoryRoot targetRoot,
			IProgressMonitor monitor) throws CoreException {
		int count = 0;
		for (ISCTheoryRoot importTheory : importedTheories) {
			ISCImportTheory scImport = targetRoot
					.getImportTheory(IMPORT_NAME_PREFIX + count++);
			scImport.create(null, monitor);
			scImport.setImportTheory(importTheory, monitor);
		}
	}

	@Override
	public IModuleType<?> getModuleType() {
		// TODO Auto-generated method stub
		return MODULE_TYPE;
	}

}
