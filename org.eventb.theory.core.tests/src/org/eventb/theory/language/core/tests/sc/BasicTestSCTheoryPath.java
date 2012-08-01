/**
 * 
 */
package org.eventb.theory.language.core.tests.sc;

import org.eventb.core.IAccuracyElement;
import org.eventb.theory.core.DatabaseUtilitiesTheoryPath;
import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.core.ISCAvailableTheory;
import org.eventb.theory.core.ISCAvailableTheoryProject;
import org.eventb.theory.core.ISCTheoryPathRoot;
import org.eventb.theory.core.ITheoryPathRoot;
import org.eventb.theory.language.core.tests.BuilderTestTheoryPath;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

/**
 * @author renatosilva
 *
 */
public class BasicTestSCTheoryPath extends BuilderTestTheoryPath {
	
	public static final String THEORYPATH_NAME = "thyPath";

	public BasicTestSCTheoryPath(String name) {
		super(name);
	}
	
	@Override
	protected ITheoryPathRoot createTheoryPath(String bareName) throws RodinDBException {
		ITheoryPathRoot theory = super.createTheoryPath(bareName);
		theory.setConfiguration(DatabaseUtilitiesTheoryPath.THEORY_PATH_CONFIGURATION, null);
		return theory;
	}
	
	public void isAccurate(IAccuracyElement element) throws RodinDBException {
		boolean acc = element.isAccurate();

		assertEquals("element is not accurate", true, acc);
	}
	
	protected void containsDeployedTheory(ISCTheoryPathRoot scTheoryPathRoot,
			IDeployedTheoryRoot dt1) throws RodinDBException {
		
		for(ISCAvailableTheory theory: scTheoryPathRoot.getSCAvailableTheories()){
			if(theory.getSCDeployedTheoryRoot().equals(dt1))
				return;
		}
		
		fail("DeployedTheoryRoot " + dt1.getComponentName() + "should exist in SC file " + scTheoryPathRoot.getElementName());
	}
	
	protected void containsProject(ISCTheoryPathRoot scTheoryPathRoot,
			IRodinProject rodinProject) throws RodinDBException {
		
		for(ISCAvailableTheoryProject project: scTheoryPathRoot.getSCAvailableTheoryProjects()){
			if(project.getSCAvailableTheoryProject().equals(rodinProject))
				return;
		}
		
		fail("Project " + rodinProject.getElementName() + "should exist in SC file " + scTheoryPathRoot.getElementName());
	}
	
	public void isNotAccurate(IAccuracyElement element) throws RodinDBException {
		boolean acc = element.isAccurate();

		assertEquals("element is accurate but it should not be", false, acc);
	}

}
