/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.sc.states;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.ast.DefaultVisitor;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.ast.extensions.maths.AstUtilities;
import org.eventb.core.ast.extensions.maths.MathExtensionsFactory;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.state.ISCState;
import org.eventb.core.tool.IStateType;
import org.eventb.internal.core.tool.state.State;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.sc.Messages;

/**
 * @author maamria
 *
 */
@SuppressWarnings("restriction")
public class DatatypeTable extends State implements ISCState {
	
	public static final IStateType<DatatypeTable> STATE_TYPE = SCCore.getToolStateType(TheoryPlugin.PLUGIN_ID + ".datatypeTable");
	
	private Map<String, DatatypeEntry> datatypes;
	// current datatype details
	private Type typeExpression;
	//private List<String> referencedTypes;
	private String currentDatatype = null;
	private String currentConstructor = null;
	private boolean isAdmissible = true;
	
	private FormulaFactory initialFactory;
	private FormulaFactory decoyFactory;

	public DatatypeTable(FormulaFactory initialFactory){
		this.initialFactory = initialFactory;
		decoyFactory = FormulaFactory.getInstance(initialFactory.getExtensions());
		datatypes = new LinkedHashMap<String, DatatypeTable.DatatypeEntry>();
	}
	
	@Override
	public IStateType<?> getStateType() {
		return STATE_TYPE;
	}
	
	public FormulaFactory augmentDecoyFormulaFactory() throws CoreException{
		assertMutable();
		DatatypeEntry entry = datatypes.get(currentDatatype);
		Set<IFormulaExtension> extensions  = entry.generateTypeExpression();
		if(extensions != null){
			decoyFactory = decoyFactory.withExtensions(extensions);
		}
		typeExpression = AstUtilities.
				createTypeExpression(currentDatatype, Arrays.asList(entry.typeArguments), decoyFactory);
		return decoyFactory;
	}
	
	public FormulaFactory augmentFormulaFactory() throws CoreException{
		assertMutable();
		DatatypeEntry entry = datatypes.get(currentDatatype);
		Set<IFormulaExtension> extensions  = entry.generateDatatypeExtensions();
		if(extensions != null){
			initialFactory = initialFactory.withExtensions(extensions);
		}
		decoyFactory = FormulaFactory.getInstance(initialFactory.getExtensions());
		return initialFactory;
	}
	
	public FormulaFactory reset() throws CoreException{
		assertMutable();
		decoyFactory = FormulaFactory.getInstance(initialFactory.getExtensions());
		currentDatatype = null;
		currentConstructor = null;
		isAdmissible = true;
		return initialFactory;
	}
	
	public void setErrorProne() throws CoreException{
		assertMutable();
		DatatypeEntry entry = datatypes.get(currentDatatype);
		if(entry != null)
			entry.setErrorProne();
	}
	
	public boolean isErrorProne() throws CoreException{
		assertMutable();
		DatatypeEntry entry = datatypes.get(currentDatatype);
		if(entry != null)
			return entry.isErrorProne();
		return true;
	}
	
	public void addDatatype(String name, String[] typeArgs) throws CoreException{
		assertMutable();
		datatypes.put(name, new DatatypeEntry(name, typeArgs));
		currentDatatype = name;
	}
	
	public boolean datatypeHasBaseConstructor() throws CoreException{
		assertMutable();
		return datatypes.get(currentDatatype).hasBaseConstructor(typeExpression, decoyFactory);
	}
	
	public boolean isAdmissible() {
		return isAdmissible;
	}
	
	public boolean isAllowedIdentifier(String identifier) throws CoreException {
		assertMutable();
		return datatypes.get(currentDatatype).isAllowedIdentifier(identifier);
	}
	
	@Override
	public void makeImmutable() {
		datatypes = Collections.unmodifiableMap(datatypes);
		super.makeImmutable();
	}
	
	public String checkName(String name) throws CoreException{
		assertMutable();
		if(datatypes.containsKey(name)){
			return Messages.scuser_IdenIsADatatypeNameError;
		}
		String code = null;
		for (DatatypeEntry entry : datatypes.values()){
			code = entry.checkName(name);
			if(code != null){
				break;
			}
		}
		return code;
	}

	public void addConstructor(String consName) throws CoreException{
		assertMutable();
		datatypes.get(currentDatatype).addConstructor(consName);
		currentConstructor = consName;
	}
	
	public boolean addDestructor(String destName, Type type) throws CoreException{
		assertMutable();
		boolean admissibility = datatypes.get(currentDatatype).addDestructor(currentConstructor, destName, type);
		if (!admissibility){
			isAdmissible = false;
		}
		return admissibility;
	}
	
	private class DatatypeEntry{
		
		String identifier;
		String[] typeArguments;
		List<String> listTypeArguments;
		LinkedHashMap<String, ConstructorEntry> constructors;
		boolean isErrorProne = false;
		
		public DatatypeEntry(String identifier, String[] typeArguments){
			this.identifier = identifier;
			this.typeArguments = typeArguments;
			this.listTypeArguments = Arrays.asList(typeArguments);
			this.constructors = new LinkedHashMap<String, DatatypeTable.DatatypeEntry.ConstructorEntry>();
		}
		
		public boolean isAllowedIdentifier(String identifier) throws CoreException {
			return listTypeArguments.contains(identifier);
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
		
		
		public String checkName(String name){
			if(constructors.containsKey(name)){
				return Messages.scuser_IdenIsAConsNameError;
			}
			String code = null;
			for (ConstructorEntry entry : constructors.values()){
				code = entry.checkName(name);
				if(code != null){
					break;
				}
			}
			return code;
		}
		
		public boolean addDestructor(String constructor, String destructor, Type type){
			return constructors.get(constructor).addDestructor(destructor, type);
		}
		
		public Set<IFormulaExtension> generateTypeExpression(){
			return MathExtensionsFactory.getSimpleDatatypeExtensions(identifier, typeArguments, decoyFactory);
		}
		
		public Set<IFormulaExtension> generateDatatypeExtensions(){
			if(isErrorProne)
				return null;
			Map<String, Map<String, String>> consMap = new LinkedHashMap<String, Map<String,String>>();
			for(String entry : constructors.keySet()){
				Map<String, String> destMap = new LinkedHashMap<String, String>();
				ConstructorEntry consEntry = constructors.get(entry);
				for(String destEntry : consEntry.destructors.keySet()){
					destMap.put(destEntry, consEntry.destructors.get(destEntry).toString());
				}
				consMap.put(entry, destMap);
			}
			return MathExtensionsFactory.getCompleteDatatypeExtensions(identifier, typeArguments, consMap, initialFactory);
		}
		
		public class ConstructorEntry{
			
			Map<String, Type> destructors;
			
			public ConstructorEntry(){
				destructors = new LinkedHashMap<String, Type>();
			}
			
			public String checkName(String name){
				if(destructors.containsKey(name)){
					return Messages.scuser_IdenIsADesNameError;
				}
				return null;
			}
			
			public boolean isBase(Type typeExpression, FormulaFactory ff){
				for(Type type : destructors.values()){
					BaseTypeVisitor typeVisitor = new BaseTypeVisitor(typeExpression, ff);
					type.toExpression(ff).accept(typeVisitor);
					if(!typeVisitor.isBaseType()){
						return false;
					}
				}
				return true;
			}
			
			public boolean addDestructor(String name, Type type){
				// check for admissibility before inserting
				{
					
				}
				destructors.put(name, type);
				return true;
			}
		}
		
		public class BaseTypeVisitor extends DefaultVisitor {

			private Expression typeExpression;
			private boolean isBase = true;
			
			public BaseTypeVisitor(Type type, FormulaFactory ff){
				this.typeExpression = type.toExpression(ff);
			}
			
			public boolean isBaseType(){
				return isBase;
			}
			
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
