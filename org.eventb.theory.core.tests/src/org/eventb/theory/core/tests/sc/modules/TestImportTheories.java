package org.eventb.theory.core.tests.sc.modules;

import static org.eventb.theory.core.DatabaseUtilities.getTheory;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eventb.core.ast.extension.IOperatorProperties.FormulaType;
import org.eventb.core.ast.extension.IOperatorProperties.Notation;
import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.core.IImportTheory;
import org.eventb.theory.core.IImportTheoryProject;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.ITheoryRoot;
import org.eventb.theory.core.TheoryAttributes;
import org.eventb.theory.core.sc.modules.ImportTheoryModule;
import org.eventb.theory.core.tests.sc.BasicTheorySCTestWithThyConfig;
import org.junit.Test;

/**
 * @see ImportTheoryModule
 * @author maamria
 * @author asiehsalehi
 *
 */
public class TestImportTheories extends BasicTheorySCTestWithThyConfig{

	/**
	 * No error
	 */
	@Test
	public void testImportTheories_001_NoError() throws Exception{
		IProgressMonitor monitor = new NullProgressMonitor();
		
		ITheoryRoot root1 = createTheory(THEORY_NAME+1);
		ISCTheoryRoot scTheoryRoot1 = root1.getSCTheoryRoot();
		IDeployedTheoryRoot deployedTheoryRoot1 = root1.getDeployedTheoryRoot();
		
		saveRodinFileOf(root1);
		runBuilder();
		createDeployedTheory(scTheoryRoot1, monitor);
		
		ITheoryRoot root = createTheory(THEORY_NAME);
		
		addImportTheory(root, deployedTheoryRoot1);
		
		saveRodinFilesOf(root, root1);
		runBuilder();
		isAccurate(root.getSCTheoryRoot());
		importsTheories(root.getSCTheoryRoot(), root1.getDeployedTheoryRoot());
		containsMarkers(root, false);
	}
	
	/**
	 * Theory project attr missing
	 */
	
	@Test
	public void testImportTheories_002_TheoryProjectAttrMissing() throws Exception{
		ITheoryRoot root = createTheory(THEORY_NAME);
		final IImportTheoryProject impThyPrj = root.createChild(
				IImportTheoryProject.ELEMENT_TYPE, null, null);
		saveRodinFileOf(root);
		runBuilder();
		// FIXME isNotAccurate(root.getSCTheoryRoot());
		containsMarkers(root, true);
		containsMarkers(impThyPrj, true);
	}
	
	/**
	 * Import attr missing
	 */
	
	@Test
	public void testImportTheories_002_ImportAttrMissing() throws Exception{
		ITheoryRoot root = createTheory(THEORY_NAME);
		final IImportTheoryProject impThyPrj = root.createChild(
				IImportTheoryProject.ELEMENT_TYPE, null, null);
		impThyPrj.setTheoryProject(rodinProject, null);
		IImportTheory t = impThyPrj.createChild(IImportTheory.ELEMENT_TYPE, null, null);
		saveRodinFileOf(root);
		runBuilder();
		isNotAccurate(root.getSCTheoryRoot());
		containsMarkers(root, true);
		containsMarkers(t, true);
	}
	
	/**
	 * Target does not exist
	 */
	@Test
	public void testImportTheories_003_ImportTargetNotExist() throws Exception{
		ITheoryRoot root = createTheory(THEORY_NAME);
		final ITheoryRoot doesNotExistTheory = getTheory("DoesNotExistTheory", root.getRodinProject());
		final IImportTheory importClause = addImportTheory(root, doesNotExistTheory);
		saveRodinFilesOf(root);
		runBuilder();
		isNotAccurate(root.getSCTheoryRoot());
		importsTheories(root.getSCTheoryRoot());
		containsMarkers(root, true);
		hasMarker(importClause, TheoryAttributes.IMPORT_THEORY_ATTRIBUTE);
	}
	
	
	/**
	 * Math extensions conflict
	 */
	@Test
	public void testImportTheories_007_MathExtConflict() throws Exception{
		IProgressMonitor monitor = new NullProgressMonitor();
		
		ITheoryRoot root1 = createTheory(THEORY_NAME+1);
		ITheoryRoot root2 = createTheory(THEORY_NAME+2);
		
		addOperatorDefinitionWithDirectDef(root1, "op", Notation.PREFIX , FormulaType.EXPRESSION, false, false,
				makeSList(), makeSList(), makeSList(), "1+1");

		addOperatorDefinitionWithDirectDef(root2, "op", Notation.PREFIX , FormulaType.EXPRESSION, false, false,
				makeSList(), makeSList(), makeSList(), "1+1");
		
		ISCTheoryRoot scTheoryRoot1 = root1.getSCTheoryRoot();
//		IDeployedTheoryRoot deployedTheoryRoot1 = root1.getDeployedTheoryRoot();
		ISCTheoryRoot scTheoryRoot2 = root2.getSCTheoryRoot();
//		IDeployedTheoryRoot deployedTheoryRoot2 = root2.getDeployedTheoryRoot();
		
		saveRodinFileOf(root1);
		saveRodinFileOf(root2);
		runBuilder();
		createDeployedTheory(scTheoryRoot1, monitor);
		createDeployedTheory(scTheoryRoot2, monitor);
		
		ITheoryRoot root = createTheory(THEORY_NAME);
		
//		final IImportTheory importClause1 = addImportTheory(root, deployedTheoryRoot1);
//		final IImportTheory importClause2 = addImportTheory(root, deployedTheoryRoot2);
		saveRodinFilesOf(root, root1, root2);
//		FIXME runBuilder();
		
//      isNotAccurate(root.getSCTheoryRoot());
//		containsMarkers(root, true);
//		hasMarker(importClause1, TheoryAttributes.IMPORT_THEORY_ATTRIBUTE, 
//				TheoryGraphProblem.ImportConflict, root1.getComponentName(), root2.getComponentName());
//		hasMarker(importClause2, TheoryAttributes.IMPORT_THEORY_ATTRIBUTE, 
//				TheoryGraphProblem.ImportConflict, root2.getComponentName(), root1.getComponentName());
	}
}
