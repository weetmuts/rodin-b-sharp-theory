/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.internal.core.sc;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.SCProcessorModule;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.core.IImportTheory;
import org.eventb.theory.core.ISCImportTheory;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.ITheoryRoot;
import org.eventb.theory.core.IUseTheory;
import org.eventb.theory.core.TheoryCoreFacade;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.sc.TheoryGraphProblem;
import org.eventb.theory.internal.core.maths.extensions.TheoryTransformer;
import org.eventb.theory.internal.core.maths.extensions.graph.ProjectDependenciesGraph;
import org.eventb.theory.internal.core.util.MathExtensionsUtilities;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;

/**
 * @author maamria
 * 
 */
public class ImportTheoryModule extends SCProcessorModule {

	IModuleType<ImportTheoryModule> MODULE_TYPE = SCCore
			.getModuleType(TheoryPlugin.PLUGIN_ID + ".importTheoryModule");

	private FormulaFactory factory;
	private ITypeEnvironment typeEnvironment;

	private Set<IDeployedTheoryRoot> checkedDeployedRoots;
	private Set<ISCTheoryRoot> checkedSCTheoryRoots;

	@Override
	public void initModule(IRodinElement element,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		super.initModule(element, repository, monitor);
		factory = repository.getFormulaFactory();
		typeEnvironment = repository.getTypeEnvironment();
		IRodinFile file = (IRodinFile) element;
		final ITheoryRoot root = (ITheoryRoot) file.getRoot();
		IImportTheory[] importTheories = root.getImportTheories();
		checkedSCTheoryRoots = new LinkedHashSet<ISCTheoryRoot>();
		for (IImportTheory importTheory : importTheories) {
			if (!importTheory.hasImportedTheory()) {
				createProblemMarker(importTheory,
						EventBAttributes.TARGET_ATTRIBUTE,
						TheoryGraphProblem.ImportTheoryAttrMissing);
				continue;
			}
			ISCTheoryRoot target = importTheory.getImportedTheory();
			if (!target.exists()) {
				createProblemMarker(importTheory,
						EventBAttributes.TARGET_ATTRIBUTE,
						TheoryGraphProblem.ImportTheoryNotExist,
						target.getComponentName());
				continue;
			}
			checkedSCTheoryRoots.add(target);
		}
		ProjectDependenciesGraph graph = new ProjectDependenciesGraph();
		graph.setSCTheoryRoots(new ArrayList<ISCTheoryRoot>(checkedSCTheoryRoots));
		IDeployedTheoryRoot[] deployedRoots = TheoryCoreFacade.getDeployedTheories(root.getRodinProject());
		graph.setDeployedRoots(deployedRoots);
		checkedSCTheoryRoots = graph.getSCTheoryRoots();
		checkedDeployedRoots = graph.getIncludedTheories(checkedSCTheoryRoots);
		Set<IFormulaExtension> extensions = new LinkedHashSet<IFormulaExtension>();
		for (IDeployedTheoryRoot deployedTheoryRoot : checkedDeployedRoots) {
			if(deployedTheoryRoot.getComponentName().equals(root.getComponentName())){
				continue;
			}
			TheoryTransformer transformer = new TheoryTransformer();
			extensions.addAll(transformer.transform(deployedTheoryRoot,
					factory, typeEnvironment));
			factory = factory.withExtensions(extensions);
			typeEnvironment = MathExtensionsUtilities
					.getTypeEnvironmentForFactory(typeEnvironment, factory);
		}
		for (ISCTheoryRoot scTheoryRoot : checkedSCTheoryRoots) {
			TheoryTransformer transformer = new TheoryTransformer();
			extensions.addAll(transformer.transform(scTheoryRoot, factory,
					typeEnvironment));
			factory = factory.withExtensions(extensions);
			typeEnvironment = MathExtensionsUtilities
					.getTypeEnvironmentForFactory(typeEnvironment, factory);
		}
		repository.setFormulaFactory(factory);
		repository.setTypeEnvironment(factory.makeTypeEnvironment());
	}

	@Override
	public void process(IRodinElement element, IInternalElement target,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		final ISCTheoryRoot root = (ISCTheoryRoot) target;
		for (IDeployedTheoryRoot deployedRoot : checkedDeployedRoots) {
			if(deployedRoot.getComponentName().equals(root.getComponentName())){
				continue;
			}
			String componentName = deployedRoot.getComponentName();
			IUseTheory useTheory = root.getUsedTheory(componentName);
			useTheory.create(null, monitor);
			useTheory.setUsedTheory(deployedRoot, monitor);
		}
		for (ISCTheoryRoot scRoot : checkedSCTheoryRoots) {
			String componentName = scRoot.getComponentName();
			ISCImportTheory importTheory = root.getImportTheory(componentName);
			importTheory.create(null, monitor);
			importTheory.setImportedTheory(scRoot, monitor);
		}
	}

	@Override
	public void endModule(IRodinElement element, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		factory = null;
		typeEnvironment = null;
		super.endModule(element, repository, monitor);
	}

	@Override
	public IModuleType<?> getModuleType() {
		// TODO Auto-generated method stub
		return MODULE_TYPE;
	}

}
