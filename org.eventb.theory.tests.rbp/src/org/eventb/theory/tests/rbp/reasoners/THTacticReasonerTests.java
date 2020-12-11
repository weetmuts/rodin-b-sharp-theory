/*******************************************************************************
 * Copyright (c) 2020 CentraleSupélec.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.tests.rbp.reasoners;

import static org.junit.Assert.assertNotNull;

import java.util.Collections;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.pm.IProofAttempt;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.UntranslatableException;
import org.eventb.theory.core.IAxiomaticDefinitionAxiom;
import org.eventb.theory.core.IAxiomaticDefinitionsBlock;
import org.eventb.theory.core.IImportTheoryProject;
import org.eventb.theory.core.ISCTheorem;
import org.eventb.theory.core.ITheoryRoot;
import org.eventb.theory.internal.rbp.reasoners.input.MultipleStringInput;
import org.eventb.theory.rbp.reasoners.THReasoner;
import org.eventb.theory.rbp.rulebase.IPOContext;
import org.eventb.theory.rbp.rulebase.basis.POContext;
import org.eventb.theory.rbp.tactics.ui.TheoremsRetriever;
import org.junit.Test;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

/**
 * Tests the {@link THReasoner} used to add theorems.
 *
 * Note that parts of the code that select and instantiate theorems are inside
 * UI classes ({@link org.eventb.theory.rbp.tactics.ui.TheoremSelectorWizard}
 * and its pages) and can't be tested easily. They are simulated in these tests.
 *
 * @author Guillaume Verdier
 */
public class THTacticReasonerTests extends AbstractRBPReasonerTests {

	/**
	 * The theory being tested.
	 */
	protected ITheoryRoot testThy;

	/**
	 * A theory with a type parameter, to instantiate in {@code testThy}.
	 */
	protected ITheoryRoot polyThy;

	/**
	 * Creates the test theories.
	 *
	 * This defines:
	 * <ul>
	 * <li>{@code Polymorphic theory} with a type parameter {@code T} and a theorem
	 * {@code thm1: ∀ x· x ∈ T ⇒ x = x};</li>
	 * <li>{@code Test theory} with a theorem {@code thm1} and an axiom {@code axm1}
	 * both defined as {@code 1 + 1 = 2 ∗ 1}, and a theorem {@code test-thm: ⊥} used
	 * as a proof context for testing the reasoner.</li>
	 * </ul>
	 */
	@Override
	public ITheoryRoot[] createTheories(IRodinProject thyPrj) throws RodinDBException {
		polyThy = TheoryUtils.createTheory(thyPrj.getRodinProject(), "Polymorphic theory", null);
		TheoryUtils.createTypeParameter(polyThy, "T", null, null);
		TheoryUtils.createTheorem(polyThy, "thm1", "∀ x· x ∈ T ⇒ x = x", null);
		polyThy.getRodinFile().save(null, true);

		testThy = TheoryUtils.createTheory(thyPrj.getRodinProject(), "Test theory", null);
		TheoryUtils.createTheorem(testThy, "thm1", "1 + 1 = 2 ∗ 1", null);
		TheoryUtils.createTheorem(testThy, "test-thm", "⊥", null);
		IAxiomaticDefinitionsBlock block = testThy.createChild(IAxiomaticDefinitionsBlock.ELEMENT_TYPE, null, null);
		block.setLabel("axiomatic", null);
		IAxiomaticDefinitionAxiom axiom = block.createChild(IAxiomaticDefinitionAxiom.ELEMENT_TYPE, null, null);
		axiom.setLabel("axm1", null);
		axiom.setPredicateString("1 + 1 = 2 ∗ 1", null);
		testThy.getRodinFile().save(null, true);

		return new ITheoryRoot[] { testThy, polyThy };
	}

	@Override
	public String getReasonerID() {
		return THReasoner.REASONER_ID;
	}

	/**
	 * Gets the statement of a theorem or axiom, to be used as input of
	 * {@link THReasoner}.
	 *
	 * The test will fail if the theorem or axiom does not exist.
	 *
	 * @param pa      the current proof attempt
	 * @param project name of the project where the theorem is
	 * @param theory  name of the theory where the project is
	 * @param label   name of the theorem or axiom
	 * @return the statement of the theorem or axiom
	 * @see TheoremsRetriever
	 */
	protected String retrieveTheoremOrAxiom(IProofAttempt pa, String project, String theory, String label)
			throws CoreException {
		IPOContext context = new POContext(pa.getComponent().getPORoot(), -1, false);
		TheoremsRetriever retriever = new TheoremsRetriever(context);
		ISCTheorem theorem = retriever.getSCTheorem(project, theory, label);
		assertNotNull("theorem not found", theorem);
		ITypeEnvironment typeEnv = pa.getProofTree().getRoot().getSequent().typeEnvironment();
		return theorem.getPredicate(typeEnv).toString();
	}

	/**
	 * Applies the reasoner and checks that it succeeds.
	 *
	 * @param pa       the current proof attempt
	 * @param msg      message for debug logs
	 * @param thmPred  statement of the theorem to add
	 * @param sequents expected sequents after the reasoner application
	 */
	protected void checkReasonerSuccess(IProofAttempt pa, String msg, String thmPred, String... sequents)
			throws UntranslatableException {
		IProverSequent sequent = pa.getProofTree().getRoot().getSequent();
		IReasonerInput input = new MultipleStringInput(Collections.singletonList(thmPred));
		SuccessfullReasonerApplication appl = new SuccessfullReasonerApplication(sequent, input, sequents);
		testSuccessfulReasonerApplications(msg, appl);
	}

	/**
	 * Instantiates a polymorphic theorem.
	 *
	 * The instantiation is done directly in a UI class
	 * ({@link org.eventb.theory.rbp.tactics.ui.TheoremSelectorWizardPageTwo}) and
	 * can't be used in this test, so we do the same thing here...
	 *
	 * @param ff      formula factory to use
	 * @param thmPred statement of the theorem to instantiate
	 * @param ident   identifier to instantiate
	 * @param expr    expression that will replace {@code ident}
	 * @return statement of the instantiated theorem
	 */
	protected String substitute(FormulaFactory ff, String thmPred, String ident, String expr) {
		Map<FreeIdentifier, Expression> substs = Collections.singletonMap(ff.makeFreeIdentifier(ident, null),
				ff.parseExpression(expr, null).getParsedExpression());
		return ff.parsePredicate(thmPred, null).getParsedPredicate().substituteFreeIdents(substs).toString();
	}

	/** Adds a theorem to the hypotheses of the current proof. */
	@Test
	public void test_addTheorem() throws Exception {
		IProofAttempt pa = createProofAttempt(testThy.getSCTheoryRoot(), "test-thm/S-THM", "THTactic Reasoner Test");
		String thmPred = retrieveTheoremOrAxiom(pa, "Theories", "Test theory", "thm1");
		checkReasonerSuccess(pa, "RBP theorem instantiation", thmPred, "{}[][][1+1=2∗1] |- ⊥");
	}

	/** Adds an axiom to the hypotheses of the current proof. */
	@Test
	public void test_addAxiom() throws Exception {
		IProofAttempt pa = createProofAttempt(testThy.getSCTheoryRoot(), "test-thm/S-THM", "THTactic Reasoner Test");
		String axmPred = retrieveTheoremOrAxiom(pa, "Theories", "Test theory", "axm1");
		checkReasonerSuccess(pa, "RBP axiom instantiation", axmPred, "{}[][][1+1=2∗1] |- ⊥");
	}

	/** Instantiates and adds to the hypotheses a theorem from another theory. */
	@Test
	public void test_addPolymorphicTheorem() throws Exception {
		IImportTheoryProject importTheory = TheoryUtils.createImportTheoryProject(testThy, thyPrj.getRodinProject(),
				null);
		TheoryUtils.createImportTheory(importTheory, polyThy, null);
		testThy.getRodinFile().save(null, true);
		runBuilder(thyPrj.getRodinProject());
		IProofAttempt pa = createProofAttempt(testThy.getSCTheoryRoot(), "test-thm/S-THM", "THTactic Reasoner Test");
		FormulaFactory ff = pa.getFormulaFactory();
		String thmPred = retrieveTheoremOrAxiom(pa, "Theories", "Polymorphic theory", "thm1");
		thmPred = substitute(ff, thmPred, "T", "ℤ");
		checkReasonerSuccess(pa, "RBP theorem instantiation", thmPred, "{}[][][∀ x· x ∈ ℤ ⇒ x = x] |- ⊥");
	}

}
