/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.internal.ast.extensions.maths;

import java.util.Map;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.datatype.IConstructorBuilder;
import org.eventb.core.ast.datatype.IDatatypeBuilder;
import org.eventb.core.ast.extensions.maths.AstUtilities;

/**
 * An implementation of a datatype definition extension.
 * 
 * <p> A complete datatype definition has at least one constructor, and it has to have at
 * least one base constructor.
 * <p> A constructor is base if and only if all its destructors' types do not refer to the
 * datatype being defined.
 * 
 * @since 1.0
 * 
 * @see SimpleDatatypeExtension
 * 
 * @author maamria
 *
 */
public class CompleteDatatypeExtension extends SimpleDatatypeExtension{

	private Map<String, Map<String, String>> constructors;
	
	public CompleteDatatypeExtension(String identifier, 
			String[] typeArguments,
			Map<String, Map<String, String>> constructors) {
		super(identifier, typeArguments);
		AstUtilities.ensureNotNull(constructors);
		this.constructors = constructors;
	}

	private void addConstructors(IDatatypeBuilder dtBuilder) {
		// add the type constructor just in case we deal with inductive dt
		FormulaFactory factory = dtBuilder.getFactory();
		for (String consName : constructors.keySet()){
			final IConstructorBuilder cons = dtBuilder.addConstructor(consName);
			Map<String, String> destructors = constructors.get(consName);
			for (String dest : destructors.keySet()){
				final String typeStr = destructors.get(dest);
				final Type argumentType = factory.parseType(typeStr).getParsedType();
				cons.addArgument(dest, argumentType);
			}
		}
	}
	
	@Override
	protected IDatatypeBuilder toDatatypeBuilder(FormulaFactory factory) {
		final IDatatypeBuilder dtBuilder = super.toDatatypeBuilder(factory);
		addConstructors(dtBuilder);
		return dtBuilder;
	}
	
	public boolean equals(Object o){
		if (o == this)
			return true;
		if (o instanceof CompleteDatatypeExtension){
			CompleteDatatypeExtension other = (CompleteDatatypeExtension) o;
			return super.equals(o) && constructors.equals(other.constructors);
		}
		return false;
	}
	
	public int hashCode(){
		return super.hashCode() + 23 * constructors.hashCode();
	}

}
