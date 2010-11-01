/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *     Southampton University - adapted for rewriting
 *******************************************************************************/
package org.eventb.theory.rbp.rewriting;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.BoolExpression;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.ExtendedPredicate;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IAccumulator;
import org.eventb.core.ast.IFormulaInspector;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.MultiplePredicate;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.PredicateVariable;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.ast.UnaryPredicate;
import org.eventb.theory.rbp.base.IRuleBaseManager;
import org.eventb.theory.rbp.base.RuleBaseManager;
import org.eventb.theory.rbp.engine.IBinding;
import org.eventb.theory.rbp.engine.MatchFinder;
import org.eventb.ui.prover.ITacticApplication;

/**
 * Default implementation of <code>IFormulaInspector<ITacticApplication></code>
 * to collect <code>ITacticApplication</code>. 
 * <p> Customised for RbP.
 * 
 */
public abstract class RbPAbstractApplicationInspector implements
		IFormulaInspector<ITacticApplication> {

	protected final Predicate predicate;
	protected final MatchFinder finder;
	protected final IRuleBaseManager manager;
	protected final boolean isGoal;

	public RbPAbstractApplicationInspector(Predicate predicate, boolean isGoal, FormulaFactory factory) {
		this.predicate = predicate;
		this.isGoal = isGoal;
		this.manager = RuleBaseManager.getDefault();
		this.finder = new MatchFinder(factory);
	}
	
	/**
	 * Checks if any rules are applicable to the formula <code>form</code>.
	 * <p> Information about applicability are stored in the given accumulator.
	 * @param form the formula
	 * @param accum the accumulator
	 */
	protected abstract void select(Formula<?> form, IAccumulator<ITacticApplication> accum) ;
	
	/**
	 * Returns whether the  <code>pattern</code> is matchable to <code>form</code>.
	 * @param form the theory formula
	 * @param pattern the pattern formula
	 * @return <code>true</code> iff <code>pattern</code> is matchable to <code>form</code>
	 */
	protected boolean canFindABinding(Formula<?> form, Formula<?> pattern){
		IBinding binding = finder.calculateBindings(form, pattern, true);
		if(binding == null)
			return false;
		return true;
	}

	@Override
	public void inspect(AssociativeExpression expression,
			IAccumulator<ITacticApplication> accumulator) {
		// Nothing to do
	}

	@Override
	public void inspect(AssociativePredicate predicate,
			IAccumulator<ITacticApplication> accumulator) {
		// Nothing to do
	}

	@Override
	public void inspect(AtomicExpression expression,
			IAccumulator<ITacticApplication> accumulator) {
		// Nothing to do
	}

	@Override
	public void inspect(BinaryExpression expression,
			IAccumulator<ITacticApplication> accumulator) {
		// Nothing to do
	}

	@Override
	public void inspect(BinaryPredicate predicate,
			IAccumulator<ITacticApplication> accumulator) {
		// Nothing to do
	}

	@Override
	public void inspect(BoolExpression expression,
			IAccumulator<ITacticApplication> accumulator) {
		// Nothing to do
	}

	@Override
	public void inspect(BoundIdentDecl decl,
			IAccumulator<ITacticApplication> accumulator) {
		// Nothing to do
	}

	@Override
	public void inspect(BoundIdentifier identifier,
			IAccumulator<ITacticApplication> accumulator) {
		// Nothing to do
	}
	@Override
	public void inspect(ExtendedExpression expression,
			IAccumulator<ITacticApplication> accumulator) {
		select(expression, accumulator);
		
	}

	@Override
	public void inspect(ExtendedPredicate predicate,
			IAccumulator<ITacticApplication> accumulator) {
		select(predicate, accumulator);	
	}

	@Override
	public void inspect(FreeIdentifier identifier,
			IAccumulator<ITacticApplication> accumulator) {
		// Nothing to do
	}

	@Override
	public void inspect(IntegerLiteral literal,
			IAccumulator<ITacticApplication> accumulator) {
		// Nothing to do
	}

	@Override
	public void inspect(LiteralPredicate predicate,
			IAccumulator<ITacticApplication> accumulator) {
		// Nothing to do
	}

	@Override
	public void inspect(MultiplePredicate predicate,
			IAccumulator<ITacticApplication> accumulator) {
		// Nothing to do
	}

	@Override
	public void inspect(PredicateVariable predicate,
			IAccumulator<ITacticApplication> accumulator) {
		// Nothing to do
	}

	@Override
	public void inspect(QuantifiedExpression expression,
			IAccumulator<ITacticApplication> accumulator) {
		// Nothing to do
	}

	@Override
	public void inspect(QuantifiedPredicate predicate,
			IAccumulator<ITacticApplication> accumulator) {
		// Nothing to do
	}

	@Override
	public void inspect(RelationalPredicate predicate,
			IAccumulator<ITacticApplication> accumulator) {
		// Nothing to do
	}

	@Override
	public void inspect(SetExtension expression,
			IAccumulator<ITacticApplication> accumulator) {
		// Nothing to do
	}

	@Override
	public void inspect(SimplePredicate predicate,
			IAccumulator<ITacticApplication> accumulator) {
		// Nothing to do
	}

	@Override
	public void inspect(UnaryExpression expression,
			IAccumulator<ITacticApplication> accumulator) {
		// Nothing to do
	}

	@Override
	public void inspect(UnaryPredicate predicate,
			IAccumulator<ITacticApplication> accumulator) {
		// Nothing to do
	}

}