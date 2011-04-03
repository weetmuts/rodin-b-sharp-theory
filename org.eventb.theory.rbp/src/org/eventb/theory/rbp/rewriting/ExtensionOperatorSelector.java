/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.rewriting;

import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IAccumulator;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.extension.IExtendedFormula;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.theory.core.AstUtilities;
import org.eventb.theory.rbp.internal.tactics.ExpansionTacticApplication;
import org.eventb.theory.rbp.reasoners.input.DefinitionExpansionInput;
import org.eventb.ui.prover.ITacticApplication;


/**
 * A selector of formula positions where an extended operator occurs.
 * 
 * <p> This selector identifies where a definition expansion is applicable.
 * 
 * @since 1.0
 * 
 * @author maamria
 *
 */
public class ExtensionOperatorSelector extends RbPAbstractApplicationInspector {

	public ExtensionOperatorSelector(Predicate predicate, boolean isGoal, FormulaFactory factory) {
		super(predicate, isGoal, factory);
	}

	@Override
	protected void select(Formula<?> form,
			IAccumulator<ITacticApplication> accum) {
		if (form instanceof IExtendedFormula){
			IExtendedFormula eform = (IExtendedFormula) form;
			IFormulaExtension extension = eform.getExtension();
			if(AstUtilities.isATheoryExtension(extension)){
				accum.add(new ExpansionTacticApplication(new 
						DefinitionExpansionInput(extension.getId(), 
								extension.getSyntaxSymbol(), 
								isGoal ? null : predicate, 
								accum.getCurrentPosition())));
			}
		}
	}

	
}
