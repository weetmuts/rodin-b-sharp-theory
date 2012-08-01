package org.eventb.theory.core.tests;

import junit.framework.TestCase;

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
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.tests.ResourceUtils;
import org.eventb.theory.core.DatabaseUtilities;
import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.ITheoryRoot;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.RodinMarkerUtil;
import org.rodinp.internal.core.debug.DebugHelpers;

public abstract class BuilderTest extends TestCase {

	public static final String PLUGIN_ID = "org.eventb.theory.core.tests";
	
	protected static final FormulaFactory defaultFactory = FormulaFactory.getDefault();
	
	protected IRodinProject rodinProject;

	protected IWorkspace workspace = ResourcesPlugin.getWorkspace();
	
	public BuilderTest() {
		super();
	}

	public BuilderTest(String name) {
		super(name);
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
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		// ensure autobuilding is turned off
		IWorkspaceDescription wsDescription = workspace.getDescription();
		if (wsDescription.isAutoBuilding()) {
			wsDescription.setAutoBuilding(false);
			workspace.setDescription(wsDescription);
		}
		
		rodinProject = createRodinProject("P");
		
		DebugHelpers.disableIndexing();
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
	
	protected void importProject(String prjName) throws Exception {
		ResourceUtils.importProjectFiles(rodinProject.getProject(), prjName);
	}

	public static void setReadOnly(IResource resource, boolean readOnly)
			throws CoreException {
		final ResourceAttributes attrs = resource.getResourceAttributes();
		if (attrs != null && attrs.isReadOnly() != readOnly) {
			attrs.setReadOnly(readOnly);
			resource.setResourceAttributes(attrs);
		}
	}

	@Override
	protected void tearDown() throws Exception {
		cleanupWorkspace();
		super.tearDown();
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
