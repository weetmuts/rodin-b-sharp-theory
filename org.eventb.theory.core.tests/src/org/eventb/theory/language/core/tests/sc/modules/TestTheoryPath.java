/**
 * 
 */
package org.eventb.theory.language.core.tests.sc.modules;

import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.core.ISCTheoryPathRoot;
import org.eventb.theory.core.ITheoryPathRoot;
import org.eventb.theory.language.core.tests.sc.BasicTestSCTheoryPath;
import org.rodinp.core.IRodinProject;

/**
 * @author renatosilva
 *
 */
public class TestTheoryPath extends BasicTestSCTheoryPath {
	
	public TestTheoryPath(String name) {
		super(name);
	}

	/**
	 * No Error
	 */
	public void testTheoryPath_001_NoError() throws Exception {
		ITheoryPathRoot root = createTheoryPath(THEORYPATH_NAME);
		IDeployedTheoryRoot dt1 = createDeployedTheory("dt1");
		addDeployedTheory(root,dt1, null);

		saveRodinFileOf(root);
		runBuilder();
		
		ISCTheoryPathRoot scTheoryPathRoot = root.getSCTheoryPathRoot();
		isAccurate(scTheoryPathRoot);
		containsProject(scTheoryPathRoot, rodinProject);
		containsDeployedTheory(scTheoryPathRoot, dt1);
	}

	/**
	 * No Error, deployed theory from different project
	 */
	public void testTheoryPath_002_NoError() throws Exception {
		ITheoryPathRoot root = createTheoryPath(THEORYPATH_NAME);
		IRodinProject rodinProject = createRodinProject("PRJ1");
		IDeployedTheoryRoot dt1 = createDeployedTheory("dt1",rodinProject);
		addDeployedTheory(root,dt1, null);
		
		saveRodinFileOf(root);
		runBuilder();
		
		ISCTheoryPathRoot scTheoryPathRoot = root.getSCTheoryPathRoot();
		isAccurate(scTheoryPathRoot);
		containsProject(scTheoryPathRoot, rodinProject);
		containsDeployedTheory(scTheoryPathRoot, dt1);
	}
	
	/**
	 * Error, project where the deployed theory is located was deleted
	 */
	public void testTheoryPath_003_DeletedProject() throws Exception {
		ITheoryPathRoot root = createTheoryPath(THEORYPATH_NAME);
		IRodinProject rodinProject = createRodinProject("PRJ1");
		IDeployedTheoryRoot dt1 = createDeployedTheory("dt1",rodinProject);
		addDeployedTheory(root,dt1, null);
		
		rodinProject.getProject().delete(true, null);
		
		saveRodinFileOf(root);
		runBuilder();
		
		assertEquals(false,rodinProject.exists());
		
		ISCTheoryPathRoot scTheoryPathRoot = root.getSCTheoryPathRoot();
		isNotAccurate(scTheoryPathRoot);
	}
	
	/**
	 * Error, project where the deployed theory is located was closed
	 */
	public void testTheoryPath_004_ClosedProject() throws Exception {
		ITheoryPathRoot root = createTheoryPath(THEORYPATH_NAME);
		IRodinProject rodinProject = createRodinProject("PRJ1");
		IDeployedTheoryRoot dt1 = createDeployedTheory("dt1",rodinProject);
		addDeployedTheory(root,dt1, null);
		
		rodinProject.getProject().close(null);
		
		saveRodinFileOf(root);
		runBuilder();
		
		assertEquals(false,rodinProject.exists());
		
		ISCTheoryPathRoot scTheoryPathRoot = root.getSCTheoryPathRoot();
		isNotAccurate(scTheoryPathRoot);
	}
	
	/**
	 * Error, deployed theory does not exist
	 */
	public void testTheoryPath_005_NoDeployedTheory() throws Exception {
		ITheoryPathRoot root = createTheoryPath(THEORYPATH_NAME);
		IRodinProject rodinProject = createRodinProject("PRJ1");
		IDeployedTheoryRoot dt1 = createDeployedTheory("dt1",rodinProject);
		addDeployedTheory(root,dt1, null);
		
		dt1.getRodinFile().delete(true, null);
		
		saveRodinFileOf(root);
		runBuilder();
		
		assertEquals(false,dt1.exists());
		
		ISCTheoryPathRoot scTheoryPathRoot = root.getSCTheoryPathRoot();
		isNotAccurate(scTheoryPathRoot);
	}

}
