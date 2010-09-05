/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.internal.core.sc.states;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.ExtendedPredicate;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.ExtensionFactory;
import org.eventb.core.ast.extension.ICompatibilityMediator;
import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.core.ast.extension.IExtendedFormula;
import org.eventb.core.ast.extension.IExtensionKind;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.ast.extension.IPredicateExtension;
import org.eventb.core.ast.extension.IPriorityMediator;
import org.eventb.core.ast.extension.ITypeCheckMediator;
import org.eventb.core.ast.extension.ITypeMediator;
import org.eventb.core.ast.extension.IWDMediator;
import org.eventb.core.ast.extension.IOperatorProperties.FormulaType;
import org.eventb.core.ast.extension.IOperatorProperties.Notation;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.state.ISCState;
import org.eventb.core.tool.IStateType;
import org.eventb.internal.core.ast.extension.ExtensionKind;
import org.eventb.internal.core.tool.state.State;
import org.eventb.theory.core.plugin.TheoryPlugin;

/**
 * @author maamria
 * 
 */
@SuppressWarnings("restriction")
public class OperatorInformation extends State implements ISCState {

	/**
	 * 
	 */
	protected static final String DUMMY_OPERATOR_GROUP = "NEW THEORY GROUP";

	public final static IStateType<OperatorInformation> STATE_TYPE = SCCore
			.getToolStateType(TheoryPlugin.PLUGIN_ID + ".operatorInformation");

	private String operatorID;
	private String syntax;
	private FormulaType formulaType;
	private Notation notation;
	private boolean isAssociative = false;
	private boolean isCommutative = false;
	private Predicate wdCondition;
	private List<String> allowedIdentifiers;
	private Formula<?> directDefinition;
	private List<Type> argumentTypes;
	private Type expressionType;

	private boolean hasError = false;

	public OperatorInformation(String operatorID) {
		this.operatorID = operatorID;
		this.allowedIdentifiers = new ArrayList<String>();
		this.argumentTypes = new ArrayList<Type>();
	}

	public void addAllowedIdentifier(FreeIdentifier ident) {
		addAllowedIdentifier(ident.getName());
	}

	public void addAllowedIdentifier(String ident) {
		if (!allowedIdentifiers.contains(ident))
			allowedIdentifiers.add(ident);
	}

	public void addAllowedIdentifiers(FreeIdentifier[] idents) {
		for (FreeIdentifier ident : idents) {
			addAllowedIdentifier(ident);
		}
	}

	public void addArgumentType(Type type) {
		argumentTypes.add(type);
	}

	public List<Type> getArgumentTypesList() {
		return argumentTypes;
	}

	public Type[] getArgumentTypesArray() {
		return argumentTypes.toArray(new Type[argumentTypes.size()]);
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

	public IFormulaExtension getExtension() {
		if (isExpressionOperator()) {
			return new IExpressionExtension() {

				@Override
				public Predicate getWDPredicate(IExtendedFormula formula,
						IWDMediator wdMediator) {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public String getSyntaxSymbol() {
					return syntax;
				}

				@Override
				public Object getOrigin() {
					return null;
				}

				@Override
				public IExtensionKind getKind() {
					return new ExtensionKind(notation, formulaType,
							ExtensionFactory.makeAllExpr(ExtensionFactory
									.makeArity(argumentTypes.size(),
											argumentTypes.size())),
							isAssociative);

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
				public boolean conjoinChildrenWD() {
					return true;
				}

				@Override
				public void addPriorities(IPriorityMediator mediator) {
					// TODO Auto-generated method stub
				}

				@Override
				public void addCompatibilities(ICompatibilityMediator mediator) {
					if (isAssociative)
						mediator.addAssociativity(operatorID);
				}

				@Override
				public boolean verifyType(Type proposedType,
						Expression[] childExprs, Predicate[] childPreds) {
					// TODO Auto-generated method stub
					return false;
				}

				@Override
				public Type typeCheck(ExtendedExpression expression,
						ITypeCheckMediator tcMediator) {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public Type synthesizeType(Expression[] childExprs,
						Predicate[] childPreds, ITypeMediator mediator) {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public boolean isATypeConstructor() {
					return false;
				}
			};
		} else {
			return new IPredicateExtension() {

				@Override
				public Predicate getWDPredicate(IExtendedFormula formula,
						IWDMediator wdMediator) {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public String getSyntaxSymbol() {
					return syntax;
				}

				@Override
				public Object getOrigin() {
					return null;
				}

				@Override
				public IExtensionKind getKind() {
					return new ExtensionKind(notation, formulaType,
							ExtensionFactory.makeAllExpr(ExtensionFactory
									.makeArity(argumentTypes.size(),
											argumentTypes.size())),
							false);
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
				public boolean conjoinChildrenWD() {
					return true;
				}

				@Override
				public void addPriorities(IPriorityMediator mediator) {
					// TODO Auto-generated method stub
				}

				@Override
				public void addCompatibilities(ICompatibilityMediator mediator) {
					// TODO Auto-generated method stub
				}

				@Override
				public void typeCheck(ExtendedPredicate predicate,
						ITypeCheckMediator tcMediator) {
					// TODO Auto-generated method stub

				}
			};
		}
	}

}
