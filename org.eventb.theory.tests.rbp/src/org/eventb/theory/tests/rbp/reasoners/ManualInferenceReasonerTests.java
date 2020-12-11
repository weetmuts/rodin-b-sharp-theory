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

package org.eventb.theory.tests.rbp.reasoners;

import org.eventb.core.IContextRoot;
import org.eventb.core.pm.IProofAttempt;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.theory.core.IApplicabilityElement.RuleApplicability;
import org.eventb.theory.core.IProofRulesBlock;
import org.eventb.theory.core.ITheoryRoot;
import org.eventb.theory.internal.rbp.reasoners.input.IPRMetadata;
import org.eventb.theory.internal.rbp.reasoners.input.InferenceInput;
import org.eventb.theory.internal.rbp.reasoners.input.PRMetadata;
import org.junit.Test;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

import ch.ethz.eventb.utils.EventBUtils;

public class ManualInferenceReasonerTests extends AbstractRBPReasonerTests {

	@Override
	public ITheoryRoot[] createTheories(IRodinProject thyPrj) throws RodinDBException {
		ITheoryRoot thyRoot = TheoryUtils.createTheory(
				thyPrj.getRodinProject(), "ManualInference", nullMonitor);
		IProofRulesBlock prfRulesBlk = TheoryUtils.createProofRulesBlock(
				thyRoot, "ManualInference", nullMonitor);
		TheoryUtils.createMetavariable(prfRulesBlk, "x", "ℤ", nullMonitor);
		TheoryUtils.createMetavariable(prfRulesBlk, "y", "ℤ", nullMonitor);

		TheoryUtils.createAutoInferenceRule(prfRulesBlk,
				"infer", RuleApplicability.INTERACTIVE, "Simple inference",
				new String[] {"2 ∗ x = y"}, new String[] {"x + x = y"},
				new boolean[] {false}, nullMonitor);

		thyRoot.getRodinFile().save(nullMonitor, true);

		return new ITheoryRoot[] {thyRoot};
	}

	@Override
	public String getReasonerID() {
		return "org.eventb.theory.rbp.manualInferenceReasoner";
	}

	/**
	 * <ul>
	 * <li><b>Purpose</b>: A successful application of an inference rule</li>
	 *
	 * <li><b>Setting</b>: A proof attempt for a theorem "2 ∗ 1 = 3" in the
	 * context "c".</li>
	 *
	 * <li><b>Expected result</b>: A successful application.</li>
	 * </ul>
	 */
	@Test
	public void testContext_Successful_Goal1() throws Exception {
		IContextRoot ctxRoot = EventBUtils.createContext(ebPrj, "c",
				nullMonitor);
		EventBUtils.createAxiom(ctxRoot, "thm1", "2 ∗ 1 = 3", true, null,
				nullMonitor);
		ctxRoot.getRodinFile().save(nullMonitor, true);
		runBuilder(ebPrj.getRodinProject());

		IProofAttempt pa = createProofAttempt(ctxRoot, "thm1/THM",
				"Manual Inference Reasoner Test");
		IProofTreeNode root = pa.getProofTree().getRoot();
		IProverSequent sequent = root.getSequent();

		IPRMetadata prMetadata = new PRMetadata("Theories",
				"ManualInference", "infer");
		InferenceInput input = new InferenceInput(prMetadata, null);
		SuccessfullReasonerApplication appl = new SuccessfullReasonerApplication(
				sequent, input,
				"{}[][][] |- ⊤",
				"{}[][][⊤] |- 1 + 1 = 3");
		testSuccessfulReasonerApplications("RbP Manual Inference", appl);
	}

}
