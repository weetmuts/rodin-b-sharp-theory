/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.maths;

import java.util.ArrayList;
import java.util.Map;

import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.datatype.IArgument;
import org.eventb.core.ast.extension.datatype.IConstructorMediator;

/**
 * An implementation of a datatype definition extension.
 * 
 * <p> A complete datatype definition has at least one constructor, and it has to have at
 * least one base constructor.
 * <p> A constructor is base if and only if all its destructors' types do not refer to the
 * datatype being defined.
 * 
 * @author maamria
 *
 */
class CompleteDatatypeExtension extends SimpleDatatypeExtension{

	private Map<String, Map<String, Type>> constructors;
	
	/**
	 * @param identifier
	 * @param typeArguments
	 */
	public CompleteDatatypeExtension(String identifier, 
			String[] typeArguments,
			Map<String, Map<String, Type>> constructors) {
		super(identifier, typeArguments);
		this.constructors = constructors;
	}
	
	@Override
	public void addConstructors(IConstructorMediator mediator) {
		for (String cons : constructors.keySet()){
			Map<String, Type> destructors = constructors.get(cons);
			if(destructors.size() == 0 ){
				mediator.addConstructor(cons, cons + CONS_ID);
			}
			else{
				ArrayList<IArgument> arguments = new ArrayList<IArgument>();
				for (String dest : destructors.keySet()){
					arguments.add(mediator.newArgument(dest,mediator.newArgumentType(destructors.get(dest))));
				}
				mediator.addConstructor(cons, cons + CONS_ID, arguments );
			}
		}
		
	}

}
