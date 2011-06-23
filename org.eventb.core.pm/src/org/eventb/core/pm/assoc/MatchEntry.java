/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.pm.assoc;

import java.util.Collections;
import java.util.List;

import org.eventb.core.ast.Formula;

/**
 * Immutable match entry implementation.
 * @author maamria
 *
 */
public class MatchEntry<F extends Formula<F>> {

	private IndexedFormula<F> indexedPattern;
	private List<Match<F>> matches;
	
	public MatchEntry(IndexedFormula<F> indexedPattern, List<Match<F>> matches){
		this.indexedPattern = indexedPattern;
		this.matches = matches;
	}
	
	public int getRank(){
		return matches.size();
	}
	
	public IndexedFormula<F> getIndexedPattern() {
		return indexedPattern;
	}
	public List<Match<F>> getMatches() {
		return Collections.unmodifiableList(matches);
	}
	
	@Override
	public String toString() {
		return "MatchEntry{" + indexedPattern.toString() + "=>" + matches+"}";
	}
}
