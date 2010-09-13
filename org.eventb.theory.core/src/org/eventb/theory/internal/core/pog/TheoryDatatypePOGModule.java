/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.internal.core.pog;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.pog.POGCore;
import org.eventb.core.pog.state.IPOGStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.theory.core.ISCDatatypeDefinition;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.maths.extensions.DatatypeTransformer;
import org.eventb.theory.core.maths.extensions.IDefinitionTransformer;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.rodinp.core.IRodinElement;

/**
 * @author maamria
 * 
 */
public class TheoryDatatypePOGModule extends TheoryAbstractExtensionModule<ISCDatatypeDefinition> {

	public static final IModuleType<TheoryDatatypePOGModule> MODULE_TYPE = POGCore
			.getModuleType(TheoryPlugin.PLUGIN_ID + ".theoryDatatypeModule"); //$NON-NLS-1$

	@Override
	public void process(IRodinElement element, IPOGStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		// all done in initialisation

	}

	@Override
	public IModuleType<?> getModuleType() {
		// TODO Auto-generated method stub
		return MODULE_TYPE;
	}

	@Override
	protected IDefinitionTransformer<ISCDatatypeDefinition> getTransformer() {
		// TODO Auto-generated method stub
		return new DatatypeTransformer();
	}

	@Override
	protected ISCDatatypeDefinition[] getExtensionElements(ISCTheoryRoot parent)
			throws CoreException {
		// TODO Auto-generated method stub
		return parent.getSCDatatypeDefinitions();
	}


	@Override
	protected void generateCorrespondingPOs(IFormulaExtension extension, IProgressMonitor monitor) 
	throws CoreException{
		// nothing to generate
		
	}

}
