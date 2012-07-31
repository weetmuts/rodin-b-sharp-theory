/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.internal.ast.extensions.maths;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.LanguageVersion;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.ast.extension.datatype.IArgument;
import org.eventb.core.ast.extension.datatype.IConstructorMediator;
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

	@Override
	public void addConstructors(IConstructorMediator mediator) {
		// add the type constructor just in case we deal with inductive dt
		FormulaFactory factory = mediator.getFactory();
		factory = factory.withExtensions(Collections.singleton((IFormulaExtension)
						mediator.getTypeConstructor()));
		for (String cons : constructors.keySet()){
			Map<String, String> destructors = constructors.get(cons);
			if(destructors.size() == 0 ){
				mediator.addConstructor(cons, cons + CONS_ID);
			}
			else{
				List<IArgument> arguments = new ArrayList<IArgument>();
				for (String dest : destructors.keySet()){
					String typeStr = destructors.get(dest);
					Type argumentType = factory.parseType(typeStr, LanguageVersion.V2).getParsedType();
					arguments.add(mediator.newArgument(dest,mediator.newArgumentType(argumentType)));
				}
				mediator.addConstructor(cons, cons + CONS_ID, arguments );
			}
		}
		
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