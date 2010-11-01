/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.maths;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.extension.ExtensionFactory;
import org.eventb.core.ast.extension.IExtensionKind;
import org.eventb.core.ast.extension.IOperatorProperties.FormulaType;
import org.eventb.core.ast.extension.IOperatorProperties.Notation;
import org.eventb.core.seqprover.eventbExtensions.DLib;
import org.eventb.internal.core.ast.extension.ExtensionKind;
import org.eventb.theory.internal.core.util.MathExtensionsUtilities;

/**
 * Basic implementation for an operator extension.
 * 
 * @author maamria
 * 
 */
@SuppressWarnings("restriction")
public abstract class AbstractOperatorExtension<F extends Formula<F>> implements IOperatorExtension<F> {

	private static String VAR_TEMP_NAME = "_z_";
	/**
	 * Operator ID
	 */
	protected String operatorID;
	/**
	 * Syntax symbol
	 */
	protected String syntax;
	/**
	 * Formula type
	 */
	protected FormulaType formulaType;
	/**
	 * Notation
	 */
	protected Notation notation;
	/**
	 * Well-definedness condition
	 */
	protected IOperatorTypingRule<F> operatorTypingRule;
	/**
	 * Direct definition if any
	 */
	protected F directDefinition;

	/**
	 * Operator group
	 */
	protected String operatorGroup;
	/**
	 * Operator properties
	 */
	protected boolean isCommutative = false;
	protected boolean isAssociative = false;
	/**
	 * Source could be <code>IRodinElement</code>
	 */
	protected Object source;

	/**
	 * Constructs an operator extension within the specified operator group
	 * using the supplied details.
	 * 
	 * @param operatorID
	 *            the operator ID
	 * @param syntax
	 *            the syntax symbol
	 * @param formulaType
	 *            formula type
	 * @param notation
	 *            the notaion
	 * @param groupID
	 * @param isCommutative
	 *            whether operator is commutative
	 * @param isAssocaitive
	 *            whether operator is associative
	 * @param operatorTypingRule
	 *            the typing rule
	 * @param directDefinition
	 *            the definition if any
	 * @param source
	 *            the origin
	 */
	protected AbstractOperatorExtension(String operatorID, String syntax,
			FormulaType formulaType, Notation notation, String groupID,
			boolean isCommutative, boolean isAssociative,
			IOperatorTypingRule<F> operatorTypingRule,
			F directDefinition, Object source) {
		this.operatorID = operatorID;
		this.syntax = syntax;
		this.formulaType = formulaType;
		this.notation = notation;
		this.directDefinition = directDefinition;
		this.isCommutative = isCommutative;
		this.isAssociative = isAssociative;
		this.operatorTypingRule = operatorTypingRule;
		this.source = source;
		this.operatorGroup = groupID == null ? MathExtensionsUtilities
				.getGroupFor(this.formulaType, this.notation,
						this.operatorTypingRule.getArity()) : groupID;
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
		return operatorGroup;
	}

	@Override
	public Object getOrigin() {
		return source;
	}

	public Predicate getAssociativityChecker(FormulaFactory factory,
			ITypeEnvironment typeEnvironment) {
		if (directDefinition == null) {
			return MathExtensionsUtilities.BTRUE;
		}
		if (!isAssociative) {
			return null;
		}
		DLib library = DLib.mDLib(factory);
		assert (operatorTypingRule.getArity() == 2);
		IOperatorArgument[] opArgs = new IOperatorArgument[2];
		int i = 0;
		for (IOperatorArgument operatorArgument : operatorTypingRule
				.getOperatorArguments()) {
			opArgs[i] = operatorArgument;
			i++;
		}
		FreeIdentifier x = opArgs[0].toFreeIdentifier(factory);
		FreeIdentifier y = opArgs[1].toFreeIdentifier(factory);
		FreeIdentifier z = opArgs[1].makeSubstituter(VAR_TEMP_NAME, factory);
		Map<FreeIdentifier, Expression> subs = new HashMap<FreeIdentifier, Expression>();
		// left (x op y) op z
		subs.put(y, z);
		Expression y_by_z = (Expression) directDefinition.substituteFreeIdents(
				subs, factory);
		subs.clear();
		subs.put(x, (Expression) directDefinition);
		Expression left = y_by_z.substituteFreeIdents(subs, factory);
		subs.clear();
		// right x op (y op z)
		subs.put(y, z);
		Expression y_by_z2 = (Expression) directDefinition
				.substituteFreeIdents(subs, factory);
		subs.clear();
		subs.put(x, y);
		Expression x_by_y2 = y_by_z2.substituteFreeIdents(subs, factory);
		subs.clear();
		subs.put(y, x_by_y2);
		Expression right = (Expression) directDefinition.substituteFreeIdents(
				subs, factory);
		Predicate assocCond = library.makeEq(left, right);

		List<FreeIdentifier> identsToBind = new ArrayList<FreeIdentifier>();
		Predicate[] typingPreds = new Predicate[3];
		BoundIdentDecl[] decls = new BoundIdentDecl[3];
		identsToBind.add(x);
		decls[0] = factory.makeBoundIdentDecl(x.getName(), null, x.getType());
		typingPreds[0] = factory.makeRelationalPredicate(Formula.IN, x, x
				.getType().toExpression(factory), null);
		identsToBind.add(y);
		decls[1] = factory.makeBoundIdentDecl(y.getName(), null, y.getType());
		typingPreds[1] = factory.makeRelationalPredicate(Formula.IN, y, y
				.getType().toExpression(factory), null);
		identsToBind.add(z);
		decls[2] = factory.makeBoundIdentDecl(z.getName(), null, z.getType());
		typingPreds[2] = factory.makeRelationalPredicate(Formula.IN, z, z
				.getType().toExpression(factory), null);

		Predicate rawCondition = library.makeImp(library.makeConj(typingPreds),
				library.makeImp(assocCond.getWDPredicate(factory), assocCond));

		rawCondition = rawCondition.bindTheseIdents(identsToBind, factory);
		rawCondition = library.makeUnivQuant(decls, rawCondition);
		ITypeCheckResult result = rawCondition.typeCheck(typeEnvironment);
		assert !result.hasProblem();
		return rawCondition;

	}

	public Predicate getWellDefinednessChecker(FormulaFactory factory,
			ITypeEnvironment typeEnvironment) {
		if (directDefinition == null) {
			return MathExtensionsUtilities.BTRUE;
		}
		return getCorePredicate(directDefinition.getWDPredicate(factory),
				factory, typeEnvironment);
	}

	public Predicate getCommutativityChecker(FormulaFactory factory,
			ITypeEnvironment typeEnvironment) {
		if (directDefinition == null) {
			return MathExtensionsUtilities.BTRUE;
		}
		if (!isCommutative) {
			return null;
		}
		DLib library = DLib.mDLib(factory);
		assert (operatorTypingRule.getArity() == 2);
		IOperatorArgument[] opArgs = new IOperatorArgument[2];
		int i = 0;
		for (IOperatorArgument operatorArgument : operatorTypingRule
				.getOperatorArguments()) {
			opArgs[i] = operatorArgument;
			i++;
		}
		FreeIdentifier x = opArgs[0].toFreeIdentifier(factory);
		FreeIdentifier y = opArgs[1].toFreeIdentifier(factory);
		Formula<?> commutForm = swap(x, y, directDefinition, factory);
		Predicate commutPred = null;
		if (commutForm instanceof Expression) {
			commutPred = library.makeEq((Expression) directDefinition,
					(Expression) commutForm);
		} else {
			commutPred = factory.makeBinaryPredicate(Formula.LEQV,
					(Predicate) directDefinition, (Predicate) commutForm, null);
		}
		return getCorePredicate(commutPred, factory, typeEnvironment);
	}

	public boolean equals(Object o) {
		if(o == this)
			return true;
		if (o == null || !(o instanceof AbstractOperatorExtension)) {
			return false;
		}
		AbstractOperatorExtension<?> abs = (AbstractOperatorExtension<?>) o;
		return abs.operatorID.equals(operatorID)
				&& abs.operatorGroup.equals(operatorGroup)
				&& abs.syntax.equals(syntax)
				&& abs.formulaType.equals(formulaType)
				&& abs.notation.equals(notation)
				&& abs.isAssociative == isAssociative
				&& abs.isCommutative == isCommutative
				&& abs.directDefinition.equals(directDefinition)
				&& abs.operatorTypingRule.equals(operatorTypingRule);
		
	}

	public int hashCode() {
		return 84 + operatorID.hashCode() + operatorGroup.hashCode()
				* syntax.hashCode() - notation.hashCode()+ formulaType.hashCode()
				+operatorGroup.hashCode()*2 + (new Boolean(isAssociative)).hashCode() 
				+ (new Boolean(isCommutative)).hashCode() + directDefinition.hashCode()+
				operatorTypingRule.hashCode();
	}
	
	public String toString(){
		return syntax +"-->" + operatorTypingRule.toString();
	}
	
	public boolean isCommutative(){
		return isCommutative;
	}
	
	@Override
	public boolean isAssociative() {
		return isAssociative;
	}
	
	public F expandDefinition(F extendedFormula, FormulaFactory factory){
		return operatorTypingRule.expandDefinition(this, extendedFormula, factory);
	}
	
	@Override
	public Notation getNotation() {
		return notation;
	}
	
	@Override
	public IExtensionKind getKind() {
		return new ExtensionKind(notation, formulaType,
				ExtensionFactory.makeAllExpr(ExtensionFactory.makeArity(
						operatorTypingRule.getArity(), operatorTypingRule.getArity())), false);
	}
	///////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////UTILS///////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////
	// adds any WD implicands
	protected Predicate getCorePredicate(Predicate condition,
			FormulaFactory factory, ITypeEnvironment typeEnvironment) {
		DLib library = DLib.mDLib(factory);
		if (operatorTypingRule.getWDPredicate().equals(condition)
				|| condition.equals(library.True())) {
			return library.True();
		}
		Predicate boundPred = library.makeImp(
				operatorTypingRule.getWDPredicate(), condition);
		List<Predicate> typingPreds = new ArrayList<Predicate>();
		List<FreeIdentifier> identifiers = new ArrayList<FreeIdentifier>();
		List<BoundIdentDecl> decls = new ArrayList<BoundIdentDecl>();
		for (IOperatorArgument arg : operatorTypingRule.getOperatorArguments()) {
			typingPreds.add(factory.makeRelationalPredicate(Formula.IN, arg
					.toFreeIdentifier(factory), arg.getArgumentType()
					.toExpression(factory), null));
			identifiers.add(arg.toFreeIdentifier(factory));
			decls.add(factory.makeBoundIdentDecl(arg.getArgumentName(), null,
					arg.getArgumentType()));
		}
		Predicate initial = library.makeImp(library.makeConj(typingPreds),
				boundPred);

		initial = initial.bindTheseIdents(identifiers, factory);
		Predicate pred = null;
		if (decls.size() == 0) {
			pred = initial;
		} else {
			pred = library.makeUnivQuant(
					decls.toArray(new BoundIdentDecl[decls.size()]), initial);
		}
		ITypeCheckResult result = pred.typeCheck(typeEnvironment);
		assert !result.hasProblem();
		return pred;
	}
	// swaps ident1 for ident2 and ident2 for ident1 in formula
	protected Formula<?> swap(FreeIdentifier ident1, FreeIdentifier ident2,
			Formula<?> formula, FormulaFactory factory) {
		Map<FreeIdentifier, Expression> subs = new HashMap<FreeIdentifier, Expression>();
		FreeIdentifier temp = factory.makeFreeIdentifier(VAR_TEMP_NAME, null,
				ident1.getType());
		subs.put(ident1, temp);
		Formula<?> form1 = formula.substituteFreeIdents(subs, factory);
		subs.clear();
		subs.put(ident2, ident1);
		Formula<?> form2 = form1.substituteFreeIdents(subs, factory);
		subs.clear();
		subs.put(temp, ident2);
		Formula<?> form3 = form2.substituteFreeIdents(subs, factory);
		return form3;
	}
}
