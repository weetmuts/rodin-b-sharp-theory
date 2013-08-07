package org.eventb.theory.core.tests.sc.modules;

import static org.junit.Assert.assertTrue;

import org.eventb.core.EventBAttributes;
import org.eventb.core.sc.GraphProblem;
import org.eventb.theory.core.IApplicabilityElement.RuleApplicability;
import org.eventb.theory.core.IProofRulesBlock;
import org.eventb.theory.core.IRewriteRule;
import org.eventb.theory.core.IRewriteRuleRightHandSide;
import org.eventb.theory.core.ISCRewriteRule;
import org.eventb.theory.core.ISCTheoryRoot;
import org.eventb.theory.core.ITheoryRoot;
import org.eventb.theory.core.TheoryAttributes;
import org.eventb.theory.core.sc.TheoryGraphProblem;
import org.eventb.theory.core.sc.modules.RewriteRuleFilterModule;
import org.eventb.theory.core.sc.modules.RewriteRuleModule;
import org.eventb.theory.core.sc.modules.RewriteRuleRHSFilterModule;
import org.eventb.theory.core.sc.modules.RewriteRuleRHSModule;
import org.eventb.theory.core.tests.sc.BasicTheorySCTestWithThyConfig;
import org.junit.Test;

/**
 * 
 * @see {@link RewriteRuleModule}, {@link RewriteRuleFilterModule},
 *      {@link RewriteRuleRHSModule}, {@link RewriteRuleRHSFilterModule}
 * 
 * @author maamria
 * 
 */
public class TestRewriteRules extends BasicTheorySCTestWithThyConfig {

	/**
	 * No Error
	 */
	@Test
	public void testRewriteRule_001_NoError() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		addTypeParameters(root, "S");
		addRewriteRule(root, BLOCK_LABEL, REWRITE_LABEL, "S = S", true, RuleApplicability.AUTOMATIC_AND_INTERACTIVE,
				"Dummy Desc", makeSList(RHS_LABEL), makeSList("⊤"), makeSList("⊤"));
		saveRodinFileOf(root);
		runBuilder();

		ISCTheoryRoot scRoot = root.getSCTheoryRoot();
		isAccurate(scRoot);
		isAccurate(getRewriteRule(scRoot, BLOCK_LABEL, REWRITE_LABEL));
		isComplete(getRewriteRule(scRoot, BLOCK_LABEL, REWRITE_LABEL));
	}

	/**
	 * missing label
	 */
	@Test
	public void testRewriteRule_002_MissingLabel() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		IProofRulesBlock block = addProofRulesBlock(root, BLOCK_LABEL);
		IRewriteRule rew = block.createChild(IRewriteRule.ELEMENT_TYPE, null, null);
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scRoot = root.getSCTheoryRoot();
		isNotAccurate(scRoot);
		hasMarker(rew, EventBAttributes.LABEL_ATTRIBUTE, GraphProblem.LabelUndefError);
	}

	/**
	 * conflict labels
	 */
	@Test
	public void testRewriteRule_003_LabelConflict() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		addTypeParameters(root, "S");
		IRewriteRule rew1 = addRewriteRule(root, BLOCK_LABEL, REWRITE_LABEL, "S = S", true,
				RuleApplicability.AUTOMATIC_AND_INTERACTIVE, "Dummy Desc", makeSList(RHS_LABEL), makeSList("⊤"),
				makeSList("⊤"));
		IRewriteRule rew2 = addRewriteRule(root, BLOCK_LABEL + 1, REWRITE_LABEL, "1 = 1", true,
				RuleApplicability.AUTOMATIC_AND_INTERACTIVE, "Dummy Desc", makeSList(RHS_LABEL), makeSList("⊤"),
				makeSList("⊤"));
		saveRodinFileOf(root);
		runBuilder();

		ISCTheoryRoot scRoot = root.getSCTheoryRoot();
		isNotAccurate(scRoot);
		hasMarker(rew1, EventBAttributes.LABEL_ATTRIBUTE);
		hasMarker(rew2, EventBAttributes.LABEL_ATTRIBUTE);
	}

	/**
	 * missing formula
	 */
	@Test
	public void testRewriteRule_004_MissingLhsFormula() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		IProofRulesBlock block = addProofRulesBlock(root, BLOCK_LABEL);
		IRewriteRule rew = block.createChild(IRewriteRule.ELEMENT_TYPE, null, null);
		rew.setLabel(REWRITE_LABEL, null);
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scRoot = root.getSCTheoryRoot();
		isNotAccurate(scRoot);
		hasMarker(rew, TheoryAttributes.FORMULA_ATTRIBUTE, TheoryGraphProblem.MissingFormulaError);
	}

	/**
	 * problem with formula
	 */
	@Test
	public void testRewriteRule_006_EmptyStrFormula() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		IProofRulesBlock block = addProofRulesBlock(root, BLOCK_LABEL);
		IRewriteRule rew1 = addRewriteRule(block, REWRITE_LABEL, "", true, RuleApplicability.AUTOMATIC, "",
				makeSList(), makeSList(), makeSList());
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scRoot = root.getSCTheoryRoot();
		isNotAccurate(scRoot);
		hasMarker(rew1, TheoryAttributes.FORMULA_ATTRIBUTE);
	}

	/**
	 * issues with formula
	 */
	@Test
	public void testRewriteRule_007_UnparsableLhs() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		addTypeParameters(root, "S");
		IProofRulesBlock block = addProofRulesBlock(root, BLOCK_LABEL);
		addMetavariable(block, "a", "BOOL");
		addMetavariable(block, "b", "ℤ");
		// fails type check
		IRewriteRule rew1 = addRewriteRule(block, REWRITE_LABEL, "a=b", true, RuleApplicability.AUTOMATIC, "",
				makeSList(), makeSList(), makeSList());
		// fails parse
		IRewriteRule rew2 = addRewriteRule(block, REWRITE_LABEL + 1, "a=#~b", true, RuleApplicability.AUTOMATIC, "",
				makeSList(), makeSList(), makeSList());
		// c undeclared
		IRewriteRule rew3 = addRewriteRule(block, REWRITE_LABEL + 2, "a=c", true, RuleApplicability.AUTOMATIC, "",
				makeSList(), makeSList(), makeSList());
		// lhs is ident
		IRewriteRule rew4 = addRewriteRule(block, REWRITE_LABEL + 3, "a", true, RuleApplicability.AUTOMATIC, "",
				makeSList(), makeSList(), makeSList());
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scRoot = root.getSCTheoryRoot();
		isNotAccurate(scRoot);
		hasMarker(rew1, TheoryAttributes.FORMULA_ATTRIBUTE);
		hasMarker(rew2, TheoryAttributes.FORMULA_ATTRIBUTE);
		hasMarker(rew3, TheoryAttributes.FORMULA_ATTRIBUTE, GraphProblem.UndeclaredFreeIdentifierError, "c");
		hasMarker(rew4, TheoryAttributes.FORMULA_ATTRIBUTE, TheoryGraphProblem.LHSIsIdentErr);
	}

	/**
	 * wd-strict issues
	 */
	@Test
	public void testRewriteRule_008_WDStrictess() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		IProofRulesBlock block = addProofRulesBlock(root, BLOCK_LABEL);
		IRewriteRule rew1 = addRewriteRule(block, REWRITE_LABEL, "⊤ ∧ 1=1", true, RuleApplicability.AUTOMATIC, "",
				makeSList(), makeSList(), makeSList());
		IRewriteRule rew2 = addRewriteRule(block, REWRITE_LABEL + 1, "⊤ ∨ 1=1 ", true, RuleApplicability.AUTOMATIC, "",
				makeSList(), makeSList(), makeSList());
		IRewriteRule rew3 = addRewriteRule(block, REWRITE_LABEL + 2, "0≠0 ⇒ ⊥", true, RuleApplicability.AUTOMATIC, "",
				makeSList(), makeSList(), makeSList());
		IRewriteRule rew4 = addRewriteRule(block, REWRITE_LABEL + 3, "0≠0 ⇔ ⊥", true, RuleApplicability.AUTOMATIC, "",
				makeSList(), makeSList(), makeSList());
		IRewriteRule rew5 = addRewriteRule(block, REWRITE_LABEL + 4, "∀ x·x ∈ℕ ⇒ x+1∈ℕ", true,
				RuleApplicability.AUTOMATIC, "", makeSList(), makeSList(), makeSList());
		IRewriteRule rew6 = addRewriteRule(block, REWRITE_LABEL + 5, "∃ x· x÷2 = 4", true, RuleApplicability.AUTOMATIC,
				"", makeSList(), makeSList(), makeSList());

		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scRoot = root.getSCTheoryRoot();
		isNotAccurate(scRoot);
		hasMarker(rew1, TheoryAttributes.FORMULA_ATTRIBUTE, TheoryGraphProblem.LHS_IsNotWDStrict);
		hasMarker(rew2, TheoryAttributes.FORMULA_ATTRIBUTE, TheoryGraphProblem.LHS_IsNotWDStrict);
		hasMarker(rew3, TheoryAttributes.FORMULA_ATTRIBUTE, TheoryGraphProblem.LHS_IsNotWDStrict);
		hasMarker(rew4, TheoryAttributes.FORMULA_ATTRIBUTE, TheoryGraphProblem.LHS_IsNotWDStrict);
		hasMarker(rew5, TheoryAttributes.FORMULA_ATTRIBUTE, TheoryGraphProblem.LHS_IsNotWDStrict);
		hasMarker(rew6, TheoryAttributes.FORMULA_ATTRIBUTE, TheoryGraphProblem.LHS_IsNotWDStrict);
	}

	/**
	 * untypable lhs
	 */
	@Test
	public void testRewriteRule_028_UntypableLhs() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		addTypeParameters(root, makeSList("S"));
		IProofRulesBlock block = addProofRulesBlock(root, BLOCK_LABEL);
		addMetavariable(block, "b", "S");
		IRewriteRule rew1 = addRewriteRule(block, REWRITE_LABEL, "a=a", true, RuleApplicability.AUTOMATIC, "",
				makeSList(), makeSList(), makeSList());
		IRewriteRule rew2 = addRewriteRule(block, REWRITE_LABEL + 1, "a=1", true, RuleApplicability.AUTOMATIC, "",
				makeSList(), makeSList(), makeSList());
		IRewriteRule rew3 = addRewriteRule(block, REWRITE_LABEL + 2, "b∈{b}", true, RuleApplicability.AUTOMATIC, "desc",
				makeSList(RHS_LABEL), makeSList("⊤"), makeSList("⊤"));
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scRoot = root.getSCTheoryRoot();
		isNotAccurate(scRoot);
		hasMarker(rew1, TheoryAttributes.FORMULA_ATTRIBUTE);
		hasMarker(rew2, TheoryAttributes.FORMULA_ATTRIBUTE);
		hasNotMarker(rew3);
	}
	
	// to test the case where an undeclared variable appears only on the rhs of
	// a oftype
	@Test
	public void testRewriteRule_010_UndeclaredIdentInOfTypeInLhs() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		IProofRulesBlock block = addProofRulesBlock(root, BLOCK_LABEL);
		IRewriteRule rew1 = addRewriteRule(block, REWRITE_LABEL, "∅ ⦂ ℙ(b)", true, RuleApplicability.AUTOMATIC, "",
				makeSList(), makeSList(), makeSList());
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scRoot = root.getSCTheoryRoot();
		isNotAccurate(scRoot);
		hasMarker(rew1, TheoryAttributes.FORMULA_ATTRIBUTE, TheoryGraphProblem.NonTypeParOccurError, "b");
	}
	
	/**
	 * complete attr missing
	 */
	@Test
	public void testRewriteRule_011_MissingCompleteAttr() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		addTypeParameters(root, "S");
		IRewriteRule rew = addRewriteRule(root, BLOCK_LABEL, REWRITE_LABEL, "S = S", true,
				RuleApplicability.AUTOMATIC_AND_INTERACTIVE, "Dummy Desc", makeSList(RHS_LABEL), makeSList("⊤"),
				makeSList("⊤"));
		rew.removeAttribute(TheoryAttributes.COMPLETE_ATTRIBUTE, null);
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scRoot = root.getSCTheoryRoot();
		hasMarker(rew, TheoryAttributes.COMPLETE_ATTRIBUTE, TheoryGraphProblem.CompleteUndefWarning);
		// complete attr absence not major, so theory should be accurate still/
		// rule set to incomplete
		isNotComplete(getRewriteRule(scRoot, BLOCK_LABEL, REWRITE_LABEL));
		isAccurate(scRoot);

	}

	/**
	 * applicability attr missing
	 */
	@Test
	public void testRewriteRule_012_MissingApplAttr() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		addTypeParameters(root, "S");
		IRewriteRule rew = addRewriteRule(root, BLOCK_LABEL, REWRITE_LABEL, "S = S", true,
				RuleApplicability.AUTOMATIC_AND_INTERACTIVE, "Dummy Desc", makeSList(RHS_LABEL), makeSList("⊤"),
				makeSList("⊤"));
		rew.removeAttribute(TheoryAttributes.APPLICABILITY_ATTRIBUTE, null);
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scRoot = root.getSCTheoryRoot();
		hasMarker(rew, TheoryAttributes.APPLICABILITY_ATTRIBUTE, TheoryGraphProblem.ApplicabilityUndefError);
		isAccurate(scRoot);
		isInteractive(getRewriteRule(scRoot, BLOCK_LABEL, REWRITE_LABEL));
	}

	/**
	 * desc attr missing
	 */
	@Test
	public void testRewriteRule_013_MissingDescAttr() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		addTypeParameters(root, "S");
		IRewriteRule rew = addRewriteRule(root, BLOCK_LABEL, REWRITE_LABEL, "S = S", true,
				RuleApplicability.AUTOMATIC_AND_INTERACTIVE, "Dummy Desc", makeSList(RHS_LABEL), makeSList("⊤"),
				makeSList("⊤"));
		rew.removeAttribute(TheoryAttributes.DESC_ATTRIBUTE, null);
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scRoot = root.getSCTheoryRoot();
		hasMarker(rew, TheoryAttributes.DESC_ATTRIBUTE, TheoryGraphProblem.DescNotSupplied, REWRITE_LABEL);
		isAccurate(scRoot);
		ISCRewriteRule scRule = getRewriteRule(scRoot, BLOCK_LABEL, REWRITE_LABEL);
		isInteractive(scRule);
		isAutomatic(scRule);
		isAccurate(scRule);
		assertTrue(scRule.getDescription().equals(THEORY_NAME + "." + REWRITE_LABEL));
	}

	/**
	 * test no rhs's
	 */
	@Test
	public void testRewriteRule_014_NoRHSs() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		IRewriteRule rew = addRewriteRule(root, BLOCK_LABEL, REWRITE_LABEL, "1=2", true,
				RuleApplicability.AUTOMATIC_AND_INTERACTIVE, "Dummy Desc", makeSList(), makeSList(), makeSList());
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scRoot = root.getSCTheoryRoot();
		isAccurate(scRoot);
		ISCRewriteRule scRule = getRewriteRule(scRoot, BLOCK_LABEL, REWRITE_LABEL);
		isNotAccurate(scRule);
		hasMarker(rew, EventBAttributes.LABEL_ATTRIBUTE, TheoryGraphProblem.RuleNoRhsError, REWRITE_LABEL);
	}

	/**
	 * missing rhs label
	 */
	@Test
	public void testRewriteRule_015_MissingRHSLabel() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		IRewriteRule rew = addRewriteRule(root, BLOCK_LABEL, REWRITE_LABEL, "1=2", true,
				RuleApplicability.AUTOMATIC_AND_INTERACTIVE, "Dummy Desc", makeSList(), makeSList(), makeSList());
		IRewriteRuleRightHandSide rhs = addRuleRhs(rew, RHS_LABEL, "⊤", "⊥");
		rhs.removeAttribute(EventBAttributes.LABEL_ATTRIBUTE, null);
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scRoot = root.getSCTheoryRoot();
		ISCRewriteRule scRew = getRewriteRule(scRoot, BLOCK_LABEL, REWRITE_LABEL);
		hasMarker(rhs, EventBAttributes.LABEL_ATTRIBUTE, GraphProblem.LabelUndefError);
		isNotAccurate(scRew);
		isAccurate(scRoot);
	}

	/**
	 * conflict in labels
	 */
	@Test
	public void testRewriteRule_016_RHSLabelConflict() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		IRewriteRule rew = addRewriteRule(root, BLOCK_LABEL, REWRITE_LABEL, "1=2", true,
				RuleApplicability.AUTOMATIC_AND_INTERACTIVE, "Dummy Desc", makeSList(), makeSList(), makeSList());
		IRewriteRuleRightHandSide rhs = addRuleRhs(rew, RHS_LABEL, "⊤", "⊥");
		IRewriteRuleRightHandSide rhs2 = addRuleRhs(rew, RHS_LABEL, "⊤", "⊥");
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scRoot = root.getSCTheoryRoot();
		ISCRewriteRule scRew = getRewriteRule(scRoot, BLOCK_LABEL, REWRITE_LABEL);
		isNotAccurate(scRew);
		isAccurate(scRoot);
		hasMarker(rhs, EventBAttributes.LABEL_ATTRIBUTE, TheoryGraphProblem.RhsLabelConflictError, RHS_LABEL);
		hasMarker(rhs2, EventBAttributes.LABEL_ATTRIBUTE, TheoryGraphProblem.RhsLabelConflictError, RHS_LABEL);
	}

	/**
	 * problem with condition of rhs
	 */
	// condition attr absent
	@Test
	public void testRewriteRule_017_MissingRHSCondition() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		IRewriteRule rew = addRewriteRule(root, BLOCK_LABEL, REWRITE_LABEL, "1=2", true,
				RuleApplicability.AUTOMATIC_AND_INTERACTIVE, "Dummy Desc", makeSList(), makeSList(), makeSList());
		IRewriteRuleRightHandSide rhs = addRuleRhs(rew, RHS_LABEL, "⊤", "⊥");
		rhs.removeAttribute(EventBAttributes.PREDICATE_ATTRIBUTE, null);
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scRoot = root.getSCTheoryRoot();
		isAccurate(scRoot);
		ISCRewriteRule scRew = getRewriteRule(scRoot, BLOCK_LABEL, REWRITE_LABEL);
		isNotAccurate(scRew);
		hasMarker(rhs, EventBAttributes.PREDICATE_ATTRIBUTE, TheoryGraphProblem.CondUndefError);
	}

	/**
	 * condition unparsable
	 */
	@Test
	public void testRewriteRule_018_UnparsableRHSCondition() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		IRewriteRule rew = addRewriteRule(root, BLOCK_LABEL, REWRITE_LABEL, "1=2", true,
				RuleApplicability.AUTOMATIC_AND_INTERACTIVE, "Dummy Desc", makeSList(), makeSList(), makeSList());
		IRewriteRuleRightHandSide rhs = addRuleRhs(rew, RHS_LABEL, "a-⊤", "⊥");
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scRoot = root.getSCTheoryRoot();
		isAccurate(scRoot);
		ISCRewriteRule scRew = getRewriteRule(scRoot, BLOCK_LABEL, REWRITE_LABEL);
		isNotAccurate(scRew);
		hasMarker(rhs, EventBAttributes.PREDICATE_ATTRIBUTE);
	}

	/**
	 * some idents in condition are not defined/ cond cannot be typed
	 */
	@Test
	public void testRewriteRule_019_RHSCOnditionIdentNotDecl_or_CondCannotType() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		IRewriteRule rew = addRewriteRule(root, BLOCK_LABEL, REWRITE_LABEL, "1=2", true,
				RuleApplicability.AUTOMATIC_AND_INTERACTIVE, "Dummy Desc", makeSList(), makeSList(), makeSList());
		IRewriteRuleRightHandSide rhs = addRuleRhs(rew, RHS_LABEL, "a=a", "⊥");
		IRewriteRuleRightHandSide rhs1 = addRuleRhs(rew, RHS_LABEL+1, "ℕ ∈ ℕ", "⊥");
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scRoot = root.getSCTheoryRoot();
		isAccurate(scRoot);
		ISCRewriteRule scRew = getRewriteRule(scRoot, BLOCK_LABEL, REWRITE_LABEL);
		isNotAccurate(scRew);
		hasMarker(rhs, EventBAttributes.PREDICATE_ATTRIBUTE);
		hasMarker(rhs1, EventBAttributes.PREDICATE_ATTRIBUTE);
	}
	
	/**
	 * some condition identifiers do not occur in left hand side
	 */
	@Test
	public void testRewriteRule_020_CondHasIdentNotInLHS() throws Exception{
		ITheoryRoot root = createTheory(THEORY_NAME);
		IProofRulesBlock block = addProofRulesBlock(root, BLOCK_LABEL);
		addMetavariable(block, "a", "ℤ");
		addMetavariable(block, "b", "ℤ");
		addMetavariable(block, "c", "ℤ");
		IRewriteRule rew = addRewriteRule(block, REWRITE_LABEL, "a∗b", true, 
				RuleApplicability.AUTOMATIC, "", makeSList(), makeSList(), makeSList());
		
		IRewriteRuleRightHandSide rhs1 = addRuleRhs(rew, RHS_LABEL, "c=1", "c∗a∗b");
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scRoot = root.getSCTheoryRoot();
		isAccurate(scRoot);
		hasMarker(rhs1, EventBAttributes.PREDICATE_ATTRIBUTE, TheoryGraphProblem.CondIdentsNotSubsetOfLHSIdents, RHS_LABEL);
	}
	
	/**
	 * condition contains undeclared identifier
	 */
	@Test
	public void testRewriteRule_021_CondHasUndeclIdents() throws Exception{
		ITheoryRoot root = createTheory(THEORY_NAME);
		IProofRulesBlock block = addProofRulesBlock(root, BLOCK_LABEL);
		addMetavariable(block, "a", "ℤ");
		addMetavariable(block, "b", "ℤ");
		addMetavariable(block, "c", "ℤ");
		IRewriteRule rew = addRewriteRule(block, REWRITE_LABEL, "a∗b", true, 
				RuleApplicability.AUTOMATIC, "", makeSList(), makeSList(), makeSList());
		IRewriteRuleRightHandSide rhs1 = addRuleRhs(rew, RHS_LABEL+1, "d=1", "d∗a∗b");
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scRoot = root.getSCTheoryRoot();
		isAccurate(scRoot);
		hasMarker(rhs1, EventBAttributes.PREDICATE_ATTRIBUTE, GraphProblem.UndeclaredFreeIdentifierError, "d");
	}
	
	/**
	 * rhs unparsable
	 */
	@Test
	public void testRewriteRule_022_RHSFormulaUnparsable() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		IRewriteRule rew = addRewriteRule(root, BLOCK_LABEL, REWRITE_LABEL, "1=2", true,
				RuleApplicability.AUTOMATIC_AND_INTERACTIVE, "Dummy Desc", makeSList(), makeSList(), makeSList());
		IRewriteRuleRightHandSide rhs = addRuleRhs(rew, RHS_LABEL, "⊤", "a-⊥");
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scRoot = root.getSCTheoryRoot();
		isAccurate(scRoot);
		ISCRewriteRule scRew = getRewriteRule(scRoot, BLOCK_LABEL, REWRITE_LABEL);
		isNotAccurate(scRew);
		hasMarker(rhs, TheoryAttributes.FORMULA_ATTRIBUTE);
	}

	/**
	 * some idents in rhs are not defined/ rhs cannot be typed
	 */
	@Test
	public void testRewriteRule_023_RHSFormulaHasUndeclIdents_or_RHSFormulaCannotType() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		IRewriteRule rew = addRewriteRule(root, BLOCK_LABEL, REWRITE_LABEL, "1=2", true,
				RuleApplicability.AUTOMATIC_AND_INTERACTIVE, "Dummy Desc", makeSList(), makeSList(), makeSList());
		IRewriteRuleRightHandSide rhs = addRuleRhs(rew, RHS_LABEL, "⊤", "a=a");
		IRewriteRuleRightHandSide rhs1 = addRuleRhs(rew, RHS_LABEL+1, "⊤", "1=∅");
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scRoot = root.getSCTheoryRoot();
		isAccurate(scRoot);
		ISCRewriteRule scRew = getRewriteRule(scRoot, BLOCK_LABEL, REWRITE_LABEL);
		isNotAccurate(scRew);
		hasMarker(rhs, TheoryAttributes.FORMULA_ATTRIBUTE);
		hasMarker(rhs1, TheoryAttributes.FORMULA_ATTRIBUTE);
	}
	
	/**
	 * some rhs identifiers do not occur in left hand side
	 */
	@Test
	public void testRewriteRule_024_RHSHasIdentNotInLHS() throws Exception{
		ITheoryRoot root = createTheory(THEORY_NAME);
		IProofRulesBlock block = addProofRulesBlock(root, BLOCK_LABEL);
		addMetavariable(block, "a", "ℤ");
		addMetavariable(block, "b", "ℤ");
		addMetavariable(block, "c", "ℤ");
		IRewriteRule rew = addRewriteRule(block, REWRITE_LABEL, "a∗b", true, 
				RuleApplicability.AUTOMATIC, "", makeSList(), makeSList(), makeSList());
		
		IRewriteRuleRightHandSide rhs1 = addRuleRhs(rew, RHS_LABEL, "⊤", "(c÷c)∗a∗b");
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scRoot = root.getSCTheoryRoot();
		isAccurate(scRoot);
		hasMarker(rhs1, TheoryAttributes.FORMULA_ATTRIBUTE, TheoryGraphProblem.RHSIdentsNotSubsetOfLHSIdents, RHS_LABEL);
	}
	
	/**
	 * rhs contains undeclared identifier
	 */
	@Test
	public void testRewriteRule_025_RHSFormulaHasUndeclIdents() throws Exception{
		ITheoryRoot root = createTheory(THEORY_NAME);
		IProofRulesBlock block = addProofRulesBlock(root, BLOCK_LABEL);
		addMetavariable(block, "a", "ℤ");
		addMetavariable(block, "b", "ℤ");
		addMetavariable(block, "c", "ℤ");
		IRewriteRule rew = addRewriteRule(block, REWRITE_LABEL, "a∗b", true, 
				RuleApplicability.AUTOMATIC, "", makeSList(), makeSList(), makeSList());
		IRewriteRuleRightHandSide rhs1 = addRuleRhs(rew, RHS_LABEL+1, "⊤", "(d÷d)∗a∗b");
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scRoot = root.getSCTheoryRoot();
		isAccurate(scRoot);
		hasMarker(rhs1, TheoryAttributes.FORMULA_ATTRIBUTE, GraphProblem.UndeclaredFreeIdentifierError, "d");
	}
	
	/**
	 * rhs does not have same type as lhs
	 */
	@Test
	public void testRewriteRule_026_RHSandLHSTypeClash() throws Exception{
		ITheoryRoot root = createTheory(THEORY_NAME);
		IProofRulesBlock block = addProofRulesBlock(root, BLOCK_LABEL);
		addMetavariable(block, "a", "ℤ");
		addMetavariable(block, "b", "BOOL");
		IRewriteRule rew = addRewriteRule(block, REWRITE_LABEL, "a↦b", true, 
				RuleApplicability.AUTOMATIC, "", makeSList(), makeSList(), makeSList());
		IRewriteRuleRightHandSide rhs1 = addRuleRhs(rew, RHS_LABEL+1, "⊤", "b↦a");
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scRoot = root.getSCTheoryRoot();
		isAccurate(scRoot);
		hasMarker(rhs1, TheoryAttributes.FORMULA_ATTRIBUTE, TheoryGraphProblem.RuleTypeMismatchError,"ℤ×BOOL","BOOL×ℤ");
	}
	
	/**
	 * Syntactic class mismatch
	 */
	@Test
	public void testRewriteRule_027_RHSandLHSSynClassMismatch() throws Exception{
		ITheoryRoot root = createTheory(THEORY_NAME);
		IProofRulesBlock block = addProofRulesBlock(root, BLOCK_LABEL);
		addMetavariable(block, "a", "ℤ");
		addMetavariable(block, "b", "BOOL");
		IRewriteRule rew = addRewriteRule(block, REWRITE_LABEL, "a↦b", true, 
				RuleApplicability.AUTOMATIC, "", makeSList(), makeSList(), makeSList());
		IRewriteRuleRightHandSide rhs1 = addRuleRhs(rew, RHS_LABEL+1, "⊤", "a=a");
		saveRodinFileOf(root);
		runBuilder();
		ISCTheoryRoot scRoot = root.getSCTheoryRoot();
		isAccurate(scRoot);
		// we do not have a specific error here
		// the lhs is parsed by which point we know whether we should expect expression or predicates
		hasMarker(rhs1, TheoryAttributes.FORMULA_ATTRIBUTE);
	}
}
