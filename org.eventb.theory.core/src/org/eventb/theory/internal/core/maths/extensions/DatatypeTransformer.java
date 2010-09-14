/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.internal.core.maths.extensions;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.theory.core.IElementTransformer;
import org.eventb.theory.core.ISCConstructorArgument;
import org.eventb.theory.core.ISCDatatypeConstructor;
import org.eventb.theory.core.ISCDatatypeDefinition;
import org.eventb.theory.core.ISCTypeArgument;
import org.eventb.theory.core.maths.MathExtensionsFacilitator;
import org.rodinp.core.IInternalElementType;

/**
 * @author maamria
 *
 */
public class DatatypeTransformer implements IElementTransformer<ISCDatatypeDefinition, Set<IFormulaExtension>>{

	
	
	@Override
	public Set<IFormulaExtension> transform(final ISCDatatypeDefinition definition, 
			final FormulaFactory factory, ITypeEnvironment typeEnvironment) throws CoreException {
		if(definition.hasError()){
			return null;
		}
		
		final String typeName = definition.getIdentifierString();
		ISCTypeArgument[] scTypeArguments = definition.getTypeArguments();
		final String[] typeArguments = new String[scTypeArguments.length];
		for (int i = 0 ; i < typeArguments.length; i++){
			typeArguments[i] = scTypeArguments[i].getSCGivenType(factory).toString();
		}
		FormulaFactory tempFactory = factory.withExtensions( 
				MathExtensionsFacilitator.getSimpleDatatypeExtensions(typeName, typeArguments, factory));
						
		ISCDatatypeConstructor[] constructors = definition.getConstructors();
		final Map<String, Map<String, Type>> datatypeCons = new HashMap<String, Map<String,Type>>();
		for(ISCDatatypeConstructor cons : constructors){
			String consIdent = cons.getIdentifierString();
			ISCConstructorArgument[] destructors = cons.getConstructorArguments();
			if(destructors.length == 0 ){
				datatypeCons.put(consIdent, new HashMap<String, Type>());
			}
			else {
				Map<String, Type> datatypeDes = new HashMap<String, Type>();
				for(ISCConstructorArgument dest : destructors){
					datatypeDes.put(dest.getIdentifierString(), dest.getType(tempFactory));
				}
				datatypeCons.put(consIdent, datatypeDes);
			}
		}
		return MathExtensionsFacilitator.getCompleteDatatypeExtensions(typeName, typeArguments, datatypeCons, factory);
	}

	@Override
	public IInternalElementType<ISCDatatypeDefinition> getElementType()
			throws CoreException {
		// TODO Auto-generated method stub
		return ISCDatatypeDefinition.ELEMENT_TYPE;
	}

}
