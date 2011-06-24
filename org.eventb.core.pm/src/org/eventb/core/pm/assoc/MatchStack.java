/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.pm.assoc;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Stack;

import org.eventb.core.ast.Formula;
import org.eventb.core.pm.IBinding;
import org.eventb.core.pm.Matcher;

/**
 * 
 * @author maamria
 * 
 */
public class MatchStack<F extends Formula<F>> {

	private static final long serialVersionUID = 1246209054635172376L;
	private Matcher matcher;

	private Stack<Match<F>> matchesStack;
	private Stack<IndexedFormula<F>> usedUp;
	private Stack<List<Match<F>>> exploredMatches;

	public MatchStack(Matcher matcher) {
		this.matcher = matcher;
		this.matchesStack = new Stack<Match<F>>();
		this.usedUp = new Stack<IndexedFormula<F>>();
		this.exploredMatches = new Stack<List<Match<F>>>();
	}

	public void push(Match<F> match) {
		if (!exploredMatches.empty()){
			exploredMatches.peek().add(match);
		}
		matchesStack.push(match);
		usedUp.push(match.getIndexedFormula());
		exploredMatches.push(new ArrayList<Match<F>>());
	}

	public void pop() {
		matchesStack.pop();
		usedUp.pop();
		exploredMatches.pop();
	}

	protected boolean isMatchAcceptable(Match<F> match) {
		if (exploredMatches.peek().contains(match)){
			return false;
		}
		IBinding internalBinding = getFinalBinding();
		return internalBinding.isBindingInsertable(match.getBinding()) && !usedUp.contains(match.getIndexedFormula());
	}

	public IBinding getFinalBinding() {
		IBinding internalBinding = matcher.getMatchingFactory().createBinding(false, matcher.getFactory());
		Enumeration<Match<F>> elements = matchesStack.elements();
		while (elements.hasMoreElements()) {
			internalBinding.insertBinding(elements.nextElement().getBinding());
		}
		return internalBinding;
	}
}
