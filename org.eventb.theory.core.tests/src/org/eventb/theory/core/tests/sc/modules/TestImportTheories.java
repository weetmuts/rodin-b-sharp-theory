package org.eventb.theory.core.tests.sc.modules;

import static org.eventb.core.ast.extension.IOperatorProperties.*;

import org.eventb.theory.core.IImportTheory;
import org.eventb.theory.core.ITheoryRoot;
import org.eventb.theory.core.TheoryAttributes;
import org.eventb.theory.core.sc.TheoryGraphProblem;
import org.eventb.theory.core.sc.modules.ImportTheoryModule;
import org.eventb.theory.core.tests.sc.BasicTheorySCTestWithThyConfig;
import org.junit.Test;

/**
 * @see ImportTheoryModule
 * @author maamria
 *
 */
public class TestImportTheories extends BasicTheorySCTestWithThyConfig{

	/**
	 * No error
	 */
	@Test
	public void testImportTheories_001_NoError() throws Exception{
		ITheoryRoot root = createTheory(THEORY_NAME);
		ITheoryRoot root1 = createTheory(THEORY_NAME+1);
		addImportTheory(root, root1.getComponentName());
		
		saveRodinFilesOf(root, root1);
		runBuilder();
		isAccurate(root.getSCTheoryRoot());
		importsTheories(root.getSCTheoryRoot(), makeSList(THEORY_NAME+1));
		containsMarkers(root, false);
	}
	
	/**
	 * Import attr missing
	 */
	
	@Test
	public void testImportTheories_002_ImportAttrMissing() throws Exception{
		ITheoryRoot root = createTheory(THEORY_NAME);
		IImportTheory t = root.createChild(IImportTheory.ELEMENT_TYPE, null, null);
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
		addImportTheory(root, THEORY_NAME + 1);
		saveRodinFilesOf(root);
		runBuilder();
		isNotAccurate(root.getSCTheoryRoot());
		importsTheories(root.getSCTheoryRoot());
		containsMarkers(root, true);
		hasMarker(root.getImportTheories()[0], TheoryAttributes.IMPORT_THEORY_ATTRIBUTE);
	}
	
	/**
	 * Direct redundancy 
	 */
	@Test
	public void testImportTheories_005_ImportDirectRedundancy() throws Exception{
		ITheoryRoot root = createTheory(THEORY_NAME);
		ITheoryRoot root1 = createTheory(THEORY_NAME+1);
		addImportTheory(root, root1.getComponentName());
		addImportTheory(root, root1.getComponentName());
		
		saveRodinFilesOf(root, root1);
		runBuilder();
		isNotAccurate(root.getSCTheoryRoot());
		importsTheories(root.getSCTheoryRoot(), makeSList(THEORY_NAME+1));
		containsMarkers(root, true);
		hasMarker(root.getImportTheories()[1], TheoryAttributes.IMPORT_THEORY_ATTRIBUTE);
	}
	
	/**
	 * Indirect redundancy
	 */
	@Test
	public void testImportTheories_006_IndirectRedundancy() throws Exception{
		ITheoryRoot root = createTheory(THEORY_NAME);
		ITheoryRoot root1 = createTheory(THEORY_NAME+1);
		ITheoryRoot root2 = createTheory(THEORY_NAME+2);
		
		addImportTheory(root1, THEORY_NAME+2);
		addImportTheory(root, THEORY_NAME+1);
		addImportTheory(root, THEORY_NAME+2);
		saveRodinFilesOf(root, root1, root2);
		runBuilder();
		isNotAccurate(root.getSCTheoryRoot());
		containsMarkers(root, true);
		hasMarker(root.getImportTheories()[1], TheoryAttributes.IMPORT_THEORY_ATTRIBUTE);
	}
	
	/**
	 * Math extensions conflict
	 */
	@Test
	public void testImportTheories_007_MathExtConflict() throws Exception{
		ITheoryRoot root = createTheory(THEORY_NAME);
		ITheoryRoot root1 = createTheory(THEORY_NAME+1);
		addOperatorDefinitionWithDirectDef(root1, "op", Notation.PREFIX , FormulaType.EXPRESSION, false, false,
				makeSList(), makeSList(), makeSList(), "1+1");
		ITheoryRoot root2 = createTheory(THEORY_NAME+2);
		addOperatorDefinitionWithDirectDef(root2, "op", Notation.PREFIX , FormulaType.EXPRESSION, false, false,
				makeSList(), makeSList(), makeSList(), "1+1");
		addImportTheory(root, THEORY_NAME+1);
		addImportTheory(root, THEORY_NAME+2);
		saveRodinFilesOf(root, root1, root2);
		runBuilder();
		isNotAccurate(root.getSCTheoryRoot());
		containsMarkers(root, true);
		hasMarker(root.getImportTheories()[0], TheoryAttributes.IMPORT_THEORY_ATTRIBUTE, 
				TheoryGraphProblem.ImportConflict, root1.getComponentName(), root2.getComponentName());
		hasMarker(root.getImportTheories()[1], TheoryAttributes.IMPORT_THEORY_ATTRIBUTE, 
				TheoryGraphProblem.ImportConflict, root2.getComponentName(), root1.getComponentName());
	}
}
