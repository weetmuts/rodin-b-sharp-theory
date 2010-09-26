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
import org.eventb.theory.core.IFormulaExtensionsSource;
import org.eventb.theory.core.ISCDatatypeDefinition;
import org.eventb.theory.core.ISCNewOperatorDefinition;
import org.eventb.theory.core.ISCTypeParameter;
import org.eventb.theory.internal.core.util.MathExtensionsUtilities;

/**
 * @author maamria
 *
 */
public class TheoryTransformer 
extends DefinitionTransformer<IFormulaExtensionsSource<?>>{

	private ITypeEnvironment typeEnvironment;
	private IFormulaExtensionsSource<?> source;

	protected void initialise() throws CoreException {
		ISCTypeParameter[] typeParameters = getTypeParameters(source);
		for (ISCTypeParameter typeParameter : typeParameters) {
			typeEnvironment.addGivenSet(typeParameter.getIdentifierString());
		}
	}

	protected ISCNewOperatorDefinition[] getOperators(IFormulaExtensionsSource<?> source)
		throws CoreException{
		return source.getSCNewOperatorDefinitions();
	}
	
	protected ISCDatatypeDefinition[] getDatatypes(IFormulaExtensionsSource<?> source)
		throws CoreException{
		return source.getSCDatatypeDefinitions();
	}

	protected ISCTypeParameter[] getTypeParameters(IFormulaExtensionsSource<?> source) 
		throws CoreException{
		return source.getSCTypeParameters();
	}

	@Override
	public Set<IFormulaExtension> transform(IFormulaExtensionsSource<?> source,
			FormulaFactory factory, ITypeEnvironment typeEnvironment)
			throws CoreException {

		this.typeEnvironment = typeEnvironment;
		this.source = source;
		Set<IFormulaExtension> extensions = new LinkedHashSet<IFormulaExtension>();
		
		initialise();

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
