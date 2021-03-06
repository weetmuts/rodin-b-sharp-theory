/*******************************************************************************
 * Copyright (c) 2015, 2020 University of Southampton and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University of Southampton - initial API and implementation
 *******************************************************************************/

package org.eventb.theory.tests.rbp.reasoners;

import org.eventb.core.EventBPlugin;
import org.eventb.core.IContextRoot;
import org.eventb.core.pm.IProofAttempt;
import org.eventb.core.pm.IProofComponent;
import org.eventb.core.pm.IProofManager;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.theory.core.IApplicabilityElement.RuleApplicability;
import org.eventb.theory.core.IProofRulesBlock;
import org.eventb.theory.core.IRewriteRule;
import org.eventb.theory.core.ITheoryRoot;
import org.eventb.theory.internal.rbp.reasoners.input.AutoRewriteInput;
import org.eventb.theory.internal.rbp.reasoners.input.IPRMetadata;
import org.eventb.theory.internal.rbp.reasoners.input.PRMetadata;
import org.junit.Test;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

import ch.ethz.eventb.utils.EventBUtils;

/**
 * <p>
 *
 * </p>
 *
 * @author htson
 * @version
 * @see
 * @since
 */
public class AutoRewriteReasonerTests extends AbstractRBPReasonerTests {

	/*
	 * (non-Javadoc)
	 * 
	 * @see AbstractReasonerTests#getReasonerID()
	 */
	@Override
	public final String getReasonerID() {
		return "org.eventb.theory.rbp.autoRewriteReasoner";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * AbstractRBPReasonerTests#createTheories
	 * (org.rodinp.core.IRodinProject)
	 */
	@Override
	public ITheoryRoot[] createTheories(IRodinProject thyPrj)
			throws RodinDBException {
		// Create the "AutoRewrite" theory
		ITheoryRoot thyRoot = TheoryUtils.createTheory(
				thyPrj.getRodinProject(), "AutoRewrite", nullMonitor);

		IProofRulesBlock prfRulesBlk = TheoryUtils.createProofRulesBlock(
				thyRoot, "AutoRewrite", nullMonitor);

		TheoryUtils.createMetavariable(prfRulesBlk, "x", "ℤ", nullMonitor);

		IRewriteRule rule = TheoryUtils.createAutoRewriteRule(prfRulesBlk,
				"rewrite", "x + x", true, RuleApplicability.AUTOMATIC,
				"Silly rewrite", nullMonitor);

		TheoryUtils.createRewriteRuleRHS(rule, "rule1", "⊤", "2 ∗ x",
				nullMonitor);

		thyRoot.getRodinFile().save(nullMonitor, true);

		return new ITheoryRoot[] {thyRoot};
	}

	@Test
	public void testRewrite() throws Exception {
		IContextRoot ctxRoot = EventBUtils.createContext(ebPrj, "c",
				nullMonitor);
		EventBUtils.createAxiom(ctxRoot, "thm1", "1 + 1 = 3", true, null,
				nullMonitor);
		ctxRoot.getRodinFile().save(nullMonitor, true);
		runBuilder(ebPrj.getRodinProject());

		IProofManager pm = EventBPlugin.getProofManager();
		IProofComponent proofComponent = pm.getProofComponent(ctxRoot);
		IProofAttempt pa = proofComponent.createProofAttempt("thm1/THM",
				"Auto Rewrite Reasoner Test", nullMonitor);
		IProofTreeNode root = pa.getProofTree().getRoot();
		IProverSequent sequent = root.getSequent();
		IPRMetadata prMetadata = new PRMetadata("Theories",
				"AutoRewrite", "rewrite");
		AutoRewriteInput rewriteInput = new AutoRewriteInput(prMetadata);
		SuccessfullReasonerApplication appl = new SuccessfullReasonerApplication(
				sequent, rewriteInput,
				"{}[][][] |- ⊤",
				"{}[][][⊤] |- 2 ∗ 1 = 3");
		testSuccessfulReasonerApplications("", appl);
	}
}
