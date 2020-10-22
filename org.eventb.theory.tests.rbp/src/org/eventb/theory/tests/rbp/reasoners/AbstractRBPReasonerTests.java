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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IEventBProject;
import org.eventb.core.IEventBRoot;
import org.eventb.core.IPOSequent;
import org.eventb.core.ast.extension.IOperatorProperties.FormulaType;
import org.eventb.core.ast.extension.IOperatorProperties.Notation;
import org.eventb.core.pm.IProofAttempt;
import org.eventb.core.pm.IProofComponent;
import org.eventb.core.pm.IProofManager;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.UntranslatableException;
import org.eventb.core.seqprover.reasonerExtensionTests.AbstractReasonerTests;
import org.eventb.theory.core.IAvailableTheoryProject;
import org.eventb.theory.core.IDatatypeConstructor;
import org.eventb.theory.core.IDatatypeDefinition;
import org.eventb.theory.core.INewOperatorDefinition;
import org.eventb.theory.core.IRecursiveOperatorDefinition;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.ITheoryPathRoot;
import org.eventb.theory.core.ITheoryRoot;
import org.eventb.theory.core.basis.TheoryDeployer;
import org.junit.After;
import org.junit.Assert;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.RodinMarkerUtil;
import org.rodinp.internal.core.debug.DebugHelpers;

import ch.ethz.eventb.utils.EventBUtils;

/**
 * <p>
 * The abstract class for testing rule-based prover reasoner. The
 * {@link #setUp()} method is used to create the theory project. Theories are
 * created by the method {@link #createTheories()}) which must be implemented by
 * the client. These theories are then deployed as a part of the
 * {@link #setUp()} method.
 * </p>
 *
 * @author htson
 * @version 0.1
 * @see #createTheories(IRodinProject)
 * @since 2.1.0
 */
@SuppressWarnings("restriction")
public abstract class AbstractRBPReasonerTests extends AbstractReasonerTests {

	/**
	 * The testing workspace.
	 */
	public IWorkspace workspace = ResourcesPlugin.getWorkspace();

	/**
	 * The null progress monitor.
	 */
	protected IProgressMonitor nullMonitor = new NullProgressMonitor();

	/**
	 * The theory project.
	 */
	protected IRodinProject thyPrj;
	
	/**
	 * The theories within the theory project.
	 */
	protected ITheoryRoot[] thyRoots;

	/**
	 * The testing Event-B project.
	 */
	protected IEventBProject ebPrj;
	
	/**
	 * The theory path root of the Event-B project.
	 */
	protected ITheoryPathRoot thyPathRoot;
	
	/**
	 * The import theory project from the Event-B project to the theory project.
	 */
	protected IAvailableTheoryProject importedThyPrj;
	
	/**
	 * A collection of proof attempts. The collection is created during the
	 * {@link AbstractRBPReasonerTests#setUp()} method. These will be
	 * automatically disposed in the {@link AbstractRBPReasonerTests#tearDown}
	 * method. Clients must use
	 * {@link #createProofAttempt(IEventBRoot, String, String)} method to create
	 * the proof attempts in order for them to be disposed automatically.
	 */
	private Collection<IProofAttempt> proofAttempts;
	
	/**
	 * For this method, the following steps are carried out:
	 * <ol>
	 * <li>The super method, i.e., {@link AbstractReasonerTests#setUp()}.</li>
	 * 
	 * <li>Turn of auto-building.</li>
	 * 
	 * <li>Disable indexing.</li>
	 * 
	 * <li>Delete the old workspace.</li>
	 * 
	 * <li>Create the "Theories" project (the theory project).</li>
	 * 
	 * <li>Create the theories within the theory project by calling
	 * {@link #createTheories(IRodinProject)}.</li>
	 * 
	 * <li>Build the theory project.</li>
	 * 
	 * <li>Deploy all the created theories.</li>
	 * 
	 * <li>Create a test project "P".</li>
	 * 
	 * <li>Create a theory path to the theory project and import all the
	 * deployed theories.</li>
	 * 
	 * <li>Build the workspace.</li>
	 * 
	 * <li>Initialise the collection of proof attempts.</li>
	 * </ol>
	 */
	@Override
	public final void setUp() throws Exception {
		// Call the super method.
		super.setUp();
		
		// ensure autobuilding is turned off
		IWorkspaceDescription wsDescription = workspace.getDescription();
		if (wsDescription.isAutoBuilding()) {
			wsDescription.setAutoBuilding(false);
			workspace.setDescription(wsDescription);
		}

		// disable indexing
		DebugHelpers.disableIndexing();

		// Delete the old workspace
		workspace.getRoot().delete(true, null);

		// Create the "Theories" project
		thyPrj = EventBUtils.createEventBProject("Theories", nullMonitor)
				.getRodinProject();

		// Create the "Theories" within project.
		thyRoots = createTheories(thyPrj);

		// Build the "Theories" project.
		runBuilder(thyPrj);

		// Deploy the theories.
		deployTheories(thyPrj, thyRoots);

		// Create the test "P" project
		ebPrj = EventBUtils.createEventBProject("P", nullMonitor);

		// Create the theory path
		thyPathRoot = TheoryUtils.createTheoryPath(
				ebPrj.getRodinProject(), "Theory", nullMonitor);

		// Imported "Theories" project.
		importedThyPrj = TheoryUtils
				.createAvailableTheoryProject(thyPathRoot, thyPrj, nullMonitor);

		// Imported all deployed theory roots.
		for (ITheoryRoot thyRoot : thyRoots) {
			TheoryUtils.createAvailableTheory(importedThyPrj,
					thyRoot.getDeployedTheoryRoot(), nullMonitor);
		}
		
		// Save the theory path
		thyPathRoot.getRodinFile().save(nullMonitor, true);
		
		// Run the builder.
		runBuilder(ebPrj.getRodinProject());
		
		// Initialise the proof attempts.
		proofAttempts = new HashSet<IProofAttempt>();
	}
	
	@After
	public void tearDown() throws Exception {
		for (IProofAttempt proofAttempt : proofAttempts) {
			proofAttempt.dispose();
		}
	}

	protected IProofAttempt createProofAttempt(IEventBRoot root, String poName, String owner)
			throws RodinDBException {
		IProofManager pm = EventBPlugin.getProofManager();
		IProofComponent proofComponent = pm.getProofComponent(root);
		IProofAttempt pa = proofComponent.createProofAttempt(poName,
				owner, nullMonitor);
		proofAttempts.add(pa);
		return pa;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see AbstractReasonerTests#getSuccessfulReasonerApplications()
	 */
	@Override
	public final SuccessfullReasonerApplication[] getSuccessfulReasonerApplications() {
		// No tests
		return new SuccessfullReasonerApplication[] {};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see AbstractReasonerTests#getUnsuccessfullReasonerApplications()
	 */
	@Override
	public final UnsuccessfullReasonerApplication[] getUnsuccessfullReasonerApplications() {
		// No tests
		return new UnsuccessfullReasonerApplication[] {};
	}

	/**
	 * Utility method to deploy a set of theory roots within a theory project.
	 * 
	 * @param thyPrj
	 *            the theory project.
	 * @param thyRoots
	 *            the array of theory roots within the input project.
	 */
	protected void deployTheories(IRodinProject thyPrj,
			ITheoryRoot... thyRoots) {
		Set<ISCTheoryRoot> scThyRoots = new HashSet<ISCTheoryRoot>(
				thyRoots.length);
		for (ITheoryRoot thyRoot : thyRoots) {
			scThyRoots.add(thyRoot.getSCTheoryRoot());
		}
		TheoryDeployer theoryDeployer = new TheoryDeployer(thyPrj, scThyRoots);
		try {
			theoryDeployer.deploy(nullMonitor);
		} catch (CoreException e) {
			e.printStackTrace();
			Assert.fail("Deploying theories failed");
		}
	}

	/**
	 * This method is called within the {@link #setUp()} method to create the
	 * theories within the theory project.
	 * 
	 * @param thyPrj
	 *            the theory project (created in the {@link #setUp()} method).
	 * @return the array of newly created theory roots.
	 * @throws RodinDBException
	 *             if some unexpected error occurs.
	 */
	public abstract ITheoryRoot[] createTheories(IRodinProject thyPrj)
			throws RodinDBException;

	/**
	 * Utility method to run the builder on a Rodin project. Fails if some
	 * errors occurred during the build.
	 * 
	 * @param rodinPrj
	 *            the Rodin project.
	 * @throws CoreException
	 *             if some unexpected errors occurred.
	 */
	protected void runBuilder(IRodinProject rodinPrj) throws CoreException {
		final IProject project = rodinPrj.getProject();
		project.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, null);
		IMarker[] buildPbs = project.findMarkers(
				RodinMarkerUtil.BUILDPATH_PROBLEM_MARKER, true,
				IResource.DEPTH_INFINITE);
		if (buildPbs.length != 0) {
			for (IMarker marker : buildPbs) {
				System.out.println("Build problem for " + marker.getResource());
				System.out.println("  " + marker.getAttribute(IMarker.MESSAGE));
			}
			Assert.fail("Build produced build problems, see console");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see AbstractReasonerTests#translateSequent(IProverSequent)
	 */
	@Override
	protected IProverSequent translateSequent(IProverSequent sequent) throws UntranslatableException {
		try {
			// Create the "RBPTest" within project.
			ITheoryRoot thyRoot = createListTheory();

			// Imported all deployed theory roots.
			TheoryUtils.createAvailableTheory(importedThyPrj,
					thyRoot.getDeployedTheoryRoot(), nullMonitor);

			// Save the theory path
			thyPathRoot.getRodinFile().save(nullMonitor, true);

			// Run the builder.
			runBuilder(ebPrj.getRodinProject());

		} catch (RodinDBException e) {
			throw new UntranslatableException(e);
		} catch (CoreException e) {
			throw new UntranslatableException(e);
		}
		
		Object origin = sequent.getOrigin();
		if (origin instanceof IPOSequent) {
			IPOSequent poSequent = (IPOSequent) origin;
			String name = poSequent.getElementName();
			IEventBRoot root = (IEventBRoot) poSequent.getRoot();
			IProofAttempt pa;
			try {
				pa = createProofAttempt(root, name, "Translation replay");
			} catch (RodinDBException e) {
				throw new UntranslatableException(e);
			}
			return pa.getProofTree().getRoot().getSequent();
		}
		else {
			throw new UntranslatableException(new Exception("Unexpected origin"));
		}
	}

	/**
	 * @return
	 * @throws CoreException 
	 */
	private ITheoryRoot createListTheory() throws RodinDBException, CoreException {
		ITheoryRoot thyRoot = TheoryUtils.createTheory(
				thyPrj.getRodinProject(), "List", nullMonitor);

		TheoryUtils.createTypeParameter(thyRoot, "S", null, nullMonitor);
		IDatatypeDefinition listDT = TheoryUtils.createDataType(thyRoot,
				"List", null, nullMonitor);
		TheoryUtils.createTypeArgument(listDT, "S", null, nullMonitor);
		TheoryUtils.createConstructor(listDT, "EmptyList", null, nullMonitor);
		
		IDatatypeConstructor appendConstr = TheoryUtils.createConstructor(
				listDT, "Append", null, nullMonitor);
		TheoryUtils.createDestructor(appendConstr, "l", "List(S)", null,
				nullMonitor);
		TheoryUtils.createDestructor(appendConstr, "e", "S", null,
				nullMonitor);

		INewOperatorDefinition length = TheoryUtils.createOperator(thyRoot,
				"length", false, false, FormulaType.EXPRESSION,
				Notation.PREFIX, null, nullMonitor);
		
		TheoryUtils.createArgument(length, "L", "List(S)", null, nullMonitor);
		
		IRecursiveOperatorDefinition recDef = TheoryUtils
				.createRecursiveDefinition(length, "L", null, nullMonitor);
		TheoryUtils.createRecursiveCase(recDef, "EmptyList", "0", null, nullMonitor);
		TheoryUtils.createRecursiveCase(recDef, "Append(t,s)", "length(t) + 1"
				+ "", null, nullMonitor);
		
		thyRoot.getRodinFile().save(nullMonitor, true);
		

		// Build the "Theories" project.
		runBuilder(thyPrj);

		// Deploy the theories.
		deployTheories(thyPrj, thyRoot);

		return thyRoot;
	}

}
