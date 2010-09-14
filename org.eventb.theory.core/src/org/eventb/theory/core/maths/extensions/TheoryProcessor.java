/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.maths.extensions;

import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.theory.core.ISCDatatypeDefinition;
import org.eventb.theory.core.ISCNewOperatorDefinition;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.ISCTypeParameter;
import org.eventb.theory.core.maths.MathExtensionsFacilitator;
import org.eventb.theory.internal.core.maths.extensions.DatatypeTransformer;
import org.eventb.theory.internal.core.maths.extensions.OperatorTransformer;

/**
 * @author maamria
 * 
 */
public class TheoryProcessor {

	private ISCTheoryRoot root;
	private FormulaFactory factory;
	private ITypeEnvironment typeEnvironment;
	private Set<IFormulaExtension> extensions;

	public TheoryProcessor(ISCTheoryRoot root, FormulaFactory factory) {
		this.root = root;
		this.factory = factory;
		this.typeEnvironment = factory.makeTypeEnvironment();
		this.extensions = new LinkedHashSet<IFormulaExtension>();
	}

	public void initialise() throws CoreException {
		ISCTypeParameter[] typeParameters = root.getSCTypeParameters();
		for (ISCTypeParameter typeParameter : typeParameters) {
			typeEnvironment.addGivenSet(typeParameter.getIdentifierString());
		}
	}

	public void processExtensions() throws CoreException {
		ISCDatatypeDefinition datatypeDefinitions[] = root
				.getSCDatatypeDefinitions();
		for (ISCDatatypeDefinition definition : datatypeDefinitions) {
			if (!definition.hasError() && definition.hasValidatedAttribute()
					&& definition.isValidated()) {
				DatatypeTransformer transformer = new DatatypeTransformer();
				extensions.addAll(transformer.transform(definition, factory,
						typeEnvironment));
				factory = factory.withExtensions(extensions);
				typeEnvironment = MathExtensionsFacilitator
						.getTypeEnvironmentForFactory(typeEnvironment, factory);
			}
		}
		ISCNewOperatorDefinition operatorDefinitions[] = root
				.getSCNewOperatorDefinitions();
		for (ISCNewOperatorDefinition definition : operatorDefinitions) {
			if (!definition.hasError() && definition.hasValidatedAttribute()
					&& definition.isValidated()) {
				OperatorTransformer transformer = new OperatorTransformer();
				extensions.addAll(transformer.transform(definition, factory,
						typeEnvironment));
				factory = factory.withExtensions(extensions);
				typeEnvironment = MathExtensionsFacilitator
						.getTypeEnvironmentForFactory(typeEnvironment, factory);
			}
		}
	}
	
	public Set<IFormulaExtension> getExtensions(){
		return extensions;
	}

}
