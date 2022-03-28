/*******************************************************************************
 * Copyright (c) 2022 Université de Lorraine.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.tests.sc.modules;

import org.eventb.core.ast.extension.IOperatorProperties.FormulaType;
import org.eventb.core.ast.extension.IOperatorProperties.Notation;
import org.eventb.core.sc.ParseProblem;
import org.eventb.theory.core.IAxiomaticDefinitionsBlock;
import org.eventb.theory.core.IAxiomaticOperatorDefinition;
import org.eventb.theory.core.ITheoryRoot;
import org.eventb.theory.core.TheoryAttributes;
import org.eventb.theory.core.sc.TheoryGraphProblem;
import org.eventb.theory.core.tests.sc.BasicTheorySCTestWithThyConfig;
import org.junit.Test;

/**
 * @author Guillaume Verdier
 */
public class TestAxiomaticOperators extends BasicTheorySCTestWithThyConfig {

	/**
	 * no error
	 */
	@Test
	public void testAxiomaticOperators_001_NoError() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		IAxiomaticDefinitionsBlock block = addAxiomaticDefinitionsBlock(root, BLOCK_LABEL);
		addAxiomaticOperatorDefinition(block, "op1", Notation.PREFIX, FormulaType.EXPRESSION, false, false,
				makeSList("x"), makeSList("ℤ"), "ℤ");
		addAxiomaticOperatorDefinition(block, "op2", Notation.PREFIX, FormulaType.PREDICATE, false, false,
				makeSList("x"), makeSList("ℤ"), null);
		saveRodinFileOf(root);
		runBuilder();
		isAccurate(root.getSCTheoryRoot());
	}

	/**
	 * invalid type (provided type does not exist)
	 */
	@Test
	public void testAxiomaticOperators_002_InvalidType() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		IAxiomaticDefinitionsBlock block = addAxiomaticDefinitionsBlock(root, BLOCK_LABEL);
		IAxiomaticOperatorDefinition opDef = addAxiomaticOperatorDefinition(block, "op1", Notation.PREFIX,
				FormulaType.EXPRESSION, false, false, makeSList("x"), makeSList("ℤ"), "Anything");
		saveRodinFileOf(root);
		runBuilder();
		isNotAccurate(root.getSCTheoryRoot());
		hasMarker(opDef, TheoryAttributes.TYPE_ATTRIBUTE, ParseProblem.TypeUnknownError);
	}

	/**
	 * invalid type (expression provided instead of type)
	 */
	@Test
	public void testAxiomaticOperators_003_InvalidType() throws Exception {
		ITheoryRoot root = createTheory(THEORY_NAME);
		IAxiomaticDefinitionsBlock block = addAxiomaticDefinitionsBlock(root, BLOCK_LABEL);
		IAxiomaticOperatorDefinition opDef = addAxiomaticOperatorDefinition(block, "op1", Notation.PREFIX,
				FormulaType.EXPRESSION, false, false, makeSList("x"), makeSList("ℤ"), "0");
		saveRodinFileOf(root);
		runBuilder();
		isNotAccurate(root.getSCTheoryRoot());
		hasMarker(opDef, TheoryAttributes.TYPE_ATTRIBUTE, TheoryGraphProblem.AxiomaticInvalidTypeError, "op1");
	}

}
