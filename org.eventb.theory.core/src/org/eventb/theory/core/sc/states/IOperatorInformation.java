/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.sc.states;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.ast.extension.IOperatorProperties.FormulaType;
import org.eventb.core.ast.extension.IOperatorProperties.Notation;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.state.ISCState;
import org.eventb.core.tool.IStateType;
import org.eventb.theory.core.INewOperatorDefinition;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.plugin.TheoryPlugin;

/**
 * Common protocol for a repository state holding information about a new operator.
 * 
 * <p> Objects of this type can be readily "converted" into an operator extension, if and only if they provide
 * all information needed to create such an extension as per AST requirements.
 * 
 * @author maamria
 *
 */
public interface IOperatorInformation extends ISCState{

	public final static IStateType<IOperatorInformation> STATE_TYPE = SCCore
		.getToolStateType(TheoryPlugin.PLUGIN_ID + ".operatorInformation");
	
	/**
	 * Returns whether this operator is an expression operator.
	 * @return whether this operator is an expression
	 */
	public boolean isExpressionOperator() ;
	
	/**
	 * Returns whether the given identifier is allowed to occur in the definition or the condition of
	 * this operator.
	 * @param ident the identifier
	 * @return whether the identifier is allowed to be used
	 */
	public boolean isAllowedIdentifier(FreeIdentifier ident) ;

	/**
	 * Adds an operator argument with the given name and type.
	 * @param ident the name of the argument
	 * @param type the type of the argument
	 */
	public void addOperatorArgument(String ident, Type type);
	
	/**
	 * Returns the operator arguments of this operator
	 * @return operator arguments
	 */
	public Map<String, Type> getOperatorArguments();
	
	/**
	 * @return the syntax
	 */
	public String getSyntax() ;

	/**
	 * @param syntax
	 *            the syntax to set
	 */
	public void setSyntax(String syntax) ;

	/**
	 * @return the formulaType
	 */
	public FormulaType getFormulaType() ;

	/**
	 * @param formulaType
	 *            the formulaType to set
	 */
	public void setFormulaType(FormulaType formulaType) ;

	/**
	 * @param notation
	 *            the notation to set
	 */
	public void setNotation(Notation notation) ;
	
	/**
	 * @return the notation
	 */
	public Notation getNotation() ;

	/**
	 * @param isAssociative
	 *            the isAssociative to set
	 */
	public void setAssociative(boolean isAssociative) ;

	/**
	 * @param isCommutative
	 *            the isCommutative to set
	 */
	public void setCommutative(boolean isCommutative) ;
	
	/**
	 * Sets the definition of the current operator.
	 * @param definition the definition
	 */
	public void setDefinition(IDefinition definition);
	
	/**
	 * @return the wdCondition
	 */
	public Predicate getWdCondition() ;
	
	/**
	 * Returns the well-definedness condition based on the D operator.
	 * @return the D WD condition
	 */
	public Predicate getD_WDCondition();
	
	/**
	 * Sets the D WD condition of this operator.
	 * @param dwdCondition the D WD condition
	 */
	public void setD_WDCondition(Predicate dWDCondition);

	/**
	 * @param wdCondition
	 *            the wdCondition to set
	 */
	public void addWDCondition(Predicate wdCondition) ;

	/**
	 * Returns the resultant type of this operator if it produces expressions.
	 * @return the resultant type if any, or <code>null</code>
	 */
	public Type getResultantType() ;
	
	/**
	 * Set the resultant type of this operator if it produces expressions.
	 * @param resultantType the resultant type
	 */
	public void setResultantType(Type resultantType) ;

	/**
	 * Sets the operator as having an error associated with it.
	 */
	public void setHasError() ;

	/**
	 * @return the hasError
	 */
	public boolean hasError() ;

	/**
	 * Returns the mathematical extension corresponding to this operator information.
	 * @param sourceOfExtension the source of the extension
	 * @return the formula extension
	 */
	public IFormulaExtension getExtension(Object sourceOfExtension) ;
	
	/**
	 * Returns an interim extension useful when checking a recursive definition.
	 * @return the formula extension
	 */
	public IFormulaExtension getInterimExtension() ;
	
	/**
	 * Generates the set of definitional rules that effectively provide the semantics for the operator being defined.
	 * @param originDefinition the origin definition
	 * @param theoryRoot the parent root where the rules will be created
	 * @param enhancedFactory the formula factory that know about this extension
	 * @throws CoreException
	 */
	public void generateDefinitionalRule(
			INewOperatorDefinition originDefinition, 
			ISCTheoryRoot theoryRoot,
			FormulaFactory enhancedFactory) throws CoreException;
	
	/**
	 * 
	 * @author maamria
	 *
	 */
	public static class DirectDefintion implements IDefinition{
		private Formula<?> directDefintion;
		
		public DirectDefintion(Formula<?> directDefinition){
			this.directDefintion = directDefinition;
		}
		
		public Formula<?> getDefinition(){
			return directDefintion;
		}
	}
	
	/**
	 * 
	 * @author maamria
	 *
	 */
	public static class RecursiveDefinition implements IDefinition{
		
		private FreeIdentifier operatorArgument;
		
		private Map<Expression, Formula<?>> recursiveCases;
		
		public RecursiveDefinition(FreeIdentifier operatorArgument){
			this.operatorArgument = operatorArgument;
			this.recursiveCases = new LinkedHashMap<Expression, Formula<?>>();
		}
		
		public FreeIdentifier getOperatorArgument(){
			return operatorArgument;
		}
		
		public Map<Expression, Formula<?>> getRecursiveCases(){
			return recursiveCases;
		}
		
		public void addRecursiveCase(Expression inductiveCase, Formula<?> definition){
			recursiveCases.put(inductiveCase, definition);
		}
	}
	
	/**
	 * Marker interface.
	 * @author maamria
	 *
	 */
	public static interface IDefinition{
		
	}
	
}
