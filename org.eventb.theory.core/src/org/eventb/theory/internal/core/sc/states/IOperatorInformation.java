/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.internal.core.sc.states;

import org.eclipse.core.runtime.CoreException;
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
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.plugin.TheoryPlugin;

/**
 * An implementation of a repository state holding information about a new operator.
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
	
	public boolean isExpressionOperator() ;
	
	public boolean isAllowedIdentifier(FreeIdentifier ident) ;
	
	public void addOperatorArgument(FreeIdentifier ident, Type type);
	
	public void addOperatorArgument(String ident, Type type);

	/**
	 * Generates a definitional rewrite rule for this operator.
	 * @param newFactory that already knows about this extension
	 * @param theoryRoot the SC theory root
	 * @throws CoreException
	 */
	public void generateDefinitionalRule(FormulaFactory newFactory, ISCTheoryRoot theoryRoot) throws CoreException;
	
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
	 * @return the notation
	 */
	public Notation getNotation() ;

	/**
	 * @param notation
	 *            the notation to set
	 */
	public void setNotation(Notation notation) ;

	/**
	 * @return the isAssociative
	 */
	public boolean isAssociative() ;

	/**
	 * @param isAssociative
	 *            the isAssociative to set
	 */
	public void setAssociative(boolean isAssociative) ;

	/**
	 * @return the isCommutative
	 */
	public boolean isCommutative() ;

	/**
	 * @param isCommutative
	 *            the isCommutative to set
	 */
	public void setCommutative(boolean isCommutative) ;

	/**
	 * @return the wdCondition
	 */
	public Predicate getWdCondition() ;

	/**
	 * @param wdCondition
	 *            the wdCondition to set
	 */
	public void setWdCondition(Predicate wdCondition) ;

	/**
	 * @return the directDefinition
	 */
	public Formula<?> getDirectDefinition() ;

	/**
	 * @param directDefinition
	 *            the directDefinition to set
	 */
	public void setDirectDefinition(Formula<?> directDefinition) ;

	public Type getResultantType() ;

	/**
	 * @return the operatorID
	 */
	public String getOperatorID() ;

	/**
	 * @param hasError
	 *            the hasError to set
	 */
	public void setHasError() ;

	/**
	 * @return the hasError
	 */
	public boolean hasError() ;

	/**
	 * 
	 * @return the formula extension
	 */
	public IFormulaExtension getExtension(FormulaFactory factory) ;
	
}
