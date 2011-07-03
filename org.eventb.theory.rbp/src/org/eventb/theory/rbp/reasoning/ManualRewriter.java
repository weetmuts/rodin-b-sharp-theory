package org.eventb.theory.rbp.reasoning;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pm.ComplexBinder;
import org.eventb.core.pm.IBinding;
import org.eventb.core.pm.SimpleBinder;
import org.eventb.core.seqprover.IHypAction;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.theory.rbp.rulebase.IPOContext;
import org.eventb.theory.rbp.rulebase.basis.IDeployedRewriteRule;
import org.eventb.theory.rbp.rulebase.basis.IDeployedRuleRHS;
import org.eventb.theory.rbp.utils.ProverUtilities;

/**
 * <p>An implementation of a manual rewriter.</p>
 * @author maamria
 *
 */
public class ManualRewriter extends AbstractRulesApplyer{
	
	public ManualRewriter(IPOContext context){
		super(context);
	}
	
	/**
	 * Returns the antecedents resulting from applying the specified rule.
	 * <p>
	 * 
	 * @param predicate
	 *            the predicate
	 * @param position
	 *            the position at which to apply the rewrite
	 * @param isGoal
	 *            whether <code>predicate</code> is the goal or a hypothesis
	 * @param theoryName
	 *            the name of the theory
	 * @param ruleName
	 *            the name of the rule
	 * @return the antecedents or <code>null</code> if the rule was not found or
	 *         is inapplicable.
	 */
	public IAntecedent[] getAntecedents(Predicate predicate, IPosition position, boolean isGoal, String theoryName, String ruleName){
		// get the subformula
		Formula<?> formula = predicate.getSubFormula(position);
		if (formula == null) {
			return null;
		}
		FormulaFactory factory = context.getFormulaFactory();
		// get the rule
		IDeployedRewriteRule rule = ((formula instanceof Expression ? 
				manager.getExpressionRewriteRule(ruleName, theoryName, ((Expression) formula).getClass(), context)
				: manager.getPredicateRewriteRule(ruleName, theoryName, ((Predicate) formula).getClass(), context)));
		if (rule == null) {
			return null;
		}
		Formula<?> ruleLhs = rule.getLeftHandSide();
		// calculate binding between rule lhs and subformula
		IBinding binding = finder.match(formula, ruleLhs, true);
		if (binding == null) {
			return null;
		}
		List<IDeployedRuleRHS> ruleRHSs = rule.getRightHandSides();
		// @BUG FIX: when rule is unconditional there is no need to generate
		// extra antecedent
		boolean doesNotRequiresAdditionalAntecedents = rule.isComplete() || !rule.isConditional();
		IAntecedent[] antecedents = (doesNotRequiresAdditionalAntecedents ? 
				new IAntecedent[ruleRHSs.size()] : 
					new IAntecedent[ruleRHSs.size() + 1]);
		// may need to make an extra antecedent if rule incomplete
		List<Predicate> allConditions = (doesNotRequiresAdditionalAntecedents ? null : new ArrayList<Predicate>());
		int index = 0;
		// binder for the condition
		SimpleBinder simpleBinder = new SimpleBinder(factory);
		// binder for the rhs
		ComplexBinder complexBinder =  new ComplexBinder(factory);
		// for each right hand side make an antecedent
		for (IDeployedRuleRHS rhs : ruleRHSs) {
			// get the condition
			Predicate condition = (Predicate) simpleBinder.bind(rhs.getCondition(), binding);
			// if rule is incomplete keep it till later as we will make negation
			// of disjunction of all conditions
			if (!doesNotRequiresAdditionalAntecedents)
				allConditions.add(condition);
			// get the new subformula
			Formula<?> rhsFormula = complexBinder.bind(rhs.getRHSFormula(), binding, true);
			// apply the rewriting at the given position
			Predicate newPred = predicate.rewriteSubFormula(position, rhsFormula, factory);
			Predicate goal = (isGoal ? newPred : null);
			Set<Predicate> addedHyps = new HashSet<Predicate>();
			// add interesting hyps only (no T)
			if (!condition.equals(ProverUtilities.BTRUE))
				addedHyps.add(condition);
			if (!isGoal) {
				if (!newPred.equals(ProverUtilities.BTRUE))
					addedHyps.add(newPred);
			}
			List<IHypAction> hypActions = new ArrayList<IHypAction>();
			if (!condition.equals(ProverUtilities.BTRUE)) {
				hypActions.add(ProverFactory.makeSelectHypAction(Collections.singleton(condition)));
			}
			if (!isGoal) {
				hypActions.add(ProverFactory.makeHideHypAction(Collections.singleton(predicate)));
				if (!newPred.equals(ProverUtilities.BTRUE))
					hypActions.add(ProverFactory.makeSelectHypAction(Collections.singleton(newPred)));
			}
			addedHyps = addedHyps.size() > 0 ? addedHyps : null;
			antecedents[index] = ProverFactory.makeAntecedent(goal, addedHyps, null, hypActions);
			index++;
		}
		if (!doesNotRequiresAdditionalAntecedents) {
			// we have one left to fill
			Predicate negOfDisj = factory.makeUnaryPredicate(Formula.NOT,
					allConditions.size() == 1 ? allConditions.get(0) : factory.makeAssociativePredicate(Formula.LOR, allConditions, null)
							, null);
			Predicate goal = (isGoal ? predicate : null);
			antecedents[index] = ProverFactory.makeAntecedent(goal, Collections.singleton(negOfDisj), 
					ProverFactory.makeSelectHypAction(Collections.singleton(negOfDisj)));
		}
		return antecedents;
	}
}
