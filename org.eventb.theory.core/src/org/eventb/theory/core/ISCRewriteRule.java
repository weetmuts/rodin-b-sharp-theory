/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.core;

import org.eventb.core.IAccuracyElement;
import org.eventb.core.ILabeledElement;
import org.eventb.core.ITraceableElement;
import org.eventb.theory.core.plugin.TheoryPlugin;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * @author maamria
 * 
 */
public interface ISCRewriteRule extends ILabeledElement,
	ISCFormulaElement, IAutomaticElement, IInteractiveElement, ICompleteElement,
	IToolTipElement, IDescriptionElement, ITraceableElement, IAccuracyElement{

	IInternalElementType<ISCRewriteRule> ELEMENT_TYPE = RodinCore
			.getInternalElementType(TheoryPlugin.PLUGIN_ID + ".scRewriteRule");

	/**
	 * <p>
	 * Returns the right hand side sub-element with the given name.
	 * </p>
	 * <p>
	 * This is handle-only method.
	 * </p>
	 * 
	 * @param name
	 *            of the rhs
	 * @return the designated rhs
	 */
	ISCRewriteRuleRightHandSide getRuleRHS(String name);

	/**
	 * <p>
	 * Returns all right hand side sub-elements of the rule.
	 * </p>
	 * 
	 * @return all right hand side sub-elements of the rule
	 * @throws RodinDBException
	 */
	ISCRewriteRuleRightHandSide[] getRuleRHSs() throws RodinDBException;
}
