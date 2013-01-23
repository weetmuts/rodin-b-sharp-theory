package org.eventb.theory.core.tests.sc.modules;

import org.eventb.core.EventBAttributes;
import org.eventb.theory.core.IAxiomaticDefinitionsBlock;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.ITheoryRoot;
import org.eventb.theory.core.sc.TheoryGraphProblem;
import org.eventb.theory.core.tests.sc.BasicTheorySCTestWithThyConfig;

/**
 * 
 * @author maamria
 *
 */
public class TestAxiomaticBlocks extends BasicTheorySCTestWithThyConfig{

	/**
	 * no error
	 */
	public void testAxiomaticBlocks_001_NoError() throws Exception{
		ITheoryRoot root = createTheory(THEORY_NAME);
		addAxiomaticDefinitionsBlock(root, BLOCK_LABEL);
		addAxiomaticDefinitionsBlock(root, BLOCK_LABEL+1);
		saveRodinFileOf(root);
		runBuilder();
		isAccurate(root.getSCTheoryRoot());
		getAxiomaticDefinitionsBlock(root.getSCTheoryRoot(), BLOCK_LABEL);
		getAxiomaticDefinitionsBlock(root.getSCTheoryRoot(), BLOCK_LABEL+1);
	}
	
	/**
	 * Missing label
	*/
	public void testAxiomaticBlocks_002_NoLabel() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		IAxiomaticDefinitionsBlock blk = root.createChild(IAxiomaticDefinitionsBlock.ELEMENT_TYPE, null, null);
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scRoot = root.getSCTheoryRoot();
		isNotAccurate(scRoot);
		hasMarker(blk, EventBAttributes.LABEL_ATTRIBUTE);
	}
	
	/**
	 * conflict labels
	 */
	public void testAxiomaticBlocks_003_LabelConf() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		IAxiomaticDefinitionsBlock blk1 = addAxiomaticDefinitionsBlock(root, BLOCK_LABEL);
		IAxiomaticDefinitionsBlock blk2 = addAxiomaticDefinitionsBlock(root, BLOCK_LABEL);
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scRoot = root.getSCTheoryRoot();
		isNotAccurate(scRoot);
		getProofRulesBlocks(scRoot);
		hasMarker(blk1, EventBAttributes.LABEL_ATTRIBUTE, TheoryGraphProblem.AxiomaticBlockLabelProblemError, BLOCK_LABEL);
		hasMarker(blk2, EventBAttributes.LABEL_ATTRIBUTE, TheoryGraphProblem.AxiomaticBlockLabelProblemError, BLOCK_LABEL);
	}
	
}
