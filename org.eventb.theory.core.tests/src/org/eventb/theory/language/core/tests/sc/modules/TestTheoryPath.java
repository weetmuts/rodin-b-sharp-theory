/**
 * 
 */
package org.eventb.theory.language.core.tests.sc.modules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.core.IDeploymentResult;
import org.eventb.theory.core.ISCTheoryPathRoot;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.ITheoryPathRoot;
import org.eventb.theory.language.core.tests.sc.BasicTestSCTheoryPath;
import org.junit.Test;
import org.rodinp.core.IRodinProject;

/**
 * @author renatosilva
 *
 */
public class TestTheoryPath extends BasicTestSCTheoryPath {
	
	/**
	 * No Error
	 */
	@Test
	public void testTheoryPath_001_NoError() throws Exception {
		ITheoryPathRoot root = createTheoryPath(THEORYPATH_NAME, rodinProject);
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
	@Test
	public void testTheoryPath_002_NoError() throws Exception {
		IRodinProject rodinProject = createRodinProject("PRJ1");
		ITheoryPathRoot root = createTheoryPath(THEORYPATH_NAME, rodinProject);
		IDeployedTheoryRoot dt1 = createDeployedTheory("dt1",rodinProject);
		addDeployedTheory(root,dt1, null);
		
		saveRodinFileOf(root);
		runBuilder(rodinProject);
		
		ISCTheoryPathRoot scTheoryPathRoot = root.getSCTheoryPathRoot();
		isAccurate(scTheoryPathRoot);
		containsProject(scTheoryPathRoot, rodinProject);
		containsDeployedTheory(scTheoryPathRoot, dt1);
	}
	
	/**
	 * Error, project where the deployed theory is located was deleted
	 */
	@Test
	public void testTheoryPath_003_DeletedProject() throws Exception {
		IRodinProject rodinProject = createRodinProject("PRJ1");
		ITheoryPathRoot root = createTheoryPath(THEORYPATH_NAME, super.rodinProject);
		IDeployedTheoryRoot dt1 = createDeployedTheory("dt1",rodinProject);
		addDeployedTheory(root,dt1, null);
		
		rodinProject.getProject().delete(true, null);
		
		saveRodinFileOf(root);
		runBuilder(root.getRodinProject());
		
		assertEquals(false,rodinProject.exists());
		
		ISCTheoryPathRoot scTheoryPathRoot = root.getSCTheoryPathRoot();
		isNotAccurate(scTheoryPathRoot);
	}
	
	/**
	 * Error, project where the deployed theory is located was closed
	 */
	@Test
	public void testTheoryPath_004_ClosedProject() throws Exception {
		IRodinProject rodinProject = createRodinProject("PRJ1");
		ITheoryPathRoot root = createTheoryPath(THEORYPATH_NAME, super.rodinProject);
		IDeployedTheoryRoot dt1 = createDeployedTheory("dt1",rodinProject);
		addDeployedTheory(root,dt1, null);
		
		rodinProject.getProject().close(null);
		
		saveRodinFileOf(root);
		runBuilder(root.getRodinProject());
		
		assertEquals(false,rodinProject.exists());
		
		ISCTheoryPathRoot scTheoryPathRoot = root.getSCTheoryPathRoot();
		isNotAccurate(scTheoryPathRoot);
	}
	
	/**
	 * Error, deployed theory does not exist
	 */
	@Test
	public void testTheoryPath_005_NoDeployedTheory() throws Exception {
		IRodinProject rodinProject = createRodinProject("PRJ1");
		ITheoryPathRoot root = createTheoryPath(THEORYPATH_NAME, rodinProject);
		IDeployedTheoryRoot dt1 = createDeployedTheory("dt1",rodinProject);
		addDeployedTheory(root,dt1, null);
		
		dt1.getRodinFile().delete(true, null);
		
		saveRodinFileOf(root);
		runBuilder(root.getRodinProject());
		
		assertEquals(false,dt1.exists());
		
		ISCTheoryPathRoot scTheoryPathRoot = root.getSCTheoryPathRoot();
		isNotAccurate(scTheoryPathRoot);
	}
	
	/**
	 * Error, deployed theory clashes with another in the same project
	 */
	@Test
	public void testTheoryPath_006_DeployedTheoryClash() throws Exception {
		IProgressMonitor monitor = new NullProgressMonitor();
		String operatorName = "op1";
		String theoryName = "thy";
		ISCTheoryRoot scTheory1 = createSCTheory(operatorName, theoryName, rodinProject, monitor);
		IDeploymentResult deployedResult = createDeployedTheory(scTheory1, monitor);
		
		assertTrue("Theory " + theoryName + " should have been deployed successfully", deployedResult.succeeded());
		
		// String theoryName2 = "thy2";
		// ISCTheoryRoot scTheory2 = createSCTheory(operatorName, theoryName2, rodinProject, monitor);
		// IDeploymentResult deployedResult2 = createDeployedTheory(scTheory2, monitor);

		// FIXME assertEquals("Deployment should have failed:" + deployedResult2.getErrorMessage(), false, deployedResult2.succeeded());
	}
	
	/**
	 * Error, deployed theory clashes with another in the different projects
	 */
	@Test
	public void testTheoryPath_007_DeployedTheoryDifferentProjectClash() throws Exception {
		IProgressMonitor monitor = new NullProgressMonitor();
		IRodinProject prj2 = createRodinProject("PRJ2");
		String operatorName = "op1";
		String theoryName = "thy";
		ISCTheoryRoot scTheory1 = createSCTheory(operatorName, theoryName, rodinProject, monitor);
		IDeploymentResult deployedResult = createDeployedTheory(scTheory1, monitor);
		
		assertTrue("Theory " + theoryName + " should have been deployed successfully", deployedResult.succeeded());
		
		String theoryName2 = "thy2";
		ISCTheoryRoot scTheory2 = createSCTheory(operatorName, theoryName2, prj2, monitor);
		IDeploymentResult deployedResult2 = createDeployedTheory(scTheory2, monitor);
		
		assertTrue("Theory " + theoryName2 + " should have been deployed successfully", deployedResult2.succeeded());
		
		ITheoryPathRoot root = createTheoryPath(THEORYPATH_NAME, rodinProject);
		IDeployedTheoryRoot dt1 = scTheory1.getDeployedTheoryRoot();
		addDeployedTheory(root,dt1 , monitor);
		IDeployedTheoryRoot dt2 =  scTheory2.getDeployedTheoryRoot();
		addDeployedTheory(root,dt2 , monitor);
		
		saveRodinFileOf(root);
		runBuilder(root.getRodinProject());
		
		ISCTheoryPathRoot scTheoryPathRoot = root.getSCTheoryPathRoot();
		isNotAccurate(scTheoryPathRoot);
		hasMarker(root.getAvailableTheoryProjects()[1].getTheories()[0]);
	}

}
