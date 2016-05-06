/*******************************************************************************
 * Copyright (c) 2016 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University of Southampton - initial API and implementation
 *******************************************************************************/

package org.eventb.core.internal.ast.extensions.maths;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eventb.core.ast.Type;
import org.eventb.core.ast.datatype.IDatatype;
import org.eventb.core.ast.extensions.maths.IDatatypeOrigin;

/**
 * <p>
 * An implementation of datatype origin. 
 * </p>
 *
 * @author htson
 * @version 0.1
 * @see IDatatype
 * @since 3.1.0
 * @noextend This class is not intended to be subclassed by clients.
 */
public class DatatypeOrigin implements IDatatypeOrigin {
	// The name of the datatype.
	private String name;

	// The map of type arguments to their corresponding type.
	private Map<String, Type> typeArguments;
	
	// The list of constructors.
	private List<String> constructors;
	
	// The map of constructors' destructors
	private Map<String, List<String>> destructors;
	
	// The map of constructors' destructor types.
	private Map<String, List<Type>> destructorTypes;
	
	/**
	 * Construct an instance of the datatype origin for datatype with a given
	 * name.
	 * 
	 * @param name
	 *            the name of the datatype as defined by this origin.
	 */
	public DatatypeOrigin(String name) {
		this.name = name;
		this.typeArguments = new HashMap<String, Type>();
		this.constructors = new ArrayList<String>();
		this.destructors = new HashMap<String, List<String>>();
		this.destructorTypes = new HashMap<String, List<Type>>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IDatatypeDefinition#addTypeArgument(String, Type)
	 */
	@Override
	public void addTypeArgument(String typeArgument, Type type) {
		typeArguments.put(typeArgument, type);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IDatatypeDefinition#addConstructor(String[])
	 */
	@Override
	public void addConstructor(String... constructors) {
		Collections.addAll(this.constructors, constructors);
		for (String constructor : constructors) {
			destructors.put(constructor, new ArrayList<String>());
			destructorTypes.put(constructor, new ArrayList<Type>());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IDatatypeDefinition#addDestructor(String, String, Type)
	 */
	@Override
	public void addDestructor(String constructor, String destructor, Type type) {
		// Assert PRECONDITION
		assert constructors.contains(constructor);

		destructors.get(constructor).add(destructor);
		destructorTypes.get(constructor).add(type);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IDatatypeOrigin#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	/* (non-Javadoc)
	 * @see IDatatypeOrigin#getTypeArguments()
	 */
	@Override
	public String[] getTypeArguments() {
		Set<String> keySet = typeArguments.keySet();
		return keySet.toArray(new String[keySet.size()]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IDatatypeOrigin#getGivenType(String)
	 */
	@Override
	public Type getGivenType(String typeArgument) {
		return typeArguments.get(typeArgument);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IDatatypeOrigin#getConstructors()
	 */
	@Override
	public String[] getConstructors() {
		return constructors.toArray(new String[constructors.size()]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IDatatypeOrigin#getDestructors(String)
	 */
	@Override
	public String[] getDestructors(String constructor) {
		List<String> d = destructors.get(constructor);
		return d.toArray(new String[d.size()]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IDatatypeOrigin#getDestructorTypes(String)
	 */
	@Override
	public Type[] getDestructorTypes(String constructor) {
		List<Type> t = destructorTypes.get(constructor);
		return t.toArray(new Type[t.size()]);
	}

}
