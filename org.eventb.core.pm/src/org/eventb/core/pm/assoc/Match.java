/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.pm.assoc;

import org.eventb.core.ast.Formula;
import org.eventb.core.pm.basis.IBinding;

/**
 * 
 * @author maamria
 *
 */
public class Match<F extends Formula<F>> {

	private IndexedFormula<F> indexedFormula;
	private IndexedFormula<F> indexedPattern;
	private IBinding binding;
	
	/**
	 * Creates a match between the formula and the patterns. The supplied binding, when applied, must match formula to the pattern.
	 * @param indexedFormula the indexed formula
	 * @param indexedPattern the indexed pattern
	 * @param binding the binding
	 */
	public Match(IndexedFormula<F> indexedFormula, IndexedFormula<F> indexedPattern, IBinding binding){
		this.indexedFormula = indexedFormula;
		this.indexedPattern = indexedPattern;
		this.binding = binding;
	}

	public IndexedFormula<F> getIndexedFormula() {
		return indexedFormula;
	}

	public IndexedFormula<F> getIndexedPattern() {
		return indexedPattern;
	}

	public IBinding getBinding() {
		return binding;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Match{");
		builder.append("Formula : " + indexedFormula + ",");
		builder.append("Pattern : " + indexedPattern + ",");
		builder.append("Binding : "+binding+"}");
		return builder.toString();
	}
	
}
