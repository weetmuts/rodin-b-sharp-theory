/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.sc.modules;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.extension.IOperatorProperties.FormulaType;
import org.eventb.core.ast.extension.IOperatorProperties.Notation;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.SCFilterModule;
import org.eventb.core.sc.state.ILabelSymbolInfo;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.tool.IModuleType;
import org.eventb.theory.core.IDirectOperatorDefinition;
import org.eventb.theory.core.INewOperatorDefinition;
import org.eventb.theory.core.IRecursiveOperatorDefinition;
import org.eventb.theory.core.TheoryAttributes;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.eventb.theory.core.sc.TheoryGraphProblem;
import org.eventb.theory.core.sc.states.OperatorLabelSymbolTable;
import org.eventb.theory.internal.core.util.MathExtensionsUtilities;
import org.rodinp.core.IRodinElement;

/**
 * 
 * @author maamria
 * 
 */
public class OperatorFilterModule extends SCFilterModule {

	private final IModuleType<OperatorFilterModule> MODULE_TYPE = SCCore
			.getModuleType(TheoryPlugin.PLUGIN_ID + ".operatorFilterModule");

	private FormulaFactory factory;
	private ITypeEnvironment typeEnvironment;

	@SuppressWarnings("restriction")
	@Override
	public boolean accept(IRodinElement element, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		INewOperatorDefinition opDef = (INewOperatorDefinition) element;
		String opID = opDef.getLabel();
		// check against the symbol table for operator labels
		OperatorLabelSymbolTable labelSymbolTable = (OperatorLabelSymbolTable) repository
				.getState(OperatorLabelSymbolTable.STATE_TYPE);
		ILabelSymbolInfo symbolInfo = labelSymbolTable.getSymbolInfo(opID);
		if (symbolInfo == null) {
			return false;
		}
		// check ID is unique
		if (!MathExtensionsUtilities.checkOperatorID(opDef.getLabel(), factory)) {
			createProblemMarker(opDef, EventBAttributes.LABEL_ATTRIBUTE,
					TheoryGraphProblem.OperatorIDExistsError, opID);
			return false;
		}
		if (!opDef.hasSyntaxSymbol() || opDef.getSyntaxSymbol().equals("")) {
			createProblemMarker(opDef,
					TheoryAttributes.SYNTAX_SYMBOL_ATTRIBUTE,
					TheoryGraphProblem.OperatorSynMissingError, opID);
			return false;
		}
		String syntax = opDef.getSyntaxSymbol();
		// check syntax
		if (typeEnvironment.contains(syntax)) {
			createProblemMarker(opDef,
					TheoryAttributes.SYNTAX_SYMBOL_ATTRIBUTE,
					TheoryGraphProblem.OperatorSynIsATypeParError, syntax);
			return false;
		}
		if (!MathExtensionsUtilities.checkOperatorSyntaxSymbol(syntax, factory)) {
			createProblemMarker(opDef,
					TheoryAttributes.SYNTAX_SYMBOL_ATTRIBUTE,
					TheoryGraphProblem.OperatorSynExistsError, syntax);
			return false;
		}
		if (!FormulaFactory.checkSymbol(syntax) || syntax.contains(" ")) {
			createProblemMarker(opDef,
					TheoryAttributes.SYNTAX_SYMBOL_ATTRIBUTE,
					TheoryGraphProblem.OperatorInvalidSynError, syntax);
			return false;
		}
		symbolInfo.setAttributeValue(TheoryAttributes.SYNTAX_SYMBOL_ATTRIBUTE,
				syntax);
		if (!opDef.hasFormulaType()) {
			createProblemMarker(opDef, TheoryAttributes.FORMULA_TYPE_ATTRIBUTE,
					TheoryGraphProblem.OperatorFormTypeMissingError, opID);
			return false;
		}
		FormulaType formType = opDef.getFormulaType();
		symbolInfo.setAttributeValue(TheoryAttributes.FORMULA_TYPE_ATTRIBUTE,
				MathExtensionsUtilities.isExpressionOperator(formType));
		if (!opDef.hasNotationType()) {
			createProblemMarker(opDef,
					TheoryAttributes.NOTATION_TYPE_ATTRIBUTE,
					TheoryGraphProblem.OperatorNotationTypeMissingError, opID);
			return false;
		}
		Notation notation = opDef.getNotationType();
		symbolInfo.setAttributeValue(TheoryAttributes.NOTATION_TYPE_ATTRIBUTE,
				notation.toString());

		if (!opDef.hasAssociativeAttribute()) {
			createProblemMarker(opDef, TheoryAttributes.ASSOCIATIVE_ATTRIBUTE,
					TheoryGraphProblem.OperatorAssocMissingError, opID);
			return false;
		}
		symbolInfo.setAttributeValue(TheoryAttributes.ASSOCIATIVE_ATTRIBUTE,
				opDef.isAssociative());
		if (!opDef.hasCommutativeAttribute()) {
			createProblemMarker(opDef, TheoryAttributes.COMMUTATIVE_ATTRIBUTE,
					TheoryGraphProblem.OperatorCommutMissingError, opID);
			return false;
		}
		symbolInfo.setAttributeValue(TheoryAttributes.COMMUTATIVE_ATTRIBUTE,
				opDef.isCommutative());

		// Check number of definitions
		{
			IDirectOperatorDefinition[] opDefs = opDef.getDirectOperatorDefinitions();
			IRecursiveOperatorDefinition[] recDefs = opDef.getRecursiveOperatorDefinitions();
			if (opDefs.length + recDefs.length != 1) {
				if (opDefs.length + recDefs.length == 0) {
					createProblemMarker(opDef,
							EventBAttributes.LABEL_ATTRIBUTE,
							TheoryGraphProblem.OperatorNoDefError,
							opDef.getLabel());
				} else {
					createProblemMarker(opDef,
							EventBAttributes.LABEL_ATTRIBUTE,
							TheoryGraphProblem.OperatorHasMoreThan1DefError,
							opDef.getLabel());
				}
				return false;
			}
		}
		return true;
	}

	@Override
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	@Override
	public void initModule(ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		super.initModule(repository, monitor);
		factory = repository.getFormulaFactory();
		typeEnvironment = repository.getTypeEnvironment();
	}

	@Override
	public void endModule(ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		factory = null;
		typeEnvironment = null;
		super.endModule(repository, monitor);
	}
}
