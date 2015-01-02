/*******************************************************************************
 * Copyright (c) 2015 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.ast.extensions.maths.tests;

import static org.eventb.core.ast.Formula.BTRUE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.extensions.maths.AstUtilities;
import org.junit.Test;

/**
 * Unit tests about {@link AstUtilities}.
 *
 * @author Laurent Voisin
 */
public class TestAstUtilities {

	/**
	 * Ensures that makeBTRUE raises a NPE when called with a null argument.
	 */
	@Test(expected = NullPointerException.class)
	public void makeBTRUE_nullArgument() {
		AstUtilities.makeBTRUE(null);
	}

	/**
	 * Ensures that makeBTRUE builds the right AST node.
	 */
	@Test
	public void makeBTRUE_success() {
		final FormulaFactory factory //
		= FormulaFactory.getInstance(FormulaFactory.getCond());
		final Predicate btrue = AstUtilities.makeBTRUE(factory);
		assertSame(factory, btrue.getFactory());
		assertEquals(BTRUE, btrue.getTag());
	}

}
