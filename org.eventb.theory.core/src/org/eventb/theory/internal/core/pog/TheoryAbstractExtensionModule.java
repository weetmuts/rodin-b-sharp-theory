/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.internal.core.pog;

import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IPORoot;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.pog.POGProcessorModule;
import org.eventb.core.pog.state.IPOGStateRepository;
import org.eventb.internal.core.ast.extension.Cond;
import org.eventb.theory.core.IElementTransformer;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.internal.core.util.MathExtensionsUtilities;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;

/**
 * @author maamria
 * 
 */
@SuppressWarnings("restriction")
public abstract class TheoryAbstractExtensionModule<E extends IInternalElement> extends POGProcessorModule {

	protected FormulaFactory factory;
	protected ITypeEnvironment typeEnvironment;
	protected IPORoot target;

	@Override
	public void initModule(IRodinElement element,
			IPOGStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		super.initModule(element, repository, monitor);
		factory = FormulaFactory.getInstance(MathExtensionsUtilities.singletonExtension(Cond.getCond()));
		typeEnvironment = repository.getTypeEnvironment();
		target = repository.getTarget();
		
		IRodinFile scTheoryFile = (IRodinFile) element;
		ISCTheoryRoot scTheoryRoot = (ISCTheoryRoot) scTheoryFile.getRoot();
		E[] scDefinitions = getExtensionElements(scTheoryRoot);
		for (E definition : scDefinitions) {
			IElementTransformer<E, Set<IFormulaExtension>> transformer = getTransformer();
			Set<IFormulaExtension> extensions = transformer.transform(
					definition, factory, typeEnvironment);
			if (extensions == null || extensions.isEmpty()) {
				continue;
			}
			
			factory = factory.withExtensions(extensions);
			repository.setFormulaFactory(factory);
			typeEnvironment = MathExtensionsUtilities.getTypeEnvironmentForFactory(
					typeEnvironment, factory);
			repository.setTypeEnvironment(typeEnvironment);
			// redefined extensions
			for(IFormulaExtension extension: extensions){
				generateCorrespondingPOs(extension, monitor);
			}
			
		}
	}

	protected abstract void generateCorrespondingPOs(IFormulaExtension extension, IProgressMonitor monitor)
	throws CoreException;
	
	protected abstract IElementTransformer<E, Set<IFormulaExtension>> getTransformer();
	
	protected abstract E[] getExtensionElements(ISCTheoryRoot parent) throws CoreException;

	@Override
	public void endModule(IRodinElement element,
			IPOGStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		typeEnvironment = null;
		factory = null;
		target = null;
		super.endModule(element, repository, monitor);
	}

}
