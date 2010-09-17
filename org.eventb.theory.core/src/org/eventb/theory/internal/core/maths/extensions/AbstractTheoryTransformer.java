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
import org.eventb.theory.internal.core.util.MathExtensionsUtilities;
import org.rodinp.core.IInternalElement;

/**
 * @author maamria
 *
 */
public abstract class AbstractTheoryTransformer<E extends IInternalElement> 
extends DefinitionTransformer<E>{

	private ITypeEnvironment typeEnvironment;
	private E source;

	protected void initialise() throws CoreException {
		ISCTypeParameter[] typeParameters = getTypeParameters(source);
		for (ISCTypeParameter typeParameter : typeParameters) {
			typeEnvironment.addGivenSet(typeParameter.getIdentifierString());
		}
	}

	protected abstract ISCNewOperatorDefinition[] getOperators(E source)
		throws CoreException;
	
	protected abstract ISCDatatypeDefinition[] getDatatypes(E source)
		throws CoreException;

	protected abstract ISCTypeParameter[] getTypeParameters(E source) 
		throws CoreException;

	@Override
	public Set<IFormulaExtension> transform(E source,
			FormulaFactory factory, ITypeEnvironment typeEnvironment)
			throws CoreException {

		this.typeEnvironment = typeEnvironment;
		this.source = source;

		initialise();

		Set<IFormulaExtension> extensions = new LinkedHashSet<IFormulaExtension>();

		ISCDatatypeDefinition datatypeDefinitions[] = getDatatypes(source);

		for (ISCDatatypeDefinition definition : datatypeDefinitions) {
			DatatypeTransformer transformer = new DatatypeTransformer();
			Set<IFormulaExtension> addedExtensions = transformer.transform(
					definition, factory, typeEnvironment);
			if (addedExtensions != null) {
				extensions.addAll(addedExtensions);
			}
			factory = factory.withExtensions(extensions);
			typeEnvironment = MathExtensionsUtilities
					.getTypeEnvironmentForFactory(typeEnvironment, factory);
		}
		ISCNewOperatorDefinition operatorDefinitions[] = getOperators(source);
		for (ISCNewOperatorDefinition definition : operatorDefinitions) {
			OperatorTransformer transformer = new OperatorTransformer();
			Set<IFormulaExtension> addedExtensions = transformer.transform(
					definition, factory, typeEnvironment);
			if (addedExtensions != null) {
				extensions.addAll(addedExtensions);
			}
			factory = factory.withExtensions(extensions);
			typeEnvironment = MathExtensionsUtilities
					.getTypeEnvironmentForFactory(typeEnvironment, factory);

		}
		return extensions;
	}
}
