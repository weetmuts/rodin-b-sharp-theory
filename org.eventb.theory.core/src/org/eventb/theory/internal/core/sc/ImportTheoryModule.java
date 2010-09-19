/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.internal.core.sc;

import java.util.ArrayList;
import java.util.List;
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
import org.eventb.theory.core.IFormulaExtensionsSource;
import org.eventb.theory.core.IImportTheory;
import org.eventb.theory.core.ISCImportTheory;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.ITheoryRoot;
import org.eventb.theory.core.TheoryCoreFacade;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.sc.TheoryGraphProblem;
import org.eventb.theory.internal.core.maths.extensions.DefinitionTransformer;
import org.eventb.theory.internal.core.maths.extensions.TheoryTransformer;
import org.eventb.theory.internal.core.util.MathExtensionsUtilities;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;

/**
 * @author maamria
 *
 */
public class ImportTheoryModule extends SCProcessorModule{

	IModuleType<ImportTheoryModule> MODULE_TYPE = SCCore.getModuleType(TheoryPlugin.PLUGIN_ID
						+ ".importTheoryModule");
	
	private FormulaFactory factory;
	private ITypeEnvironment typeEnvironment;
	private List<String> checkedImports;
	
	@Override
	public void initModule(IRodinElement element,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		super.initModule(element, repository, monitor);
		factory = repository.getFormulaFactory();
		typeEnvironment = repository.getTypeEnvironment();
		IRodinFile file = (IRodinFile) element;
		ITheoryRoot root = (ITheoryRoot) file.getRoot();
		IImportTheory[] importTheories = root.getImportTheories();
		checkedImports = new ArrayList<String>();
		for(IImportTheory importTheory: importTheories){
			if(!importTheory.hasImportedTheory()){
				createProblemMarker(importTheory, EventBAttributes.TARGET_ATTRIBUTE, TheoryGraphProblem.ImportTheoryAttrMissing);
				continue;
			}
			ISCTheoryRoot target = importTheory.getImportedTheory();
			if(!target.exists()){
				createProblemMarker(importTheory, EventBAttributes.TARGET_ATTRIBUTE, 
						TheoryGraphProblem.ImportTheoryNotExist, target.getComponentName());
				continue;
			}
			DefinitionTransformer<IFormulaExtensionsSource> transformer = new TheoryTransformer();
			Set<IFormulaExtension> extensions = transformer.transform(target, factory, typeEnvironment);
			factory = factory.withExtensions(extensions);
			typeEnvironment = MathExtensionsUtilities.getTypeEnvironmentForFactory(typeEnvironment, factory);
			checkedImports.add(target.getComponentName());
		}
		repository.setFormulaFactory(factory);
		repository.setTypeEnvironment(typeEnvironment);
	}

	@Override
	public void process(IRodinElement element, IInternalElement target,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		ISCTheoryRoot root = (ISCTheoryRoot) target;
		for (String impo: checkedImports){
			ISCImportTheory importTheory = root.getImportTheory(impo);
			importTheory.create(null, monitor);
			importTheory.setImportedTheory(TheoryCoreFacade.getSCTheory(impo, root.getRodinProject()), monitor);
		}
	}
	
	@Override
	public void endModule(IRodinElement element, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		factory = null;
		typeEnvironment = null;
		checkedImports = null;
		super.endModule(element, repository, monitor);
	}
	
	

	@Override
	public IModuleType<?> getModuleType() {
		// TODO Auto-generated method stub
		return MODULE_TYPE;
	}

}
