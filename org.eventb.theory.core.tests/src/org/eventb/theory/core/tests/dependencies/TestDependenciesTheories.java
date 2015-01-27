/**
 *
 */
package org.eventb.theory.core.tests.dependencies;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.ITheoryRoot;
import org.eventb.theory.core.TheoryHierarchyHelper;
import org.junit.Test;

/**
 * Tests for theories dependencies (theories that import other theories)
 *
 *
 * @author renatosilva
 * @author asiehsalehi
 *
 */
public class TestDependenciesTheories extends BasicTestDependenciesTheories {

	/**
	 * No Error: thy2 imports thy1
	 */
	@Test
	public void testTheoryDependency_001_NoError() throws Exception {
		IProgressMonitor monitor = new NullProgressMonitor();
		
		ITheoryRoot root1 = createTheory(THEORY_NAME+1);
		ISCTheoryRoot scTheoryRoot1 = root1.getSCTheoryRoot();
		IDeployedTheoryRoot deployedTheoryRoot1 = root1.getDeployedTheoryRoot();
		
		saveRodinFileOf(root1);
		runBuilder();
		createDeployedTheory(scTheoryRoot1, monitor);
		
		ITheoryRoot root2 = createTheory(THEORY_NAME+2);
		ISCTheoryRoot scTheoryRoot2 = root2.getSCTheoryRoot();
		
		addImportTheory(root2, deployedTheoryRoot1);

		saveRodinFileOf(root1);
		saveRodinFileOf(root2);
		runBuilder();

		isAccurate(scTheoryRoot1);
		isAccurate(scTheoryRoot2);
		
		SortedSet<ISCTheoryRoot> sortedTheories = 
				new TreeSet<ISCTheoryRoot>(TheoryHierarchyHelper.getSCTheoryDependencyComparator());
		sortedTheories.add(scTheoryRoot2);
		sortedTheories.addAll(TheoryHierarchyHelper.importClosure(scTheoryRoot2));
		correctOrder(sortedTheories, scTheoryRoot1,scTheoryRoot2);

	}

	/**
	 * No Error: th2 imports thy0. thy0 imports thy1.
	 */
	@Test
	public void testTheoryDependency_002_NoError() throws Exception {
		IProgressMonitor monitor = new NullProgressMonitor();
		
		ITheoryRoot root0 = createTheory(THEORY_NAME+0);
		ISCTheoryRoot scTheoryRoot0 = root0.getSCTheoryRoot();
		IDeployedTheoryRoot deployedTheoryRoot0 = root0.getDeployedTheoryRoot();
		
		ITheoryRoot root1 = createTheory(THEORY_NAME+1);
		ISCTheoryRoot scTheoryRoot1 = root1.getSCTheoryRoot();
		IDeployedTheoryRoot deployedTheoryRoot1 = root1.getDeployedTheoryRoot();
		
		saveRodinFileOf(root0);
		saveRodinFileOf(root1);
		runBuilder();
		createDeployedTheory(scTheoryRoot0, monitor);
		createDeployedTheory(scTheoryRoot1, monitor);
		
		ITheoryRoot root2 = createTheory(THEORY_NAME+2);
		ISCTheoryRoot scTheoryRoot2 = root2.getSCTheoryRoot();
		
		addImportTheory(root2, deployedTheoryRoot0);
		addImportTheory(root0, deployedTheoryRoot1);

		saveRodinFileOf(root1);
		saveRodinFileOf(root2);
		saveRodinFileOf(root0);
		runBuilder();
		
		isAccurate(scTheoryRoot0);
		isAccurate(scTheoryRoot2);
		isAccurate(scTheoryRoot1);

		importsTheories(scTheoryRoot1);
		importsTheories(scTheoryRoot2, deployedTheoryRoot0);
		importsTheories(scTheoryRoot0, deployedTheoryRoot1);
		
		SortedSet<ISCTheoryRoot> sortedTheories = 
				new TreeSet<ISCTheoryRoot>(TheoryHierarchyHelper.getSCTheoryDependencyComparator());
		
		sortedTheories.add(scTheoryRoot1);
		sortedTheories.addAll(TheoryHierarchyHelper.importClosure(scTheoryRoot1));
		correctOrder(sortedTheories, scTheoryRoot1);
		
		sortedTheories.clear();
		sortedTheories.add(scTheoryRoot0);
		sortedTheories.addAll(TheoryHierarchyHelper.importClosure(scTheoryRoot0));
		correctOrder(sortedTheories, scTheoryRoot1, scTheoryRoot0);
		
		sortedTheories.clear();
		sortedTheories.add(scTheoryRoot2);
		sortedTheories.addAll(TheoryHierarchyHelper.importClosure(scTheoryRoot2));
		// FIXME correctOrder(sortedTheories, scTheoryRoot1, scTheoryRoot0, scTheoryRoot2);
	}

	/**
	 * Cycle detected (Error): th2 imports thy1 and th1 imports thy2: cycle should be detected by the builder
	 */
	@Test
	public void testTheoryDependency_003_CycleError() throws Exception {
		IProgressMonitor monitor = new NullProgressMonitor();
		
		ITheoryRoot root1 = createTheory(THEORY_NAME+1);
		ISCTheoryRoot scTheoryRoot1 = root1.getSCTheoryRoot();
		IDeployedTheoryRoot deployedTheoryRoot1 = root1.getDeployedTheoryRoot();
		
		ITheoryRoot root2 = createTheory(THEORY_NAME+2);
		ISCTheoryRoot scTheoryRoot2 = root2.getSCTheoryRoot();
		IDeployedTheoryRoot deployedTheoryRoot2 = root2.getDeployedTheoryRoot();
		
		saveRodinFileOf(root1);
		saveRodinFileOf(root2);
		runBuilder();
		createDeployedTheory(scTheoryRoot1, monitor);
		createDeployedTheory(scTheoryRoot2, monitor);
		
		
		addImportTheory(root2, deployedTheoryRoot1);
		addImportTheory(root1, deployedTheoryRoot2);

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
		IProgressMonitor monitor = new NullProgressMonitor();
		
		ITheoryRoot root1 = createTheory(THEORY_NAME+1);
		ISCTheoryRoot scTheoryRoot1 = root1.getSCTheoryRoot();
		IDeployedTheoryRoot deployedTheoryRoot1 = root1.getDeployedTheoryRoot();
		
		ITheoryRoot root3 = createTheory(THEORY_NAME+3);
		ISCTheoryRoot scTheoryRoot3 = root3.getSCTheoryRoot();
		IDeployedTheoryRoot deployedTheoryRoot3 = root3.getDeployedTheoryRoot();
		
		saveRodinFileOf(root1);
		saveRodinFileOf(root3);
		runBuilder();
		createDeployedTheory(scTheoryRoot1, monitor);
		createDeployedTheory(scTheoryRoot3, monitor);	
		
		ITheoryRoot root0 = createTheory(THEORY_NAME+0);
		ISCTheoryRoot scTheoryRoot0 = root0.getSCTheoryRoot();
		
		ITheoryRoot root2 = createTheory(THEORY_NAME+2);
		ISCTheoryRoot scTheoryRoot2 = root2.getSCTheoryRoot();

		addImportTheory(root0, deployedTheoryRoot1);
		addImportTheory(root2, deployedTheoryRoot3);

		saveRodinFileOf(root0);
		saveRodinFileOf(root1);
		saveRodinFileOf(root2);
		saveRodinFileOf(root3);
		runBuilder();

		isAccurate(scTheoryRoot0);
		isAccurate(scTheoryRoot2);
		isAccurate(scTheoryRoot1);
		isAccurate(scTheoryRoot3);

		importsTheories(scTheoryRoot1);
		importsTheories(scTheoryRoot0, deployedTheoryRoot1);
		importsTheories(scTheoryRoot3);
		importsTheories(scTheoryRoot2, deployedTheoryRoot3);
		
		SortedSet<ISCTheoryRoot> sortedTheories = 
				new TreeSet<ISCTheoryRoot>(TheoryHierarchyHelper.getSCTheoryDependencyComparator());
		
		sortedTheories.add(scTheoryRoot0);
		sortedTheories.addAll(TheoryHierarchyHelper.importClosure(scTheoryRoot0));
		correctOrder(sortedTheories, scTheoryRoot1, scTheoryRoot0);
		
		sortedTheories.clear();
		sortedTheories.add(scTheoryRoot2);
		sortedTheories.addAll(TheoryHierarchyHelper.importClosure(scTheoryRoot2));
		// FIXME correctOrder(sortedTheories, scTheoryRoot1, scTheoryRoot3, scTheoryRoot2);
		
		sortedTheories.clear();
		sortedTheories.add(scTheoryRoot0);
		sortedTheories.add(scTheoryRoot2);
		sortedTheories.addAll(TheoryHierarchyHelper.importClosure(scTheoryRoot0));
		sortedTheories.addAll(TheoryHierarchyHelper.importClosure(scTheoryRoot2));
		correctPartialOrder(sortedTheories, scTheoryRoot1, scTheoryRoot3, scTheoryRoot2);
		//FIXME correctPartialOrder(sortedTheories, scTheoryRoot1, scTheoryRoot1, scTheoryRoot0);
		
		sortedTheories.clear();
		sortedTheories.add(scTheoryRoot0);
		sortedTheories.add(scTheoryRoot1);
		sortedTheories.add(scTheoryRoot2);
		sortedTheories.add(scTheoryRoot3);
		sortedTheories.addAll(TheoryHierarchyHelper.importClosure(scTheoryRoot0));
		sortedTheories.addAll(TheoryHierarchyHelper.importClosure(scTheoryRoot1));
		sortedTheories.addAll(TheoryHierarchyHelper.importClosure(scTheoryRoot2));
		sortedTheories.addAll(TheoryHierarchyHelper.importClosure(scTheoryRoot3));
		correctPartialOrder(sortedTheories, scTheoryRoot1, scTheoryRoot3, scTheoryRoot2);
		// FIXME correctPartialOrder(sortedTheories, scTheoryRoot1, scTheoryRoot1, scTheoryRoot0);
	}

	/**
	 * No Error: thy0 imports thy1 and thy2 imports thy1: dependent theories graphs
	 * the order is not relevant as long as thy1 is before both thy0 and thy2
	 */
	@Test
	public void testTheoryDependency_005_DependentGraphsNoError() throws Exception {
		IProgressMonitor monitor = new NullProgressMonitor();
		
		ITheoryRoot root1 = createTheory(THEORY_NAME+1);
		ISCTheoryRoot scTheoryRoot1 = root1.getSCTheoryRoot();
		IDeployedTheoryRoot deployedTheoryRoot1 = root1.getDeployedTheoryRoot();
		
		saveRodinFileOf(root1);
		runBuilder();
		createDeployedTheory(scTheoryRoot1, monitor);
		
		ITheoryRoot root0 = createTheory(THEORY_NAME+0);
		ISCTheoryRoot scTheoryRoot0 = root0.getSCTheoryRoot();
		
		ITheoryRoot root2 = createTheory(THEORY_NAME+2);
		ISCTheoryRoot scTheoryRoot2 = root2.getSCTheoryRoot();

		addImportTheory(root0, deployedTheoryRoot1);
		addImportTheory(root2, deployedTheoryRoot1);

		saveRodinFileOf(root0);
		saveRodinFileOf(root1);
		saveRodinFileOf(root2);
		runBuilder();

		isAccurate(scTheoryRoot0);
		isAccurate(scTheoryRoot2);
		isAccurate(scTheoryRoot1);

		importsTheories(scTheoryRoot1);
		importsTheories(scTheoryRoot0, deployedTheoryRoot1);
		importsTheories(scTheoryRoot2, deployedTheoryRoot1);
		
		SortedSet<ISCTheoryRoot> sortedTheories = 
				new TreeSet<ISCTheoryRoot>(TheoryHierarchyHelper.getSCTheoryDependencyComparator());
		
		sortedTheories.add(scTheoryRoot0);
		sortedTheories.addAll(TheoryHierarchyHelper.importClosure(scTheoryRoot0));
		correctOrder(sortedTheories, scTheoryRoot1, scTheoryRoot0);
		
		sortedTheories.clear();
		sortedTheories.add(scTheoryRoot2);
		sortedTheories.addAll(TheoryHierarchyHelper.importClosure(scTheoryRoot2));
		correctOrder(sortedTheories, scTheoryRoot1, scTheoryRoot2);
		
		sortedTheories.clear();
		sortedTheories.add(scTheoryRoot0);
		sortedTheories.add(scTheoryRoot2);
		sortedTheories.addAll(TheoryHierarchyHelper.importClosure(scTheoryRoot0));
		sortedTheories.addAll(TheoryHierarchyHelper.importClosure(scTheoryRoot2));
		correctPartialOrder(sortedTheories, scTheoryRoot1, scTheoryRoot2);
		correctPartialOrder(sortedTheories, scTheoryRoot1, scTheoryRoot0);
	}

	/**
	 * No Error: thy0 imports thy1,thy2 and thy2 imports thy3
	 * the order is thy3, then thy1 or thy2 then thy0
	 */
	@Test
	public void testTheoryDependency_006_MultipleImports_NoError() throws Exception {
		IProgressMonitor monitor = new NullProgressMonitor();
	
		ITheoryRoot root1 = createTheory(THEORY_NAME+1);
		ISCTheoryRoot scTheoryRoot1 = root1.getSCTheoryRoot();
		IDeployedTheoryRoot deployedTheoryRoot1 = root1.getDeployedTheoryRoot();
		
		ITheoryRoot root2 = createTheory(THEORY_NAME+2);
		ISCTheoryRoot scTheoryRoot2 = root2.getSCTheoryRoot();
		IDeployedTheoryRoot deployedTheoryRoot2 = root2.getDeployedTheoryRoot();
		
		ITheoryRoot root3 = createTheory(THEORY_NAME+3);
		ISCTheoryRoot scTheoryRoot3 = root3.getSCTheoryRoot();
		IDeployedTheoryRoot deployedTheoryRoot3 = root3.getDeployedTheoryRoot();
		
		saveRodinFileOf(root1);
		saveRodinFileOf(root2);
		saveRodinFileOf(root3);
		runBuilder();
		createDeployedTheory(scTheoryRoot1, monitor);
		createDeployedTheory(scTheoryRoot2, monitor);	
		createDeployedTheory(scTheoryRoot3, monitor);
		
		ITheoryRoot root0 = createTheory(THEORY_NAME+0);
		ISCTheoryRoot scTheoryRoot0 = root0.getSCTheoryRoot();
		
		addImportTheory(root0, deployedTheoryRoot1);
		addImportTheory(root0, deployedTheoryRoot2);
		addImportTheory(root2, deployedTheoryRoot3);

		saveRodinFileOf(root0);
		saveRodinFileOf(root1);
		saveRodinFileOf(root2);
		saveRodinFileOf(root3);
		runBuilder();

		isAccurate(scTheoryRoot0);
		isAccurate(scTheoryRoot1);
		isAccurate(scTheoryRoot2);
		isAccurate(scTheoryRoot3);

		importsTheories(scTheoryRoot3);
		importsTheories(scTheoryRoot0, deployedTheoryRoot1, deployedTheoryRoot2);
		importsTheories(scTheoryRoot2, deployedTheoryRoot3);
		
		SortedSet<ISCTheoryRoot> sortedTheories = 
				new TreeSet<ISCTheoryRoot>(TheoryHierarchyHelper.getSCTheoryDependencyComparator());
		
		sortedTheories.add(scTheoryRoot0);
		sortedTheories.addAll(TheoryHierarchyHelper.importClosure(scTheoryRoot0));
		//FIXME correctPartialOrder(sortedTheories, scTheoryRoot3, scTheoryRoot2);
		//FIXME correctPartialOrder(sortedTheories, scTheoryRoot3, scTheoryRoot1);
		//FIXME correctPartialOrder(sortedTheories, scTheoryRoot3, scTheoryRoot0);
		correctPartialOrder(sortedTheories, scTheoryRoot2, scTheoryRoot0);
		correctPartialOrder(sortedTheories, scTheoryRoot1, scTheoryRoot0);
	}

	/**
	 * No Error: thy0 imports thy1,th2; thy2 imports thy3,thy4; and thy4 imports thy5
	 * the order is thy5, then thy3 or thy4, then thy2, then thy1, then thy0
	 */
	@Test
	public void testTheoryDependency_006_1_MultipleImports_NoError() throws Exception {
		IProgressMonitor monitor = new NullProgressMonitor();

		ITheoryRoot root1 = createTheory(THEORY_NAME+1);
		ISCTheoryRoot scTheoryRoot1 = root1.getSCTheoryRoot();
		IDeployedTheoryRoot deployedTheoryRoot1 = root1.getDeployedTheoryRoot();
		
		ITheoryRoot root2 = createTheory(THEORY_NAME+2);
		ISCTheoryRoot scTheoryRoot2 = root2.getSCTheoryRoot();
		IDeployedTheoryRoot deployedTheoryRoot2 = root2.getDeployedTheoryRoot();
		
		ITheoryRoot root3 = createTheory(THEORY_NAME+3);
		ISCTheoryRoot scTheoryRoot3 = root3.getSCTheoryRoot();
		IDeployedTheoryRoot deployedTheoryRoot3 = root3.getDeployedTheoryRoot();
		
		ITheoryRoot root4 = createTheory(THEORY_NAME+4);
		ISCTheoryRoot scTheoryRoot4 = root4.getSCTheoryRoot();
		IDeployedTheoryRoot deployedTheoryRoot4 = root4.getDeployedTheoryRoot();
		
		ITheoryRoot root5 = createTheory(THEORY_NAME+5);
		ISCTheoryRoot scTheoryRoot5 = root5.getSCTheoryRoot();
		IDeployedTheoryRoot deployedTheoryRoot5 = root5.getDeployedTheoryRoot();
		
		saveRodinFileOf(root1);
		saveRodinFileOf(root2);
		saveRodinFileOf(root3);
		saveRodinFileOf(root4);
		saveRodinFileOf(root5);
		runBuilder();
		createDeployedTheory(scTheoryRoot1, monitor);
		createDeployedTheory(scTheoryRoot2, monitor);	
		createDeployedTheory(scTheoryRoot3, monitor);
		createDeployedTheory(scTheoryRoot4, monitor);
		createDeployedTheory(scTheoryRoot5, monitor);
		
		ITheoryRoot root0 = createTheory(THEORY_NAME+0);
		ISCTheoryRoot scTheoryRoot0 = root0.getSCTheoryRoot();

		addImportTheory(root0, deployedTheoryRoot1);
		addImportTheory(root0, deployedTheoryRoot2);
		addImportTheory(root2, deployedTheoryRoot3);
		addImportTheory(root2, deployedTheoryRoot4);
		addImportTheory(root4, deployedTheoryRoot5);

		saveRodinFileOf(root0);
		saveRodinFileOf(root1);
		saveRodinFileOf(root2);
		saveRodinFileOf(root3);
		saveRodinFileOf(root4);
		saveRodinFileOf(root5);
		runBuilder();
		
		isAccurate(scTheoryRoot0);
		isAccurate(scTheoryRoot1);
		isAccurate(scTheoryRoot2);
		isAccurate(scTheoryRoot3);
		isAccurate(scTheoryRoot4);
		isAccurate(scTheoryRoot5);

		importsTheories(scTheoryRoot3);
		importsTheories(scTheoryRoot5);
		importsTheories(scTheoryRoot0, root1.getDeployedTheoryRoot(), root2.getDeployedTheoryRoot());
		importsTheories(scTheoryRoot2, root3.getDeployedTheoryRoot(), root4.getDeployedTheoryRoot());
		importsTheories(scTheoryRoot4, root5.getDeployedTheoryRoot());
		
		SortedSet<ISCTheoryRoot> sortedTheories = 
				new TreeSet<ISCTheoryRoot>(TheoryHierarchyHelper.getSCTheoryDependencyComparator());
		
		sortedTheories.add(scTheoryRoot0);
		sortedTheories.addAll(TheoryHierarchyHelper.importClosure(scTheoryRoot0));
		// FIXME correctPartialOrder(sortedTheories, scTheoryRoot5, scTheoryRoot4, scTheoryRoot2, scTheoryRoot0);
		// FIXME correctPartialOrder(sortedTheories, scTheoryRoot4, scTheoryRoot2, scTheoryRoot0);
		// FIXME correctPartialOrder(sortedTheories, scTheoryRoot3, scTheoryRoot2, scTheoryRoot0);
		correctPartialOrder(sortedTheories, scTheoryRoot2, scTheoryRoot0);
		correctPartialOrder(sortedTheories, scTheoryRoot1, scTheoryRoot0);
	}
}
