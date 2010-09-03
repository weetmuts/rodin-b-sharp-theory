/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.internal.core.sc.states;

import java.util.ArrayList;
import java.util.HashMap;

import org.eventb.core.ast.DefaultVisitor;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.datatype.IArgument;
import org.eventb.core.ast.extension.datatype.IConstructorMediator;
import org.eventb.core.ast.extension.datatype.IDatatype;
import org.eventb.core.ast.extension.datatype.IDatatypeExtension;
import org.eventb.core.ast.extension.datatype.ITypeConstructorMediator;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.state.ISCState;
import org.eventb.core.tool.IStateType;
import org.eventb.internal.core.tool.state.State;
import org.eventb.theory.core.plugin.TheoryPlugin;

/**
 * @author maamria
 *
 */
@SuppressWarnings("restriction")
public class DatatypeTable extends State implements ISCState{
	
	public static enum ERROR_CODE{NAME_IS_A_DATATYPE, NAME_IS_A_CONSTRUCTOR, NAME_IS_A_DESTRUCTOR};
	
	public final static IStateType<DatatypeTable> STATE_TYPE = SCCore.getToolStateType(
			TheoryPlugin.PLUGIN_ID + ".datatypeTable");
	
	final static String DATATYPE_ID = " Datatype";
	final static String CONS_ID = " Constructor";
	
	private HashMap<String, DatatypeEntry> datatypes;
	
	private String currentDatatype = null;
	private String currentConstructor = null;
	
	private FormulaFactory initialFactory;
	private FormulaFactory decoyFactory;

	
	public DatatypeTable(FormulaFactory initialFactory){
		this.initialFactory = initialFactory;
		decoyFactory = FormulaFactory.getInstance(initialFactory.getExtensions());
		datatypes = new HashMap<String, DatatypeTable.DatatypeEntry>();
	}
	
	@Override
	public IStateType<?> getStateType() {
		// TODO Auto-generated method stub
		return STATE_TYPE;
	}
	
	
	public FormulaFactory augmentDecoyFormulaFactory(){
		IDatatypeExtension extension  = datatypes.get(currentDatatype).generateTypeExpression();
		if(extension != null){
			IDatatype datatype = decoyFactory.makeDatatype(extension);
			decoyFactory = decoyFactory.withExtensions(datatype.getExtensions());
		}
		return decoyFactory;
	}
	
	public FormulaFactory augmentFormulaFactory(){
		IDatatypeExtension extension  = datatypes.get(currentDatatype).generateDatatypeExtension();
		if(extension != null){
			IDatatype datatype = initialFactory.makeDatatype(extension);
			initialFactory = initialFactory.withExtensions(datatype.getExtensions());
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
	
	/**
	 * A call to method <code>isNameOk(String)</code> should be made to ensure unique names.
	 * @param name
	 * @param typeArgs
	 */
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
		
		public IDatatypeExtension generateTypeExpression(){
			return new IDatatypeExtension() {
				
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
					for (String arg : typeArguments){
						mediator.addTypeParam(arg);
					}
					
				}
				
				@Override
				public void addConstructors(IConstructorMediator mediator) {
					// initially no constructors
					
				}
			};
		}
		
		public IDatatypeExtension generateDatatypeExtension(){
			if(isErrorProne)
				return null;
			return new IDatatypeExtension() {
				
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
					for (String cons : constructors.keySet()){
						ConstructorEntry entry = constructors.get(cons);
						if(!entry.hasDestructors()){
							mediator.addConstructor(cons, cons + CONS_ID);
						}
						else{
							ArrayList<IArgument> arguments = new ArrayList<IArgument>();
							for (String dest : entry.destructors.keySet()){
								arguments.add(mediator.newArgument(dest,mediator.newArgumentType(entry.destructors.get(dest))));
							}
							mediator.addConstructor(cons, cons + CONS_ID, arguments );
						}
					}
					
				}
			};
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
			
			public boolean hasDestructors(){
				return destructors.size() > 0 ;
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
