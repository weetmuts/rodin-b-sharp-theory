/*******************************************************************************
 * Copyright (c) 2020 CentraleSupélec
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     CentraleSupélec - initial tests
 *******************************************************************************/
package org.eventb.theory.language.core.tests.sc.modules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eventb.core.IContextRoot;
import org.eventb.core.IPOSequent;
import org.eventb.core.ISCContextRoot;
import org.eventb.core.ast.extension.IOperatorProperties.FormulaType;
import org.eventb.core.ast.extension.IOperatorProperties.Notation;
import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.core.IDeploymentResult;
import org.eventb.theory.core.INewOperatorDefinition;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.ITheoryPathRoot;
import org.eventb.theory.core.ITheoryRoot;
import org.eventb.theory.language.core.tests.sc.BasicTestSCTheoryPath;
import org.junit.Test;
import org.rodinp.core.RodinDBException;

import ch.ethz.eventb.utils.EventBUtils;

/**
 * Test that elements defined in theories can actually be used in contexts.
 *
 * @author Guillaume Verdier
 *
 */
public class TestDeployedTheories extends BasicTestSCTheoryPath {

	public static final String CONTEXT_NAME = "ctx";

	/**
	 * Deploys a theory and adds it to the Theory Path.
	 *
	 * This method also checks that the theory contains no errors and that the
	 * deployment succeeds.
	 *
	 * @param root Theory to deploy
	 */
	protected void deployAndAddToTheoryPath(ITheoryRoot root) throws Exception {
		IProgressMonitor nullMonitor = new NullProgressMonitor();
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scTheoryRoot = root.getSCTheoryRoot();
		isAccurate(scTheoryRoot);
		containsMarkers(scTheoryRoot, false);
		ITheoryPathRoot path = createTheoryPath(THEORYPATH_NAME, rodinProject);
		runBuilder();
		IDeploymentResult depRes = createDeployedTheory(scTheoryRoot, nullMonitor);
		assertTrue("deployment failed", depRes.succeeded());
		IDeployedTheoryRoot deployedRoot = scTheoryRoot.getDeployedTheoryRoot();
		addDeployedTheory(path, deployedRoot, null);
		saveRodinFileOf(path);
		runBuilder();
	}

	/**
	 * Creates and deploys a theory containing an operator with a direct definition.
	 *
	 * The theory is named THEORY_NAME.
	 *
	 * This method also checks that the theory contains no errors and that the
	 * deployment succeeds.
	 *
	 * @param label                   Name of the operator
	 * @param notation                Whether the operator is prefix or infix
	 * @param type                    Whether the operator is an expression or a
	 *                                predicate
	 * @param arguments               Names of the arguments
	 * @param argumentTypeExpressions Types of the arguments
	 * @param wdConditions            Well-definedness conditions
	 * @param definition              Definition of the operator
	 * @return the created theory
	 */
	protected ITheoryRoot createAndDeployTheoryWithOperator(String label, Notation notation, FormulaType type,
			String[] arguments, String[] argumentTypeExpressions, String[] wdConditions, String definition)
			throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		INewOperatorDefinition op = addRawOperatorDefinition(root, label, notation, type, false, false, arguments,
				argumentTypeExpressions, wdConditions);
		addDirectOperatorDefinition(op, definition);
		deployAndAddToTheoryPath(root);
		return root;
	}

	/**
	 * Saves a context and checks that it passes static checking without errors.
	 *
	 * @param ctxRoot Context to save and check
	 */
	protected void saveAndCheckContext(IContextRoot ctxRoot) throws Exception {
		saveRodinFileOf(ctxRoot);
		runBuilder();
		ISCContextRoot scCtxRoot = ctxRoot.getSCContextRoot();
		isAccurate(scCtxRoot);
		containsMarkers(scCtxRoot, false);
	}

	/**
	 * Creates a context containing a theorem and checks that it passes static
	 * checking without errors.
	 *
	 * The context is named CONTEXT_NAME and the theorem is named THEOREM_LABEL.
	 *
	 * @param theoremDefinition Definition of the theorem
	 */
	protected IContextRoot createAndBuildContextWithTheorem(String theoremDefinition) throws Exception {
		IContextRoot ctxRoot = EventBUtils.createContext(eventBProject, CONTEXT_NAME, null);
		EventBUtils.createAxiom(ctxRoot, THEOREM_LABEL, theoremDefinition, true, null, null);
		saveAndCheckContext(ctxRoot);
		return ctxRoot;
	}

	/**
	 * Checks that a context contains two proof obligations: a theorem and its
	 * well-definedness condition.
	 *
	 * The theorem has to be named THEOREM_LABEL.
	 */
	protected void checkPOForTheoremAndWD(ISCContextRoot ctxRoot) throws RodinDBException {
		IPOSequent[] proofs = ctxRoot.getPORoot().getChildrenOfType(IPOSequent.ELEMENT_TYPE);
		assertEquals("expects two children: user theorem and WD", proofs.length, 2);
		assertEquals(proofs[0].getElementName(), THEOREM_LABEL + "/WD");
		assertEquals(proofs[1].getElementName(), THEOREM_LABEL + "/THM");
	}

	/**
	 * Tests a prefix predicate with a WD condition.
	 */
	@Test
	public void test_prefixPredicate() throws Exception {
		createAndDeployTheoryWithOperator("equal", Notation.PREFIX, FormulaType.PREDICATE, makeSList("a", "b"),
				makeSList("ℤ", "ℤ"), makeSList("a ≠ 0"), "a = b");
		IContextRoot ctxRoot = createAndBuildContextWithTheorem("equal(1, 1)");
		checkPOForTheoremAndWD(ctxRoot.getSCContextRoot());
	}

	/**
	 * Tests an infix predicate with a WD condition.
	 */
	@Test
	public void test_infixPredicate() throws Exception {
		createAndDeployTheoryWithOperator("==", Notation.INFIX, FormulaType.PREDICATE, makeSList("a", "b"),
				makeSList("ℤ", "ℤ"), makeSList("a ≠ 0"), "a = b");
		IContextRoot ctxRoot = createAndBuildContextWithTheorem("1 == 1");
		checkPOForTheoremAndWD(ctxRoot.getSCContextRoot());
	}

	/**
	 * Tests a prefix expression with a WD condition.
	 */
	@Test
	public void test_prefixExpression() throws Exception {
		createAndDeployTheoryWithOperator("div", Notation.PREFIX, FormulaType.EXPRESSION, makeSList("a", "b"),
				makeSList("ℤ", "ℤ"), makeSList("b ≠ 0"), "a ÷ b");
		IContextRoot ctxRoot = createAndBuildContextWithTheorem("div(2, 2) = 1");
		checkPOForTheoremAndWD(ctxRoot.getSCContextRoot());
	}

	/**
	 * Tests an infix expression with a WD condition.
	 */
	@Test
	public void test_infixExpression() throws Exception {
		createAndDeployTheoryWithOperator("/", Notation.INFIX, FormulaType.EXPRESSION, makeSList("a", "b"),
				makeSList("ℤ", "ℤ"), makeSList("b ≠ 0"), "a ÷ b");
		IContextRoot ctxRoot = createAndBuildContextWithTheorem("2 / 2 = 1");
		checkPOForTheoremAndWD(ctxRoot.getSCContextRoot());
	}

	/**
	 * Tests creating a theory after a context and using it in the context.
	 */
	@Test
	public void test_createTheoryAfterContext() throws Exception {
		// Step 1: create a basic Rodin context
		IContextRoot ctxRoot = createAndBuildContextWithTheorem("0 = 0");
		// Step 2: create a theory, deploy it and add a theory path
		createAndDeployTheoryWithOperator("ident", Notation.PREFIX, FormulaType.EXPRESSION, makeSList("x"),
				makeSList("ℤ"), makeSList(), "x");
		// Step 3: use something from this theory in the already existing context
		EventBUtils.createAxiom(ctxRoot, THEOREM_LABEL + "2", "ident(0) = 0", true, null, null);
		// Result: there should be no errors in the context
		saveAndCheckContext(ctxRoot);
	}

	/**
	 * Tests editing a theory after it was deployed.
	 */
	@Test
	public void test_editTheoryAfterDeployment() throws Exception {
		// Step 1: create a theory, deploy it and add a theory path
		ITheoryRoot thyRoot = createAndDeployTheoryWithOperator("ident", Notation.PREFIX, FormulaType.EXPRESSION,
				makeSList("x"), makeSList("ℤ"), makeSList(), "x");
		// Step 2: create a context that uses this theory
		IContextRoot ctxRoot = createAndBuildContextWithTheorem("ident(0) = 0");
		// Step 3: modify the theory
		INewOperatorDefinition op = addRawOperatorDefinition(thyRoot, "ident2", Notation.PREFIX, FormulaType.EXPRESSION,
				false, false, makeSList("y"), makeSList("ℤ"), makeSList());
		addDirectOperatorDefinition(op, "y");
		deployAndAddToTheoryPath(thyRoot);
		// Step 4: use the updated theory
		EventBUtils.createAxiom(ctxRoot, THEOREM_LABEL + "2", "ident(0) = ident2(0)", true, null, null);
		// Result: there should be no errors in the context
		saveAndCheckContext(ctxRoot);
	}
}
