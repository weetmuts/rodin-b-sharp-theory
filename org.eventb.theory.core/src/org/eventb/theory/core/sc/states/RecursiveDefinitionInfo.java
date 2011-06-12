package org.eventb.theory.core.sc.states;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.core.ast.extension.datatype.IDatatype;
import org.eventb.core.tool.IStateType;
import org.eventb.internal.core.tool.state.State;
import org.eventb.theory.core.IRecursiveDefinitionCase;

/**
 * 
 * @author maamria
 * 
 */
@SuppressWarnings("restriction")
public class RecursiveDefinitionInfo extends State implements IRecursiveDefinitionInfo {

	private Map<IRecursiveDefinitionCase, CaseEntry> baseEntries;
	private Map<IRecursiveDefinitionCase, CaseEntry> inductiveEntries;
	private FreeIdentifier inductiveArgument;
	private boolean accurate = true;
	private IDatatype datatype;
	private Set<IExpressionExtension> coveredConstructors;
	
	public RecursiveDefinitionInfo(){
		baseEntries = new LinkedHashMap<IRecursiveDefinitionCase, CaseEntry>();
		inductiveEntries = new LinkedHashMap<IRecursiveDefinitionCase, CaseEntry>();
		coveredConstructors = new LinkedHashSet<IExpressionExtension>();
	}
	
	public void setInductiveArgument(FreeIdentifier ident, FormulaFactory factory) throws CoreException{
		assertMutable();
		this.inductiveArgument = ident;
		Type type = ident.getType();
		
		if (type == null || !( type.toExpression(factory) instanceof ExtendedExpression) ||
				!(((ExtendedExpression)type.toExpression(factory)).getExtension().getOrigin() instanceof IDatatype)){
			throw new IllegalStateException("Illegal inductive argument");
		}
		IExpressionExtension extension = ((ExtendedExpression)type.toExpression(factory)).getExtension();
		datatype = (IDatatype) extension.getOrigin();
	}
	
	public boolean isConstructor(IExpressionExtension extension) throws CoreException{
		assertMutable();
		return datatype.isConstructor(extension);
	}
	
	public boolean isCoveredConstuctor(IExpressionExtension extension) throws CoreException{
		assertMutable();
		return coveredConstructors.contains(extension);
	}
	
	public boolean coveredAllConstructors() throws CoreException{
		assertImmutable();
		return coveredConstructors.containsAll(datatype.getConstructors());
	}
	
	public FreeIdentifier getInductiveArgument() throws CoreException{
		return inductiveArgument;
	}
	
	public void addEntry(IRecursiveDefinitionCase defCase, 
			ExtendedExpression exp, ITypeEnvironment typeEnvironment) throws CoreException{
		assertMutable();
		CaseEntry caseEntry = new CaseEntry(exp, typeEnvironment);
		if (caseEntry.isBaseCase()){
			baseEntries.put(defCase, caseEntry);
		}
		else {
			inductiveEntries.put(defCase, caseEntry);
		}
		coveredConstructors.add(exp.getExtension());
	}
	
	public Map<IRecursiveDefinitionCase, CaseEntry> getBaseEntries() throws CoreException{
		assertImmutable();
		return baseEntries;
	}
	
	public Map<IRecursiveDefinitionCase, CaseEntry> getInductiveEntries() throws CoreException{
		assertImmutable();
		return inductiveEntries;
	}
	
	public boolean isAccurate() throws CoreException{
		assertImmutable();
		return accurate;
	}

	public void setNotAccurate() throws CoreException{
		assertMutable();
		this.accurate = false;
	}

	@Override
	public void makeImmutable() {
		baseEntries = Collections.unmodifiableMap(baseEntries);
		inductiveEntries = Collections.unmodifiableMap(inductiveEntries);
		super.makeImmutable();
	}
	
	@Override
	public IStateType<?> getStateType() {
		return STATE_TYPE;
	}
	
	

}

