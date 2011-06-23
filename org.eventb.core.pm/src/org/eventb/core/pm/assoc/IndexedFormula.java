/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.pm.assoc;

import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.PredicateVariable;

/**
 * 
 * @author maamria
 *
 */
public class IndexedFormula<F extends Formula<F>>{

	private F formula;
	private int index;
	private int numberOfMatches = 0;
	private boolean immutable = false;
	
	public IndexedFormula(int index, F formula){
		this.index = index;
		this.formula = formula;
	}
	
	public boolean isVariable(){
		return formula instanceof FreeIdentifier || formula instanceof PredicateVariable;
	}
	
	public void incrementNumberOfMatches(){
		if (immutable){
			throw new UnsupportedOperationException("Indexed formula is immutable : cannot increment number of matches");
		}
		numberOfMatches++;
	}
	
	public void makeImmutable(){
		this.immutable = true;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof IndexedFormula<?>)){
			return false;
		}
		if (this == obj){
			return true;
		}
		IndexedFormula<?> indexedFormula = (IndexedFormula<?>)obj;
		return indexedFormula.formula.equals(formula) && 
				indexedFormula.index == index && 
				indexedFormula.numberOfMatches == numberOfMatches &&
				indexedFormula.immutable == immutable;
	}
	
	@Override
	public int hashCode() {
		return formula.hashCode() + 17* index + 31*numberOfMatches + new Boolean(immutable).hashCode();
	}
	
	@Override
	public String toString() {
		return "IndexedFormula{" + index + ":" + formula + "}";
	}

	public F getFormula() {
		return formula;
	}

	public int getIndex() {
		return index;
	}

	public int getNumberOfMatches() {
		return numberOfMatches;
	}

	public boolean isImmutable() {
		return immutable;
	}
}
