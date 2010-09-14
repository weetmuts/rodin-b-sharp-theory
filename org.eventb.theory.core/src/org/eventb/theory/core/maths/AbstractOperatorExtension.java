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
import org.eventb.core.ast.extension.IExtendedFormula;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.ast.extension.IOperatorProperties.FormulaType;
import org.eventb.core.ast.extension.IOperatorProperties.Notation;
import org.eventb.core.ast.extension.IWDMediator;
import org.eventb.core.seqprover.eventbExtensions.DLib;
import org.eventb.theory.internal.core.maths.IOperatorArgument;
import org.eventb.theory.internal.core.maths.IOperatorTypingRule;
import org.eventb.theory.internal.core.util.MathExtensionsUtilities;

/**
 * Basic implementation for an operator extension.
 * 
 * @author maamria
 * 
 * @param F the type of the extension
 *
 */
abstract class AbstractOperatorExtension<F extends IFormulaExtension> implements IOperatorExtension{
	
	private static String VAR_TEMP_NAME = "_z_";
	
	protected String operatorID;
	protected String syntax;
	protected FormulaType formulaType;
	protected Notation notation;
	protected Predicate wdCondition;
	protected Formula<?> directDefinition;
	protected List<IOperatorArgument> opArguments;
	protected IOperatorTypingRule<F> operatorTypeRule;
	protected boolean isCommutative = false;
	protected boolean isAssociative = false;
	protected Object source;
	
	protected AbstractOperatorExtension(
			String operatorID, String syntax,
			FormulaType formulaType,
			Notation notation, boolean isCommutative,
			Formula<?> directDefinition, Predicate wdCondition, 
			List<IOperatorArgument> opArguments, Object source){
		
		this.operatorID = operatorID;
		this.syntax =syntax;
		this.formulaType = formulaType;
		this.notation = notation;
		this.directDefinition =directDefinition;
		this.wdCondition = wdCondition;
		this.opArguments = opArguments;
		this.isCommutative = isCommutative;
		this.source = source;
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
		return MathExtensionsUtilities.getGroupFor(formulaType, notation, opArguments.size());
	}

	@Override
	public Predicate getWDPredicate(IExtendedFormula formula,
			IWDMediator wdMediator) {
		return operatorTypeRule.getWDPredicate(formula, wdMediator);
	}
	
	@Override
	public Object getOrigin() {
		return source;
	}
	
	/**
	 * Returns the predicate condition that checks the associativity (if any) of this operator.
	 * @param factory the formula factory
	 * @param typeEnvironment the type environment
	 * @return the verification condition
	 */
	public Predicate getAssociativityChecker(FormulaFactory factory, ITypeEnvironment typeEnvironment){
		if(!isAssociative){
			return null;
		}
		DLib library = DLib.mDLib(factory);
		assert (opArguments.size() == 2);
		IOperatorArgument[] opArgs = new IOperatorArgument[2];
		int i = 0;
		for (IOperatorArgument operatorArgument : opArguments) {
			opArgs[i] = operatorArgument;
			i++;
		}
		FreeIdentifier x = opArgs[0].toFreeIdentifier(factory);
		FreeIdentifier y = opArgs[1].toFreeIdentifier(factory);
		FreeIdentifier z = opArgs[1].makeSubstituter(VAR_TEMP_NAME, factory);
		Map<FreeIdentifier, Expression> subs = new HashMap<FreeIdentifier, Expression>();
		// left (x op y) op z
		subs.put(y, z);
		Expression y_by_z = (Expression) directDefinition.substituteFreeIdents(subs, factory);
		subs.clear();
		subs.put(x, (Expression)directDefinition);
		Expression left = y_by_z.substituteFreeIdents(subs, factory);
		subs.clear();
		// right x op (y op z)
		subs.put(y, z);
		Expression y_by_z2 = (Expression) directDefinition.substituteFreeIdents(subs, factory);
		subs.clear();
		subs.put(x, y);
		Expression x_by_y2 = y_by_z2.substituteFreeIdents(subs, factory);
		subs.clear();
		subs.put(y, x_by_y2);
		Expression right = (Expression) directDefinition.substituteFreeIdents(subs, factory);
		Predicate assocCond = library.makeEq(left, right);
		
		List<FreeIdentifier> identsToBind = new ArrayList<FreeIdentifier>();
		Predicate[] typingPreds = new Predicate[3];
		BoundIdentDecl[] decls = new BoundIdentDecl[3];
		identsToBind.add(x);
		decls[0] = factory.makeBoundIdentDecl(x.getName(), null, x.getType());
		typingPreds[0] = factory.makeRelationalPredicate(Formula.IN, x, 
				x.getType().toExpression(factory), null);
		identsToBind.add(y);
		decls[1] = factory.makeBoundIdentDecl(y.getName(), null, y.getType());
		typingPreds[1] = factory.makeRelationalPredicate(Formula.IN, y, 
				y.getType().toExpression(factory), null);
		identsToBind.add(z);
		decls[2] = factory.makeBoundIdentDecl(z.getName(), null, z.getType());
		typingPreds[2] = factory.makeRelationalPredicate(Formula.IN, z, 
				z.getType().toExpression(factory), null);
		
		Predicate rawCondition = library.makeImp(library.makeConj(typingPreds), 
				library.makeImp(assocCond.getWDPredicate(factory), assocCond));
		
		
		rawCondition = rawCondition.bindTheseIdents(identsToBind, factory);
		rawCondition = library.makeUnivQuant(decls, rawCondition);
		ITypeCheckResult result = rawCondition.typeCheck(typeEnvironment);
		assert !result.hasProblem();
		return rawCondition;
		
	}
	
	/**
	 * Returns the predicate condition that checks that the supplied well-definednes condition is indeed
	 * sufficient.
	 * @param factory the formula factory
	 * @param typeEnvironment the type environment
	 * @return the verification condition
	 */
	public Predicate getWellDefinednessChecker(FormulaFactory factory, ITypeEnvironment typeEnvironment){
		return getCorePredicate(
				directDefinition.getWDPredicate(factory), factory, typeEnvironment);
	}
	
	/**
	 * Returns the predicate condition that checks the commutativity (if any) of this operator.
	 * @param factory the formula factory
	 * @param typeEnvironment the type environment
	 * @return the verification condition
	 */
	public Predicate getCommutativityChecker(FormulaFactory factory, ITypeEnvironment typeEnvironment){
		if(!isCommutative){
			return null;
		}
		DLib library = DLib.mDLib(factory);
		assert (opArguments.size() == 2);
		IOperatorArgument[] opArgs = new IOperatorArgument[2];
		int i = 0;
		for (IOperatorArgument operatorArgument : opArguments) {
			opArgs[i] = operatorArgument;
			i++;
		}
		FreeIdentifier x = opArgs[0].toFreeIdentifier(factory);
		FreeIdentifier y = opArgs[1].toFreeIdentifier(factory);
		Formula<?> commutForm = swap(x, y, directDefinition, factory);
		Predicate commutPred = null;
		if(commutForm instanceof Expression){
			commutPred = library.makeEq((Expression) directDefinition, (Expression)commutForm);
		}
		else {
			commutPred = factory.makeBinaryPredicate(Formula.LEQV, 
					(Predicate) directDefinition, (Predicate) commutForm, null);
		}
		return getCorePredicate(commutPred, factory, typeEnvironment);
	}

	protected Predicate getCorePredicate(Predicate condition, 
			FormulaFactory factory, ITypeEnvironment typeEnvironment){
		DLib library = DLib.mDLib(factory);
		Predicate boundPred = library.makeImp(wdCondition, 
				condition);
		List<Predicate> typingPreds = new ArrayList<Predicate>();
		List<FreeIdentifier> identifiers = new ArrayList<FreeIdentifier>();
		List<BoundIdentDecl> decls = new ArrayList<BoundIdentDecl>();
		for(IOperatorArgument arg : opArguments){
			typingPreds.add(
					factory.makeRelationalPredicate(
							Formula.IN, 
							arg.toFreeIdentifier(factory),
							arg.getArgumentType().toExpression(factory), 
							null));
			identifiers.add(arg.toFreeIdentifier(factory));
			decls.add(factory.makeBoundIdentDecl(arg.getArgumentName(), null, arg.getArgumentType()));
		}
		Predicate initial = library.makeImp(library.makeConj(typingPreds), boundPred);
		
		initial = initial.bindTheseIdents(identifiers, factory);
		Predicate pred = null;
		if(decls.size() == 0){
			pred =  initial;
		}
		else {
			pred = library.makeUnivQuant(decls.toArray(new BoundIdentDecl[decls.size()]), initial);
		}
		ITypeCheckResult result = pred.typeCheck(typeEnvironment);
		assert !result.hasProblem();
		return pred;
	}
	
	protected Predicate getUnquantifiedPredicate(Predicate pred , 
			FormulaFactory factory, ITypeEnvironment typeEnvironment){
		return null;
	}
	
	protected Formula<?> swap(FreeIdentifier ident1, FreeIdentifier ident2, 
			Formula<?> formula, FormulaFactory factory){
		Map<FreeIdentifier, Expression> subs = new HashMap<FreeIdentifier, Expression>();
		FreeIdentifier temp = factory.makeFreeIdentifier(VAR_TEMP_NAME, null, ident1.getType());
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
