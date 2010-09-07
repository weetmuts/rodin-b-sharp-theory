/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.maths.extensions;

import java.util.HashMap;

import org.eventb.core.ast.Formula;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.extension.IExtendedFormula;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.ast.extension.IOperatorProperties.FormulaType;
import org.eventb.core.ast.extension.IOperatorProperties.Notation;
import org.eventb.core.ast.extension.IWDMediator;
import org.eventb.theory.core.maths.IOperatorTypingRule;
import org.eventb.theory.core.maths.OperatorArgument;

/**
 * @author maamria
 *
 */
public abstract class AbstractOperatorExtension<F extends IFormulaExtension> implements IFormulaExtension{
	
	protected static final String DUMMY_OPERATOR_GROUP = "NEW THEORY GROUP";
	
	protected String operatorID;
	protected String syntax;
	protected FormulaType formulaType;
	protected Notation notation;
	protected Predicate wdCondition;
	protected Formula<?> directDefinition;
	protected HashMap<String, OperatorArgument> opArguments;
	protected IOperatorTypingRule<F> operatorTypeRule;
	protected boolean isCommutative = false;
	
	protected AbstractOperatorExtension(
			String operatorID, String syntax,
			FormulaType formulaType,
			Notation notation, boolean isCommutative,
			Formula<?> directDefinition, Predicate wdCondition, 
			HashMap<String, OperatorArgument> opArguments){
		this.operatorID = operatorID;
		this.syntax =syntax;
		this.formulaType = formulaType;
		this.notation = notation;
		this.directDefinition =directDefinition;
		this.wdCondition = wdCondition;
		this.opArguments = opArguments;
		this.isCommutative = isCommutative;
	}
	
	@Override
	public String getSyntaxSymbol() {
		return syntax;
	}
	
	@Override
	public String getId() {
		return operatorID;
	}

	@Override
	public String getGroupId() {
		return DUMMY_OPERATOR_GROUP;
	}

	@Override
	public Predicate getWDPredicate(IExtendedFormula formula,
			IWDMediator wdMediator) {
		return operatorTypeRule.getWDPredicate(formula, wdMediator);
	}
	
	@Override
	public Object getOrigin() {
		return null;
	}
	
}
