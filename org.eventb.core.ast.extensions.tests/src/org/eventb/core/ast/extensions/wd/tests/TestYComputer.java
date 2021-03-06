/*******************************************************************************
 * Copyright (c) 2012, 2020 University of Southampton and others.
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
 * @author maamria
 *
 */
public class TestYComputer extends AbstractWDTest {

	/**
	 * Returns a well-definedness conditions computed with
	 * {@link WDComputer#getYLemma(org.eventb.core.ast.Formula)}.
	 */
	@Override
	protected Predicate getWDLemma(Predicate pred) {
		return WDComputer.getYLemma(pred);
	}

}