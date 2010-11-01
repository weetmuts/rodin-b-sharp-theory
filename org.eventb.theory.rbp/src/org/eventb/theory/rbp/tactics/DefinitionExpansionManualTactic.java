/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.tactics;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.theory.rbp.internal.tactics.AbstractRewriteManualTactic;
import org.eventb.theory.rbp.rewriting.RbPAbstractApplicationInspector;
import org.eventb.theory.rbp.rewriting.ExtensionOperatorSelector;

/**
 * The manual tactic for expanding new operator definitions.
 * 
 * @since 1.0
 * 
 * @author maamria
 *
 */
public class DefinitionExpansionManualTactic extends AbstractRewriteManualTactic{

	@Override
	protected RbPAbstractApplicationInspector getSelector(Predicate predicate,
			boolean isGoal, FormulaFactory factory) {
		// TODO Auto-generated method stub
		return new ExtensionOperatorSelector(predicate, isGoal, factory);
	}
	
	

}
