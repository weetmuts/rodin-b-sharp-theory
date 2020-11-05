/*******************************************************************************
 * Copyright (c) 2012, 2020 University of Southampton and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.tests.sc.modules;

import org.eventb.core.EventBAttributes;
import org.eventb.core.ast.extension.IOperatorProperties.FormulaType;
import org.eventb.core.ast.extension.IOperatorProperties.Notation;
import org.eventb.core.sc.GraphProblem;
import org.eventb.theory.core.IDirectOperatorDefinition;
import org.eventb.theory.core.INewOperatorDefinition;
import org.eventb.theory.core.IOperatorArgument;
import org.eventb.theory.core.IOperatorWDCondition;
import org.eventb.theory.core.IRecursiveDefinitionCase;
import org.eventb.theory.core.IRecursiveOperatorDefinition;
import org.eventb.theory.core.ISCNewOperatorDefinition;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.ITheoryRoot;
import org.eventb.theory.core.TheoryAttributes;
import org.eventb.theory.core.sc.TheoryGraphProblem;
import org.eventb.theory.core.sc.modules.OperatorFilterModule;
import org.eventb.theory.core.sc.modules.OperatorModule;
import org.eventb.theory.core.tests.sc.BasicTheorySCTestWithThyConfig;
import org.junit.Test;

/**
 * @see OperatorModule
 * @see OperatorFilterModule
 * @author maamria
 * 
 */
public class TestOperators extends BasicTheorySCTestWithThyConfig {

	/**
	 * Missing syntax/label
	 */
	@Test
	public void testOperators_001_NoLabel() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		INewOperatorDefinition op2 = root.createChild(INewOperatorDefinition.ELEMENT_TYPE, null, null);
		INewOperatorDefinition op1 = root.createChild(INewOperatorDefinition.ELEMENT_TYPE, null, null);
		op1.setLabel("", null);
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scTheoryRoot = root.getSCTheoryRoot();
		isNotAccurate(scTheoryRoot);
		getOperators(scTheoryRoot);
		hasMarker(op2, EventBAttributes.LABEL_ATTRIBUTE, GraphProblem.LabelUndefError);
		hasMarker(op1, EventBAttributes.LABEL_ATTRIBUTE, GraphProblem.EmptyLabelError);
	}

	/**
	 * Label/syntax conflict
	 */
	@Test
	public void testOperators_002_LabelConf() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		INewOperatorDefinition op1 = root.createChild(INewOperatorDefinition.ELEMENT_TYPE, null, null);
		op1.setLabel("op1", null);
		INewOperatorDefinition op2 = root.createChild(INewOperatorDefinition.ELEMENT_TYPE, null, null);
		op2.setLabel("op1", null);

		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scTheoryRoot = root.getSCTheoryRoot();
		isNotAccurate(scTheoryRoot);
		hasMarker(op2, EventBAttributes.LABEL_ATTRIBUTE, TheoryGraphProblem.OperatorSynConflictError, "op1");
		hasMarker(op1);
	}

	/**
	 * Syntax is a type par
	 */
	@Test
	public void testOperators_003_SynIsTypePar() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		addTypeParameters(root, "S");
		INewOperatorDefinition op1 = root.createChild(INewOperatorDefinition.ELEMENT_TYPE, null, null);
		op1.setLabel("S", null);
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scTheoryRoot = root.getSCTheoryRoot();
		isNotAccurate(scTheoryRoot);
		hasMarker(op1, EventBAttributes.LABEL_ATTRIBUTE, TheoryGraphProblem.OperatorSynIsATypeParError, "S");
	}

	/**
	 * Syntax is invalid
	 */
	@Test
	public void testOperators_004_SynInvalid() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		INewOperatorDefinition op1 = root.createChild(INewOperatorDefinition.ELEMENT_TYPE, null, null);
		op1.setLabel("oops space", null);
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scTheoryRoot = root.getSCTheoryRoot();
		isNotAccurate(scTheoryRoot);
		hasMarker(op1, EventBAttributes.LABEL_ATTRIBUTE, TheoryGraphProblem.OperatorInvalidSynError, "oops space");
	}

	/**
	 * Syntax exists already
	 */
	@Test
	public void testOperators_005_SynExists() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		INewOperatorDefinition op1 = addOperatorDefinitionWithDirectDef(root, "finite", Notation.PREFIX , FormulaType.EXPRESSION, false, false,
				makeSList(), makeSList(), makeSList(), "1+1");
		saveRodinFileOf(root);
		runBuilder();
		// ISCTheoryRoot scTheoryRoot = root.getSCTheoryRoot();
		// FIXME isNotAccurate(scTheoryRoot);
		hasMarker(op1, EventBAttributes.LABEL_ATTRIBUTE, TheoryGraphProblem.OperatorWithSameSynJustBeenAddedError, "finite");
	}

	/**
	 * Formula type issues
	 */
	@Test
	public void testOperators_006_NoFormType() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		INewOperatorDefinition op1 = root.createChild(INewOperatorDefinition.ELEMENT_TYPE, null, null);
		op1.setLabel("seq", null);
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scTheoryRoot = root.getSCTheoryRoot();
		isNotAccurate(scTheoryRoot);
		hasMarker(op1, TheoryAttributes.FORMULA_TYPE_ATTRIBUTE, TheoryGraphProblem.OperatorFormTypeMissingError, "seq");
	}

	/**
	 * Notation type issues
	 */
	@Test
	public void testOperators_017_NoNotationType() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		INewOperatorDefinition op1 = root.createChild(INewOperatorDefinition.ELEMENT_TYPE, null, null);
		op1.setLabel("seq", null);
		op1.setFormulaType(FormulaType.EXPRESSION, null);
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scTheoryRoot = root.getSCTheoryRoot();
		isNotAccurate(scTheoryRoot);
		hasMarker(op1, TheoryAttributes.NOTATION_TYPE_ATTRIBUTE, TheoryGraphProblem.OperatorNotationTypeMissingError,
				"seq");
	}

	@Test
	public void testOperators_007_PostfixNotSupported() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		INewOperatorDefinition op1 = root.createChild(INewOperatorDefinition.ELEMENT_TYPE, null, null);
		op1.setLabel("seq", null);
		op1.setFormulaType(FormulaType.EXPRESSION, null);
		op1.setNotationType(Notation.POSTFIX.toString(), null);
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scTheoryRoot = root.getSCTheoryRoot();
		isNotAccurate(scTheoryRoot);
		hasMarker(op1, TheoryAttributes.NOTATION_TYPE_ATTRIBUTE, TheoryGraphProblem.OperatorCannotBePostfix);
	}

	/**
	 * Associativity tag issues
	 */
	@Test
	public void testOperators_008_NoAssoc() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		INewOperatorDefinition op1 = root.createChild(INewOperatorDefinition.ELEMENT_TYPE, null, null);
		op1.setLabel("seq", null);
		op1.setFormulaType(FormulaType.EXPRESSION, null);
		op1.setNotationType(Notation.INFIX.toString(), null);
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scTheoryRoot = root.getSCTheoryRoot();
		isNotAccurate(scTheoryRoot);
		hasMarker(op1, TheoryAttributes.ASSOCIATIVE_ATTRIBUTE, TheoryGraphProblem.OperatorAssocMissingError, "seq");
	}

	/**
	 * Commutativity tag issues
	 */
	@Test
	public void testOperators_009_NoCommut() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		INewOperatorDefinition op1 = root.createChild(INewOperatorDefinition.ELEMENT_TYPE, null, null);
		op1.setLabel("seq", null);
		op1.setFormulaType(FormulaType.EXPRESSION, null);
		op1.setNotationType(Notation.INFIX.toString(), null);
		op1.setAssociative(false, null);
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scTheoryRoot = root.getSCTheoryRoot();
		isNotAccurate(scTheoryRoot);
		hasMarker(op1, TheoryAttributes.COMMUTATIVE_ATTRIBUTE, TheoryGraphProblem.OperatorCommutMissingError, "seq");
	}

	/**
	 * Operator arguments missing ident
	 */
	@Test
	public void testOperators_010_OpArgMissIdent() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		INewOperatorDefinition def = addRawOperatorDefinition(root, "seq", Notation.PREFIX, FormulaType.EXPRESSION,
				false, false, makeSList(), makeSList(), makeSList());
		def.createChild(IDirectOperatorDefinition.ELEMENT_TYPE, null, null);
		IOperatorArgument arg = def.createChild(IOperatorArgument.ELEMENT_TYPE, null, null);
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scTheoryRoot = root.getSCTheoryRoot();
		ISCNewOperatorDefinition scDef = getOperatorDefinition(scTheoryRoot, "seq");
		isAccurate(scTheoryRoot);
		hasError(scDef);
		hasMarker(arg, EventBAttributes.IDENTIFIER_ATTRIBUTE, GraphProblem.IdentifierUndefError);
	}

	/**
	 * Operator arguments ident issue
	 */
	@Test
	public void testOperators_011_OpArgIdentIssues() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		addTypeParameters(root, "S");
		INewOperatorDefinition def = addRawOperatorDefinition(root, "seq", Notation.PREFIX, FormulaType.EXPRESSION,
				false, false, makeSList(), makeSList(), makeSList());
		def.createChild(IDirectOperatorDefinition.ELEMENT_TYPE, null, null);
		IOperatorArgument arg1 = def.createChild(IOperatorArgument.ELEMENT_TYPE, null, null);
		arg1.setIdentifierString("a#a", null);
		IOperatorArgument arg2 = def.createChild(IOperatorArgument.ELEMENT_TYPE, null, null);
		arg2.setIdentifierString("S", null);
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scTheoryRoot = root.getSCTheoryRoot();
		ISCNewOperatorDefinition scDef = getOperatorDefinition(scTheoryRoot, "seq");
		isNotAccurate(scTheoryRoot);
		hasError(scDef);
		hasMarker(arg1, EventBAttributes.IDENTIFIER_ATTRIBUTE);
		hasMarker(arg2, EventBAttributes.IDENTIFIER_ATTRIBUTE, TheoryGraphProblem.OperatorArgumentNameConflictError,
				"S");
	}

	/**
	 * Operator arguments missing type
	 */
	@Test
	public void testOperators_012_OpArgMissType() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		INewOperatorDefinition def = addRawOperatorDefinition(root, "seq", Notation.PREFIX, FormulaType.EXPRESSION,
				false, false, makeSList(), makeSList(), makeSList());
		def.createChild(IDirectOperatorDefinition.ELEMENT_TYPE, null, null);
		IOperatorArgument arg = def.createChild(IOperatorArgument.ELEMENT_TYPE, null, null);
		arg.setIdentifierString("a", null);
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scTheoryRoot = root.getSCTheoryRoot();
		ISCNewOperatorDefinition scDef = getOperatorDefinition(scTheoryRoot, "seq");
		isNotAccurate(scTheoryRoot);
		hasError(scDef);
		hasMarker(arg, EventBAttributes.EXPRESSION_ATTRIBUTE, GraphProblem.ExpressionUndefError);
	}

	/**
	 * Operator arguments type issues
	 */
	@Test
	public void testOperators_013_OpArgTypeIssues() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		addTypeParameters(root, "T");
		INewOperatorDefinition def = addRawOperatorDefinition(root, "seq", Notation.PREFIX, FormulaType.EXPRESSION,
				false, false, makeSList(), makeSList(), makeSList());
		def.createChild(IDirectOperatorDefinition.ELEMENT_TYPE, null, null);
		IOperatorArgument arg1 = def.createChild(IOperatorArgument.ELEMENT_TYPE, null, null);
		arg1.setIdentifierString("a", null);
		arg1.setExpressionString("S", null);
		IOperatorArgument arg2 = def.createChild(IOperatorArgument.ELEMENT_TYPE, null, null);
		arg2.setIdentifierString("b", null);
		arg2.setExpressionString("ℙ(T)", null);
		IOperatorArgument arg3 = def.createChild(IOperatorArgument.ELEMENT_TYPE, null, null);
		arg3.setIdentifierString("c", null);
		arg3.setExpressionString("TRUE", null);
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scTheoryRoot = root.getSCTheoryRoot();
		ISCNewOperatorDefinition scDef = getOperatorDefinition(scTheoryRoot, "seq");
		isNotAccurate(scTheoryRoot);
		hasError(scDef);
		hasMarker(arg1, EventBAttributes.EXPRESSION_ATTRIBUTE);
		hasNotMarker(arg2);
		hasMarker(arg3, EventBAttributes.EXPRESSION_ATTRIBUTE, TheoryGraphProblem.OpArgExprNotSet, "TRUE");
	}

	/**
	 * Operator WD cond missing
	 */
	@Test
	public void testOperators_014_WdCondMissing() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		addTypeParameters(root, "T");
		INewOperatorDefinition op = addRawOperatorDefinition(root, "size", Notation.PREFIX, FormulaType.EXPRESSION,
				false, false, makeSList("s"), makeSList("ℙ(T)"), makeSList());
		op.createChild(IDirectOperatorDefinition.ELEMENT_TYPE, null, null);
		IOperatorWDCondition wdCond1 = op.createChild(IOperatorWDCondition.ELEMENT_TYPE, null, null);
		IOperatorWDCondition wdCond2 = op.createChild(IOperatorWDCondition.ELEMENT_TYPE, null, null);
		wdCond2.setPredicateString("", null);
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scTheoryRoot = root.getSCTheoryRoot();
		ISCNewOperatorDefinition scDef = getOperatorDefinition(scTheoryRoot, "size");
		isNotAccurate(scTheoryRoot);
		hasError(scDef);
		hasMarker(wdCond1, EventBAttributes.PREDICATE_ATTRIBUTE, TheoryGraphProblem.WDPredMissingError);
		hasMarker(wdCond2, EventBAttributes.PREDICATE_ATTRIBUTE, TheoryGraphProblem.WDPredMissingError);
	}

	/**
	 * WD cond unparsable/untypable
	 */
	@Test
	public void testOperators_015_WdCondUntypUnpars() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		addTypeParameters(root, "T", "W");
		INewOperatorDefinition op = addRawOperatorDefinition(root, "size", Notation.PREFIX, FormulaType.EXPRESSION,
				false, false, makeSList("s"), makeSList("ℙ(T)"), makeSList());
		op.createChild(IDirectOperatorDefinition.ELEMENT_TYPE, null, null);
		IOperatorWDCondition wdCond1 = op.createChild(IOperatorWDCondition.ELEMENT_TYPE, null, null);
		wdCond1.setPredicateString("finite", null);
		IOperatorWDCondition wdCond2 = op.createChild(IOperatorWDCondition.ELEMENT_TYPE, null, null);
		wdCond2.setPredicateString("finite(s)", null);
		IOperatorWDCondition wdCond3 = op.createChild(IOperatorWDCondition.ELEMENT_TYPE, null, null);
		wdCond3.setPredicateString("finite(S)", null);
		IOperatorWDCondition wdCond4 = op.createChild(IOperatorWDCondition.ELEMENT_TYPE, null, null);
		wdCond4.setPredicateString("card(T)=card(W)", null);

		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scTheoryRoot = root.getSCTheoryRoot();
		ISCNewOperatorDefinition scDef = getOperatorDefinition(scTheoryRoot, "size");
		isNotAccurate(scTheoryRoot);
		hasError(scDef);
		hasMarker(wdCond1, EventBAttributes.PREDICATE_ATTRIBUTE);
		hasNotMarker(wdCond2);
		hasMarker(wdCond3, EventBAttributes.PREDICATE_ATTRIBUTE, GraphProblem.UndeclaredFreeIdentifierError, "S");
		//hasMarker(wdCond4, EventBAttributes.PREDICATE_ATTRIBUTE, TheoryGraphProblem.OpCannotReferToTheseIdents, "W");
	}

	/**
	 * Syntactic properties tests
	 */
	@Test
	public void testOperators_016_InfixNeedTwoArgs() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		INewOperatorDefinition op = addRawOperatorDefinition(root, "add", Notation.INFIX, FormulaType.EXPRESSION,
				false, false, makeSList("a"), makeSList("ℤ"), makeSList());
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scTheoryRoot = root.getSCTheoryRoot();
		isNotAccurate(scTheoryRoot);
		ISCNewOperatorDefinition scOp = getOperatorDefinition(scTheoryRoot, "add");
		hasError(scOp);
		hasMarker(op, EventBAttributes.LABEL_ATTRIBUTE, TheoryGraphProblem.OperatorExpInfixNeedsAtLeastTwoArgs);
	}

	/**
	 * Check that infix predicates can be defined, which used to be forbidden.
	 */
	@Test
	public void testOperators_018_InfixPredicate() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		INewOperatorDefinition op = addRawOperatorDefinition(root, "equals", Notation.INFIX, FormulaType.PREDICATE,
				false, false, makeSList("a", "b"), makeSList("ℤ", "ℤ"), makeSList());
		IDirectOperatorDefinition def = op.createChild(IDirectOperatorDefinition.ELEMENT_TYPE, null, null);
		def.setFormula("a=b", null);
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scTheoryRoot = root.getSCTheoryRoot();
		isAccurate(scTheoryRoot);
		containsMarkers(op, false);
	}

	@Test
	public void testOperators_019_PredNeedsOneArg() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		INewOperatorDefinition op = addRawOperatorDefinition(root, "equals", Notation.PREFIX, FormulaType.PREDICATE,
				false, false, makeSList(), makeSList(), makeSList());
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scTheoryRoot = root.getSCTheoryRoot();
		isNotAccurate(scTheoryRoot);
		ISCNewOperatorDefinition scOp = getOperatorDefinition(scTheoryRoot, "equals");
		hasError(scOp);
		hasMarker(op, EventBAttributes.LABEL_ATTRIBUTE, TheoryGraphProblem.OperatorPredNeedOneOrMoreArgs);
	}

	/**
	 * Direct definitions testing
	 */
	@Test
	public void testOperators_020_DirectDefMissingForm() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		addTypeParameters(root, "T");
		INewOperatorDefinition opDef = addRawOperatorDefinition(root, "subset", Notation.PREFIX, FormulaType.PREDICATE,
				false, false, makeSList("s1", "s2"), makeSList("ℙ(T)", "ℙ(T)"), makeSList());
		IDirectOperatorDefinition directDef = opDef.createChild(IDirectOperatorDefinition.ELEMENT_TYPE, null, null);
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scTheoryRoot = root.getSCTheoryRoot();
		isNotAccurate(scTheoryRoot);
		ISCNewOperatorDefinition scOp = getOperatorDefinition(scTheoryRoot, "subset");
		hasError(scOp);
		hasMarker(directDef, TheoryAttributes.FORMULA_ATTRIBUTE, TheoryGraphProblem.MissingFormulaError);
	}

	@Test
	public void testOperators_021_OpDefNotExpr() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		INewOperatorDefinition opDef = addRawOperatorDefinition(root, "add", Notation.INFIX, FormulaType.EXPRESSION,
				false, false, makeSList("a", "b"), makeSList("ℤ", "ℤ"), makeSList());
		IDirectOperatorDefinition directDef = opDef.createChild(IDirectOperatorDefinition.ELEMENT_TYPE, null, null);
		directDef.setFormula("a=b", null);
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scTheoryRoot = root.getSCTheoryRoot();
		isNotAccurate(scTheoryRoot);
		ISCNewOperatorDefinition scOp = getOperatorDefinition(scTheoryRoot, "add");
		hasError(scOp);
		hasMarker(directDef, TheoryAttributes.FORMULA_ATTRIBUTE, TheoryGraphProblem.OperatorDefNotExpError, "add");
	}

	@Test
	public void testOperators_022_OpDefNotPred() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		INewOperatorDefinition opDef = addRawOperatorDefinition(root, "equals", Notation.PREFIX, FormulaType.PREDICATE,
				false, false, makeSList("a", "b"), makeSList("ℤ", "ℤ"), makeSList());
		IDirectOperatorDefinition directDef = opDef.createChild(IDirectOperatorDefinition.ELEMENT_TYPE, null, null);
		directDef.setFormula("a+b", null);
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scTheoryRoot = root.getSCTheoryRoot();
		isNotAccurate(scTheoryRoot);
		ISCNewOperatorDefinition scOp = getOperatorDefinition(scTheoryRoot, "equals");
		hasError(scOp);
		hasMarker(directDef, TheoryAttributes.FORMULA_ATTRIBUTE, TheoryGraphProblem.OperatorDefNotPredError, "equals");
	}

	@Test
	public void testOperators_023_OpDefRefersToIllegalIdents() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		addTypeParameters(root, "T");
		INewOperatorDefinition opDef = addRawOperatorDefinition(root, "equals", Notation.PREFIX, FormulaType.PREDICATE,
				false, false, makeSList("a", "b"), makeSList("ℤ", "ℤ"), makeSList());
		IDirectOperatorDefinition directDef = opDef.createChild(IDirectOperatorDefinition.ELEMENT_TYPE, null, null);
		directDef.setFormula("card(T) = a+b+c", null);
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scTheoryRoot = root.getSCTheoryRoot();
		isNotAccurate(scTheoryRoot);
		ISCNewOperatorDefinition scOp = getOperatorDefinition(scTheoryRoot, "equals");
		hasError(scOp);
		//hasMarker(directDef, TheoryAttributes.FORMULA_ATTRIBUTE, TheoryGraphProblem.OpCannotReferToTheseIdents, "T, c");
		hasMarker(directDef, TheoryAttributes.FORMULA_ATTRIBUTE, TheoryGraphProblem.OpCannotReferToTheseIdents, "c");
	}

	@Test
	public void testOperators_024_OpDefUnpars() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		INewOperatorDefinition opDef = addRawOperatorDefinition(root, "equals", Notation.PREFIX, FormulaType.PREDICATE,
				false, false, makeSList("a", "b"), makeSList("ℤ", "ℤ"), makeSList());
		IDirectOperatorDefinition directDef = opDef.createChild(IDirectOperatorDefinition.ELEMENT_TYPE, null, null);
		directDef.setFormula("a+b #", null);
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scTheoryRoot = root.getSCTheoryRoot();
		isNotAccurate(scTheoryRoot);
		ISCNewOperatorDefinition scOp = getOperatorDefinition(scTheoryRoot, "equals");
		hasError(scOp);
		hasMarker(directDef, TheoryAttributes.FORMULA_ATTRIBUTE);
	}

	@Test
	public void testOperators_025_OpDefUntyp() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		INewOperatorDefinition opDef = addRawOperatorDefinition(root, "equals", Notation.PREFIX, FormulaType.PREDICATE,
				false, false, makeSList("a", "b"), makeSList("ℤ", "ℤ"), makeSList());
		IDirectOperatorDefinition directDef = opDef.createChild(IDirectOperatorDefinition.ELEMENT_TYPE, null, null);
		directDef.setFormula("a+b = ℤ", null);
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scTheoryRoot = root.getSCTheoryRoot();
		isNotAccurate(scTheoryRoot);
		ISCNewOperatorDefinition scOp = getOperatorDefinition(scTheoryRoot, "equals");
		hasError(scOp);
		hasMarker(directDef, TheoryAttributes.FORMULA_ATTRIBUTE);
	}

	/**
	 * Recursive definition testing
	 */
	@Test
	public void testOperators_026_IndArgMissing() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		addTypeParameters(root, "T");
		addDatatypeDefinition(root, "List", makeSList("T"), makeSList("nil", "cons"), new String[][] { makeSList(),
				makeSList("head", "tail") }, new String[][] { makeSList(), makeSList("T", "List(T)") });
		INewOperatorDefinition opDef = addRawOperatorDefinition(root, "listSize", Notation.PREFIX,
				FormulaType.EXPRESSION, false, false, makeSList("l"), makeSList("List(T)"), makeSList());
		IRecursiveOperatorDefinition recDef = opDef.createChild(IRecursiveOperatorDefinition.ELEMENT_TYPE, null, null);
		INewOperatorDefinition opDef1 = addRawOperatorDefinition(root, "listSize1", Notation.PREFIX,
				FormulaType.EXPRESSION, false, false, makeSList("l"), makeSList("List(T)"), makeSList());
		IRecursiveOperatorDefinition recDef1 = opDef1
				.createChild(IRecursiveOperatorDefinition.ELEMENT_TYPE, null, null);
		recDef1.setInductiveArgument("", null);
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scTheoryRoot = root.getSCTheoryRoot();
		isNotAccurate(scTheoryRoot);
		ISCNewOperatorDefinition scOp = getOperatorDefinition(scTheoryRoot, "listSize");
		ISCNewOperatorDefinition scOp1 = getOperatorDefinition(scTheoryRoot, "listSize1");
		hasError(scOp);
		hasError(scOp1);
		hasMarker(recDef, TheoryAttributes.INDUCTIVE_ARGUMENT_ATTRIBUTE, TheoryGraphProblem.InductiveArgMissing);
		hasMarker(recDef1, TheoryAttributes.INDUCTIVE_ARGUMENT_ATTRIBUTE, TheoryGraphProblem.InductiveArgMissing);
		hasNotMarker(opDef);
		hasNotMarker(opDef1);
	}

	@Test
	public void testOperators_027_IndArgProb() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		addTypeParameters(root, "T");
		addDatatypeDefinition(root, "List", makeSList("T"), makeSList("nil", "cons"), new String[][] { makeSList(),
				makeSList("head", "tail") }, new String[][] { makeSList(), makeSList("T", "List(T)") });
		INewOperatorDefinition opDef = addRawOperatorDefinition(root, "listSize", Notation.PREFIX,
				FormulaType.EXPRESSION, false, false, makeSList("l", "k"), makeSList("List(T)", "T"), makeSList());
		IRecursiveOperatorDefinition recDef = opDef.createChild(IRecursiveOperatorDefinition.ELEMENT_TYPE, null, null);
		recDef.setInductiveArgument("k", null);

		INewOperatorDefinition opDef1 = addRawOperatorDefinition(root, "listSize1", Notation.PREFIX,
				FormulaType.EXPRESSION, false, false, makeSList("l"), makeSList("List(T)"), makeSList());
		IRecursiveOperatorDefinition recDef1 = opDef1
				.createChild(IRecursiveOperatorDefinition.ELEMENT_TYPE, null, null);
		recDef1.setInductiveArgument("m", null);

		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scTheoryRoot = root.getSCTheoryRoot();
		isNotAccurate(scTheoryRoot);
		ISCNewOperatorDefinition scOp = getOperatorDefinition(scTheoryRoot, "listSize");
		hasError(scOp);
		hasMarker(recDef, TheoryAttributes.INDUCTIVE_ARGUMENT_ATTRIBUTE,
				TheoryGraphProblem.ArgumentNotExistOrNotParametric, "k");
		hasNotMarker(opDef);

		ISCNewOperatorDefinition scOp1 = getOperatorDefinition(scTheoryRoot, "listSize1");
		hasError(scOp1);
		hasMarker(recDef1, TheoryAttributes.INDUCTIVE_ARGUMENT_ATTRIBUTE,
				TheoryGraphProblem.ArgumentNotExistOrNotParametric, "m");
		hasNotMarker(opDef1);
	}

	/**
	 * Inductive cases problems
	 */
	@Test
	public void testOperators_028_NoRecCases() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		addTypeParameters(root, "T");
		addDatatypeDefinition(root, "List", makeSList("T"), makeSList("nil", "cons"), new String[][] { makeSList(),
				makeSList("head", "tail") }, new String[][] { makeSList(), makeSList("T", "List(T)") });
		INewOperatorDefinition opDef = addRawOperatorDefinition(root, "listSize", Notation.PREFIX,
				FormulaType.EXPRESSION, false, false, makeSList("l"), makeSList("List(T)"), makeSList());
		IRecursiveOperatorDefinition recDef = opDef.createChild(IRecursiveOperatorDefinition.ELEMENT_TYPE, null, null);
		recDef.setInductiveArgument("l", null);
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scTheoryRoot = root.getSCTheoryRoot();
		isAccurate(scTheoryRoot);
		ISCNewOperatorDefinition scOp = getOperatorDefinition(scTheoryRoot, "listSize");
		hasError(scOp);
		hasMarker(recDef, TheoryAttributes.INDUCTIVE_ARGUMENT_ATTRIBUTE, TheoryGraphProblem.NoRecCasesError);
	}

	@Test
	public void testOperators_029_NoExpInCase() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		addTypeParameters(root, "T");
		addDatatypeDefinition(root, "List", makeSList("T"), makeSList("nil", "cons"), new String[][] { makeSList(),
				makeSList("head", "tail") }, new String[][] { makeSList(), makeSList("T", "List(T)") });
		INewOperatorDefinition opDef = addRawOperatorDefinition(root, "listSize", Notation.PREFIX,
				FormulaType.EXPRESSION, false, false, makeSList("l"), makeSList("List(T)"), makeSList());
		IRecursiveOperatorDefinition recDef = opDef.createChild(IRecursiveOperatorDefinition.ELEMENT_TYPE, null, null);
		recDef.setInductiveArgument("l", null);
		
		IRecursiveDefinitionCase recCase1 = recDef.createChild(IRecursiveDefinitionCase.ELEMENT_TYPE, null, null);
		IRecursiveDefinitionCase recCase2 = recDef.createChild(IRecursiveDefinitionCase.ELEMENT_TYPE, null, null);
		recCase2.setExpressionString("", null);
		
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scTheoryRoot = root.getSCTheoryRoot();
		isNotAccurate(scTheoryRoot);
		ISCNewOperatorDefinition scOp = getOperatorDefinition(scTheoryRoot, "listSize");
		hasError(scOp);
		hasMarker(recCase1, EventBAttributes.EXPRESSION_ATTRIBUTE, GraphProblem.ExpressionUndefError);
		hasMarker(recCase2, EventBAttributes.EXPRESSION_ATTRIBUTE, GraphProblem.ExpressionUndefError);
	}
	
	@Test
	public void testOperators_030_UnparsCase() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		addTypeParameters(root, "T");
		addDatatypeDefinition(root, "List", makeSList("T"), makeSList("nil", "cons"), new String[][] { makeSList(),
				makeSList("head", "tail") }, new String[][] { makeSList(), makeSList("T", "List(T)") });
		INewOperatorDefinition opDef = addRawOperatorDefinition(root, "listSize", Notation.PREFIX,
				FormulaType.EXPRESSION, false, false, makeSList("l"), makeSList("List(T)"), makeSList());
		IRecursiveOperatorDefinition recDef = opDef.createChild(IRecursiveOperatorDefinition.ELEMENT_TYPE, null, null);
		recDef.setInductiveArgument("l", null);
		
		IRecursiveDefinitionCase recCase1 = recDef.createChild(IRecursiveDefinitionCase.ELEMENT_TYPE, null, null);
		recCase1.setExpressionString("as#asd", null);
		
		IRecursiveDefinitionCase recCase2 = recDef.createChild(IRecursiveDefinitionCase.ELEMENT_TYPE, null, null);
		recCase2.setExpressionString("List(T)", null);
		
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scTheoryRoot = root.getSCTheoryRoot();
		isNotAccurate(scTheoryRoot);
		ISCNewOperatorDefinition scOp = getOperatorDefinition(scTheoryRoot, "listSize");
		hasError(scOp);
		hasMarker(recCase1, EventBAttributes.EXPRESSION_ATTRIBUTE);
		hasMarker(recCase2, EventBAttributes.EXPRESSION_ATTRIBUTE, TheoryGraphProblem.InductiveCaseNotAppropriateExp, "List(T)");
	}
	
	@Test
	public void testOperators_031_IllegalCase() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		addTypeParameters(root, "T");
		addDatatypeDefinition(root, "List", makeSList("T"), makeSList("nil", "cons"), new String[][] { makeSList(),
				makeSList("head", "tail") }, new String[][] { makeSList(), makeSList("T", "List(T)") });
		INewOperatorDefinition opDef = addRawOperatorDefinition(root, "listSize", Notation.PREFIX,
				FormulaType.EXPRESSION, false, false, makeSList("l"), makeSList("List(T)"), makeSList());
		IRecursiveOperatorDefinition recDef = opDef.createChild(IRecursiveOperatorDefinition.ELEMENT_TYPE, null, null);
		recDef.setInductiveArgument("l", null);
		
		IRecursiveDefinitionCase recCase1 = recDef.createChild(IRecursiveDefinitionCase.ELEMENT_TYPE, null, null);
		recCase1.setExpressionString("cons(1, l0)", null);
		
		IRecursiveDefinitionCase recCase2 = recDef.createChild(IRecursiveDefinitionCase.ELEMENT_TYPE, null, null);
		recCase2.setExpressionString("cons(x, nil)", null);
		
		IRecursiveDefinitionCase recCase3 = recDef.createChild(IRecursiveDefinitionCase.ELEMENT_TYPE, null, null);
		recCase3.setExpressionString("cons(x, l)", null);
		
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scTheoryRoot = root.getSCTheoryRoot();
		isNotAccurate(scTheoryRoot);
		ISCNewOperatorDefinition scOp = getOperatorDefinition(scTheoryRoot, "listSize");
		hasError(scOp);
		hasMarker(recCase1, EventBAttributes.EXPRESSION_ATTRIBUTE, TheoryGraphProblem.ConsArgNotIdentInCase, "1");
		hasMarker(recCase2, EventBAttributes.EXPRESSION_ATTRIBUTE, TheoryGraphProblem.ConsArgNotIdentInCase, "nil");
		hasMarker(recCase3, EventBAttributes.EXPRESSION_ATTRIBUTE, TheoryGraphProblem.IdentCannotBeUsedAsConsArg, "l");
	}
	
	@Test
	public void testOperators_032_CoveredCons() throws Exception{
		ITheoryRoot root = createTheory(THEORY_NAME);
		addTypeParameters(root, "T");
		addDatatypeDefinition(root, "List", makeSList("T"), makeSList("nil", "cons"), new String[][] { makeSList(),
				makeSList("head", "tail") }, new String[][] { makeSList(), makeSList("T", "List(T)") });
		INewOperatorDefinition opDef = addRawOperatorDefinition(root, "listSize", Notation.PREFIX,
				FormulaType.EXPRESSION, false, false, makeSList("l"), makeSList("List(T)"), makeSList());
		IRecursiveOperatorDefinition recDef = opDef.createChild(IRecursiveOperatorDefinition.ELEMENT_TYPE, null, null);
		recDef.setInductiveArgument("l", null);
		
		IRecursiveDefinitionCase recCase1 = recDef.createChild(IRecursiveDefinitionCase.ELEMENT_TYPE, null, null);
		recCase1.setExpressionString("cons(x0, l0)", null);
		
		IRecursiveDefinitionCase recCase2 = recDef.createChild(IRecursiveDefinitionCase.ELEMENT_TYPE, null, null);
		recCase2.setExpressionString("cons(x1, l1)", null);
		
		IRecursiveDefinitionCase recCase3 = recDef.createChild(IRecursiveDefinitionCase.ELEMENT_TYPE, null, null);
		recCase3.setExpressionString("nil", null);
		
		IRecursiveDefinitionCase recCase4 = recDef.createChild(IRecursiveDefinitionCase.ELEMENT_TYPE, null, null);
		recCase4.setExpressionString("nil", null);
		
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scTheoryRoot = root.getSCTheoryRoot();
		isNotAccurate(scTheoryRoot);
		ISCNewOperatorDefinition scOp = getOperatorDefinition(scTheoryRoot, "listSize");
		hasError(scOp);
		
		hasMarker(recCase2, EventBAttributes.EXPRESSION_ATTRIBUTE, TheoryGraphProblem.RecCaseAlreadyCovered);
		hasMarker(recCase4, EventBAttributes.EXPRESSION_ATTRIBUTE, TheoryGraphProblem.RecCaseAlreadyCovered);
	}
	
	@Test
	public void testOperators_033_ConsCoverage() throws Exception{
		ITheoryRoot root = createTheory(THEORY_NAME);
		addTypeParameters(root, "T");
		addDatatypeDefinition(root, "List", makeSList("T"), makeSList("nil", "cons"), new String[][] { makeSList(),
				makeSList("head", "tail") }, new String[][] { makeSList(), makeSList("T", "List(T)") });
		INewOperatorDefinition opDef = addRawOperatorDefinition(root, "listSize", Notation.PREFIX,
				FormulaType.EXPRESSION, false, false, makeSList("l"), makeSList("List(T)"), makeSList());
		IRecursiveOperatorDefinition recDef = opDef.createChild(IRecursiveOperatorDefinition.ELEMENT_TYPE, null, null);
		recDef.setInductiveArgument("l", null);
		
		IRecursiveDefinitionCase recCase1 = recDef.createChild(IRecursiveDefinitionCase.ELEMENT_TYPE, null, null);
		recCase1.setExpressionString("cons(x0, l0)", null);
		
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scTheoryRoot = root.getSCTheoryRoot();
		isNotAccurate(scTheoryRoot);
		ISCNewOperatorDefinition scOp = getOperatorDefinition(scTheoryRoot, "listSize");
		hasError(scOp);
		hasMarker(recDef, TheoryAttributes.INDUCTIVE_ARGUMENT_ATTRIBUTE, TheoryGraphProblem.NoCoverageAllRecCase);
	}
}
