package org.eventb.theory.core.tests.sc.modules;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eventb.core.EventBAttributes;
import org.eventb.core.sc.GraphProblem;
import org.eventb.theory.core.IApplicabilityElement.RuleApplicability;
import org.eventb.theory.core.IGiven;
import org.eventb.theory.core.IInfer;
import org.eventb.theory.core.IInferenceRule;
import org.eventb.theory.core.IProofRulesBlock;
import org.eventb.theory.core.ISCGiven;
import org.eventb.theory.core.ISCInferenceRule;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.ITheoryRoot;
import org.eventb.theory.core.TheoryAttributes;
import org.eventb.theory.core.sc.TheoryGraphProblem;
import org.eventb.theory.core.sc.modules.InferenceGivenClauseModule;
import org.eventb.theory.core.sc.modules.InferenceInferClauseModule;
import org.eventb.theory.core.sc.modules.InferenceRuleFilterModule;
import org.eventb.theory.core.sc.modules.InferenceRuleModule;
import org.eventb.theory.core.tests.sc.BasicTheorySCTestWithThyConfig;
import org.junit.Test;

/**
 * @see {@link InferenceRuleModule}, {@link InferenceRuleFilterModule}
 * @see {@link InferenceGivenClauseModule}, {@link InferenceInferClauseModule}
 * @author maamria
 *
 */
public class TestInferenceRules extends BasicTheorySCTestWithThyConfig {

	/**
	 * No Error
	 */
	@Test
	public void testInferenceRules_001_NoError() throws Exception{
		ITheoryRoot root = createTheory(THEORY_NAME);
		IProofRulesBlock block = addProofRulesBlock(root, BLOCK_LABEL);
		addMetavariable(block, "a", "ℤ");
		addMetavariable(block, "b", "ℤ");
		addInferenceRule(block, INFERENCE_LABEL, RuleApplicability.AUTOMATIC,
				"", makeSList("a∗b=0"), makeSList("a = 0 ∨ b = 0"), makeBList(true));
		
		saveRodinFilesOf(root);
		runBuilder();
		ISCTheoryRoot scRoot = root.getSCTheoryRoot();
		isAccurate(scRoot);
		isAccurate(getInferenceRule(scRoot, BLOCK_LABEL, INFERENCE_LABEL));
	}
	
	/**
	 * Missing label
	 */
	@Test
	public void testInferenceRules_002_MissingLabel() throws Exception{
		ITheoryRoot root = createTheory(THEORY_NAME);
		IProofRulesBlock block = addProofRulesBlock(root, BLOCK_LABEL);
		IInferenceRule inf = addInferenceRule(block, INFERENCE_LABEL, RuleApplicability.AUTOMATIC,
				"", makeSList(), makeSList(), makeBList());
		inf.removeAttribute(EventBAttributes.LABEL_ATTRIBUTE, null);
		saveRodinFilesOf(root);
		runBuilder();
		ISCTheoryRoot scRoot = root.getSCTheoryRoot();
		hasMarker(inf, EventBAttributes.LABEL_ATTRIBUTE, GraphProblem.LabelUndefError);
		isNotAccurate(scRoot);
	}
	/**
	 * conflict in label
	 */
	@Test
	public void testInferenceRules_003_LabelConflict() throws Exception{
		ITheoryRoot root = createTheory(THEORY_NAME);
		IProofRulesBlock block = addProofRulesBlock(root, BLOCK_LABEL);
		IInferenceRule inf1 = addInferenceRule(block, INFERENCE_LABEL, RuleApplicability.AUTOMATIC,
				"inf1", makeSList(), makeSList(), makeBList());
		IInferenceRule inf2 = addInferenceRule(block, INFERENCE_LABEL, RuleApplicability.AUTOMATIC,
				"inf2", makeSList(), makeSList(), makeBList());
		saveRodinFilesOf(root);
		runBuilder();
		ISCTheoryRoot scRoot = root.getSCTheoryRoot();
		hasMarker(inf1, EventBAttributes.LABEL_ATTRIBUTE, TheoryGraphProblem.InferenceRuleLabelConflictError, INFERENCE_LABEL);
		hasMarker(inf2, EventBAttributes.LABEL_ATTRIBUTE, TheoryGraphProblem.InferenceRuleLabelConflictError, INFERENCE_LABEL);
		isNotAccurate(scRoot);
	}
	
	/**
	 * applicability attr missing
	 */
	@Test
	public void testInferenceRules_004_MissingApplAttr() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		IInferenceRule inf = addInferenceRule(root, BLOCK_LABEL, INFERENCE_LABEL,
				RuleApplicability.AUTOMATIC, "desc", makeSList("1=1"), makeSList("2=2"), makeBList(true));
		inf.removeAttribute(TheoryAttributes.APPLICABILITY_ATTRIBUTE, null);
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scRoot = root.getSCTheoryRoot();
		hasMarker(inf, TheoryAttributes.APPLICABILITY_ATTRIBUTE, TheoryGraphProblem.ApplicabilityUndefError);
		isAccurate(scRoot);
		isInteractive(getInferenceRule(scRoot, BLOCK_LABEL, INFERENCE_LABEL));
	}

	/**
	 * desc attr missing
	 */
	@Test
	public void testInferenceRules_005_MissingDescAttr() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		IInferenceRule inf = addInferenceRule(root, BLOCK_LABEL, INFERENCE_LABEL,
				RuleApplicability.AUTOMATIC_AND_INTERACTIVE, "desc", makeSList("1=1"), makeSList("2=2"), makeBList(true));
		inf.removeAttribute(TheoryAttributes.DESC_ATTRIBUTE, null);
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scRoot = root.getSCTheoryRoot();
		hasMarker(inf, TheoryAttributes.DESC_ATTRIBUTE, TheoryGraphProblem.DescNotSupplied, INFERENCE_LABEL);
		isAccurate(scRoot);
		ISCInferenceRule scRule = getInferenceRule(scRoot, BLOCK_LABEL, INFERENCE_LABEL);
		isInteractive(scRule);
		isAutomatic(scRule);
		isAccurate(scRule);
		assertTrue(scRule.getDescription().equals(THEORY_NAME + "." + INFERENCE_LABEL));
	}
	
	/**
	 * Given predicate missing
	 */
	@Test
	public void testInferenceRules_006_MissingGivenPredAttr() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		IInferenceRule inf = addInferenceRule(root, BLOCK_LABEL, INFERENCE_LABEL,
				RuleApplicability.AUTOMATIC, "desc", makeSList("1=1"), makeSList(), makeBList());
		IGiven g = inf.getGiven("g");
		g.create(null, null);
		saveRodinFileOf(root);
		runBuilder();
		hasMarker(g, EventBAttributes.PREDICATE_ATTRIBUTE, GraphProblem.PredicateUndefError);
		ISCTheoryRoot scRoot = root.getSCTheoryRoot();
		isNotAccurate(getInferenceRule(scRoot , BLOCK_LABEL, INFERENCE_LABEL));
	}
	
	/**
	 * Given predicate equals BTRUE
	 */
	@Test
	public void testInferenceRules_007_GivenPredBTRUE() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		IInferenceRule inf = addInferenceRule(root, BLOCK_LABEL, INFERENCE_LABEL,
				RuleApplicability.AUTOMATIC, "desc", makeSList("1=1"), makeSList(), makeBList());
		IGiven g = inf.getGiven("g");
		g.create(null, null);
		g.setPredicateString("⊤", null);
		saveRodinFileOf(root);
		runBuilder();
		hasMarker(g, EventBAttributes.PREDICATE_ATTRIBUTE, TheoryGraphProblem.InferenceGivenBTRUEPredWarn);
	}
	
	/**
	 * Given is hyp or not hyp
	 */
	@Test
	public void testInferenceRules_008_Hyp() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		IInferenceRule inf = addInferenceRule(root, BLOCK_LABEL, INFERENCE_LABEL,
				RuleApplicability.AUTOMATIC, "desc", makeSList("1=1"), makeSList(), makeBList());
		IGiven g1 = addGiven(inf, "1=1", true);
		IGiven g2 = addGiven(inf, "3=3", false);
		
		saveRodinFileOf(root);
		runBuilder();
		
		ISCTheoryRoot scRoot = root.getSCTheoryRoot();
		ISCInferenceRule scInf = getInferenceRule(scRoot, BLOCK_LABEL, INFERENCE_LABEL);
		ISCGiven scG1 = getGiven(scInf, g1);
		ISCGiven scG2 = getGiven(scInf, g2);
		assertTrue("given should be hyp but is not", scG1.isHyp());
		assertFalse("given should not be hyp but is", scG2.hasHypAttribute() && scG2.isHyp());
	}

	/**
	 * Syntactic issues with given
	 */
	@Test
	public void testInferenceRules_009_UnparsGiven() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		IInferenceRule inf = addInferenceRule(root, BLOCK_LABEL, INFERENCE_LABEL,
				RuleApplicability.AUTOMATIC, "desc", makeSList("1=1"), makeSList(), makeBList());
		IGiven g1 = addGiven(inf, "'#'a", true);
		
		saveRodinFileOf(root);
		runBuilder();
		
		ISCTheoryRoot scRoot = root.getSCTheoryRoot();
		ISCInferenceRule scInf = getInferenceRule(scRoot, BLOCK_LABEL, INFERENCE_LABEL);
		isNotAccurate(scInf);
		hasMarker(g1, EventBAttributes.PREDICATE_ATTRIBUTE);
	}
	
	/**
	 * Untypable given
	 */
	@Test
	public void testInferenceRules_009_UntypableGiven() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		addTypeParameters(root, makeSList("S"));
		IInferenceRule inf = addInferenceRule(root, BLOCK_LABEL, INFERENCE_LABEL,
				RuleApplicability.AUTOMATIC, "desc", makeSList("1=1"), makeSList(), makeBList());
		IGiven g1 = addGiven(inf,  "a=a", true);
		IGiven g2 = addGiven(inf,  "b=1", true);
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scRoot = root.getSCTheoryRoot();
		ISCInferenceRule scInf = getInferenceRule(scRoot , BLOCK_LABEL, INFERENCE_LABEL);
		isNotAccurate(scInf);
		hasMarker(g1, EventBAttributes.PREDICATE_ATTRIBUTE);
		hasMarker(g2, EventBAttributes.PREDICATE_ATTRIBUTE);
	}
	
	/**
	 * Infer predicate missing
	 */
	@Test
	public void testInferenceRules_010_MissingInferPredAttr() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		IInferenceRule inf = addInferenceRule(root, BLOCK_LABEL, INFERENCE_LABEL,
				RuleApplicability.AUTOMATIC, "desc", makeSList(), makeSList("1=1"), makeBList(true));
		IInfer i = addInfer(inf, "");
		saveRodinFileOf(root);
		runBuilder();
		hasMarker(i, EventBAttributes.PREDICATE_ATTRIBUTE, GraphProblem.PredicateUndefError);
		ISCTheoryRoot scRoot = root.getSCTheoryRoot();
		isNotAccurate(getInferenceRule(scRoot , BLOCK_LABEL, INFERENCE_LABEL));
	}
	
	/**
	 * Given predicate equals BTRUE
	 */
	@Test
	public void testInferenceRules_011_InferPredBTRUE() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		IInferenceRule inf = addInferenceRule(root, BLOCK_LABEL, INFERENCE_LABEL,
				RuleApplicability.AUTOMATIC, "desc", makeSList(), makeSList("1=1"), makeBList(true));
		IInfer i = addInfer(inf, "⊤");
		saveRodinFileOf(root);
		runBuilder();
		hasMarker(i, EventBAttributes.PREDICATE_ATTRIBUTE, TheoryGraphProblem.InferenceInferBTRUEPredErr);
	}
	
	/**
	 * Syntactic issues with infer
	 */
	@Test
	public void testInferenceRules_012_UnparsInfer() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		IProofRulesBlock block = addProofRulesBlock(root, BLOCK_LABEL);
		IInferenceRule inf1 = addInferenceRule(block, INFERENCE_LABEL,
				RuleApplicability.AUTOMATIC, "desc", makeSList(), makeSList("1=1"), makeBList(true));
		IInfer i1 = addInfer(inf1, "⊤+as");
		IInferenceRule inf2 = addInferenceRule(block, INFERENCE_LABEL+1,
				RuleApplicability.AUTOMATIC, "desc", makeSList(), makeSList("1=1"), makeBList(true));
		IInfer i2 = addInfer(inf2, "# a=1");
		saveRodinFileOf(root);
		runBuilder();
		
		ISCTheoryRoot scRoot = root.getSCTheoryRoot();
		ISCInferenceRule scInf1 = getInferenceRule(scRoot, BLOCK_LABEL, INFERENCE_LABEL);
		ISCInferenceRule scInf2 = getInferenceRule(scRoot, BLOCK_LABEL, INFERENCE_LABEL+1);
		isNotAccurate(scInf1);
		isNotAccurate(scInf2);
		hasMarker(i1, EventBAttributes.PREDICATE_ATTRIBUTE);
		hasMarker(i2, EventBAttributes.PREDICATE_ATTRIBUTE);
	}
	
	/**
	 * Untypable infer
	 */
	@Test
	public void testInferenceRules_013_UntypableInfer() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		addTypeParameters(root, makeSList("S"));
		IInferenceRule inf1 = addInferenceRule(root, BLOCK_LABEL, INFERENCE_LABEL,
				RuleApplicability.AUTOMATIC, "desc", makeSList(), makeSList("1=1"), makeBList(true));
		IInfer i1 = addInfer(inf1, "a=a");
		IInferenceRule inf2 = addInferenceRule(root, BLOCK_LABEL+1, INFERENCE_LABEL +1,
				RuleApplicability.AUTOMATIC, "desc", makeSList(), makeSList("1=1"), makeBList(true));
		IInfer i2 = addInfer(inf2, "b=1");
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scRoot = root.getSCTheoryRoot();
		ISCInferenceRule scInf1 = getInferenceRule(scRoot , BLOCK_LABEL, INFERENCE_LABEL);
		isNotAccurate(scInf1);
		ISCInferenceRule scInf2 = getInferenceRule(scRoot , BLOCK_LABEL+1, INFERENCE_LABEL+1);
		isNotAccurate(scInf2);
		hasMarker(i1, EventBAttributes.PREDICATE_ATTRIBUTE);
		hasMarker(i2, EventBAttributes.PREDICATE_ATTRIBUTE);
	}
	
	/**
	 * Forward applicability
	 */
	@Test
	public void testInferenceRules_014_ForwInfer() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		addTypeParameters(root, makeSList("S"));
		IProofRulesBlock block = addProofRulesBlock(root, BLOCK_LABEL);
		addMetavariable(block, "a", "ℙ(S)");
		addMetavariable(block, "b", "ℙ(S)");
		addMetavariable(block, "c", "ℙ(S)");
		addMetavariable(block, "x", "ℤ");
		addMetavariable(block, "y", "ℤ");
		IInferenceRule inf1 = addInferenceRule(block, INFERENCE_LABEL,
				RuleApplicability.AUTOMATIC, "desc", makeSList("a ⊆ c"), makeSList("a ⊆ b", "b ⊆ c"), makeBList(false, false));
		IInferenceRule inf2 = addInferenceRule(block, INFERENCE_LABEL + 1,
				RuleApplicability.AUTOMATIC, "desc", makeSList("x∗y = 0"), makeSList("x=0∨y=0"), makeBList(false));
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scRoot = root.getSCTheoryRoot();
		ISCInferenceRule scInf1 = getInferenceRule(scRoot , BLOCK_LABEL, INFERENCE_LABEL);
		ISCInferenceRule scInf2 = getInferenceRule(scRoot , BLOCK_LABEL, INFERENCE_LABEL+1);
		isAccurate(scInf1);
		hasNotMarker(inf1);
		isAccurate(scInf2);
		hasNotMarker(inf2);
		assertTrue("rule expected to be forward applicable but was not", scInf1.isSuitableForForwardReasoning());
		assertTrue("rule expected to be forward applicable but was not", scInf2.isSuitableForForwardReasoning());
	}
	/**
	 * Backward applicability
	 */
	@Test
	public void testInferenceRules_015_BackInfer() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		IProofRulesBlock block = addProofRulesBlock(root, BLOCK_LABEL);
		addMetavariable(block, "x", "ℤ");
		addMetavariable(block, "y", "ℤ");
		IInferenceRule inf1 = addInferenceRule(block, INFERENCE_LABEL,
				RuleApplicability.AUTOMATIC, "desc", makeSList("x∗y = 0"), makeSList("x=0"), makeBList(false));
		IInferenceRule inf2 = addInferenceRule(block, INFERENCE_LABEL + 1,
				RuleApplicability.AUTOMATIC, "desc", makeSList("x∗y = 0"), makeSList("x=0∨y=0"), makeBList(false));
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scRoot = root.getSCTheoryRoot();
		ISCInferenceRule scInf1 = getInferenceRule(scRoot , BLOCK_LABEL, INFERENCE_LABEL);
		ISCInferenceRule scInf2 = getInferenceRule(scRoot , BLOCK_LABEL, INFERENCE_LABEL+1);
		isAccurate(scInf1);
		hasNotMarker(inf1);
		isAccurate(scInf2);
		hasNotMarker(inf2);
		assertTrue("rule expected to be backward applicable but was not", scInf1.isSuitableForBackwardReasoning());
		assertTrue("rule expected to be backward applicable but was not", scInf2.isSuitableForBackwardReasoning());
	}
	/**
	 * Back/forward applicability
	 */
	@Test
	public void testInferenceRules_016_ForwBackInfer() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		IProofRulesBlock block = addProofRulesBlock(root, BLOCK_LABEL);
		addMetavariable(block, "x", "ℤ");
		addMetavariable(block, "y", "ℤ");
		IInferenceRule inf = addInferenceRule(block, INFERENCE_LABEL,
				RuleApplicability.AUTOMATIC, "desc", makeSList("x∗y = 0"), makeSList("x=0∨y=0"), makeBList(false));
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scRoot = root.getSCTheoryRoot();
		ISCInferenceRule scInf = getInferenceRule(scRoot , BLOCK_LABEL, INFERENCE_LABEL);
		isAccurate(scInf);
		hasNotMarker(inf);
		assertTrue("rule expected to be backward and forward applicable but was not", scInf.isSuitableForAllReasoning());
	}
}
