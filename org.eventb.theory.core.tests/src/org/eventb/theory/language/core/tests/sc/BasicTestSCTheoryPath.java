/**
 * 
 */
package org.eventb.theory.language.core.tests.sc;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.extension.IOperatorProperties.FormulaType;
import org.eventb.core.ast.extension.IOperatorProperties.Notation;
import org.eventb.theory.core.DatabaseUtilitiesTheoryPath;
import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.core.IDeploymentResult;
import org.eventb.theory.core.INewOperatorDefinition;
import org.eventb.theory.core.IRecursiveDefinitionCase;
import org.eventb.theory.core.IRecursiveOperatorDefinition;
import org.eventb.theory.core.ISCAvailableTheory;
import org.eventb.theory.core.ISCAvailableTheoryProject;
import org.eventb.theory.core.ISCTheoryPathRoot;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.ITheoryDeployer;
import org.eventb.theory.core.ITheoryPathRoot;
import org.eventb.theory.core.ITheoryRoot;
import org.eventb.theory.core.TheoryHierarchyHelper;
import org.eventb.theory.language.core.tests.BuilderTestTheoryPath;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

/**
 * @author renatosilva
 *
 */
public class BasicTestSCTheoryPath extends BuilderTestTheoryPath {
	
	public static final String THEORYPATH_NAME = "thyPath";

	@Override
	protected ITheoryPathRoot createTheoryPath(String bareName, IRodinProject rodinProject) throws RodinDBException {
		ITheoryPathRoot theory = super.createTheoryPath(bareName, rodinProject);
		theory.setConfiguration(DatabaseUtilitiesTheoryPath.THEORY_PATH_CONFIGURATION, null);
		return theory;
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
	
	/**
	 * Gets a deployed theory with a new operator definition
	 * @param monitor 
	 * @return IDeploymentResult
	 * @throws CoreException 
	 * @throws InterruptedException 
	 */
	protected ISCTheoryRoot createSCTheory(String operatorName, String theoryName, IRodinProject project,IProgressMonitor monitor) throws CoreException, InterruptedException{
		ITheoryRoot root = createTheory(theoryName,project);
		
		addTypeParameters(root, "T");
		addDatatypeDefinition(root, operatorName, makeSList("T"), makeSList("nil", "cons"), new String[][] { makeSList(),
				makeSList("head", "tail") }, new String[][] { makeSList(), makeSList("T", "List(T)") });
		INewOperatorDefinition opDef = addRawOperatorDefinition(root, "listSize", Notation.PREFIX,
				FormulaType.EXPRESSION, false, false, makeSList("l"), makeSList("List(T)"), makeSList());
		IRecursiveOperatorDefinition recDef = opDef.createChild(IRecursiveOperatorDefinition.ELEMENT_TYPE, null, monitor);
		recDef.setInductiveArgument("l", null);
		
		IRecursiveDefinitionCase recCase1 = recDef.createChild(IRecursiveDefinitionCase.ELEMENT_TYPE, null, monitor);
		recCase1.setExpressionString("cons(1, l0)", monitor);

		saveRodinFileOf(root);
		runBuilder(project);
		ISCTheoryRoot scTheoryRoot = root.getSCTheoryRoot();
		isAccurate(scTheoryRoot);
		
		return scTheoryRoot;
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
	
//	/**
//	 * Gets a deployed theory with a new operator definition
//	 * @param monitor 
//	 * @return IDeploymentResult
//	 * @throws CoreException 
//	 * @throws InterruptedException 
//	 */
//	protected IDeploymentResult createDeployedTheory1(String operatorName, String theoryName, IRodinProject project, ITheoryDeployer dep, IProgressMonitor monitor) throws CoreException, InterruptedException{
//		ITheoryRoot root = createTheory(theoryName);
//		
//		addTypeParameters(root, "T");
//		addDatatypeDefinition(root, operatorName, makeSList("T"), makeSList("nil", "cons"), new String[][] { makeSList(),
//			makeSList("head", "tail") }, new String[][] { makeSList(), makeSList("T", "List(T)") });
//		INewOperatorDefinition opDef = addRawOperatorDefinition(root, "listSize", Notation.PREFIX,
//				FormulaType.EXPRESSION, false, false, makeSList("l"), makeSList("List(T)"), makeSList());
//		IRecursiveOperatorDefinition recDef = opDef.createChild(IRecursiveOperatorDefinition.ELEMENT_TYPE, null, monitor);
//		recDef.setInductiveArgument("l", null);
//		
//		IRecursiveDefinitionCase recCase1 = recDef.createChild(IRecursiveDefinitionCase.ELEMENT_TYPE, null, monitor);
//		recCase1.setExpressionString("cons(1, l0)", monitor);
//		
//		saveRodinFileOf(root);
//		runBuilder();
//		ISCTheoryRoot scTheoryRoot = root.getSCTheoryRoot();
//		isAccurate(scTheoryRoot);
//		IDeploymentResult deploymentResult
//		
//		if(!root.hasDeployedVersion()){
//			Set<ISCTheoryRoot> set = new HashSet<ISCTheoryRoot>();
//			set.add(scTheoryRoot);
//			dep = TheoryHierarchyHelper.getDeployer(project, set);
//			dep.deploy(monitor);
//			while(dep.getDeploymentResult()==null){
//				Thread.sleep(1000);
//			}
//			IDeploymentResult deploymentResult = dep.getDeploymentResult();
//			if(deploymentResult.succeeded())
//				dt = root.getDeployedTheoryRoot();
//		}
//		
//		return deploymentResult;
//	}

}
