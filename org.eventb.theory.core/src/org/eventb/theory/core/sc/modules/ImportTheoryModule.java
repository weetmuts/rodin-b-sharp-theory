/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.sc.modules;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
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
import org.eventb.theory.core.IImportTheory;
import org.eventb.theory.core.ISCImportTheory;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.ITheoryRoot;
import org.eventb.theory.core.TheoryAttributes;
import org.eventb.theory.core.TheoryHierarchyHelper;
import org.eventb.theory.core.basis.SCTheoryDecorator;
import org.eventb.theory.core.maths.extensions.FormulaExtensionsLoader;
import org.eventb.theory.core.maths.extensions.dependencies.SCTheoriesGraph;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.sc.Messages;
import org.eventb.theory.core.sc.TheoryGraphProblem;
import org.eventb.theory.core.sc.states.TheoryAccuracyInfo;
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

	private final IModuleType<ImportTheoryModule> MODULE_TYPE = SCCore
			.getModuleType(TheoryPlugin.PLUGIN_ID + ".importTheoryModule"); //$NON-NLS-1$

	private Set<IImportTheory> importTheoriesDirectives;
	private TheoryAccuracyInfo accuracyInfo;
	
	@Override
	public void initModule(IRodinElement element,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		// Most processing is done here in the initialisation 
		accuracyInfo = (TheoryAccuracyInfo) repository.getState(TheoryAccuracyInfo.STATE_TYPE);
		IRodinFile file = (IRodinFile) element;
		ITheoryRoot root = (ITheoryRoot) file.getRoot();
		IImportTheory[] importTheories = root.getImportTheories();
		if (importTheories.length != 0) {
			monitor.subTask(Messages.bind(Messages.progress_TheoryImportTheories));
			importTheoriesDirectives = new HashSet<IImportTheory>();
			ISCTheoryRoot targetRoot = root.getSCTheoryRoot();
			processImports(importTheories, targetRoot, repository, monitor);
		}
	}

	@Override
	public void process(IRodinElement element, IInternalElement target,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		if (importTheoriesDirectives != null)
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
		boolean isAccurate = true;
		// variable used to check against direct and indirect redundancy
		Set<ISCTheoryRoot> importedTheories = new HashSet<ISCTheoryRoot>();
		for (IImportTheory importTheory : importTheories) {
			// missing attribute
			if (!importTheory.hasImportTheory()) {
				createProblemMarker(importTheory,TheoryAttributes.IMPORT_THEORY_ATTRIBUTE,TheoryGraphProblem.ImportTheoryAttrMissing);
				isAccurate = false;
				continue;
			}
			ISCTheoryRoot importRoot = importTheory.getImportTheory();
			// target does not exist
			if (!importRoot.exists()) {
				createProblemMarker(importTheory,TheoryAttributes.IMPORT_THEORY_ATTRIBUTE,TheoryGraphProblem.ImportTheoryNotExist, importRoot.getComponentName());
				isAccurate = false;
				continue;
			}
			// circularity
			if (TheoryHierarchyHelper.doesTheoryImportTheory(importRoot, targetRoot)) {
				createProblemMarker(importTheory,TheoryAttributes.IMPORT_THEORY_ATTRIBUTE, TheoryGraphProblem.ImportDepCircularity,importRoot.getComponentName(),targetRoot.getComponentName());
				isAccurate = false;
				continue;
			}
			// direct redundancy
			if (importedTheories.contains(importRoot)) {
				createProblemMarker(importTheory,TheoryAttributes.IMPORT_THEORY_ATTRIBUTE,TheoryGraphProblem.RedundantImportWarn,importRoot.getComponentName(),targetRoot.getComponentName());
				isAccurate = false;
				continue;
			}
			// add to the sets
			importedTheories.add(importRoot);
			importTheoriesDirectives.add(importTheory);
		}
		// clear to use differently
		importedTheories.clear();
		// filter imports
		isAccurate &= filterImports(importedTheories);
		patchFormulaFactory(importedTheories, repository);
		if (!isAccurate){
			accuracyInfo.setNotAccurate();
		}
	}

	/**
	 * Filters the provided set of theories to check against redundancies and conflicts.
	 * @param importedTheories the set of imported theories
	 * @return whether the filtering maintained the accuracy of the theory
	 * @throws CoreException
	 */
	protected boolean filterImports(Set<ISCTheoryRoot> importedTheories) throws CoreException {
		boolean isAccurate = true;
		// map imports with the theories closure
		Map<IImportTheory, Set<ISCTheoryRoot>> importMap = new LinkedHashMap<IImportTheory, Set<ISCTheoryRoot>>();
		// need to check for conflicts
		for (IImportTheory importTheory : importTheoriesDirectives) {
			// TODO check if being temp affects things
			ISCTheoryRoot referencedRoot = importTheory.getImportTheory();
			Set<ISCTheoryRoot> allReferencedRoots = TheoryHierarchyHelper.importClosure(referencedRoot);
			allReferencedRoots.add(referencedRoot);
			importMap.put(importTheory, allReferencedRoots);
		}
		// check redundant imports
		IImportTheory[] importTheoriesArray = importTheoriesDirectives.toArray(new IImportTheory[importTheoriesDirectives.size()]);
		boolean[] redundancy = new boolean[importTheoriesArray.length];
		for (int i = 0; i < redundancy.length - 1; i++) {
			for (int k = i+1; k < redundancy.length; k++) {
				Set<ISCTheoryRoot> importedRoots_i = importMap.get(importTheoriesArray[i]);
				Set<ISCTheoryRoot> importedRoots_k = importMap.get(importTheoriesArray[k]);
				if (importedRoots_i.containsAll(importedRoots_k)) {
					redundancy[k] = true;
				} else if (importedRoots_k.containsAll(importedRoots_i)) {
					redundancy[i] = true;
				}
			}
		}
		// we need this to issue all warnings to the user, we could have added it above!
		for (int i = 0; i < redundancy.length; i++) {
			IImportTheory currentImportTheory = importTheoriesArray[i];
			if (redundancy[i]) {
				createProblemMarker(currentImportTheory,
						TheoryAttributes.IMPORT_THEORY_ATTRIBUTE,
						TheoryGraphProblem.IndRedundantImportWarn);
				importTheoriesDirectives.remove(currentImportTheory);
				importMap.remove(currentImportTheory);
				continue;
			}
			importedTheories.add(currentImportTheory.getImportTheory());
		}
		// Now we have a clean list of imports
		// need to check for mathematical language conflicts between them
		importTheoriesArray = importTheoriesDirectives.toArray(new IImportTheory[importTheoriesDirectives.size()]);
		for (int i = 0 ; i < importTheoriesArray.length - 1 ; i++){
			ISCTheoryRoot theory = importTheoriesArray[i].getImportTheory();
			SCTheoryDecorator hierarchy = new SCTheoryDecorator(theory);
			for (int k = i+1 ; k < importTheoriesArray.length; k++){
				ISCTheoryRoot otherTheory = importTheoriesArray[k].getImportTheory();
				SCTheoryDecorator otherHierarchy = new SCTheoryDecorator(otherTheory);
				if (hierarchy.isConflicting(otherHierarchy)){
					// remove the theories causing conflict
					importTheoriesDirectives.remove(importTheoriesArray[i]);
					importTheoriesDirectives.remove(importTheoriesArray[k]);
					importedTheories.remove(importTheoriesArray[i].getImportTheory());
					importedTheories.remove(importTheoriesArray[k].getImportTheory());
					createProblemMarker(importTheoriesArray[i], TheoryAttributes.IMPORT_THEORY_ATTRIBUTE, 
							TheoryGraphProblem.ImportConflict, importTheoriesArray[i].getImportTheory().getComponentName(),
							importTheoriesArray[k].getImportTheory().getComponentName());
					createProblemMarker(importTheoriesArray[k], TheoryAttributes.IMPORT_THEORY_ATTRIBUTE, 
							TheoryGraphProblem.ImportConflict, importTheoriesArray[k].getImportTheory().getComponentName(),
							importTheoriesArray[i].getImportTheory().getComponentName());
					// theory is not accurate in this case
					isAccurate = false;
				}
			}
		}
		return isAccurate;
	}

	/**
	 * Patches the formula factory to be used for the rest of the static checking process.
	 * @param importedTheories the set of imported theories to consider
	 * @param repository the state repository
	 * @throws CoreException
	 */
	protected void patchFormulaFactory(Set<ISCTheoryRoot> importedTheories,
			ISCStateRepository repository) throws CoreException {
		// need to patch up formula factory
		SCTheoriesGraph graph = new SCTheoriesGraph();
		graph.setElements(importedTheories);
		FormulaFactory factory = repository.getFormulaFactory();
		ITypeEnvironment typeEnvironment = repository.getTypeEnvironment();

		for (ISCTheoryRoot root : graph.getElements()) {
			FormulaExtensionsLoader loader = new FormulaExtensionsLoader(root,
					factory);
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
		
		for (IImportTheory currentImportTheory : importTheoriesDirectives) {
			ISCImportTheory scImport = targetRoot
					.getImportTheory(currentImportTheory.getElementName());
			scImport.create(null, monitor);
			scImport.setSource(currentImportTheory, monitor);
			scImport.setImportTheory(currentImportTheory.getImportTheory(),
					monitor);
		}
	}
	
	@Override
	public void endModule(IRodinElement element,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		accuracyInfo = null;
	}

	@Override
	public IModuleType<?> getModuleType() {
		// TODO Auto-generated method stub
		return MODULE_TYPE;
	}

}
