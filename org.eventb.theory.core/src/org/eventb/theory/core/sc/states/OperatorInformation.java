/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.sc.states;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.ast.extension.IOperatorProperties.FormulaType;
import org.eventb.core.ast.extension.IOperatorProperties.Notation;
import org.eventb.core.ast.extensions.maths.AstUtilities;
import org.eventb.core.ast.extensions.maths.MathExtensionsFactory;
import org.eventb.core.ast.extensions.maths.OperatorExtensionProperties;
import org.eventb.core.ast.extension.IPredicateExtension;
import org.eventb.core.tool.IStateType;
import org.eventb.internal.core.tool.state.State;
import org.eventb.theory.core.IApplicabilityElement.RuleApplicability;
import org.eventb.theory.core.INewOperatorDefinition;
import org.eventb.theory.core.ISCMetavariable;
import org.eventb.theory.core.ISCProofRulesBlock;
import org.eventb.theory.core.ISCRewriteRule;
import org.eventb.theory.core.ISCRewriteRuleRightHandSide;
import org.eventb.theory.core.ISCTheoryRoot;
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
	private List<Predicate> wdConditions;
	private List<String> allowedIdentifiers;
	private Map<String, Type> opArguments;
	private Type expressionType;
	private List<GivenType> typeParameters;
	private IDefinition definition;
	
	private Predicate dWDCondition;

	private FormulaFactory factory;

	private IFormulaExtension formulaExtension = null;

	private boolean hasError = false;

	private ITypeEnvironment typeEnvironment;

	public OperatorInformation(String operatorID, FormulaFactory factory) {
		this.operatorID = operatorID;
		this.allowedIdentifiers = new ArrayList<String>();
		this.opArguments = new LinkedHashMap<String, Type>();
		this.typeParameters = new ArrayList<GivenType>();
		this.wdConditions = new ArrayList<Predicate>();
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
		if (wdConditions.size() == 0) {
			return null;
		}
		if (wdConditions.size() == 1) {
			return wdConditions.get(0);
		}
		return AstUtilities.conjunctPredicates(wdConditions, factory);
	}

	/**
	 * @param wdCondition
	 *            the wdCondition to set
	 */
	public void addWDCondition(Predicate wdCondition) {
		wdConditions.add(wdCondition);
	}

	public Type getResultantType() {
		return expressionType;
	}
	
	@Override
	public Predicate getD_WDCondition() {
		return dWDCondition;
	}

	@Override
	public void setD_WDCondition(Predicate dWDCondition) {
		this.dWDCondition = dWDCondition;
	}

	@Override
	public void addOperatorArgument(String ident, Type type) {
		if (!opArguments.containsKey(ident)) {
			for (GivenType gtype : AstUtilities.getGivenTypes(type)) {
				if (!typeParameters.contains(gtype)) {
					typeParameters.add(gtype);
					typeEnvironment.addGivenSet(gtype.getName());
				}
				if (!allowedIdentifiers.contains(gtype.getName())) {
					allowedIdentifiers.add(gtype.getName());
				}
			}
			typeEnvironment.addName(ident, type);
			opArguments.put(ident, type);
			allowedIdentifiers.add(ident);

		}

	}

	public void setHasError() {
		this.hasError = true;
	}

	public boolean hasError() {
		return hasError;
	}

	@Override
	public void setDefinition(IDefinition definition) {
		this.definition = definition;
	}

	public IFormulaExtension getExtension(Object sourceOfExtension) {
		if (!hasError) {
			OperatorExtensionProperties properties = new OperatorExtensionProperties(operatorID, syntax, formulaType, notation, null);
			if (expressionType != null) {
				formulaExtension = MathExtensionsFactory.getExpressionExtension(properties, isCommutative, isAssociative, 
						opArguments, expressionType, getWdCondition(), dWDCondition, sourceOfExtension);
			} else {
				formulaExtension = MathExtensionsFactory.getPredicateExtension(properties, isCommutative, opArguments,
						getWdCondition(), dWDCondition, sourceOfExtension);
			}
			return formulaExtension;
		} else
			return null;

	}

	@Override
	public IFormulaExtension getInterimExtension() {
		IFormulaExtension formulaExtension = null;
		OperatorExtensionProperties properties = new OperatorExtensionProperties(operatorID, syntax, formulaType, notation, null);
		if (expressionType != null) {
			formulaExtension = MathExtensionsFactory.getExpressionExtension(properties, isCommutative, isAssociative, 
					opArguments, expressionType, getWdCondition(), dWDCondition, null);
		} else {
			formulaExtension = MathExtensionsFactory.getPredicateExtension(properties, isCommutative, opArguments,
					getWdCondition(), dWDCondition, null);
		}
		return formulaExtension;
	}

	@Override
	public void setResultantType(Type resultantType) {
		this.expressionType = resultantType;
	}

	public void generateDefinitionalRule(INewOperatorDefinition originDefinition, ISCTheoryRoot theoryRoot, FormulaFactory enhancedFactory) throws CoreException {
		// a rule block
		ISCProofRulesBlock newRulesbBlock = theoryRoot.getProofRulesBlock("generatedBlock");
		if (!newRulesbBlock.exists()) {
			newRulesbBlock.create(null, null);
			newRulesbBlock.setSource(originDefinition, null);
		}
		Map<FreeIdentifier, Expression> possibleSubstitution = new HashMap<FreeIdentifier, Expression>();
		for (String arg : opArguments.keySet()) {
			Type argType = opArguments.get(arg);
			FreeIdentifier argIdent = enhancedFactory.makeFreeIdentifier(arg, null, argType);
			ISCMetavariable var = newRulesbBlock.getMetavariable(arg);
			while (var.exists() && !var.getType(enhancedFactory).equals(argType)) {
				String newName = var.getIdentifierString() + "_";
				var = newRulesbBlock.getMetavariable(newName);	
			}
			possibleSubstitution.put(argIdent, enhancedFactory.makeFreeIdentifier(arg, null, argType));
			if (!var.exists()) {
				var.create(null, null);
				var.setType(argType, null);
				var.setSource(originDefinition, null);
			}
		}
		// one rewrite rule
		if (definition instanceof DirectDefintion) {
			ISCRewriteRule rewRule = createRewriteRule(newRulesbBlock, operatorID, syntax + " expansion");
			rewRule.setSource(originDefinition, null);
			Formula<?> lhs = makeLhs(enhancedFactory).substituteFreeIdents(possibleSubstitution, enhancedFactory);
			rewRule.setSCFormula(lhs, null);
			ISCRewriteRuleRightHandSide rhs = rewRule.getRuleRHS(syntax + " rhs");
			rhs.create(null, null);
			rhs.setLabel(syntax + " rhs", null);
			rhs.setPredicate(AstUtilities.BTRUE, null);
			rhs.setSCFormula(((DirectDefintion) definition).getDefinition().substituteFreeIdents(possibleSubstitution, enhancedFactory), null);
			rhs.setSource(originDefinition, null);
		} else if (definition instanceof RecursiveDefinition) {
			RecursiveDefinition recursiveDefinition = (RecursiveDefinition) definition;
			Map<Expression, Formula<?>> recursiveCases = recursiveDefinition.getRecursiveCases();
			FreeIdentifier inductiveIdent = recursiveDefinition.getOperatorArgument();
			int index = 0;
			for (Expression indCase : recursiveCases.keySet()) {
				ISCRewriteRule rewRule = createRewriteRule(newRulesbBlock, operatorID + " case " + index++, syntax + " expansion");
				rewRule.setSource(originDefinition, null);
				for (FreeIdentifier identifier : indCase.getFreeIdentifiers()) {
					String name = identifier.getName();
					Type type = identifier.getType();
					if (!AstUtilities.isGivenSet(typeEnvironment, name)) {
						ISCMetavariable scVar = newRulesbBlock.getMetavariable(name);
						while (scVar.exists() && !scVar.getType(enhancedFactory).equals(type)) {
							String newName = scVar.getIdentifierString() + "_";
							scVar = newRulesbBlock.getMetavariable(newName);
							possibleSubstitution.put(identifier, enhancedFactory.makeFreeIdentifier(newName, null, type));
						}
						if (!scVar.exists()) {
							scVar.create(null, null);
							scVar.setType(type, null);
							scVar.setSource(originDefinition, null);
						}

					}
				}
				Formula<?> lhs = makeLhs(enhancedFactory).substituteFreeIdents(possibleSubstitution, enhancedFactory);
				Map<FreeIdentifier, Expression> indSub = new HashMap<FreeIdentifier, Expression>();
				// substitute the free identifier for the inductive var so that it reflects the namings above
				// FIXED BUG
				FreeIdentifier newInductiveIdentifier = (FreeIdentifier) inductiveIdent.substituteFreeIdents(
						possibleSubstitution, enhancedFactory);
				indSub.put(newInductiveIdentifier, indCase);
				// substitute the inductive ident if necessary
				lhs = lhs.substituteFreeIdents(indSub, enhancedFactory);
				// substitute the vars of the inductive case if necessary
				lhs = lhs.substituteFreeIdents(possibleSubstitution, enhancedFactory);
				rewRule.setSCFormula(lhs, null);
				ISCRewriteRuleRightHandSide rhs = rewRule.getRuleRHS(syntax + " rhs");
				rhs.create(null, null);
				rhs.setSource(originDefinition, null);
				rhs.setLabel(syntax + " rhs", null);
				rhs.setPredicate(AstUtilities.BTRUE, null);
				// get the rhs 
				Formula<?> indCaseDefinitionFormula = recursiveCases.get(indCase);
				// apply substitution if necessary
				indCaseDefinitionFormula = indCaseDefinitionFormula.substituteFreeIdents(possibleSubstitution, enhancedFactory);
				rhs.setSCFormula(indCaseDefinitionFormula, null);
			}
		}

	}

	@Override
	public Map<String, Type> getOperatorArguments() {
		return Collections.unmodifiableMap(opArguments);
	}

	@Override
	public Notation getNotation() {
		return notation;
	}

	@Override
	public IStateType<?> getStateType() {
		return STATE_TYPE;
	}

	protected ISCRewriteRule createRewriteRule(ISCProofRulesBlock newRulesbBlock, String name, String description) throws RodinDBException {
		ISCRewriteRule rewRule = newRulesbBlock.getRewriteRule(name);
		rewRule.create(null, null);
		rewRule.setDefinitional(true, null);
		rewRule.setApplicability(RuleApplicability.INTERACTIVE, null);
		rewRule.setComplete(true, null);
		rewRule.setDescription(description, null);
		rewRule.setLabel(name, null);
		rewRule.setAccuracy(true, null);
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
		Expression[] childExpressionsForLhs = getChildExpressionsForLhs(ff);
		if (isExpressionOperator()) {
			lhs = ff.makeExtendedExpression((IExpressionExtension) formulaExtension, childExpressionsForLhs, NO_PREDICATES, null);
			// FIXED Bug in case of generic operator (e.g., {}), this is done to
			// type check the left hand side
			if (definition instanceof DirectDefintion && childExpressionsForLhs.length == 0) {
				DirectDefintion directDefintion = (DirectDefintion) definition;
				Expression expressionDefinition = (Expression) directDefintion.getDefinition();
				RelationalPredicate typeCheckPredicate = ff.makeRelationalPredicate(Formula.EQUAL, (Expression) lhs, expressionDefinition, null);
				ITypeCheckResult typeCheckResult = typeCheckPredicate.typeCheck(typeEnvironment);
				if (typeCheckResult.hasProblem()){
					throw new IllegalStateException("Could not type check left hand side of rule for statically checked operator.");
				}
				lhs = typeCheckPredicate.getLeft();
			}

		} else {
			lhs = ff.makeExtendedPredicate((IPredicateExtension) formulaExtension, childExpressionsForLhs, NO_PREDICATES, null);
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
		Expression[] exps = new Expression[opArguments.size()];
		int i = 0;
		for (String name : opArguments.keySet()){
			exps[i] = ff.makeFreeIdentifier(name, null, opArguments.get(name));
			i ++;
		}
		return exps;
	}
}
