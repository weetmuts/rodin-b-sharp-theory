/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.rulebase;

import org.eventb.core.IEventBRoot;
import org.eventb.core.ast.FormulaFactory;

/**
 * Common protocol for proof obligation contextual information.
 * <p> Instances of this interface should only be created inside a tactic.
 * @author maamria
 * @since 1.0
 *
 */
public interface IPOContext {
	
	/**
	 * Returns the parent root corresponding to the proof obligation.
	 * @return the parent root
	 */
	public IEventBRoot getParentRoot();
	
	/**
	 * Returns whether the proof obligation is related to a theory component.
	 * @return whether the proof obligation is related to a theory component
	 */
	public boolean isTheoryRelated();
	
	/**
	 * Returns whether the proof obligation corresponds to a component in the <code>MathExtensions</code> project.
	 * @return  whether the proof obligation corresponds to a component in the <code>MathExtensions</code> project
	 */
	public boolean inMathExtensions();
	
	/**
	 * Returns the formula factory suitable for this context.
	 * @return the suitable formula factory
	 */
	public FormulaFactory getFormulaFactory();
	
}
