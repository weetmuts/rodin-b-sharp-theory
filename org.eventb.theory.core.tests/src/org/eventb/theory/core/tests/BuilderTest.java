/*******************************************************************************
 * Copyright (c) 2012, 2020 University of Southampton and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.tests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourceAttributes;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IEventBProject;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.theory.core.DatabaseUtilities;
import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.core.IDeploymentResult;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.ITheoryDeployer;
import org.eventb.theory.core.ITheoryRoot;
import org.eventb.theory.core.TheoryHierarchyHelper;
import org.junit.After;
import org.junit.Before;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.RodinMarkerUtil;

import ch.ethz.eventb.utils.EventBUtils;

public abstract class BuilderTest {

	public static final String PLUGIN_ID = "org.eventb.theory.core.tests";
	
	protected static final FormulaFactory defaultFactory = FormulaFactory.getDefault();

	protected IEventBProject eventBProject;
	
	protected IRodinProject rodinProject;

	protected IWorkspace workspace = ResourcesPlugin.getWorkspace();
	
	public BuilderTest() {
		super();
	}

	protected ITheoryRoot createTheory(String bareName, IRodinProject rodinProject) throws RodinDBException {
		final ITheoryRoot result = DatabaseUtilities.getTheory(bareName, rodinProject);
		createRodinFileOf(result);
		return result;
	}
	
	protected ITheoryRoot createTheory(String bareName) throws RodinDBException {
		final ITheoryRoot result = DatabaseUtilities.getTheory(bareName, rodinProject);
		createRodinFileOf(result);
		return result;
	}
	
	protected ISCTheoryRoot createSCTheory(String bareName) throws RodinDBException {
		final ISCTheoryRoot result = DatabaseUtilities.getSCTheory(bareName, rodinProject);
		createRodinFileOf(result);
		return result;
	}
	
	protected IDeployedTheoryRoot createDeployedTheory(String bareName) throws RodinDBException {
		final IDeployedTheoryRoot result = DatabaseUtilities.getDeployedTheory(bareName, rodinProject);
		createRodinFileOf(result);
		return result;
	}
	
	protected IDeploymentResult createDeployedTheory(ISCTheoryRoot scTheoryRoot, IProgressMonitor monitor) throws CoreException, InterruptedException{
		assertNotNull(scTheoryRoot);
		ITheoryDeployer dep = null;
		
		if(!scTheoryRoot.hasDeployedVersion()){
			Set<ISCTheoryRoot> set = new HashSet<ISCTheoryRoot>();
			set.add(scTheoryRoot);
			dep = TheoryHierarchyHelper.getDeployer(scTheoryRoot.getRodinProject(), set);
			dep.deploy(monitor);
			while(dep.getDeploymentResult()==null){
				Thread.sleep(1000);
			}
		}
		
		assertNotNull(dep);
		
		return dep.getDeploymentResult();
	}
	
	protected void createRodinFileOf(IInternalElement result)
			throws RodinDBException {
		result.getRodinFile().create(true, null);
	}
	
	public static void saveRodinFileOf(IInternalElement elem) throws RodinDBException {
		elem.getRodinFile().save(null, false);
	}
	
	public static void saveRodinFilesOf(IInternalElement... elems) throws RodinDBException {
		for(IInternalElement elem : elems){
			saveRodinFileOf(elem);
		}
	}
	
	protected void runBuilder() throws CoreException {
		runBuilder(rodinProject);
	}

	protected void runBuilder(IRodinProject rp) throws CoreException {
		final IProject project = rp.getProject();
		project.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, null);
		IMarker[] buildPbs= project.findMarkers(
				RodinMarkerUtil.BUILDPATH_PROBLEM_MARKER,
				true,
				IResource.DEPTH_INFINITE
		);
		if (buildPbs.length != 0) {
			for (IMarker marker: buildPbs) {
				System.out.println("Build problem for " + marker.getResource());
				System.out.println("  " + marker.getAttribute(IMarker.MESSAGE));
			}
			fail("Build produced build problems, see console");
		}
	}
	
	@Before
	public void setUp() throws Exception {
		
		// ensure autobuilding is turned off
		IWorkspaceDescription wsDescription = workspace.getDescription();
		if (wsDescription.isAutoBuilding()) {
			wsDescription.setAutoBuilding(false);
			workspace.setDescription(wsDescription);
		}
		
		eventBProject = EventBUtils.createEventBProject("P", null);
		rodinProject = eventBProject.getRodinProject();
		
		disableIndexing();
	}

	@SuppressWarnings("restriction")
	private void disableIndexing() {
		org.rodinp.internal.core.debug.DebugHelpers.disableIndexing();
	}

	protected IRodinProject createRodinProject(String projectName)
			throws CoreException {
		IProject project = workspace.getRoot().getProject(projectName);
		project.create(null);
		project.open(null);
		IProjectDescription pDescription = project.getDescription();
		pDescription.setNatureIds(new String[] {RodinCore.NATURE_ID});
		project.setDescription(pDescription, null);
		IRodinProject result = RodinCore.valueOf(project);
		return result;
	}
	
	public static void setReadOnly(IResource resource, boolean readOnly)
			throws CoreException {
		final ResourceAttributes attrs = resource.getResourceAttributes();
		if (attrs != null && attrs.isReadOnly() != readOnly) {
			attrs.setReadOnly(readOnly);
			resource.setResourceAttributes(attrs);
		}
	}

	@After
	public void tearDown() throws Exception {
		cleanupWorkspace();
	}

	/**
	 * Deletes all resources, markers, etc from the workspace. We need to first
	 * remove all read-only attributes on resources to ensure that they get
	 * properly deleted.
	 */
	private void cleanupWorkspace() throws CoreException {
		final IWorkspaceRoot root = workspace.getRoot();
		root.accept(CLEANUP);
		root.delete(true, null);
	}

	private static final IResourceVisitor CLEANUP = new IResourceVisitor() {

		@Override
		public boolean visit(final IResource resource) throws CoreException {
			setReadOnly(resource, false);
			return true;
		}

	};
}
