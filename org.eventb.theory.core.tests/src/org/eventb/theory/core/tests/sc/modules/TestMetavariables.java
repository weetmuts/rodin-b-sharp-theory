package org.eventb.theory.core.tests.sc.modules;

import org.eventb.core.EventBAttributes;
import org.eventb.core.sc.GraphProblem;
import org.eventb.theory.core.IMetavariable;
import org.eventb.theory.core.IProofRulesBlock;
import org.eventb.theory.core.ITheoryRoot;
import org.eventb.theory.core.TheoryAttributes;
import org.eventb.theory.core.sc.TheoryGraphProblem;
import org.eventb.theory.core.sc.modules.MetavariableFilterModule;
import org.eventb.theory.core.sc.modules.MetavariableModule;
import org.eventb.theory.core.tests.sc.BasicTheorySCTestWithThyConfig;
import org.junit.Test;

/**
 * @see MetavariableModule
 * @see MetavariableFilterModule
 * @author maamria
 *
 */
public class TestMetavariables extends BasicTheorySCTestWithThyConfig {

	/**
	 * No Error
	*/
	@Test
	public void testMetavariables_001_NoError() throws Exception{
		ITheoryRoot root = createTheory(THEORY_NAME);
		IProofRulesBlock block = addProofRulesBlock(root, BLOCK_LABEL);
		addMetavariable(block, "a", "BOOL");
		saveRodinFileOf(root);
		runBuilder();
		isAccurate(root.getSCTheoryRoot());
		getProofRulesBlock(root.getSCTheoryRoot(), BLOCK_LABEL);
	}
	
	/**
	 * Ident issues
	 */
	@Test
	public void testMetavariables_002_IdentIssue() throws Exception{
		ITheoryRoot root = createTheory(THEORY_NAME);
		IProofRulesBlock block = addProofRulesBlock(root, BLOCK_LABEL);
		IMetavariable v1 = addMetavariable(block, "+a", "BOOL");
		IMetavariable v2 = addMetavariable(block, "card", "BOOL");
		IMetavariable v3 = addMetavariable(block, "#", "BOOL");
		saveRodinFileOf(root);
		runBuilder();
		isNotAccurate(root.getSCTheoryRoot());
		getProofRulesBlock(root.getSCTheoryRoot(), BLOCK_LABEL);
		hasMarker(v1, EventBAttributes.IDENTIFIER_ATTRIBUTE);
		hasMarker(v2, EventBAttributes.IDENTIFIER_ATTRIBUTE);
		hasMarker(v3, EventBAttributes.IDENTIFIER_ATTRIBUTE);
	}
	
	/**
	 * Type issues
	 */
	@Test
	public void testMetavariables_003_TypeIssue() throws Exception{
		ITheoryRoot root = createTheory(THEORY_NAME);
		IProofRulesBlock block = addProofRulesBlock(root, BLOCK_LABEL);
		IMetavariable v1 = addMetavariable(block, "a", "S");
		IMetavariable v2 = addMetavariable(block, "b", "_012+2");
		saveRodinFileOf(root);
		runBuilder();
		isNotAccurate(root.getSCTheoryRoot());
		getProofRulesBlock(root.getSCTheoryRoot(), BLOCK_LABEL);
		hasMarker(v1, TheoryAttributes.TYPE_ATTRIBUTE);
		hasMarker(v2, TheoryAttributes.TYPE_ATTRIBUTE);
	}
	
	@Test
	public void testMetavariables_004_TypeIssue() throws Exception{
		ITheoryRoot root = createTheory(THEORY_NAME);
		IProofRulesBlock block = addProofRulesBlock(root, BLOCK_LABEL);
		IMetavariable v1 = block.createChild(IMetavariable.ELEMENT_TYPE, null, null);
		v1.setIdentifierString("v1", null);
		saveRodinFileOf(root);
		runBuilder();
		isNotAccurate(root.getSCTheoryRoot());
		getProofRulesBlock(root.getSCTheoryRoot(), BLOCK_LABEL);
		hasMarker(v1, TheoryAttributes.TYPE_ATTRIBUTE, TheoryGraphProblem.TypeAttrMissingError, "v1");
	}
	
	@Test
	public void testMetavariables_005_TypeIssue() throws Exception{
		ITheoryRoot root = createTheory(THEORY_NAME);
		IProofRulesBlock block = addProofRulesBlock(root, BLOCK_LABEL);
		IMetavariable v1 = addMetavariable(block, "a", "S");
		saveRodinFileOf(root);
		runBuilder();
		isNotAccurate(root.getSCTheoryRoot());
		getProofRulesBlock(root.getSCTheoryRoot(), BLOCK_LABEL);
		hasMarker(v1, TheoryAttributes.TYPE_ATTRIBUTE, GraphProblem.UndeclaredFreeIdentifierError, "S");
	}
	
	@Test
	public void testMetavariables_006_TypeIssue() throws Exception{
		ITheoryRoot root = createTheory(THEORY_NAME);
		IProofRulesBlock block = addProofRulesBlock(root, BLOCK_LABEL);
		addMetavariable(block, "a", "BOOL");
		IMetavariable v1 = addMetavariable(block, "b", "a");
		saveRodinFileOf(root);
		runBuilder();
		isNotAccurate(root.getSCTheoryRoot());
		getProofRulesBlock(root.getSCTheoryRoot(), BLOCK_LABEL);
		hasMarker(v1, TheoryAttributes.TYPE_ATTRIBUTE, TheoryGraphProblem.IdentIsNotTypeParError, "a");
	}
}
