package org.eventb.theory.core.tests.sc.modules;

import org.eventb.core.EventBAttributes;
import org.eventb.theory.core.IInferenceRule;
import org.eventb.theory.core.IProofRulesBlock;
import org.eventb.theory.core.IRewriteRule;
import org.eventb.theory.core.ITheorem;
import org.eventb.theory.core.ITheoryRoot;
import org.eventb.theory.core.IApplicabilityElement.RuleApplicability;
import org.eventb.theory.core.sc.TheoryGraphProblem;
import org.eventb.theory.core.tests.sc.BasicTheorySCTestWithThyConfig;

/**
 * 
 * This is triggered by a heavy change in SC of labelled elements, so this is to verify
 * that the modules work as intended.
 * 
 * TODO reconsider this for removal
 * 
 * @author maamria
 *
 */
public class TestIntegratedTheories extends BasicTheorySCTestWithThyConfig{

	/**
	 * No conflict errors between theorems, rule blocks and rewrite rules
	 */
	public void testIntegratedTheories_001_NoLabelConf_Thm_RBlock_Rew() throws Exception{
		ITheoryRoot root = createTheory(THEORY_NAME);
		ITheorem thm = addTheorem(root, BLOCK_LABEL, "1=1");
		IProofRulesBlock blk = addProofRulesBlock(root, BLOCK_LABEL);
		IRewriteRule rew = addRewriteRule(blk, BLOCK_LABEL, "1=1", true, RuleApplicability.AUTOMATIC,
				"desc", makeSList(RHS_LABEL), makeSList("⊤"), makeSList("⊤"));
		saveRodinFileOf(root);
		runBuilder();
		hasNotMarker(thm);
		hasNotMarker(blk);
		hasNotMarker(rew);
	}
	
	/**
	 * No conflict errors between theorems, rule blocks and inference rules
	 */
	public void testIntegratedTheories_002_NoLabelConf_Thm_RBlock_Inf() throws Exception{
		ITheoryRoot root = createTheory(THEORY_NAME);
		ITheorem thm = addTheorem(root, BLOCK_LABEL, "1=1");
		IProofRulesBlock blk = addProofRulesBlock(root, BLOCK_LABEL);
		IInferenceRule inf = addInferenceRule(blk, BLOCK_LABEL, RuleApplicability.AUTOMATIC, "desc", makeSList("1=1"), makeSList("2=2"), makeBList(true));
		saveRodinFileOf(root);
		runBuilder();
		hasNotMarker(thm);
		hasNotMarker(blk);
		hasNotMarker(inf);
	}
	
	/**
	 * Conflict error between theorems, rule blocks and inference rules
	 */
	public void testIntegratedTheories_003_LabelConf_Rew_Inf() throws Exception{
		ITheoryRoot root = createTheory(THEORY_NAME);
		IProofRulesBlock blk = addProofRulesBlock(root, BLOCK_LABEL);
		IInferenceRule inf = addInferenceRule(blk, BLOCK_LABEL, RuleApplicability.AUTOMATIC, "desc", makeSList("1=1"), makeSList("2=2"), makeBList(true));
		IRewriteRule rew = addRewriteRule(blk, BLOCK_LABEL, "1=1", true, RuleApplicability.AUTOMATIC,
				"desc", makeSList(RHS_LABEL), makeSList("⊤"), makeSList("⊤"));
		saveRodinFileOf(root);
		runBuilder();
		hasNotMarker(blk);
		hasMarker(rew, EventBAttributes.LABEL_ATTRIBUTE, TheoryGraphProblem.RewriteRuleLabelConflictError, BLOCK_LABEL);
		hasMarker(inf, EventBAttributes.LABEL_ATTRIBUTE, TheoryGraphProblem.InferenceRuleLabelConflictError, BLOCK_LABEL);
	}
}
