/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.ast.extensions.pm;

import static org.eventb.core.ast.Formula.LAND;

import java.util.List;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.extensions.pm.assoc.ACPredicateProblem;
import org.eventb.core.ast.extensions.pm.assoc.ACProblem;
import org.eventb.core.ast.extensions.pm.engine.Binding;

/**
 * An implementation of a matching engine.
 * <p>
 * All matching processes are initiated from within a matcher. Each matcher
 * works with a an instance of a <code>FormulaFactory</code> to ensure
 * consistency of mathematical extensions used throughout a matching process.
 * <p>
 * This class is not intended to be sub-classed by clients.
 * 
 * @author maamria
 * @since 1.0
 * 
 */
public final class Matcher {

	private FormulaFactory factory;
	private MatchingFactory matchingFactory;

	public Matcher(FormulaFactory factory) {
		this.factory = factory;
		this.matchingFactory = MatchingFactory.getInstance();
	}

	/**
	 * Matches the formula and the pattern and produces a matching result.
	 * <p>
	 * The matching process can be instructed to produce partial matches. This
	 * is relevant when matching two associative expressions (or predicates).
	 * 
	 * @param form
	 *            the formula
	 * @param pattern
	 *            the pattern
	 * @param acceptPartialMatch
	 *            whether to accept a partial match
	 * @return the binding, or <code>null</code> if matching failed
	 */
	public IBinding match(Formula<?> form, Formula<?> pattern, boolean acceptPartialMatch) {
		// if they are not of the same class, do not bother
		if (!form.getClass().equals(pattern.getClass())) {
			return null;
		}
		Binding initialBinding = (Binding) matchingFactory.createBinding(form, pattern, acceptPartialMatch, factory);
		if (matchingFactory.match(form, pattern, initialBinding)) {
			initialBinding.makeImmutable();
			return initialBinding;
		}
		return null;
	}

	/**
	 * Matches all the given patterns against some of the given formulas under
	 * the constraint of an initial binding. Returns a new binding which is a
	 * superset of the given binding. The returned binding is immutable.
	 * 
	 * @param formulae
	 *            some formulae
	 * @param patterns
	 *            some patterns
	 * @param binding
	 *            an initial binding
	 * @return the binding, or <code>null</code> if matching failed
	 */
	public IBinding match(List<Predicate> formulae, List<Predicate> patterns,
			IBinding binding) {
		Predicate[] fs = formulae.toArray(new Predicate[formulae.size()]);
		Predicate[] ps = patterns.toArray(new Predicate[patterns.size()]);
		ACProblem<?> problem = new ACPredicateProblem(LAND, fs, ps, binding);
		return problem.solve(true);
	}

	/**
	 * Returns the formula factory with which this matcher is working.
	 * 
	 * @return the formula factory
	 */
	public FormulaFactory getFactory() {
		return factory;
	}

	/**
	 * Returns the matching factory used by this matcher.
	 * 
	 * @return the matching factory
	 */
	public MatchingFactory getMatchingFactory() {
		return matchingFactory;
	}

	/**
	 * Returns whether the two arrays of declarations match (simple
	 * implementation).
	 * 
	 * @param formulaDecs
	 *            the formula declarations
	 * @param patternDecs
	 *            the pattern declarations
	 * @param existingBinding
	 *            the existing binding
	 * @return whether the declarations match
	 */
	public static boolean boundIdentDecsMatch(BoundIdentDecl[] formulaDecs, BoundIdentDecl[] patternDecs,
			Binding existingBinding) {
		if (formulaDecs.length == patternDecs.length) {
			int index = 0;
			for (BoundIdentDecl pDec : patternDecs) {
				BoundIdentDecl fDec = formulaDecs[index];
				// type unification should gather any new information and put it
				// in binding
				if (!existingBinding.unifyTypes(fDec.getType(), pDec.getType(), true)) {
					return false;
				}
				index++;
			}
			return true;
		} else
			return false;
	}

}
