/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.pm.assoc;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.pm.IBinding;

/**
 * 
 * @author maamria
 *
 */
public class ACExpressionProblem extends ACProblem<Expression>{

	public ACExpressionProblem(int tag, Expression[] formulae, Expression[] patterns, FormulaFactory factory) {
		super(tag, formulae, patterns, factory);
	}

	@Override
	protected void putVariableMapping(IndexedFormula<Expression> var, IndexedFormula<Expression> indexedFormula, IBinding initialBinding) {
		initialBinding.putExpressionMapping((FreeIdentifier) var.getFormula(), indexedFormula.getFormula());
	}

}
