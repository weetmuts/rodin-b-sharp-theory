/*******************************************************************************
 * Copyright (c) 2021 CentraleSupélec.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core.tests.maths.extensions;

import static org.junit.Assert.assertSame;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.ILanguage;
import org.eventb.core.IPRProof;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.theory.core.ITheoryRoot;
import org.eventb.theory.core.tests.sc.BasicTheorySCTestWithThyConfig;
import org.junit.Test;

/**
 * Test the formula extension provider.
 *
 * @author Guillaume Verdier
 */
public class TestTheoryFormulaExtensionProvider extends BasicTheorySCTestWithThyConfig {

	/**
	 * Ensure that datatype type parameters are saved and restored in the right order.
	 */
	@Test
	public void test_datatypeParameters() throws CoreException {
		ITheoryRoot root = createTheory(THEORY_NAME);
		addTypeParameters(root, "A", "B");
		addDatatypeDefinition(root, "Datatype", makeSList("B", "A"), makeSList("constructor"),
				new String[][] { makeSList() }, new String[][] { makeSList() });
		saveRodinFileOf(root);
		runBuilder();
		IPRProof proof = root.getPRRoot().createChild(IPRProof.ELEMENT_TYPE, null, null);
		ILanguage lang = proof.createChild(ILanguage.ELEMENT_TYPE, null, null);
		FormulaFactory factory = root.getSCTheoryRoot().getFormulaFactory();
		lang.setFormulaFactory(factory, null);
		assertSame(factory, lang.getFormulaFactory(null));
	}

}
