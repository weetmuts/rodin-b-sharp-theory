/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.rewriting;

import java.util.List;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.BoolExpression;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.Expression;
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
import org.eventb.core.pm.Matcher;
import org.eventb.theory.rbp.internal.rulebase.IDeployedRewriteRule;
import org.eventb.theory.rbp.internal.tactics.RewriteTacticApplication;
import org.eventb.theory.rbp.reasoners.input.RewriteInput;
import org.eventb.theory.rbp.rulebase.BaseManager;
import org.eventb.theory.rbp.rulebase.IPOContext;
import org.eventb.ui.prover.ITacticApplication;

/**
 * A selector of applicable interactive rewrite rules.
 * 
 * @since 1.0
 * 
 * @author maamria
 *
 */
public class RewritesSelector implements IFormulaInspector<ITacticApplication>{

	protected final Predicate predicate;
	protected final Matcher finder;
	protected final BaseManager manager;
	protected final boolean isGoal;
	protected final FormulaFactory factory;
	
	private IPOContext context;
	
	public RewritesSelector(Predicate predicate, boolean isGoal, FormulaFactory factory, IPOContext context){
		this.predicate = predicate;
		this.isGoal = isGoal;
		this.factory = factory;
		this.manager = BaseManager.getDefault();
		this.finder = new Matcher(factory);
		this.context = context;
	}
	
	/**
	 * Checks if any rules are applicable to the formula <code>form</code>.
	 * <p> Information about applicability are stored in the given accumulator.
	 * @param form the formula
	 * @param accum the accumulator
	 */
	protected void select(Formula<?> form, IAccumulator<ITacticApplication> accum) {
		List<IDeployedRewriteRule> rules = ((form instanceof Expression 
				? manager.getExpressionRewriteRules(false, ((Expression)form).getClass(), context, factory)
				: manager.getPredicateRewriteRules(false, ((Predicate) form).getClass(), context, factory)));
		for (IDeployedRewriteRule rule : rules) {
			if (canFindABinding(form, rule.getLeftHandSide())){
				if(rule.isConditional() && !predicate.isWDStrict(accum.getCurrentPosition())){
					continue;
				}
				accum.add(
					new RewriteTacticApplication(
						new RewriteInput(rule.getTheoryName(), rule.getRuleName(), 
								rule.getDescription(), isGoal ? null : predicate, 
								accum.getCurrentPosition()), 
						rule.getToolTip(), context)
					);
			}
		}
	}
	
	/**
	 * Returns whether the  <code>pattern</code> is matchable to <code>form</code>.
	 * @param form the theory formula
	 * @param pattern the pattern formula
	 * @return <code>true</code> iff <code>pattern</code> is matchable to <code>form</code>
	 */
	protected boolean canFindABinding(Formula<?> form, Formula<?> pattern){
		return true;
	}
	
	@Override
	public void inspect(AssociativeExpression expression,
			IAccumulator<ITacticApplication> accumulator) {
		select(expression, accumulator);
	}

	@Override
	public void inspect(AssociativePredicate predicate,
			IAccumulator<ITacticApplication> accumulator) {
		select(predicate, accumulator);
		
	}

	@Override
	public void inspect(AtomicExpression expression,
			IAccumulator<ITacticApplication> accumulator) {
		select(expression, accumulator);
		
	}

	@Override
	public void inspect(BinaryExpression expression,
			IAccumulator<ITacticApplication> accumulator) {
		select(expression, accumulator);
		
	}

	@Override
	public void inspect(BinaryPredicate predicate,
			IAccumulator<ITacticApplication> accumulator) {
		select(predicate, accumulator);
		
	}

	@Override
	public void inspect(BoolExpression expression,
			IAccumulator<ITacticApplication> accumulator) {
		select(expression, accumulator);
	}

	@Override
	public void inspect(BoundIdentDecl decl,
			IAccumulator<ITacticApplication> accumulator) {
		
	}

	@Override
	public void inspect(BoundIdentifier identifier,
			IAccumulator<ITacticApplication> accumulator) {

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
		
	}


	@Override
	public void inspect(IntegerLiteral literal,
			IAccumulator<ITacticApplication> accumulator) {
		select(literal, accumulator);
		
	}

	@Override
	public void inspect(LiteralPredicate predicate,
			IAccumulator<ITacticApplication> accumulator) {
		select(predicate, accumulator);
		
	}

	@Override
	public void inspect(MultiplePredicate predicate,
			IAccumulator<ITacticApplication> accumulator) {
		select(predicate, accumulator);
		
	}

	@Override
	public void inspect(PredicateVariable predicate,
			IAccumulator<ITacticApplication> accumulator) {
		
	}

	@Override
	public void inspect(QuantifiedExpression expression,
			IAccumulator<ITacticApplication> accumulator) {
		select(expression, accumulator);
		
	}

	@Override
	public void inspect(QuantifiedPredicate predicate,
			IAccumulator<ITacticApplication> accumulator) {
		select(predicate, accumulator);
		
	}

	@Override
	public void inspect(RelationalPredicate predicate,
			IAccumulator<ITacticApplication> accumulator) {
		select(predicate, accumulator);
		
	}

	@Override
	public void inspect(SetExtension expression,
			IAccumulator<ITacticApplication> accumulator) {
		select(expression, accumulator);
		
	}

	@Override
	public void inspect(SimplePredicate predicate,
			IAccumulator<ITacticApplication> accumulator) {
		select(predicate, accumulator);
		
	}

	@Override
	public void inspect(UnaryExpression expression,
			IAccumulator<ITacticApplication> accumulator) {
		select(expression, accumulator);
		
	}

	@Override
	public void inspect(UnaryPredicate predicate,
			IAccumulator<ITacticApplication> accumulator) {
		select(predicate, accumulator);
		
	}

}
