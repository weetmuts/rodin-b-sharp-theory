package org.eventb.theory.core.tests.sc.modules;

import org.eventb.core.EventBAttributes;
import org.eventb.core.sc.GraphProblem;
import org.eventb.theory.core.IAxiomaticDefinitionsBlock;
import org.eventb.theory.core.IAxiomaticTypeDefinition;
import org.eventb.theory.core.ISCAxiomaticDefinitionsBlock;
import org.eventb.theory.core.ITheoryRoot;
import org.eventb.theory.core.sc.TheoryGraphProblem;
import org.eventb.theory.core.tests.sc.BasicTheorySCTestWithThyConfig;
import org.junit.Test;

/**
 * 
 * @author maamria
 *
 */
public class TestAxiomaticTypeDefinitions extends BasicTheorySCTestWithThyConfig{

	@Test
	public void testAxiomaticTypeDefinitions_001_NoError() throws Exception{
		ITheoryRoot root = createTheory(THEORY_NAME);
		IAxiomaticDefinitionsBlock adb = addAxiomaticDefinitionsBlock(root, BLOCK_LABEL);
		addAxiomaticTypeDefinition(adb, "REAL");
		saveRodinFileOf(root);
		runBuilder();
		isAccurate(root.getSCTheoryRoot());
		ISCAxiomaticDefinitionsBlock scAdb = getAxiomaticDefinitionsBlock(root.getSCTheoryRoot(), BLOCK_LABEL);
		getSCAxiomaticTypeDefinition(scAdb, "REAL");
	}
	
	@Test
	public void testAxiomaticTypeDefinitions_002_NoIdent() throws Exception{
		ITheoryRoot root = createTheory(THEORY_NAME);
		IAxiomaticDefinitionsBlock adb = addAxiomaticDefinitionsBlock(root, BLOCK_LABEL);
		IAxiomaticTypeDefinition atd = addAxiomaticTypeDefinition(adb, "");
		saveRodinFileOf(root);
		runBuilder();
		isNotAccurate(root.getSCTheoryRoot());
		getAxiomaticDefinitionsBlock(root.getSCTheoryRoot(), BLOCK_LABEL);
		hasMarker(atd, EventBAttributes.IDENTIFIER_ATTRIBUTE, GraphProblem.IdentifierUndefError);
	}
	
	@Test
	public void testAxiomaticTypeDefinitions_003_IdentProb() throws Exception{
		ITheoryRoot root = createTheory(THEORY_NAME);
		addTypeParameters(root, "LP");
		IAxiomaticDefinitionsBlock adb = addAxiomaticDefinitionsBlock(root, BLOCK_LABEL);
		IAxiomaticTypeDefinition at1 = addAxiomaticTypeDefinition(adb, "finite");
		IAxiomaticTypeDefinition at2 = addAxiomaticTypeDefinition(adb, ";pooa");
		IAxiomaticTypeDefinition at3 = addAxiomaticTypeDefinition(adb, "LP");
		saveRodinFileOf(root);
		runBuilder();
		isNotAccurate(root.getSCTheoryRoot());
		getAxiomaticDefinitionsBlock(root.getSCTheoryRoot(), BLOCK_LABEL);
		hasMarker(at1, EventBAttributes.IDENTIFIER_ATTRIBUTE);
		hasMarker(at2, EventBAttributes.IDENTIFIER_ATTRIBUTE);
		hasMarker(at3, EventBAttributes.IDENTIFIER_ATTRIBUTE, TheoryGraphProblem.AxiomaticTypeNameAlreadyATypeParError, "LP");
	}
	
	@Test
	public void testAxiomaticTypeDefinitions_004_AugmentFF() throws Exception{
		ITheoryRoot root = createTheory(THEORY_NAME);
		IAxiomaticDefinitionsBlock adb = addAxiomaticDefinitionsBlock(root, BLOCK_LABEL);
		addAxiomaticTypeDefinition(adb, "REAL");
		addTheorem(root, THEOREM_LABEL, "REAL = REAL");
		saveRodinFileOf(root);
		runBuilder();
		isAccurate(root.getSCTheoryRoot());
	}
	
}
