/**
 * 
 */
package org.eventb.theory.language.core.tests;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.theory.core.DatabaseUtilities;
import org.eventb.theory.core.DatabaseUtilitiesTheoryPath;
import org.eventb.theory.core.IAvailableTheory;
import org.eventb.theory.core.IAvailableTheoryProject;
import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.core.ITheoryPathRoot;
import org.eventb.theory.core.tests.BuilderTest;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

/**
 * @author renatosilva
 *
 */
public abstract class BuilderTestTheoryPath extends BuilderTest {
	
	/**
	 * @param name
	 */
	public BuilderTestTheoryPath(String name) {
		super(name);
	}
	
	protected ITheoryPathRoot createTheoryPath(String bareName) throws RodinDBException {
		final ITheoryPathRoot result = DatabaseUtilitiesTheoryPath.getTheoryPath(bareName, rodinProject);
		createRodinFileOf(result);
		return result;
	}
	
	protected void addDeployedTheory(ITheoryPathRoot root, IDeployedTheoryRoot dt1, IProgressMonitor monitor) throws RodinDBException {
		IAvailableTheoryProject availableTheoryProject = root.getAvailableTheoryProject(dt1.getRodinProject().getElementName());
		availableTheoryProject.create(null, monitor);
		availableTheoryProject.setTheoryProject(dt1.getRodinProject(), monitor);
		
		IAvailableTheory theory = availableTheoryProject.getTheory(dt1.getComponentName());
		theory.create(null, monitor);
		theory.setAvailableTheory(dt1, monitor);
	}
	
	protected IDeployedTheoryRoot createDeployedTheory(String bareName, IRodinProject rodinProject) throws RodinDBException {
		final IDeployedTheoryRoot result = DatabaseUtilities.getDeployedTheory(bareName, rodinProject);
		createRodinFileOf(result);
		return result;
	}

}
