/*******************************************************************************
 * Copyright (c) 2020 CentraleSupélec.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.tests.rbp.tactics;

import org.eventb.theory.rbp.tactics.CachedRewritesManualTactic;
import org.eventb.theory.rbp.tactics.RewritesManualTactic;

/**
 * Tests the {@link CachedRewritesManualTactic} tactic provider.
 *
 * {@link CachedRewritesManualTactic} should have the exact same behavior as
 * {@link RewritesManualTactic}, but with faster results due to the cache.
 * Therefore, this test class reuses the tests defined for
 * {@link RewritesManualTactic}.
 *
 * @author Guillaume Verdier
 */
public class CachedRewritesManualTacticTests extends RewritesManualTacticTests {

	/**
	 * Initializes {@link AbstractTacticTests#tacticProvider} to the tested tactic
	 * provider and sets the number of test iterations to two, to make sure that
	 * results returned from the cache work like results obtained directly.
	 */
	public CachedRewritesManualTacticTests() {
		tacticProvider = new CachedRewritesManualTactic();
		numberIterations = 2;
	}

}
