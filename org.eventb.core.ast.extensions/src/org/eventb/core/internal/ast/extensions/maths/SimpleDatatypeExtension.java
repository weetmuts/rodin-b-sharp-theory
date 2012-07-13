/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.internal.ast.extensions.maths;

import java.util.Arrays;

import org.eventb.core.ast.extension.datatype.IConstructorMediator;
import org.eventb.core.ast.extension.datatype.IDatatypeExtension;
import org.eventb.core.ast.extension.datatype.ITypeConstructorMediator;
import org.eventb.core.ast.extensions.maths.AstUtilities;

/**
 * An implementation of a datatype type expression extension. This requires the name of
 * the datatype and the types on which it is polymorphic e.g., List(A).
 * 
 * @since 1.0
 * 
 * @author maamria
 *
 */
public class SimpleDatatypeExtension implements IDatatypeExtension{

	/**
	 * ID used to name the datatype.
	 */
	public final static String DATATYPE_ID = " Datatype";
	/**
	 * ID used by constructors.
	 */
	public final static String CONS_ID = " Constructor";
	
	protected String identifier;
	protected String[] typeArguments;
	
	public SimpleDatatypeExtension(String identifier, String[] typeArguments){
		AstUtilities.ensureNotNull(identifier, typeArguments);
		this.identifier = identifier;
		this.typeArguments = typeArguments;
	}
	
	@Override
	public String getTypeName() {
		return identifier;
	}

	
	@Override
	public String getId() {
		return identifier + DATATYPE_ID;
	}

	@Override
	public void addTypeParameters(ITypeConstructorMediator mediator) {
		for(String arg : typeArguments){
			mediator.addTypeParam(arg);
		}
	}

	@Override
	public void addConstructors(IConstructorMediator mediator) {
		// no constructors
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (obj instanceof SimpleDatatypeExtension){
			SimpleDatatypeExtension other = (SimpleDatatypeExtension) obj;
			return identifier.equals(other.identifier) &&
					Arrays.asList(typeArguments).equals(Arrays.asList(other.typeArguments));
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return identifier.hashCode()*13 + Arrays.asList(typeArguments).hashCode()*17;
	}
}
