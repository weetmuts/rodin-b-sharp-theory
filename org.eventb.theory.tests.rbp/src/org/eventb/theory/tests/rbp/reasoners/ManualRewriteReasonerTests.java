/*******************************************************************************
 * Copyright (c) 2016, 2020 University of Southampton and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University of Southampton - initial API and implementation
 *******************************************************************************/

package org.eventb.theory.tests.rbp.reasoners;

import org.eventb.core.IContextRoot;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pm.IProofAttempt;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.core.seqprover.tests.TestLib;
import org.eventb.theory.core.IApplicabilityElement.RuleApplicability;
import org.eventb.theory.core.IImportTheoryProject;
import org.eventb.theory.core.IProofRulesBlock;
import org.eventb.theory.core.IRewriteRule;
import org.eventb.theory.core.ITheoryRoot;
import org.eventb.theory.internal.rbp.reasoners.input.IPRMetadata;
import org.eventb.theory.internal.rbp.reasoners.input.PRMetadata;
import org.eventb.theory.internal.rbp.reasoners.input.RewriteInput;
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
public class ManualRewriteReasonerTests extends AbstractRBPReasonerTests {

	/*
	 * (non-Javadoc)
	 * 
	 * @see AbstractRBPReasonerTests#createTheories(IRodinProject)
	 */
	@Override
	public ITheoryRoot[] createTheories(IRodinProject thyPrj)
			throws RodinDBException {
		// Create the "ManualRewrite" theory
		ITheoryRoot thyRoot = TheoryUtils.createTheory(
				thyPrj.getRodinProject(), "ManualRewrite", nullMonitor);

		IProofRulesBlock prfRulesBlk = TheoryUtils.createProofRulesBlock(
				thyRoot, "ManualRewrite", nullMonitor);

		TheoryUtils.createMetavariable(prfRulesBlk, "x", "ℤ", nullMonitor);

		IRewriteRule rule = TheoryUtils.createAutoRewriteRule(prfRulesBlk,
				"rewrite", "x + x", true, RuleApplicability.INTERACTIVE,
				"Silly rewrite", nullMonitor);

		TheoryUtils.createRewriteRuleRHS(rule, "rule1", "⊤", "2 ∗ x",
				nullMonitor);

		thyRoot.getRodinFile().save(nullMonitor, true);

		return new ITheoryRoot[] {thyRoot};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see AbstractReasonerTests#getReasonerID()
	 */
	@Override
	public String getReasonerID() {
		return "org.eventb.theory.rbp.manualRewriteReasoner";
	}

	/**
	 * <ul>
	 * <li><b>Purpose</b>: An unsuccessful test where an invalid rewriting
	 * position is given.</li>
	 * 
	 * <li><b>Setting</b>: A proof attempt for a theorem "1 + 1 = 3" in the
	 * context "c". An reasoner input for rewriting at an (invalid) position
	 * "0.0.1".</li>
	 * 
	 * <li><b>Expected result</b>: A reasoner failure for invalid position.</li>
	 * </ul>
	 */
	@Test
	public void testContext_InvalidPosition_Goal() throws Exception {
		IContextRoot ctxRoot = EventBUtils.createContext(ebPrj, "c",
				nullMonitor);
		EventBUtils.createAxiom(ctxRoot, "thm1", "1 + 1 = 3", true, null,
				nullMonitor);
		ctxRoot.getRodinFile().save(nullMonitor, true);
		runBuilder(ebPrj.getRodinProject());

		IProofAttempt pa = createProofAttempt(ctxRoot, "thm1/THM",
				"Manual Rewrite Reasoner Test");
		IProofTreeNode root = pa.getProofTree().getRoot();
		IProverSequent sequent = root.getSequent();

		Predicate predicate = null;
		IPosition position = FormulaFactory.makePosition("0.0.1");

		IPRMetadata prMetadata = new PRMetadata("Theories",
				"ManualRewrite", "rewrite");
		RewriteInput rewriteInput = new RewriteInput(predicate, position,
				prMetadata);
		UnsuccessfullReasonerApplication appl = new UnsuccessfullReasonerApplication(
				sequent, rewriteInput,
				"Invalid position 0.0.1 for goal 1+1=3");
		testUnsuccessfulReasonerApplications("Invalid Position", appl);
	}

	/**
	 * <ul>
	 * <li><b>Purpose</b>: An unsuccessful test where an invalid rewriting
	 * position is given.</li>
	 * 
	 * <li><b>Setting</b>: A proof attempt for a theorem "⊥" in the context "c"
	 * with axiom "1 + 1 = 3". An reasoner input for rewriting at an (invalid)
	 * position "0.0.1" for the hypothesis.</li>
	 * 
	 * <li><b>Expected result</b>: A reasoner failure for invalid position.</li>
	 * </ul>
	 */
	@Test
	public void testContext_InvalidPosition_Hypothesis() throws Exception {
		IContextRoot ctxRoot = EventBUtils.createContext(ebPrj, "c",
				nullMonitor);
		EventBUtils.createAxiom(ctxRoot, "axm1", "1 + 1 = 3", false, null,
				nullMonitor);
		EventBUtils.createAxiom(ctxRoot, "thm1", "⊥", true, null,
				nullMonitor);
		ctxRoot.getRodinFile().save(nullMonitor, true);
		runBuilder(ebPrj.getRodinProject());

		IProofAttempt pa = createProofAttempt(ctxRoot, "thm1/THM",
				"RbP Manual Rewriter Test");
		IProofTreeNode root = pa.getProofTree().getRoot();
		IProverSequent sequent = root.getSequent();

		Predicate predicate = TestLib.genPred("1 + 1 = 3", ff);
		IPosition position = FormulaFactory.makePosition("0.0.1");

		IPRMetadata prMetadata = new PRMetadata("Theories",
				"ManualRewrite", "rewrite");
		RewriteInput rewriteInput = new RewriteInput(predicate, position,
				prMetadata);
		UnsuccessfullReasonerApplication appl = new UnsuccessfullReasonerApplication(
				sequent, rewriteInput,
				"Invalid position 0.0.1 for hypothesis 1+1=3");
		testUnsuccessfulReasonerApplications("Invalid Position", appl);
	}

	/**
	 * <ul>
	 * <li><b>Purpose</b>: An unsuccessful test where an invalid rewriting
	 * position is given.</li>
	 * 
	 * <li><b>Setting</b>: A proof attempt for a theorem "⊥" in the context "c"
	 * with axiom "1 + 1 = 3". An reasoner input for rewriting at an (invalid)
	 * position "0.0.1" for the hypothesis.</li>
	 * 
	 * <li><b>Expected result</b>: A reasoner failure for invalid position.</li>
	 * </ul>
	 */
	@Test
	public void testContext_InvalidHypothesis() throws Exception {
		IContextRoot ctxRoot = EventBUtils.createContext(ebPrj, "c",
				nullMonitor);
		EventBUtils.createAxiom(ctxRoot, "axm1", "1 + 1 = 3", false, null,
				nullMonitor);
		EventBUtils.createAxiom(ctxRoot, "thm1", "⊥", true, null,
				nullMonitor);
		ctxRoot.getRodinFile().save(nullMonitor, true);
		runBuilder(ebPrj.getRodinProject());

		IProofAttempt pa = createProofAttempt(ctxRoot, "thm1/THM",
				"RbP Manual Rewriter Test");
		IProofTreeNode root = pa.getProofTree().getRoot();
		IProverSequent sequent = root.getSequent();

		Predicate predicate = TestLib.genPred("1 + 1 = 4", ff);
		IPosition position = FormulaFactory.makePosition("0");

		IPRMetadata prMetadata = new PRMetadata("Theories",
				"ManualRewrite", "rewrite");
		RewriteInput rewriteInput = new RewriteInput(predicate, position,
				prMetadata);
		UnsuccessfullReasonerApplication appl = new UnsuccessfullReasonerApplication(
				sequent, rewriteInput,
				"Nonexistent hypothesis: 1+1=4");
		testUnsuccessfulReasonerApplications("Invalid Position", appl);
	}

	/**
	 * <ul>
	 * <li><b>Purpose</b>: An unsuccessful test where an invalid rewriting
	 * position is given.</li>
	 * 
	 * <li><b>Setting</b>: A proof attempt for a theorem "1 + 1 = 3" in the
	 * theory "TestTheory". An reasoner input for rewriting at an (invalid)
	 * position "0.0.1".</li>
	 * 
	 * <li><b>Expected result</b>: A reasoner failure for invalid position.</li>
	 * </ul>
	 */
	@Test
	public void testTheory_InvalidPosition_Goal() throws Exception {
		ITheoryRoot thyRoot = TheoryUtils.createTheory(
				ebPrj.getRodinProject(), "TestTheory", nullMonitor);
		IImportTheoryProject importThyPrj = TheoryUtils
				.createImportTheoryProject(thyRoot, thyPrj, nullMonitor);
		for (ITheoryRoot root : thyRoots) {
			TheoryUtils.createImportTheory(importThyPrj, root, nullMonitor);
		}

		TheoryUtils
				.createTheorem(thyRoot, "thm1", "1 + 1 = 3", nullMonitor);
		thyRoot.getRodinFile().save(nullMonitor, true);
		runBuilder(ebPrj.getRodinProject());

		IProofAttempt pa = createProofAttempt(thyRoot, "thm1/S-THM",
				"Manual Rewrite Reasoner Test");
		IProofTreeNode root = pa.getProofTree().getRoot();
		IProverSequent sequent = root.getSequent();

		Predicate predicate = null;
		IPosition position = FormulaFactory.makePosition("0.0.1");

		IPRMetadata prMetadata = new PRMetadata("Theories",
				"ManualRewrite", "rewrite");
		RewriteInput rewriteInput = new RewriteInput(predicate, position,
				prMetadata);
		UnsuccessfullReasonerApplication appl = new UnsuccessfullReasonerApplication(
				sequent, rewriteInput,
				"Invalid position 0.0.1 for goal 1+1=3");
		testUnsuccessfulReasonerApplications("Invalid Position", appl);
	}

	/**
	 * <ul>
	 * <li><b>Purpose</b>: An unsuccessful test where an invalid rewriting
	 * position is given.</li>
	 * 
	 * <li><b>Setting</b>: A proof attempt for a theorem "⊥" in the theory
	 * "TestTheory". A theorem "1 + 1 = 3" is declared before. An reasoner input
	 * for rewriting at an (invalid) position "0.0.1".</li>
	 * 
	 * <li><b>Expected result</b>: A reasoner failure for invalid position.</li>
	 * </ul>
	 */
	@Test
	public void testTheory_InvalidPosition_Hypothesis() throws Exception {
		ITheoryRoot thyRoot = TheoryUtils.createTheory(
				ebPrj.getRodinProject(), "TestTheory", nullMonitor);
		IImportTheoryProject importThyPrj = TheoryUtils
				.createImportTheoryProject(thyRoot, thyPrj, nullMonitor);
		for (ITheoryRoot root : thyRoots) {
			TheoryUtils.createImportTheory(importThyPrj, root, nullMonitor);
		}

		TheoryUtils.createTheorem(thyRoot, "thm1", "1 + 1 = 3 ⇒ ⊥", nullMonitor);
		thyRoot.getRodinFile().save(nullMonitor, true);
		runBuilder(ebPrj.getRodinProject());

		IProofAttempt pa = createProofAttempt(thyRoot, "thm1/S-THM",
				"Manual Rewrite Reasoner Test");
		IProofTreeNode root = pa.getProofTree().getRoot();

		// move the left part of the implication to the hypotheses
		ITactic impI = Tactics.impI();
		impI.apply(root, null);
		root = root.getFirstOpenDescendant();

		IProverSequent sequent = root.getSequent();

		Predicate predicate = TestLib.genPred("1 + 1 = 3");
		IPosition position = FormulaFactory.makePosition("0.0.1");

		IPRMetadata prMetadata = new PRMetadata("Theories",
				"ManualRewrite", "rewrite");
		RewriteInput rewriteInput = new RewriteInput(predicate, position,
				prMetadata);
		UnsuccessfullReasonerApplication appl = new UnsuccessfullReasonerApplication(
				sequent, rewriteInput,
				"Invalid position 0.0.1 for hypothesis 1+1=3");
		testUnsuccessfulReasonerApplications("Invalid Position", appl);
	}

	/**
	 * <ul>
	 * <li><b>Purpose</b>: A successful test for a context with rewriting the
	 * goal.</li>
	 * 
	 * <li><b>Setting</b>: A proof attempt for a theorem "1 + 1 = 3" in the
	 * context "c".</li>
	 * 
	 * <li><b>Expected result</b>: A successful application.</li>
	 * </ul>
	 */
	@Test
	public void testContext_Successful_Goal1() throws Exception {
		IContextRoot ctxRoot = EventBUtils.createContext(ebPrj, "c",
				nullMonitor);
		EventBUtils.createAxiom(ctxRoot, "thm1", "1 + 1 = 3", true, null,
				nullMonitor);
		ctxRoot.getRodinFile().save(nullMonitor, true);
		runBuilder(ebPrj.getRodinProject());

		IProofAttempt pa = createProofAttempt(ctxRoot, "thm1/THM",
				"Manual Rewrite Reasoner Test");
		IProofTreeNode root = pa.getProofTree().getRoot();
		IProverSequent sequent = root.getSequent();

		Predicate predicate = null;
		IPosition position = FormulaFactory.makePosition("0");

		IPRMetadata prMetadata = new PRMetadata("Theories",
				"ManualRewrite", "rewrite");

		RewriteInput rewriteInput = new RewriteInput(predicate, position,
				prMetadata);
		SuccessfullReasonerApplication appl = new SuccessfullReasonerApplication(
				sequent, rewriteInput, "{}[][][] |- ⊤", "{}[][][⊤] |- 2 ∗ 1 = 3");
		testSuccessfulReasonerApplications("RbP Manual Rewrite", appl);
	}

	/**
	 * <ul>
	 * <li><b>Purpose</b>: A successful test for a context with rewriting a
	 * hypothesis.</li>
	 * 
	 * <li><b>Setting</b>: A proof attempt for a theorem "⊥" in the context "c".
	 * An axiom "1 + 1 = 3" is declared before that.</li>
	 * 
	 * <li><b>Expected result</b>: A successful application.</li>
	 * </ul>
	 */
	@Test
	public void testContext_Successful_Hypothesis1() throws Exception {
		IContextRoot ctxRoot = EventBUtils.createContext(ebPrj, "c",
				nullMonitor);
		EventBUtils.createAxiom(ctxRoot, "axm1", "1 + 1 = 3", false, null,
				nullMonitor);
		EventBUtils.createAxiom(ctxRoot, "thm1", "⊥", true, null,
				nullMonitor);
		ctxRoot.getRodinFile().save(nullMonitor, true);
		runBuilder(ebPrj.getRodinProject());

		IProofAttempt pa = createProofAttempt(ctxRoot, "thm1/THM",
				"Manual Rewrite Reasoner Test");
		IProofTreeNode root = pa.getProofTree().getRoot();
		IProverSequent sequent = root.getSequent();

		Predicate predicate = TestLib.genPred("1 + 1 = 3", ff);
		IPosition position = FormulaFactory.makePosition("0");

		IPRMetadata prMetadata = new PRMetadata("Theories",
				"ManualRewrite", "rewrite");

		RewriteInput rewriteInput = new RewriteInput(predicate, position,
				prMetadata);
		SuccessfullReasonerApplication appl = new SuccessfullReasonerApplication(
				sequent, rewriteInput, "{}[][][1+1=3] |- ⊤", "{}[1+1=3][][⊤;;2∗1=3] |- ⊥");
		testSuccessfulReasonerApplications("RbP Manual Rewrite", appl);
	}
	
}
