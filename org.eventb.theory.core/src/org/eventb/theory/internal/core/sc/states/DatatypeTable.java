/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.internal.core.sc.states;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eventb.core.ast.DefaultVisitor;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.tool.IStateType;
import org.eventb.internal.core.tool.state.State;
import org.eventb.theory.core.maths.MathExtensionsFactory;

/**
 * @author maamria
 *
 */
@SuppressWarnings("restriction")
public class DatatypeTable extends State implements IDatatypeTable{
	
	private HashMap<String, DatatypeEntry> datatypes;
	
	private String currentDatatype = null;
	private String currentConstructor = null;
	
	private FormulaFactory initialFactory;
	private FormulaFactory decoyFactory;
	
	private final MathExtensionsFactory extensionsFactory;

	
	public DatatypeTable(FormulaFactory initialFactory){
		this.initialFactory = initialFactory;
		decoyFactory = FormulaFactory.getInstance(initialFactory.getExtensions());
		datatypes = new HashMap<String, DatatypeTable.DatatypeEntry>();
		extensionsFactory = MathExtensionsFactory.getExtensionsFactory();
	}
	
	@Override
	public IStateType<?> getStateType() {
		// TODO Auto-generated method stub
		return STATE_TYPE;
	}
	
	
	public FormulaFactory augmentDecoyFormulaFactory(){
		Set<IFormulaExtension> extensions  = datatypes.get(currentDatatype).generateTypeExpression();
		if(extensions != null){
			decoyFactory = decoyFactory.withExtensions(extensions);
		}
		return decoyFactory;
	}
	
	public FormulaFactory augmentFormulaFactory(){
		Set<IFormulaExtension> extensions  = datatypes.get(currentDatatype).generateDatatypeExtensions();
		if(extensions != null){
			initialFactory = initialFactory.withExtensions(extensions);
		}
		decoyFactory = FormulaFactory.getInstance(initialFactory.getExtensions());
		return initialFactory;
	}
	
	public FormulaFactory reset(){
		decoyFactory = FormulaFactory.getInstance(initialFactory.getExtensions());
		currentDatatype = null;
		currentConstructor = null;
		return initialFactory;
	}
	
	public void setErrorProne(){
		DatatypeEntry entry = datatypes.get(currentDatatype);
		if(entry != null)
			entry.setErrorProne();
	}
	
	public boolean isErrorProne(){
		DatatypeEntry entry = datatypes.get(currentDatatype);
		if(entry != null)
			return entry.isErrorProne();
		return true;
	}
	
	public void addDatatype(String name, String[] typeArgs){
		datatypes.put(name, new DatatypeEntry(name, typeArgs));
		currentDatatype = name;
	}
	
	public boolean datatypeHasBaseConstructor(Type typeExpression){
		return datatypes.get(currentDatatype).hasBaseConstructor(typeExpression, decoyFactory);
	}
	
	public ERROR_CODE isNameOk(String name){
		if(datatypes.containsKey(name)){
			return ERROR_CODE.NAME_IS_A_DATATYPE;
		}
		ERROR_CODE code = null;
		for (DatatypeEntry entry : datatypes.values()){
			code = entry.isNameOk(name);
			if(code != null){
				break;
			}
		}
		return code;
	}

	public void addConstructor(String consName){
		datatypes.get(currentDatatype).addConstructor(consName);
		currentConstructor = consName;
	}
	
	public void addDestructor(String destName, Type type){
		datatypes.get(currentDatatype).addDestructor(currentConstructor, destName, type);
	}
	
	private class DatatypeEntry{
		
		String identifier;
		String[] typeArguments;
		HashMap<String, ConstructorEntry> constructors;
		boolean isErrorProne = false;
		
		public DatatypeEntry(String identifier, String[] typeArguments){
			this.identifier = identifier;
			this.typeArguments = typeArguments;
			this.constructors = new HashMap<String, DatatypeTable.DatatypeEntry.ConstructorEntry>();
		}
		
		public void setErrorProne(){
			isErrorProne = true;
		}
		
		public boolean isErrorProne(){
			return isErrorProne;
		}
		
		public boolean hasBaseConstructor(Type typeExpression, FormulaFactory ff){
			boolean result = false;
			for(ConstructorEntry entry : constructors.values()){
				if(entry.isBase(typeExpression, ff)){
					result = true;
					break;
				}
			}
			return result;
		}
		
		public void addConstructor(String name){
			constructors.put(name, new ConstructorEntry());
		}
		
		
		public ERROR_CODE isNameOk(String name){
			if(constructors.containsKey(name)){
				return ERROR_CODE.NAME_IS_A_CONSTRUCTOR;
			}
			ERROR_CODE code = null;
			for (ConstructorEntry entry : constructors.values()){
				code = entry.isNameOk(name);
				if(code != null){
					break;
				}
			}
			return code;
		}
		
		public void addDestructor(String constructor, String destructor, Type type){
			constructors.get(constructor).addDestructor(destructor, type);
		}
		
		public Set<IFormulaExtension> generateTypeExpression(){
			return extensionsFactory.getSimpleDatatypeExtensions(identifier, typeArguments, decoyFactory);
		}
		
		public Set<IFormulaExtension> generateDatatypeExtensions(){
			if(isErrorProne)
				return null;
			Map<String, Map<String, Type>> consMap = new HashMap<String, Map<String,Type>>();
			for(String entry : constructors.keySet()){
				Map<String, Type> destMap = new HashMap<String, Type>();
				ConstructorEntry consEntry = constructors.get(entry);
				for(String destEntry : consEntry.destructors.keySet()){
					destMap.put(destEntry, consEntry.destructors.get(destEntry));
				}
				consMap.put(entry, destMap);
			}
			return extensionsFactory.getCompleteDatatypeExtensions(identifier, typeArguments, consMap, initialFactory);
		}
		
		public class ConstructorEntry{
			
			HashMap<String, Type> destructors;
			
			public ConstructorEntry(){
				destructors = new HashMap<String, Type>();
			}
			
			public ERROR_CODE isNameOk(String name){
				if(destructors.containsKey(name)){
					return ERROR_CODE.NAME_IS_A_DESTRUCTOR;
				}
				return null;
			}
			
			public boolean isBase(Type typeExpression, FormulaFactory ff){
				for(Type type : destructors.values()){
					TypeVisitor typeVisitor = new TypeVisitor(typeExpression, ff);
					type.toExpression(ff).accept(typeVisitor);
					if(!typeVisitor.isBaseType()){
						return false;
					}
				}
				return true;
			}
			
			public void addDestructor(String name, Type type){
				destructors.put(name, type);
			}
		}
		
		public class TypeVisitor extends DefaultVisitor {

			private Expression typeExpression;
			private boolean isBase = true;
			
			public TypeVisitor(Type type, FormulaFactory ff){
				this.typeExpression = type.toExpression(ff);
			}
			
			public boolean isBaseType(){
				return isBase;
			}
			
			/**
			 * @since 2.0
			 */
			@Override
			public boolean enterExtendedExpression(ExtendedExpression expression) {
				if(expression.getTag() == typeExpression.getTag()){
					isBase = false;
					return false;
				}
				return true;
			}
		}
	}
	
}
