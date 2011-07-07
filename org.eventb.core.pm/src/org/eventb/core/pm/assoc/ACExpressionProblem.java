/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.pm.assoc;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.pm.AssociativeExpressionComplement;
import org.eventb.core.pm.IBinding;
import org.eventb.core.pm.basis.engine.MatchingUtilities;

/**
 * 
 * @author maamria
 *
 */
public class ACExpressionProblem extends ACProblem<Expression>{

	public ACExpressionProblem(int tag, Expression[] formulae, Expression[] patterns, IBinding existingBinding) {
		super(tag, formulae, patterns, existingBinding);
	}

	@Override
	protected void putVariableMapping(IndexedFormula<Expression> var, IndexedFormula<Expression> indexedFormula, IBinding initialBinding) {
		initialBinding.putExpressionMapping((FreeIdentifier) var.getFormula(), indexedFormula.getFormula());
	}

	@Override
	protected void addAssociativeComplement(List<IndexedFormula<Expression>> formulae, IBinding binding) {
		List<Expression> list = new ArrayList<Expression>();
		for (IndexedFormula<Expression> formula : formulae){
			list.add(formula.getFormula());
		}
		Expression comp = MatchingUtilities.makeAssociativeExpression(
					tag, binding.getFormulaFactory(), list.toArray(new Expression[list.size()]));
		binding.setAssociativeExpressionComplement(new AssociativeExpressionComplement(tag, null, comp));
	}
}
