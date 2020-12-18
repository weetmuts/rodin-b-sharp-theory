/*******************************************************************************
 * Copyright (c) 2020 CentraleSupélec.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.tests.rbp.reasoners;

import java.util.Collections;
import java.util.Map;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.pm.IProofAttempt;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.UntranslatableException;
import org.eventb.theory.core.ITheoryRoot;
import org.eventb.theory.internal.rbp.reasoners.input.MultipleStringInput;
import org.eventb.theory.rbp.reasoners.THReasoner;
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
	 * Creates the test theories.
	 *
	 * This defines {@code Test theory} with a theorem {@code test-thm: ⊥} used as a
	 * proof context for testing the reasoner.
	 */
	@Override
	public ITheoryRoot[] createTheories(IRodinProject thyPrj) throws RodinDBException {
		testThy = TheoryUtils.createTheory(thyPrj.getRodinProject(), "Test theory", null);
		TheoryUtils.createTheorem(testThy, "test-thm", "⊥", null);
		testThy.getRodinFile().save(null, true);

		return new ITheoryRoot[] { testThy };
	}

	@Override
	public String getReasonerID() {
		return THReasoner.REASONER_ID;
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
		checkReasonerSuccess(pa, "RBP theorem instantiation", "1 + 1 = 2 ∗ 1", "{}[][][1+1=2∗1] |- ⊥");
	}

	/** Instantiates and adds to the hypotheses a theorem from another theory. */
	@Test
	public void test_addPolymorphicTheorem() throws Exception {
		IProofAttempt pa = createProofAttempt(testThy.getSCTheoryRoot(), "test-thm/S-THM", "THTactic Reasoner Test");
		FormulaFactory ff = pa.getFormulaFactory();
		String thmPred = substitute(ff, "∀ x· x ∈ T ⇒ x = x", "T", "ℤ");
		checkReasonerSuccess(pa, "RBP theorem instantiation", thmPred, "{}[][][∀ x· x ∈ ℤ ⇒ x = x] |- ⊥");
	}

}
