/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.sc.states;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.ast.extension.IOperatorProperties.FormulaType;
import org.eventb.core.ast.extension.IOperatorProperties.Notation;
import org.eventb.core.ast.extension.IPredicateExtension;
import org.eventb.core.tool.IStateType;
import org.eventb.internal.core.tool.state.State;
import org.eventb.theory.core.INewOperatorDefinition;
import org.eventb.theory.core.ISCMetavariable;
import org.eventb.theory.core.ISCProofRulesBlock;
import org.eventb.theory.core.ISCRewriteRule;
import org.eventb.theory.core.ISCRewriteRuleRightHandSide;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.maths.IOperatorArgument;
import org.eventb.theory.core.maths.MathExtensionsFactory;
import org.eventb.theory.core.maths.OperatorExtensionProperties;
import org.eventb.theory.core.sc.modules.ModulesUtils;
import org.eventb.theory.internal.core.maths.ExpressionOperatorTypingRule;
import org.eventb.theory.internal.core.maths.OperatorArgument;
import org.eventb.theory.internal.core.maths.PredicateOperatorTypingRule;
import org.eventb.theory.internal.core.util.MathExtensionsUtilities;
import org.rodinp.core.RodinDBException;

/**
 * A simple implementation of an operator information state.
 * 
 * @author maamria
 * 
 */
@SuppressWarnings("restriction")
public class OperatorInformation extends State implements IOperatorInformation {

	private static final Predicate[] NO_PREDICATES = new Predicate[0];

	private String operatorID;
	private String syntax;
	private FormulaType formulaType;
	private Notation notation;
	private boolean isAssociative = false;
	private boolean isCommutative = false;
	private Predicate wdCondition;
	private List<String> allowedIdentifiers;
	private Map<String, IOperatorArgument> opArguments;
	private Type expressionType;
	private List<GivenType> typeParameters;
	private IDefinition definition;

	private FormulaFactory factory;

	private final MathExtensionsFactory extensionsFactory;

	private IFormulaExtension formulaExtension = null;

	private int currentArgumentIndex = 0;

	private boolean hasError = false;

	private ITypeEnvironment typeEnvironment;

	public OperatorInformation(String operatorID, FormulaFactory factory) {
		this.operatorID = operatorID;
		this.allowedIdentifiers = new ArrayList<String>();
		this.opArguments = new LinkedHashMap<String, IOperatorArgument>();
		this.typeParameters = new ArrayList<GivenType>();
		this.factory = factory;
		this.typeEnvironment = this.factory.makeTypeEnvironment();
		this.extensionsFactory = MathExtensionsFactory.getDefault();
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
	 * @param notation
	 *            the notation to set
	 */
	public void setNotation(Notation notation) {
		this.notation = notation;
	}

	/**
	 * @param isAssociative
	 *            the isAssociative to set
	 */
	public void setAssociative(boolean isAssociative) {
		this.isAssociative = isAssociative;
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

	public Type getResultantType() {
		return expressionType;
	}

	@Override
	public void addOperatorArgument(String ident, Type type) {
		if (!opArguments.containsKey(ident)) {
			for (GivenType gtype : ModulesUtils.getTypesOccurringIn(type,
					factory)) {
				if (!typeParameters.contains(gtype)) {
					typeParameters.add(gtype);
					typeEnvironment.addGivenSet(gtype.getName());
				}
				if (!allowedIdentifiers.contains(gtype.getName())) {
					allowedIdentifiers.add(gtype.getName());
				}
			}
			typeEnvironment.addName(ident, type);
			opArguments.put(ident, new OperatorArgument(currentArgumentIndex++,
					ident, type));
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

	@Override
	public void setDefinition(IDefinition definition) {
		// TODO Auto-generated method stub
		this.definition = definition;
	}

	@Override
	public IDefinition getDefinition() {
		// TODO Auto-generated method stub
		return definition;
	}

	public IFormulaExtension getExtension(Object sourceOfExtension,
			final FormulaFactory formulaFactory) {
		if (!hasError) {
			OperatorExtensionProperties properties = new OperatorExtensionProperties(
					operatorID, syntax, formulaType, notation, null);
			if (expressionType != null) {
				ExpressionOperatorTypingRule typingRule = extensionsFactory
						.getTypingRule(new ArrayList<IOperatorArgument>(
								opArguments.values()), expressionType,
								wdCondition, isAssociative, formulaFactory);
				formulaExtension = extensionsFactory.getFormulaExtension(
						properties, isCommutative, isAssociative, typingRule,
						sourceOfExtension);
			} else {
				PredicateOperatorTypingRule typingRule = extensionsFactory
						.getTypingRule(new ArrayList<IOperatorArgument>(
								opArguments.values()), wdCondition,
								formulaFactory);
				formulaExtension = extensionsFactory.getFormulaExtension(
						properties, isCommutative, typingRule,
						sourceOfExtension);

			}
			return formulaExtension;
		} else
			return null;

	}

	@Override
	public void setResultantType(Type resultantType) {
		this.expressionType = resultantType;
	}

	@Override
	public Set<IOperatorArgument> getOperatorArguments() {
		return new LinkedHashSet<IOperatorArgument>(opArguments.values());
	}

	public void generateDefinitionalRule(
			INewOperatorDefinition originDefinition, ISCTheoryRoot theoryRoot,
			FormulaFactory enhancedFactory) throws CoreException {
		// a rule block
		ISCProofRulesBlock newRulesbBlock = theoryRoot
				.getProofRulesBlock("generatedBlock");
		if (!newRulesbBlock.exists()) {
			newRulesbBlock.create(null, null);
		}
		Map<FreeIdentifier, Expression> possibleSubstitution = new HashMap<FreeIdentifier, Expression>();
		for (IOperatorArgument arg : opArguments.values()) {
			ISCMetavariable var = newRulesbBlock.getMetavariable(arg
					.getArgumentName());
			while (var.exists()
					&& !var.getType(enhancedFactory).equals(
							arg.getArgumentType())) {
				String newName = var.getIdentifierString() + "_";
				var = newRulesbBlock.getMetavariable(newName);
				possibleSubstitution.put(arg.toFreeIdentifier(enhancedFactory),
						arg.makeSubstituter(newName, enhancedFactory));
			}
			if (!var.exists()) {
				var.create(null, null);
				var.setType(arg.getArgumentType(), null);
			}
		}
		// one rewrite rule
		if (definition instanceof DirectDefintion) {
			ISCRewriteRule rewRule = createRewriteRule(newRulesbBlock, operatorID, syntax + " expansion");
			
			Formula<?> lhs = makeLhs(enhancedFactory).substituteFreeIdents(
					possibleSubstitution, enhancedFactory);
			rewRule.setSCFormula(lhs, null);

			ISCRewriteRuleRightHandSide rhs = rewRule.getRuleRHS(syntax
					+ " rhs");
			rhs.create(null, null);
			rhs.setPredicate(MathExtensionsUtilities.BTRUE, null);
			rhs.setSCFormula(
					((DirectDefintion) definition).getDefinition()
							.substituteFreeIdents(possibleSubstitution,
									enhancedFactory), null);
		} else if (definition instanceof RecursiveDefinition) {
			RecursiveDefinition recursiveDefinition = (RecursiveDefinition) definition;
			Map<Expression, Formula<?>> recursiveCases = recursiveDefinition
					.getRecursiveCases();
			FreeIdentifier inductiveIdent = recursiveDefinition
					.getOperatorArgument().toFreeIdentifier(enhancedFactory);
			int index = 0;
			for (Expression indCase : recursiveCases.keySet()) {
				ISCRewriteRule rewRule = createRewriteRule(newRulesbBlock, operatorID + " case"+index++ , syntax + " expansion");
				Formula<?> lhs = makeLhs(enhancedFactory).substituteFreeIdents(
						possibleSubstitution, enhancedFactory);
				Map<FreeIdentifier, Expression> indSub = new HashMap<FreeIdentifier, Expression>();
				indSub.put(inductiveIdent, indCase);
				lhs = lhs.substituteFreeIdents(indSub, enhancedFactory);
				rewRule.setSCFormula(lhs, null);
				ISCRewriteRuleRightHandSide rhs = rewRule.getRuleRHS(syntax
						+ " rhs");
				rhs.create(null, null);
				rhs.setPredicate(MathExtensionsUtilities.BTRUE, null);
				rhs.setSCFormula(
						recursiveCases.get(indCase).substituteFreeIdents(possibleSubstitution,enhancedFactory), 
						null);
			}
		}

	}

	protected ISCRewriteRule createRewriteRule(ISCProofRulesBlock newRulesbBlock, String name, String description)
			throws RodinDBException {
		ISCRewriteRule rewRule = newRulesbBlock.getRewriteRule(name);
		rewRule.create(null, null);
		rewRule.setDefinitional(true, null);
		rewRule.setAutomatic(false, null);
		rewRule.setInteractive(true, null);
		rewRule.setComplete(true, null);
		rewRule.setDescription(description, null);
		rewRule.setAccuracy(true, null);
		rewRule.setValidated(true, null);
		return rewRule;
	}

	/**
	 * Returns the formula corresponding to the lhs of the definitional rule
	 * e.g., op(a1,a2).
	 * 
	 * @param ff
	 *            the formula factory with <code>formulaExtension</code> added
	 * @return the lhs formula
	 */
	protected Formula<?> makeLhs(FormulaFactory ff) {
		Formula<?> lhs = null;
		if (isExpressionOperator()) {
			lhs = ff.makeExtendedExpression(
					(IExpressionExtension) formulaExtension,
					getChildExpressionsForLhs(ff), NO_PREDICATES, null);
		} else {
			lhs = ff.makeExtendedPredicate(
					(IPredicateExtension) formulaExtension,
					getChildExpressionsForLhs(ff), NO_PREDICATES, null);
		}
		return lhs;
	}

	/**
	 * Returns the child expressions corresponding to the operator arguments.
	 * 
	 * @param ff
	 *            the formula factory with <code>formulaExtension</code> added
	 * @return the child expressions
	 */
	protected Expression[] getChildExpressionsForLhs(FormulaFactory ff) {
		IOperatorArgument[] opArgsArray = (MathExtensionsUtilities
				.sort(opArguments.values()))
				.toArray(new IOperatorArgument[opArguments.size()]);
		Expression[] exps = new Expression[opArguments.size()];
		for (int i = 0; i < exps.length; i++) {
			exps[i] = ff.makeFreeIdentifier(opArgsArray[i].getArgumentName(),
					null, opArgsArray[i].getArgumentType());
		}
		return exps;
	}
}
