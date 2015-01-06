/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.internal.ast.extensions.pm.assoc;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

import org.eventb.core.ast.Formula;
import org.eventb.core.ast.extensions.pm.Matcher;
import org.eventb.core.ast.extensions.pm.engine.Binding;

/**
 * 
 * @author maamria
 *
 */
public class ACMatchStack<F extends Formula<F>> {

	private Matcher matcher;
	
	private Deque<Match<F>> matchesStack;
	private Deque<IndexedFormula<F>> usedUp;
	private Deque<List<Match<F>>> exploredMatches;
	private Binding initialBinding;
	
	public ACMatchStack(Matcher matcher, Match<F> initialMatch){
		this.matcher = matcher;
		matchesStack = new ArrayDeque<Match<F>>();
		usedUp = new ArrayDeque<IndexedFormula<F>>();
		exploredMatches = new ArrayDeque<List<Match<F>>>();
		
		this.initialBinding = initialMatch.getBinding();
		usedUp.push(initialMatch.getIndexedFormula());
		exploredMatches.push(new ArrayList<Match<F>>());
	}
	
	public boolean push(Match<F> nextMatch){
		if (exploredMatches.contains(nextMatch)){
			return false;
		}
		if (usedUp.contains(nextMatch.getIndexedFormula())){
			return false;
		}
		if (!isMatchAcceptable(nextMatch)){
			return false;
		}
		exploredMatches.peek().add(nextMatch);
		matchesStack.push(nextMatch);
		usedUp.push(nextMatch.getIndexedFormula());
		exploredMatches.push(new ArrayList<Match<F>>());
		return true;
	}
	
	public void pop() {
		if (matchesStack.size()>0 && usedUp.size()>0 && exploredMatches.size()>0){
			matchesStack.pop();
			usedUp.pop();
			exploredMatches.pop();
		}
	}
	
	public Binding getFinalBinding() {
		Binding internalBinding = (Binding) matcher.getMatchingFactory().createBinding(false, matcher.getFactory());
		internalBinding.insertBinding(initialBinding);
		Iterator<Match<F>> elements = matchesStack.descendingIterator();
		while (elements.hasNext()) {
			internalBinding.insertBinding(elements.next().getBinding());
		}
		return internalBinding;
	}
	
	public List<IndexedFormula<F>> getUsedUpFormulae(){
		return new ArrayList<IndexedFormula<F>>(usedUp);
	}
	
	private boolean isMatchAcceptable(Match<F> match) {
		Binding internalBinding = getFinalBinding();
		return internalBinding.isBindingInsertable(match.getBinding());
	}
}
