/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.internal.ast.extensions.maths;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.datatype.IDatatype;
import org.eventb.core.ast.datatype.IDatatypeBuilder;
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
public class SimpleDatatypeExtension {

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
	
	/**
	 * Makes and populates a datatype builder from this datatype specification.
	 * Subclasses may override in order to populate with additional items, but
	 * must call this method to get a pre populated datatype builder instance.
	 * 
	 * @param factory
	 *            the formula factory used to make a IDatatypeBuilder instance
	 * @return a datatype builder
	 */
	protected IDatatypeBuilder toDatatypeBuilder(FormulaFactory factory) {
		final List<GivenType> parameters = new ArrayList<GivenType>();
		for (String parameter : typeArguments) {
			parameters.add(factory.makeGivenType(parameter));
		}
		return factory.makeDatatypeBuilder(identifier, parameters);
	}
	
	/**
	 * Converts this datatype specification into a finalized datatype instance.
	 * 
	 * @param factory
	 *            the formula factory used to make a IDatatypeBuilder instance
	 * @return a finalized datatype
	 * @see FormulaFactory#makeDatatypeBuilder(String, List)
	 */
	public final IDatatype toDatatype(FormulaFactory factory) {
		return toDatatypeBuilder(factory).finalizeDatatype();
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
