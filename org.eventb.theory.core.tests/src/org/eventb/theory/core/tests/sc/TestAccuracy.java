package org.eventb.theory.core.tests.sc;

import static org.eventb.theory.core.DatabaseUtilities.getTheory;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eventb.theory.core.IApplicabilityElement.RuleApplicability;
import org.eventb.theory.core.IDeployedTheoryRoot;
import org.eventb.theory.core.IProofRulesBlock;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.ITheoryRoot;
import org.junit.Test;

/**
 * 
 * @author maamria
 * @author asiehsalehi
 * 
 */
public class TestAccuracy extends BasicTheorySCTestWithThyConfig {

	/**
	 * erroneous type par should make theory inaccurate
	 */
	@Test
	public void testAcc_001_ErroneousType() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		addTypeParameters(root, makeSList("finite"));
		saveRodinFileOf(root);
		runBuilder();
		isNotAccurate(root.getSCTheoryRoot());
	}

	@Test
	public void testAcc_002_ErroneousType() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		addTypeParameters(root, makeSList("1_qw"));
		saveRodinFileOf(root);
		runBuilder();
		isNotAccurate(root.getSCTheoryRoot());
	}

	/**
	 * erroneous import should make theory inaccurate
	 */
	@Test
	public void testAcc_003_ErroneousImport() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		final ITheoryRoot doesNotExistTheory = getTheory("DoesNotExistTheory", root.getRodinProject());
		addImportTheory(root, doesNotExistTheory);
		saveRodinFileOf(root);
		runBuilder();
		isNotAccurate(root.getSCTheoryRoot());
	}

	@Test
	public void testAcc_004_ErroneousImport() throws Exception {
		IProgressMonitor monitor = new NullProgressMonitor();
		
		ITheoryRoot anotherRoot = createTheory("anotherThy");
		ISCTheoryRoot scTheoryAnotherRoot = anotherRoot.getSCTheoryRoot();
		IDeployedTheoryRoot deployedTheoryAnotherRoot = anotherRoot.getDeployedTheoryRoot();
		
		saveRodinFileOf(anotherRoot);
		runBuilder();
		createDeployedTheory(scTheoryAnotherRoot, monitor);
		
		ITheoryRoot root = createTheory(THEORY_NAME);
		
		addImportTheory(root, deployedTheoryAnotherRoot);
		saveRodinFileOf(root);
		saveRodinFileOf(anotherRoot);
		runBuilder();
		isAccurate(root.getSCTheoryRoot()); 
	}

	/**
	 * erroneous theorem should make theory inaccurate
	 */
	@Test
	public void testAcc_005_ErroneousTheorem() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		addTheorem(root, THEOREM_LABEL, "asdas asdasd");
		saveRodinFileOf(root);
		runBuilder();
		isNotAccurate(root.getSCTheoryRoot());
	}

	@Test
	public void testAcc_006_ErroneousTheorem() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		addTheorem(root, "", "2=2");
		saveRodinFileOf(root);
		runBuilder();
		isNotAccurate(root.getSCTheoryRoot());
	}

	/**
	 * rules block without label make theory inaccurate
	 */
	@Test
	public void testAcc_007_ErroneousBlock() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		root.createChild(IProofRulesBlock.ELEMENT_TYPE, null, null);
		saveRodinFileOf(root);
		runBuilder();
		// theory not accurate
		isNotAccurate(root.getSCTheoryRoot());
	}

	@Test
	public void testAcc_008_BlockNoError() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		addProofRulesBlock(root, BLOCK_LABEL);
		saveRodinFileOf(root);
		runBuilder();
		isAccurate(root.getSCTheoryRoot());
	}

	/**
	 * rewrite rules and accuracy
	 */
	@Test
	public void testAcc_009_RewNoError() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		IProofRulesBlock block = addProofRulesBlock(root, BLOCK_LABEL);
		addRewriteRule(block, REWRITE_LABEL, "1=1", true, RuleApplicability.AUTOMATIC_AND_INTERACTIVE, RHS_LABEL,
				makeSList("1"), makeSList("⊤"), makeSList("⊤"));
		saveRodinFileOf(root);
		runBuilder();
		isAccurate(root.getSCTheoryRoot());
		isAccurate(getRewriteRule(root.getSCTheoryRoot(), BLOCK_LABEL, REWRITE_LABEL));
	}

	// missing formula should make theory inaccurate
	@Test
	public void testAcc_010_ErroneousRew() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		IProofRulesBlock block = addProofRulesBlock(root, BLOCK_LABEL);
		addRewriteRule(block, REWRITE_LABEL, "", true, RuleApplicability.AUTOMATIC_AND_INTERACTIVE, RHS_LABEL,
				makeSList("1"), makeSList("⊤"), makeSList("⊤"));
		saveRodinFileOf(root);
		runBuilder();
		isNotAccurate(root.getSCTheoryRoot());
	}

	// problem with rhs should make rewrite inaccurate but theory accuracy not affected
	@Test
	public void testAcc_011_ErroneousRew() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		IProofRulesBlock block = addProofRulesBlock(root, BLOCK_LABEL);
		addRewriteRule(block, REWRITE_LABEL, "1=1", true, RuleApplicability.AUTOMATIC_AND_INTERACTIVE, RHS_LABEL,
				makeSList(""), makeSList("⊤"), makeSList("⊤"));
		saveRodinFileOf(root);
		runBuilder();
		// theory is accurate
		isAccurate(root.getSCTheoryRoot());
		// rewrite rule is not accurate
		isNotAccurate(getRewriteRule(root.getSCTheoryRoot(), BLOCK_LABEL, REWRITE_LABEL));
	}

	/**
	 * Inference rules and accuracy
	 */
	// problem with infer/given clause should make inference inaccurate
	@Test
	public void testAcc_012_ErroneousInference() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		IProofRulesBlock block = addProofRulesBlock(root, BLOCK_LABEL);
		// no infer
		addInferenceRule(block, INFERENCE_LABEL, RuleApplicability.AUTOMATIC, "inf", makeSList(""), makeSList("1=1"), makeBList(true));
		saveRodinFileOf(root);
		runBuilder();
		// theory still accurate
		// FIXME isAccurate(root.getSCTheoryRoot());
		// I do not see why theory is accurate
		isNotAccurate(root.getSCTheoryRoot());
		// but inference rule is not
		isNotAccurate(getInferenceRule(root.getSCTheoryRoot(), BLOCK_LABEL, INFERENCE_LABEL));
	}

	@Test
	public void testAcc_013_InferenceNoError() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		IProofRulesBlock block = addProofRulesBlock(root, BLOCK_LABEL);
		addInferenceRule(block, INFERENCE_LABEL, RuleApplicability.AUTOMATIC, "inf", makeSList("2=0"), makeSList("⊥"), makeBList(true));
		saveRodinFileOf(root);
		runBuilder();
		isAccurate(root.getSCTheoryRoot());
		isAccurate(getInferenceRule(root.getSCTheoryRoot(), BLOCK_LABEL, INFERENCE_LABEL));
	}

	@Test
	public void testAcc_014_ErroneousInference() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		IProofRulesBlock block = addProofRulesBlock(root, BLOCK_LABEL);
		addInferenceRule(block, INFERENCE_LABEL, RuleApplicability.AUTOMATIC, "inf", makeSList("⊤"), makeSList("⊥"), makeBList(true));
		saveRodinFileOf(root);
		runBuilder();
		// theory still accurate
		// FIXME isAccurate(root.getSCTheoryRoot());
		// I do not see why theory is accurate
		isNotAccurate(root.getSCTheoryRoot());
		// inference not accurate
		isNotAccurate(getInferenceRule(root.getSCTheoryRoot(), BLOCK_LABEL, INFERENCE_LABEL));
	}
	// problem with inference itself should make theory inaccurate
	@Test
	public void testAcc_018_ErroneousInference() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		IProofRulesBlock block = addProofRulesBlock(root, BLOCK_LABEL);
		addInferenceRule(block, "", RuleApplicability.AUTOMATIC, "inf", makeSList("2=0"), makeSList("⊥"), makeBList(true));
		saveRodinFileOf(root);
		runBuilder();
		// theory inaccurate
		isNotAccurate(root.getSCTheoryRoot());
	}

	/**
	 * Datatypes and accuracy
	 */

	@Test
	public void testAcc_015_ErroneousDatatype() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		addDatatypeDefinition(root, DATATYPE_NAME, makeSList(), makeSList(CONS_NAME), 
				new String[][]{makeSList(DEST_NAME)}, new String[][]{makeSList("ℕ")});
		saveRodinFileOf(root);
		runBuilder();
		// theory accurate
		// FIXME isAccurate(root.getSCTheoryRoot());
		// I do not see why theory is accurate
		isNotAccurate(root.getSCTheoryRoot());
		// dt has error
		hasError(getDatatype(root.getSCTheoryRoot(), DATATYPE_NAME));
		
	}

	@Test
	public void testAcc_016_DatatypeNoError() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		addDatatypeDefinition(root, DATATYPE_NAME, makeSList(), makeSList(CONS_NAME), 
				new String[][]{makeSList(DEST_NAME)}, new String[][]{makeSList("ℤ")});
		saveRodinFileOf(root);
		runBuilder();
		isAccurate(root.getSCTheoryRoot());
		doesNotHaveError(getDatatype(root.getSCTheoryRoot(), DATATYPE_NAME));
	}
	// no element cons makes dt has error but theory accuracy unaffected
	@Test
	public void testAcc_017_ErroneousDatatype() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		addDatatypeDefinition(root, DATATYPE_NAME, makeSList(), makeSList(), 
				new String[][]{}, new String[][]{});
		saveRodinFileOf(root);
		runBuilder();
		// theory accurate
		// FIXME isAccurate(root.getSCTheoryRoot());
		// I do not see why theory is accurate
		isNotAccurate(root.getSCTheoryRoot());
		// dt has error
		hasError(getDatatype(root.getSCTheoryRoot(), DATATYPE_NAME));
	}
	
	// problem with dt name should make theory inaccurate
	@Test
	public void testAcc_019_ErroneousDatatype() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		addDatatypeDefinition(root, "", makeSList(), makeSList(), 
				new String[][]{}, new String[][]{});
		saveRodinFileOf(root);
		runBuilder();
		// theory inaccurate
		isNotAccurate(root.getSCTheoryRoot());
	}
	
	@Test
	public void testAcc_020_AxmBlockConflict() throws Exception{
		ITheoryRoot root = createTheory(THEORY_NAME);
		addAxiomaticDefinitionsBlock(root, BLOCK_LABEL);
		addAxiomaticDefinitionsBlock(root, BLOCK_LABEL);
		saveRodinFileOf(root);
		runBuilder();
		// theory inaccurate
		isNotAccurate(root.getSCTheoryRoot());
		
		
	}
}
