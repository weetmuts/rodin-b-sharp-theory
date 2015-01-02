/**
 *
 */
package org.eventb.theory.core.tests.dependencies;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

//import java.util.Set;

import org.eclipse.core.resources.IMarker;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.ITheoryRoot;
//import org.eventb.theory.core.maths.extensions.dependencies.ProjectTheoryGraph;
import org.junit.Test;

/**
 * Tests for theories dependencies (theories that import other theories)
 *
 * FIXME : Put back code using the appropriate replacement for ProjectTheoryGraph.
 *
 * @author renatosilva
 *
 */
public class TestDependenciesTheories extends BasicTestDependenciesTheories {

	/**
	 * No Error: thy2 imports thy1
	 */
	@Test
	public void testTheoryDependency_001_NoError() throws Exception {
		ITheoryRoot root1 = createTheory(THEORY_NAME+1);
		ITheoryRoot root2 = createTheory(THEORY_NAME+2);
		addImportTheory(root2, root1);

		saveRodinFileOf(root1);
		saveRodinFileOf(root2);
		runBuilder();

//		ProjectTheoryGraph projectTheoryGraph = new ProjectTheoryGraph();

		ISCTheoryRoot scTheoryRoot1 = root1.getSCTheoryRoot();
		// ISCTheoryRoot scTheoryRoot2 = root2.getSCTheoryRoot();
		isAccurate(scTheoryRoot1);
		// FIXME isAccurate(scTheoryRoot2);

//		projectTheoryGraph.setCheckedRoots(new ISCTheoryRoot[]{scTheoryRoot2});
//		Set<ISCTheoryRoot> checkedRoots2 = projectTheoryGraph.getCheckedRoots();

//		correctOrder(checkedRoots2, scTheoryRoot1,scTheoryRoot2);
	}

	/**
	 * No Error: th2 imports thy0. thy0 imports thy1.
	 */
	@Test
	public void testTheoryDependency_002_NoError() throws Exception {
		ITheoryRoot root0 = createTheory(THEORY_NAME+0);
		ITheoryRoot root1 = createTheory(THEORY_NAME+1);
		ITheoryRoot root2 = createTheory(THEORY_NAME+2);
		addImportTheory(root2, root0);
		addImportTheory(root0, root1);

		saveRodinFileOf(root1);
		saveRodinFileOf(root2);
		saveRodinFileOf(root0);
		runBuilder();

		// ProjectTheoryGraph projectTheoryGraph = new ProjectTheoryGraph();

		@SuppressWarnings("unused")
		ISCTheoryRoot scTheoryRoot0 = root0.getSCTheoryRoot();
		ISCTheoryRoot scTheoryRoot1 = root1.getSCTheoryRoot();
		@SuppressWarnings("unused")
		ISCTheoryRoot scTheoryRoot2 = root2.getSCTheoryRoot();
		// FIXME isAccurate(scTheoryRoot0);
		// FIXME isAccurate(scTheoryRoot2);
		isAccurate(scTheoryRoot1);

		importsTheories(scTheoryRoot1);
		// FIXME importsTheories(scTheoryRoot2, root0.getDeployedTheoryRoot());
		// FIXME importsTheories(scTheoryRoot0, root1.getDeployedTheoryRoot());

//		projectTheoryGraph.setCheckedRoots(new ISCTheoryRoot[]{scTheoryRoot1});
//		Set<ISCTheoryRoot> checkedRoots1 = projectTheoryGraph.getCheckedRoots();

//		correctOrder(checkedRoots1,scTheoryRoot1);

//		projectTheoryGraph.setCheckedRoots(new ISCTheoryRoot[]{scTheoryRoot0});
//		Set<ISCTheoryRoot> checkedRoots0 = projectTheoryGraph.getCheckedRoots();

//		correctOrder(checkedRoots0,scTheoryRoot1,scTheoryRoot0);

//		projectTheoryGraph.setCheckedRoots(new ISCTheoryRoot[]{scTheoryRoot2});
//		Set<ISCTheoryRoot> checkedRoots2 = projectTheoryGraph.getCheckedRoots();

//		correctOrder(checkedRoots2,scTheoryRoot1,scTheoryRoot0,scTheoryRoot2);
	}

	/**
	 * Cycle detected (Error): th2 imports thy1 and th1 imports thy2: cycle should be detected by the builder
	 */
	@Test
	public void testTheoryDependency_003_CycleError() throws Exception {
		ITheoryRoot root1 = createTheory(THEORY_NAME+1);
		ITheoryRoot root2 = createTheory(THEORY_NAME+2);
		addImportTheory(root2, root1);
		addImportTheory(root1, root2);

		saveRodinFileOf(root1);
		saveRodinFileOf(root2);
		IMarker[] runBuilderProblems = runBuilderProblems(rodinProject);
		for(IMarker marker: runBuilderProblems){
			String message = (String) marker.getAttribute(IMarker.MESSAGE);
			assertEquals("Expected dependency cycle detection error", "Resource in dependency cycle" , message);
			if(!marker.getResource().getName().equals(root1.getRodinFile().getElementName()) && !marker.getResource().getName().equals(root2.getRodinFile().getElementName()))
				fail("Error detected in wrong file");
		}
	}

	/**
	 * No Error: thy0 imports thy1 and thy2 imports thy3: independent theories graphs
	 * the order is not relevant as long as thy1 is before thy0 and thy3 is before thy2
	 */
	@Test
	public void testTheoryDependency_004_IndependentGraphsNoError() throws Exception {
		ITheoryRoot root0 = createTheory(THEORY_NAME+0);
		ITheoryRoot root1 = createTheory(THEORY_NAME+1);
		ITheoryRoot root2 = createTheory(THEORY_NAME+2);
		ITheoryRoot root3 = createTheory(THEORY_NAME+3);

		addImportTheory(root0, root1);
		addImportTheory(root2, root3);

		saveRodinFileOf(root0);
		saveRodinFileOf(root1);
		saveRodinFileOf(root2);
		saveRodinFileOf(root3);
		runBuilder();

		@SuppressWarnings("unused")
		ISCTheoryRoot scTheoryRoot0 = root0.getSCTheoryRoot();
		ISCTheoryRoot scTheoryRoot1 = root1.getSCTheoryRoot();
		@SuppressWarnings("unused")
		ISCTheoryRoot scTheoryRoot2 = root2.getSCTheoryRoot();
		ISCTheoryRoot scTheoryRoot3 = root3.getSCTheoryRoot();
		// FIXME isAccurate(scTheoryRoot0);
		// FIXME isAccurate(scTheoryRoot2);
		isAccurate(scTheoryRoot1);
		isAccurate(scTheoryRoot3);

		importsTheories(scTheoryRoot1);
		// FIXME importsTheories(scTheoryRoot0, root1.getDeployedTheoryRoot());
		importsTheories(scTheoryRoot3);
		// FIXME importsTheories(scTheoryRoot2, root3.getDeployedTheoryRoot());

//		ProjectTheoryGraph projectTheoryGraph = new ProjectTheoryGraph();
//		projectTheoryGraph.setCheckedRoots(new ISCTheoryRoot[]{scTheoryRoot0});
//		Set<ISCTheoryRoot> checkedRoots = projectTheoryGraph.getCheckedRoots();

//		correctOrder(checkedRoots,scTheoryRoot1,scTheoryRoot0);

//		projectTheoryGraph.setCheckedRoots(new ISCTheoryRoot[]{scTheoryRoot2});
//		Set<ISCTheoryRoot> checkedRoots2 = projectTheoryGraph.getCheckedRoots();

//		correctOrder(checkedRoots2,scTheoryRoot3,scTheoryRoot2);

//		projectTheoryGraph.setCheckedRoots(new ISCTheoryRoot[]{scTheoryRoot0,scTheoryRoot2});
//		Set<ISCTheoryRoot> bothCheckedRoots = projectTheoryGraph.getCheckedRoots();

//		correctPartialOrder(bothCheckedRoots,scTheoryRoot3,scTheoryRoot2);
//		correctPartialOrder(bothCheckedRoots,scTheoryRoot1,scTheoryRoot0);

//		projectTheoryGraph.setCheckedRoots(new ISCTheoryRoot[]{scTheoryRoot0, scTheoryRoot1, scTheoryRoot2, scTheoryRoot3});
//		Set<ISCTheoryRoot> allCheckedRoots = projectTheoryGraph.getCheckedRoots();

//		correctPartialOrder(allCheckedRoots,scTheoryRoot3,scTheoryRoot2);
//		correctPartialOrder(allCheckedRoots,scTheoryRoot1,scTheoryRoot0);
	}

	/**
	 * No Error: thy0 imports thy1 and thy2 imports thy1: dependent theories graphs
	 * the order is not relevant as long as thy1 is before both thy0 and thy2
	 */
	@Test
	public void testTheoryDependency_005_DependentGraphsNoError() throws Exception {
		ITheoryRoot root0 = createTheory(THEORY_NAME+0);
		ITheoryRoot root1 = createTheory(THEORY_NAME+1);
		ITheoryRoot root2 = createTheory(THEORY_NAME+2);

		addImportTheory(root0, root1);
		addImportTheory(root2, root1);

		saveRodinFileOf(root0);
		saveRodinFileOf(root1);
		saveRodinFileOf(root2);
		runBuilder();

		@SuppressWarnings("unused")
		ISCTheoryRoot scTheoryRoot0 = root0.getSCTheoryRoot();
		ISCTheoryRoot scTheoryRoot1 = root1.getSCTheoryRoot();
		@SuppressWarnings("unused")
		ISCTheoryRoot scTheoryRoot2 = root2.getSCTheoryRoot();
		// FIXME isAccurate(scTheoryRoot0);
		// FIXME isAccurate(scTheoryRoot2);
		isAccurate(scTheoryRoot1);

		importsTheories(scTheoryRoot1);
		// FIXME importsTheories(scTheoryRoot0, root1.getDeployedTheoryRoot());
		// FIXME importsTheories(scTheoryRoot2, root1.getDeployedTheoryRoot());

//		ProjectTheoryGraph projectTheoryGraph = new ProjectTheoryGraph();
//		projectTheoryGraph.setCheckedRoots(new ISCTheoryRoot[]{scTheoryRoot0});
//		Set<ISCTheoryRoot> checkedRoots = projectTheoryGraph.getCheckedRoots();

//		correctOrder(checkedRoots,scTheoryRoot1,scTheoryRoot0);

//		projectTheoryGraph.setCheckedRoots(new ISCTheoryRoot[]{scTheoryRoot2});
//		Set<ISCTheoryRoot> checkedRoots2 = projectTheoryGraph.getCheckedRoots();

//		correctOrder(checkedRoots2,scTheoryRoot1,scTheoryRoot2);

//		projectTheoryGraph.setCheckedRoots(new ISCTheoryRoot[]{scTheoryRoot0,scTheoryRoot2});
//		Set<ISCTheoryRoot> bothCheckedRoots = projectTheoryGraph.getCheckedRoots();

//		correctPartialOrder(bothCheckedRoots,scTheoryRoot1,scTheoryRoot2);
//		correctPartialOrder(bothCheckedRoots,scTheoryRoot1,scTheoryRoot0);
	}

	/**
	 * No Error: thy0 imports thy1,thy2 and thy2 imports thy3
	 * the order is thy3, then thy1 or thy2 then thy0
	 */
	@Test
	public void testTheoryDependency_006_MultipleImports_NoError() throws Exception {
		ITheoryRoot root0 = createTheory(THEORY_NAME+0);
		ITheoryRoot root1 = createTheory(THEORY_NAME+1);
		ITheoryRoot root2 = createTheory(THEORY_NAME+2);
		ITheoryRoot root3 = createTheory(THEORY_NAME+3);

		addImportTheory(root0, root1);
		addImportTheory(root0, root2);
		addImportTheory(root2, root3);

		saveRodinFileOf(root0);
		saveRodinFileOf(root1);
		saveRodinFileOf(root2);
		saveRodinFileOf(root3);
		runBuilder();

		@SuppressWarnings("unused")
		ISCTheoryRoot scTheoryRoot0 = root0.getSCTheoryRoot();
		ISCTheoryRoot scTheoryRoot1 = root1.getSCTheoryRoot();
		@SuppressWarnings("unused")
		ISCTheoryRoot scTheoryRoot2 = root2.getSCTheoryRoot();
		ISCTheoryRoot scTheoryRoot3 = root3.getSCTheoryRoot();
		// FIXME isAccurate(scTheoryRoot0);
		isAccurate(scTheoryRoot1);
		// FIXME isAccurate(scTheoryRoot2);
		isAccurate(scTheoryRoot3);

		importsTheories(scTheoryRoot3);
		// FIXME importsTheories(scTheoryRoot0, root1.getDeployedTheoryRoot(), root2.getDeployedTheoryRoot());
		// FIXME importsTheories(scTheoryRoot2, root3.getDeployedTheoryRoot());

//		ProjectTheoryGraph projectTheoryGraph = new ProjectTheoryGraph();
//		projectTheoryGraph.setCheckedRoots(new ISCTheoryRoot[]{scTheoryRoot0});
//		Set<ISCTheoryRoot> checkedRoots = projectTheoryGraph.getCheckedRoots();

//		correctPartialOrder(checkedRoots,scTheoryRoot3,scTheoryRoot2);
//		correctPartialOrder(checkedRoots,scTheoryRoot3,scTheoryRoot1);
//		correctPartialOrder(checkedRoots,scTheoryRoot3,scTheoryRoot0);
//		correctPartialOrder(checkedRoots,scTheoryRoot2,scTheoryRoot0);
//		correctPartialOrder(checkedRoots,scTheoryRoot1,scTheoryRoot0);
	}

	/**
	 * No Error: thy0 imports thy1,th2; thy2 imports thy3,thy4; and thy4 imports thy5
	 * the order is thy5, then thy3 or thy4, then thy2, then thy1, then thy0
	 */
	@Test
	public void testTheoryDependency_006_1_MultipleImports_NoError() throws Exception {
		ITheoryRoot root0 = createTheory(THEORY_NAME+0);
		ITheoryRoot root1 = createTheory(THEORY_NAME+1);
		ITheoryRoot root2 = createTheory(THEORY_NAME+2);
		ITheoryRoot root3 = createTheory(THEORY_NAME+3);
		ITheoryRoot root4 = createTheory(THEORY_NAME+4);
		ITheoryRoot root5 = createTheory(THEORY_NAME+5);

		addImportTheory(root0, root1);
		addImportTheory(root0, root2);
		addImportTheory(root2, root3);
		addImportTheory(root2, root4);
		addImportTheory(root4, root5);

		saveRodinFileOf(root0);
		saveRodinFileOf(root1);
		saveRodinFileOf(root2);
		saveRodinFileOf(root3);
		saveRodinFileOf(root4);
		saveRodinFileOf(root5);
		runBuilder();

		@SuppressWarnings("unused")
		ISCTheoryRoot scTheoryRoot0 = root0.getSCTheoryRoot();
		ISCTheoryRoot scTheoryRoot1 = root1.getSCTheoryRoot();
		@SuppressWarnings("unused")
		ISCTheoryRoot scTheoryRoot2 = root2.getSCTheoryRoot();
		ISCTheoryRoot scTheoryRoot3 = root3.getSCTheoryRoot();
		@SuppressWarnings("unused")
		ISCTheoryRoot scTheoryRoot4 = root4.getSCTheoryRoot();
		ISCTheoryRoot scTheoryRoot5 = root5.getSCTheoryRoot();
		// FIXME isAccurate(scTheoryRoot0);
		isAccurate(scTheoryRoot1);
		// FIXME isAccurate(scTheoryRoot2);
		isAccurate(scTheoryRoot3);
		// FIXME isAccurate(scTheoryRoot4);
		isAccurate(scTheoryRoot5);

		importsTheories(scTheoryRoot3);
		importsTheories(scTheoryRoot5);
		// FIXME importsTheories(scTheoryRoot0, root1.getDeployedTheoryRoot(), root2.getDeployedTheoryRoot());
		// FIXME importsTheories(scTheoryRoot2, root3.getDeployedTheoryRoot(), root4.getDeployedTheoryRoot());
		// FIXME importsTheories(scTheoryRoot4, root5.getDeployedTheoryRoot());

//		ProjectTheoryGraph projectTheoryGraph = new ProjectTheoryGraph();
//		projectTheoryGraph.setCheckedRoots(new ISCTheoryRoot[]{scTheoryRoot0});
//		Set<ISCTheoryRoot> checkedRoots = projectTheoryGraph.getCheckedRoots();

//		correctPartialOrder(checkedRoots,scTheoryRoot5,scTheoryRoot4, scTheoryRoot2, scTheoryRoot0);
//		correctPartialOrder(checkedRoots,scTheoryRoot4,scTheoryRoot2, scTheoryRoot0);
//		correctPartialOrder(checkedRoots,scTheoryRoot3,scTheoryRoot2, scTheoryRoot0);
//		correctPartialOrder(checkedRoots,scTheoryRoot2,scTheoryRoot0);
//		correctPartialOrder(checkedRoots,scTheoryRoot1,scTheoryRoot0);
	}
}
