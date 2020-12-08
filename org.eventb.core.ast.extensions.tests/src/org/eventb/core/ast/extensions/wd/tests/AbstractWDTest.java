/*******************************************************************************
 * Copyright (c) 2020 CentraleSupélec.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.ast.extensions.wd.tests;

import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.extensions.tests.BasicAstExtTest;
import org.junit.Test;

/**
 * Generic tests for well-definedness conditions computers.
 *
 * @author Guillaume Verdier
 */
public abstract class AbstractWDTest extends BasicAstExtTest {

	/**
	 * Computes the well-definedness condition.
	 *
	 * @param pred predicate on which the WD condition is computed
	 * @return the well-definedness condition
	 */
	abstract protected Predicate getWDLemma(Predicate pred);

	/**
	 * Sets up the test environment.
	 *
	 * Adds two types, {@code S} and {@code T}, to the typing environment and three
	 * variables : {@code x : S}, {@code y : T}, {@code n : ℤ}.
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		environment.addGivenSet("S");
		environment.addGivenSet("T");
		environment.addName("x", factory.makeGivenType("S"));
		environment.addName("y", factory.makeGivenType("T"));
		environment.addName("n", Z);
	}

	/**
	 * Checks the well-definedness condition.
	 *
	 * @param predicate predicate on which the WD condition is computed
	 * @param expectedWD the expected WD condition
	 */
	protected void assertWDLemma(String predicate, String expectedWD) throws Exception {
		Predicate pred = tcPredicate(predicate);
		Predicate expected = tcPredicate(expectedWD);
		Predicate actualWD = getWDLemma(pred);
		assertTrue("ill-formed WD", actualWD.isWellFormed());
		assertTrue("untyped WD", actualWD.isTypeChecked());
		assertEquals(expected, actualWD);
	}

	/**
	 * Shows that the WD predicate is true for the functional image through a
	 * built-in total function, and only in that case.
	 *
	 * Cf. RodinCore FR #357: Simpler WD for built-in total functions
	 */
	@Test
	public void testBuiltinTotalFunctionApplication() throws Exception {
		// Cases that get simplified
		assertWDLemma("prj1(x↦y) = x", "⊤");
		assertWDLemma("prj2(x↦y) = y", "⊤");
		assertWDLemma("id(x) = x", "⊤");
		assertWDLemma("succ(n) = 0", "⊤");
		assertWDLemma("pred(n) = 0", "⊤");
		assertWDLemma("id(prj1(x↦y)) = x", "⊤");

		// Cases that are not simplified
		assertWDLemma("((S×T) ◁ prj1)(x↦y) = x", //
				"x↦y ∈ dom((S×T) ◁ prj1) ∧ (S×T) ◁ prj1 ∈ S×T ⇸ S");
		assertWDLemma("(id;prj1)(x↦y) = x", //
				"x↦y ∈ dom(id;prj1) ∧ id;prj1 ∈ S×T ⇸ S");
		assertWDLemma("(succ;succ)(n) = 0", //
				"n ∈ dom(succ;succ) ∧ succ;succ ∈ ℤ ⇸ ℤ");
	}

}
