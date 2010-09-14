/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.internal.core.maths.extensions;

import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.theory.core.ISCDatatypeDefinition;
import org.eventb.theory.core.ISCNewOperatorDefinition;
import org.eventb.theory.core.ISCTypeParameter;
import org.eventb.theory.core.deploy.IDeployedTheoryRoot;
import org.eventb.theory.internal.core.util.MathExtensionsUtilities;
import org.rodinp.core.IInternalElementType;

/**
 * @author maamria
 * 
 */
public class TheoryTransformer extends
		DefinitionTransformer<IDeployedTheoryRoot> {

	private IDeployedTheoryRoot root;
	private ITypeEnvironment typeEnvironment;

	@Override
	public IInternalElementType<IDeployedTheoryRoot> getElementType()
			throws CoreException {
		// TODO Auto-generated method stub
		return IDeployedTheoryRoot.ELEMENT_TYPE;
	}

	protected void initialise() throws CoreException {
		ISCTypeParameter[] typeParameters = root.getSCTypeParameters();
		for (ISCTypeParameter typeParameter : typeParameters) {
			typeEnvironment.addGivenSet(typeParameter.getIdentifierString());
		}
	}
	
	@Override
	public Set<IFormulaExtension> transform(IDeployedTheoryRoot root,
			FormulaFactory factory, ITypeEnvironment typeEnvironment)
			throws CoreException {
		
		this.typeEnvironment = typeEnvironment;
		this.root = root;
		
		initialise();
		
		Set<IFormulaExtension> extensions =  new LinkedHashSet<IFormulaExtension>();
		
		ISCDatatypeDefinition datatypeDefinitions[] = root.getSCDatatypeDefinitions();
		
		for (ISCDatatypeDefinition definition : datatypeDefinitions) {
			DatatypeTransformer transformer = new DatatypeTransformer();
			extensions.addAll(transformer.transform(definition, factory, typeEnvironment));
			factory = factory.withExtensions(extensions);
			typeEnvironment = MathExtensionsUtilities.getTypeEnvironmentForFactory(typeEnvironment, factory);
		}
		ISCNewOperatorDefinition operatorDefinitions[] = root
				.getSCNewOperatorDefinitions();
		for (ISCNewOperatorDefinition definition : operatorDefinitions) {
			OperatorTransformer transformer = new OperatorTransformer();
			extensions.addAll(transformer.transform(definition, factory,typeEnvironment));
			factory = factory.withExtensions(extensions);
			typeEnvironment = MathExtensionsUtilities.getTypeEnvironmentForFactory(typeEnvironment, factory);

		}
		return extensions;
	}

}
