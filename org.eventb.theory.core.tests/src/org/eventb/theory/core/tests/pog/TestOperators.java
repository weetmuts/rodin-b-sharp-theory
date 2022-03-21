/*******************************************************************************
 * Copyright (c) 2022 Université de Lorraine.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.tests.pog;

import static org.junit.Assert.assertEquals;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IPORoot;
import org.eventb.core.IPOSequent;
import org.eventb.core.ast.extension.IOperatorProperties.FormulaType;
import org.eventb.core.ast.extension.IOperatorProperties.Notation;
import org.eventb.theory.core.IDirectOperatorDefinition;
import org.eventb.theory.core.INewOperatorDefinition;
import org.eventb.theory.core.IOperatorWDCondition;
import org.eventb.theory.core.IRecursiveDefinitionCase;
import org.eventb.theory.core.IRecursiveOperatorDefinition;
import org.eventb.theory.core.ITheoryRoot;
import org.eventb.theory.core.tests.sc.BasicTheorySCTestWithThyConfig;
import org.junit.Test;

/**
 * Tests for the proof obligation generator.
 *
 * @author Guillaume Verdier
 */
public class TestOperators extends BasicTheorySCTestWithThyConfig {

	/*
	 * Test the PO generated for the WD condition of an operator with a direct
	 * definition.
	 */
	@Test
	public void test_directDefWD() throws CoreException {
		ITheoryRoot root = createTheory(THEORY_NAME);
		INewOperatorDefinition opDef = addRawOperatorDefinition(root, "div", Notation.PREFIX, FormulaType.EXPRESSION,
				false, false, makeSList("a", "b"), makeSList("ℤ", "ℤ"), makeSList());
		IOperatorWDCondition wdCond = opDef.createChild(IOperatorWDCondition.ELEMENT_TYPE, null, null);
		wdCond.setPredicateString("b>0", null);
		IDirectOperatorDefinition directDef = opDef.createChild(IDirectOperatorDefinition.ELEMENT_TYPE, null, null);
		directDef.setFormula("a÷b", null);
		saveRodinFileOf(root);
		runBuilder();
		isAccurate(root.getSCTheoryRoot());
		IPORoot poRoot = root.getPORoot();
		IPOSequent[] sequents = poRoot.getChildrenOfType(IPOSequent.ELEMENT_TYPE);
		assertEquals("expected one PO", 1, sequents.length);
		assertEquals("unexpected PO name", "div/Op-WD", sequents[0].getElementName());
		assertEquals("unexpected PO goal", "∀a⦂ℤ,b⦂ℤ·a∈ℤ∧b∈ℤ⇒(b>0⇒b≠0)",
				sequents[0].getGoals()[0].getPredicateString());
	}

	/*
	 * Test the PO generated for the WD condition of an operator with a recursive
	 * definition.
	 */
	@Test
	public void test_recursiveDefWD() throws CoreException {
		ITheoryRoot root = createTheory(THEORY_NAME);
		addTypeParameters(root, "T");
		addDatatypeDefinition(root, "List", makeSList("T"), makeSList("nil", "cons"),
				new String[][] { makeSList(), makeSList("head", "tail") },
				new String[][] { makeSList(), makeSList("T", "List(T)") });
		INewOperatorDefinition opDef = addRawOperatorDefinition(root, "testOp", Notation.PREFIX, FormulaType.EXPRESSION,
				false, false, makeSList("n", "l"), makeSList("ℤ", "List(T)"), makeSList());
		IOperatorWDCondition wdCond = opDef.createChild(IOperatorWDCondition.ELEMENT_TYPE, null, null);
		wdCond.setPredicateString("n>0", null);
		IRecursiveOperatorDefinition recDef = opDef.createChild(IRecursiveOperatorDefinition.ELEMENT_TYPE, null, null);
		recDef.setInductiveArgument("l", null);
		IRecursiveDefinitionCase recCase1 = recDef.createChild(IRecursiveDefinitionCase.ELEMENT_TYPE, null, null);
		recCase1.setExpressionString("nil", null);
		recCase1.setFormula("2÷n", null);
		IRecursiveDefinitionCase recCase2 = recDef.createChild(IRecursiveDefinitionCase.ELEMENT_TYPE, null, null);
		recCase2.setExpressionString("cons(x, l2)", null);
		recCase2.setFormula("testOp(n, l2)+10÷(n+1)", null);
		saveRodinFileOf(root);
		runBuilder();
		isAccurate(root.getSCTheoryRoot());
		IPORoot poRoot = root.getPORoot();
		IPOSequent[] sequents = poRoot.getChildrenOfType(IPOSequent.ELEMENT_TYPE);
		assertEquals("expected one PO", 1, sequents.length);
		assertEquals("unexpected PO name", "testOp/Op-WD", sequents[0].getElementName());
		assertEquals("unexpected PO goal", "∀n⦂ℤ,l⦂List(T)·n∈ℤ∧l∈List(T)⇒(n>0⇒" /* Hypothesis: type info + user WD */
				+ "(∀T⦂ℙ(T)·l=(nil ⦂ List(T))⇒n≠0)∧" /* Base case */
				+ "(∀T⦂ℙ(T),l2⦂List(T),x⦂T·l=cons(x,l2)⇒n>0∧n+1≠0))", /* Recursive case */
				sequents[0].getGoals()[0].getPredicateString());
	}

	/*
	 * Test that no unneeded PO is generated for the WD condition of an operator
	 * with a recursive definition that does not require it.
	 */
	@Test
	public void test_recursiveDefNoWD() throws CoreException {
		ITheoryRoot root = createTheory(THEORY_NAME);
		addTypeParameters(root, "T");
		addDatatypeDefinition(root, "List", makeSList("T"), makeSList("nil", "cons"),
				new String[][] { makeSList(), makeSList("head", "tail") },
				new String[][] { makeSList(), makeSList("T", "List(T)") });
		INewOperatorDefinition opDef = addRawOperatorDefinition(root, "listSize", Notation.PREFIX,
				FormulaType.EXPRESSION, false, false, makeSList("l"), makeSList("List(T)"), makeSList());
		IRecursiveOperatorDefinition recDef = opDef.createChild(IRecursiveOperatorDefinition.ELEMENT_TYPE, null, null);
		recDef.setInductiveArgument("l", null);
		IRecursiveDefinitionCase recCase1 = recDef.createChild(IRecursiveDefinitionCase.ELEMENT_TYPE, null, null);
		recCase1.setExpressionString("nil", null);
		recCase1.setFormula("0", null);
		IRecursiveDefinitionCase recCase2 = recDef.createChild(IRecursiveDefinitionCase.ELEMENT_TYPE, null, null);
		recCase2.setExpressionString("cons(x, l2)", null);
		recCase2.setFormula("listSize(l2)+1", null);
		saveRodinFileOf(root);
		runBuilder();
		isAccurate(root.getSCTheoryRoot());
		IPORoot poRoot = root.getPORoot();
		IPOSequent[] sequents = poRoot.getChildrenOfType(IPOSequent.ELEMENT_TYPE);
		assertEquals("expected no PO", 0, sequents.length);
	}

}
