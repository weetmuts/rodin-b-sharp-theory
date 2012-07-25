package org.eventb.theory.core.tests.sc.modules;

import org.eventb.core.EventBAttributes;
import org.eventb.theory.core.IProofRulesBlock;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.ITheoryRoot;
import org.eventb.theory.core.sc.TheoryGraphProblem;
import org.eventb.theory.core.sc.modules.ProofRulesBlockModule;
import org.eventb.theory.core.tests.sc.BasicTheorySCTestWithThyConfig;

/**
 * @see ProofRulesBlockModule
 * @author maamria
 * 
 */
public class TestProofRuleBlocks extends BasicTheorySCTestWithThyConfig {
	/**
	 * Missing label
	*/
	public void testProofRuleBlocks_001_NoLabel() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		IProofRulesBlock blk = root.createChild(IProofRulesBlock.ELEMENT_TYPE, null, null);
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scRoot = root.getSCTheoryRoot();
		isNotAccurate(scRoot);
		getProofRulesBlocks(scRoot);
		hasMarker(blk, EventBAttributes.LABEL_ATTRIBUTE);
	}
	
	/**
	 * conflict labels
	 */
	public void testProofRuleBlocks_003_LabelConf() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		IProofRulesBlock blk1 = addProofRulesBlock(root, BLOCK_LABEL);
		IProofRulesBlock blk2 = addProofRulesBlock(root, BLOCK_LABEL);
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scRoot = root.getSCTheoryRoot();
		isNotAccurate(scRoot);
		getProofRulesBlocks(scRoot);
		hasMarker(blk1, EventBAttributes.LABEL_ATTRIBUTE, TheoryGraphProblem.RulesBlockLabelProblemError, BLOCK_LABEL);
		hasMarker(blk2, EventBAttributes.LABEL_ATTRIBUTE, TheoryGraphProblem.RulesBlockLabelProblemError, BLOCK_LABEL);
	}

	/**
	 * No Error
	*/
	public void testProofRuleBlocks_002_NoError() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		addProofRulesBlock(root, BLOCK_LABEL);
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scRoot = root.getSCTheoryRoot();
		isAccurate(scRoot);
		getProofRulesBlocks(scRoot, BLOCK_LABEL);
	}

}
