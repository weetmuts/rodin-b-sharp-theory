/*******************************************************************************
 * Copyright (c) 2020 CentraleSupélec.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.ast.extensions.wd.tests;

import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.extensions.wd.WDComputer;

/**
 * Tests well-definedness conditions computed with the D operator.
 *
 * Actual tests are inherited from {@link AbstractWDTest}.
 *
 * @author Guillaume Verdier
 */
public class TestDComputer extends AbstractWDTest {

	/**
	 * Returns a well-definedness conditions computed with
	 * {@link WDComputer#getDLemma(org.eventb.core.ast.Formula)}.
	 */
	@Override
	protected Predicate getWDLemma(Predicate pred) {
		return WDComputer.getDLemma(pred);
	}

}
