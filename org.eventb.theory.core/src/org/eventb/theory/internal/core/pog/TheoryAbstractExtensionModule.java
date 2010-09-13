/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.internal.core.pog;

import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IPORoot;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.pog.POGProcessorModule;
import org.eventb.core.pog.state.IPOGStateRepository;
import org.eventb.theory.core.IExtensionElement;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.maths.extensions.AbstractOperatorExtension;
import org.eventb.theory.core.maths.extensions.IDefinitionTransformer;
import org.eventb.theory.core.maths.extensions.MathExtensionsFacilitator;
import org.eventb.theory.core.maths.extensions.SimpleDatatypeExtension;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;

/**
 * @author maamria
 * 
 */
public abstract class TheoryAbstractExtensionModule<E extends IExtensionElement> extends POGProcessorModule {

	protected FormulaFactory factory;
	protected ITypeEnvironment typeEnvironment;
	protected IPORoot target;

	@Override
	public void initModule(IRodinElement element,
			IPOGStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		super.initModule(element, repository, monitor);
		factory = repository.getFormulaFactory();
		typeEnvironment = repository.getTypeEnvironment();
		target = repository.getTarget();
		
		IRodinFile scTheoryFile = (IRodinFile) element;
		ISCTheoryRoot scTheoryRoot = (ISCTheoryRoot) scTheoryFile.getRoot();
		E[] scDefinitions = getExtensionElements(scTheoryRoot);
		for (E definition : scDefinitions) {
			IDefinitionTransformer<E> transformer = getTransformer();
			Set<IFormulaExtension> extensions = transformer.transform(
					definition, factory, typeEnvironment);
			if (extensions == null || extensions.isEmpty()) {
				continue;
			}
			Set<IFormulaExtension> existingExts = factory.getExtensions();
			Set<IFormulaExtension> toRemove = new LinkedHashSet<IFormulaExtension>();
			for(IFormulaExtension existingExt: existingExts){
				for(IFormulaExtension newExt : extensions){
					if(newExt instanceof AbstractOperatorExtension<?>){
						if(newExt.equals(existingExt)){
							toRemove.add(newExt);
						}
					}
					else if(newExt.getOrigin() != null){
						if(newExt.getOrigin() instanceof SimpleDatatypeExtension){
							if(newExt.getOrigin().equals(existingExt.getOrigin())){
								toRemove.add(newExt);
							}
						}
					}
				}
			}
			extensions.removeAll(toRemove);
			factory = factory.withExtensions(extensions);
			repository.setFormulaFactory(factory);
			typeEnvironment = MathExtensionsFacilitator.getTypeEnvironmentForFactory(
					typeEnvironment, factory);
			repository.setTypeEnvironment(typeEnvironment);
			// redefined extensions
			for(IFormulaExtension extension: toRemove){
				generateCorrespondingPOs(extension, monitor);
			}
		}
	}

	protected abstract void generateCorrespondingPOs(IFormulaExtension extension, IProgressMonitor monitor)
	throws CoreException;
	
	protected abstract IDefinitionTransformer<E> getTransformer();
	
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
