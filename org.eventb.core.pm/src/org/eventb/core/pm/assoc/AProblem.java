/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.pm.assoc;

import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;

/**
 * 
 * @author maamria
 *
 */
public final class AProblem<F extends Formula<F>> extends AssociativityProblem<F>{

	public AProblem(int tag, F[] formulae, F[] patterns, FormulaFactory factory) {
		super(tag, formulae, patterns, factory);
	}
	
	@Override
	protected boolean explore(int patternIndex, MatchStack<F> matchStack){
		return false;
	}

}
