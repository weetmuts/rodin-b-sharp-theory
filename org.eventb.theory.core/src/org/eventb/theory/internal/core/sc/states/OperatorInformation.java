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
import java.util.List;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.ast.extension.IOperatorProperties.FormulaType;
import org.eventb.core.ast.extension.IOperatorProperties.Notation;
import org.eventb.core.tool.IStateType;
import org.eventb.internal.core.tool.state.State;
import org.eventb.theory.core.maths.OperatorArgument;
import org.eventb.theory.core.maths.extensions.MathExtensionsUtilities;
import org.eventb.theory.internal.core.util.CoreUtilities;

/**
 * @author maamria
 * 
 */
@SuppressWarnings("restriction")
public class OperatorInformation extends State implements IOperatorInformation {

	private String operatorID;
	private String syntax;
	private FormulaType formulaType;
	private Notation notation;
	private boolean isAssociative = false;
	private boolean isCommutative = false;
	private Predicate wdCondition;
	private List<String> allowedIdentifiers;
	private Formula<?> directDefinition;
	private HashMap<String, OperatorArgument> opArguments;
	private Type expressionType;
	private List<GivenType> typeParameters;
	private FormulaFactory factory;
	
	private int currentArgumentIndex = 0;

	private boolean hasError = false;

	private ITypeEnvironment typeEnvironment;

	public OperatorInformation(String operatorID, FormulaFactory factory) {
		this.operatorID = operatorID;
		this.allowedIdentifiers = new ArrayList<String>();
		this.opArguments = new HashMap<String, OperatorArgument>();
		this.typeParameters = new ArrayList<GivenType>();
		this.factory = factory;
		this.typeEnvironment = this.factory.makeTypeEnvironment();
	}

	public boolean isAllowedIdentifier(FreeIdentifier ident) {
		return allowedIdentifiers.contains(ident.getName());
	}

	public boolean isExpressionOperator() {
		return formulaType.equals(FormulaType.EXPRESSION);
	}

	/**
	 * @return the syntax
	 */
	public String getSyntax() {
		return syntax;
	}

	/**
	 * @param syntax
	 *            the syntax to set
	 */
	public void setSyntax(String syntax) {
		this.syntax = syntax;
	}

	/**
	 * @return the formulaType
	 */
	public FormulaType getFormulaType() {
		return formulaType;
	}

	/**
	 * @param formulaType
	 *            the formulaType to set
	 */
	public void setFormulaType(FormulaType formulaType) {
		this.formulaType = formulaType;
	}

	/**
	 * @return the notation
	 */
	public Notation getNotation() {
		return notation;
	}

	/**
	 * @param notation
	 *            the notation to set
	 */
	public void setNotation(Notation notation) {
		this.notation = notation;
	}

	/**
	 * @return the isAssociative
	 */
	public boolean isAssociative() {
		return isAssociative;
	}

	/**
	 * @param isAssociative
	 *            the isAssociative to set
	 */
	public void setAssociative(boolean isAssociative) {
		this.isAssociative = isAssociative;
	}

	/**
	 * @return the isCommutative
	 */
	public boolean isCommutative() {
		return isCommutative;
	}

	/**
	 * @param isCommutative
	 *            the isCommutative to set
	 */
	public void setCommutative(boolean isCommutative) {
		this.isCommutative = isCommutative;
	}

	/**
	 * @return the wdCondition
	 */
	public Predicate getWdCondition() {
		return wdCondition;
	}

	/**
	 * @param wdCondition
	 *            the wdCondition to set
	 */
	public void setWdCondition(Predicate wdCondition) {
		this.wdCondition = wdCondition;
	}

	/**
	 * @return the directDefinition
	 */
	public Formula<?> getDirectDefinition() {
		return directDefinition;
	}

	/**
	 * @param directDefinition
	 *            the directDefinition to set
	 */
	public void setDirectDefinition(Formula<?> directDefinition) {
		this.directDefinition = directDefinition;
		if (directDefinition instanceof Expression) {
			expressionType = ((Expression) directDefinition).getType();
		}
	}

	public Type getResultantType() {
		return expressionType;
	}

	/**
	 * @return the operatorID
	 */
	public String getOperatorID() {
		return operatorID;
	}


	@Override
	public void addOperatorArgument(FreeIdentifier ident, Type type) {
		// TODO check correct matching types if attempting to reinsert an ident
		addOperatorArgument(ident.getName(), type);

	}

	@Override
	public void addOperatorArgument(String ident, Type type) {
		if (!opArguments.containsKey(ident)) {
			for(GivenType gtype : CoreUtilities.getTypesOccurringIn(type, factory)){
				if(!typeParameters.contains(gtype)){
					typeParameters.add(gtype);
					typeEnvironment.addGivenSet(gtype.getName());
				}
				if(!allowedIdentifiers.contains(gtype.getName())){
					allowedIdentifiers.add(gtype.getName());
				}
			}
			typeEnvironment.addName(ident, type);
			opArguments.put(
					ident, new OperatorArgument(currentArgumentIndex++, ident, type));
			allowedIdentifiers.add(ident);
			
		}

	}

	@Override
	public IStateType<?> getStateType() {
		return STATE_TYPE;
	}

	/**
	 * @param hasError
	 *            the hasError to set
	 */
	public void setHasError() {
		this.hasError = true;
	}

	/**
	 * @return the hasError
	 */
	public boolean hasError() {
		return hasError;
	}

	public IFormulaExtension getExtension(final FormulaFactory formulaFactory) {

		return MathExtensionsUtilities.getFormulaExtension(isExpressionOperator(), 
				operatorID, syntax, formulaType, notation, isAssociative, isCommutative, 
				directDefinition, wdCondition, opArguments, typeParameters);
		
	}
}
