/*******************************************************************************
 * Copyright (c) 2020, 2021 CentraleSupélec.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.tests.rbp.tactics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IEventBProject;
import org.eventb.core.IPSRoot;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pm.IProofAttempt;
import org.eventb.core.pm.IProofComponent;
import org.eventb.core.pm.IProofManager;
import org.eventb.core.pm.IUserSupport;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.ProverLib;
import org.eventb.core.seqprover.eventbExtensions.AutoTactics;
import org.eventb.core.seqprover.tests.TestLib;
import org.eventb.theory.core.IImportTheoryProject;
import org.eventb.theory.core.ITheoryRoot;
import org.eventb.theory.core.basis.TheoryDeployer;
import org.eventb.theory.tests.rbp.reasoners.TheoryUtils;
import org.eventb.ui.prover.ITacticApplication;
import org.eventb.ui.prover.ITacticProvider;
import org.junit.Before;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.RodinMarkerUtil;

import ch.ethz.eventb.utils.EventBUtils;

/**
 * Base class for testing implementations of {@link ITacticProvider}.
 *
 * Sub-classes must initialize the {@link #tacticProvider} attribute.
 *
 * @author Guillaume Verdier
 */
public abstract class AbstractTacticTests {

	/**
	 * The project containing the testing theories.
	 */
	protected IEventBProject project;

	/**
	 * Theory containing operators, rules, etc. used by tests.
	 */
	protected ITheoryRoot theoryRoot;

	/**
	 * Theory containing the theorems used for testing the tactic provider.
	 */
	protected ITheoryRoot testTheoryRoot;

	/**
	 * The tactic provider to test.
	 *
	 * It must be initialized in sub-classes.
	 */
	protected ITacticProvider tacticProvider;

	/**
	 * How many times
	 * {@link ITacticProvider#getPossibleApplications(IProofTreeNode, Predicate, String)}
	 * will be tested consecutively: implementations that use a caching mechanism
	 * should test with at least two iterations.
	 */
	protected int numberIterations = 1;

	/**
	 * Sets up the testing environment.
	 *
	 * This first cleans the workspace to make sure that there are no leftover files
	 * from other tests. Then, it creates a new project and the theories.
	 */
	@Before
	public void setUp() throws Exception {
		IProgressMonitor nullMonitor = new NullProgressMonitor();
		ResourcesPlugin.getWorkspace().getRoot().delete(true, null);
		project = EventBUtils.createEventBProject("Project", nullMonitor);
		theoryRoot = TheoryUtils.createTheory(project.getRodinProject(), "TacticProviderTestDefinitions", nullMonitor);
		testTheoryRoot = TheoryUtils.createTheory(project.getRodinProject(), "TacticProviderTestTheorems", nullMonitor);
	}

	/**
	 * Creates a proof attempt for a given proof obligation.
	 *
	 * @param poName name of the proof obligation to prove
	 * @return a proof attempt for the proof obligation
	 */
	protected IProofAttempt createProofAttempt(String poName) throws RodinDBException {
		IProofManager pm = EventBPlugin.getProofManager();
		IProofComponent proofComponent = pm.getProofComponent(testTheoryRoot);
		return proofComponent.createProofAttempt(poName, "Tactic test", null);
	}

	/**
	 * Builds the project.
	 *
	 * This runs an incremental build and checks that there are no errors.
	 */
	protected void runBuilder() throws Exception {
		IProject eclipseProject = project.getRodinProject().getProject();
		eclipseProject.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, null);
		IMarker[] buildPbs = eclipseProject.findMarkers(RodinMarkerUtil.BUILDPATH_PROBLEM_MARKER, true,
				IResource.DEPTH_INFINITE);
		assertEquals("build failure", 0, buildPbs.length);
	}

	/**
	 * Builds, deploys and sets imports of the testing theories.
	 */
	protected void buildAndDeploy() throws Exception {
		IProgressMonitor nullMonitor = new NullProgressMonitor();

		// Step 1: build theoryRoot
		theoryRoot.getRodinFile().save(null, true);
		runBuilder();

		// Step 2: deploy theoryRoot
		TheoryDeployer theoryDeployer = new TheoryDeployer(project.getRodinProject(),
				Collections.singleton(theoryRoot.getSCTheoryRoot()));
		theoryDeployer.deploy(nullMonitor);

		// Step 3: import theoryRoot in testTheoryRoot
		IImportTheoryProject importTheory = TheoryUtils.createImportTheoryProject(testTheoryRoot,
				project.getRodinProject(), null);
		TheoryUtils.createImportTheory(importTheory, theoryRoot, null);

		// Step 4: build testTheoryRoot
		testTheoryRoot.getRodinFile().save(null, true);
		runBuilder();
	}

	/**
	 * Checks the results of the application of the tactic provider to a theorem
	 * proof.
	 *
	 * This starts a proof for the theorem named {@code thm} and uses the tactic
	 * provider to get the possible tactic applications to the goal, if
	 * {@code hypothesis} is {@code false}, or the first hypothesis of the theorem,
	 * if {@code hypothesis} is {@code true}. It then checks that:
	 * <ul>
	 * <li>the tactic provider returns as many possible applications as there are
	 * {@code expectedSequents}</li>
	 * <li>that each application uses the tactic named {@code tacticName}</li>
	 * <li>that applying the n-th application to the goal or first hypothesis,
	 * depending on the {@code hypothesis} parameter, succeeds and returns a set of
	 * sequents equal to the n-th array in {@code expectedSequents}.</li>
	 * </ul>
	 *
	 * The test is run {@link #numberIterations} times.
	 *
	 * @param thm              the theorem to use for the test
	 * @param hypothesis       {@code true} to test the tactic provider on the first
	 *                         hypothesis of the theorem, {@code false} to test the
	 *                         tactic provider on the goal
	 * @param expectedSequents the lists of sequents that should be obtained after
	 *                         applying each possible application returned by the
	 *                         tactic provider
	 */
	protected void checkTacticsApplication(String tacticName, String thm, boolean hypothesis,
			String[]... expectedSequents) throws RodinDBException {
		// Start the proof
		IProofAttempt attempt = createProofAttempt(thm);
		IUserSupport userSupport = EventBPlugin.getUserSupportManager().newUserSupport();
		userSupport.setInput((IPSRoot) attempt.getStatus().getRoot());
		userSupport.setCurrentPO(attempt.getStatus(), null);

		try {
			// If the test should use an hypothesis, fetch it
			Predicate hyp = null;
			IProofTreeNode proofRoot = attempt.getProofTree().getRoot();
			if (hypothesis) {
				ITactic impGoal = new AutoTactics.ImpGoalTac();
				impGoal.apply(proofRoot, null);
				proofRoot = proofRoot.getFirstOpenDescendant();
				hyp = proofRoot.getSequent().hypIterable().iterator().next();
			}

			int expectedLength = expectedSequents.length;
			for (int iteration = 0; iteration < numberIterations; iteration++) {
				// Get the list of applications from the tested tactic provider
				List<ITacticApplication> applications = tacticProvider.getPossibleApplications(proofRoot, hyp, null);
				assertEquals("wrong number of applications", expectedLength, applications.size());

				for (int i = 0; i < expectedLength; i++) {
					ITacticApplication application = applications.get(i);
					// Check the tactic name
					assertEquals("wrong tactic used", tacticName, application.getTacticID());
					// Try applying the tactic found by the tactic provider
					ITactic tactic = application.getTactic(null, null);
					IProofTreeNode copy = proofRoot.copySubTree().getRoot();
					if (hypothesis) {
						userSupport.applyTacticToHypotheses(tactic, Collections.singleton(hyp), false, null);
					} else {
						userSupport.applyTactic(tactic, false, null);
					}
					assertEquals("tactic application failed", null, tactic.apply(copy, null));
					IProofTreeNode[] children = copy.getChildNodes();
					assertEquals("wrong number of child nodes", expectedSequents[i].length, children.length);
					for (int j = 0; j < expectedSequents[i].length; j++) {
						IProverSequent expected = TestLib.genFullSeq(expectedSequents[i][j], attempt.getFormulaFactory());
						assertTrue(
								"wrong sequent after application (expected " + expected + "; got "
										+ children[j].getSequent() + ")",
								ProverLib.deepEquals(expected, children[j].getSequent()));
					}
				}
			}
		} finally {
			attempt.dispose();
			userSupport.dispose();
		}
	}

}
