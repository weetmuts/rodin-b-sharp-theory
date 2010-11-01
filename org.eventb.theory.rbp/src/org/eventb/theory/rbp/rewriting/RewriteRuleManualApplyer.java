package org.eventb.theory.rbp.rewriting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IHypAction;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.theory.rbp.engine.IBinding;
import org.eventb.theory.rbp.internal.base.IDeployedRewriteRule;
import org.eventb.theory.rbp.internal.base.IDeployedRuleRHS;
import org.eventb.theory.rbp.reasoning.AbstractRulesApplyer;
import org.eventb.theory.rbp.utils.ProverUtilities;

/**
 * A manual rewrite rule applyer implemntation.
 * @since 1.0
 * @author maamria
 *
 */
public class RewriteRuleManualApplyer extends AbstractRulesApplyer{
	
	public RewriteRuleManualApplyer(FormulaFactory factory) {
		super(factory);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Returns the antecedents resulting from applying the specified rule.
	 * <p>
	 * @param pred the predicate
	 * @param position the position at which to apply the rewrite
	 * @param isGoal whether <code>pred</code> is the goal or a hypothesis
	 * @param theoryName the name of the theory
	 * @param ruleName the name of the rule
	 * @return the antecedents or <code>null</code> if the rule was not found or is inapplicable.
	 */
	public IAntecedent[] applyRule(Predicate pred, IPosition position, boolean isGoal, String theoryName, String ruleName){
		// get the subformula
		Formula<?> formula = pred.getSubFormula(position);
		if(formula == null) {
			return null;
		}
		// get the rule
		@SuppressWarnings("unchecked")
		IDeployedRewriteRule rule = manager.getInteractiveRule(ruleName, theoryName, formula.getClass());
		if(rule == null){
			return null;
		}
		Formula<?> ruleLhs = rule.getLeftHandSide();
		// calculate binding between rule lhs and subformula
		IBinding binding = finder.calculateBindings(formula, ruleLhs, true);
		if(binding == null){
			return null;
		}
		List<IDeployedRuleRHS> ruleRHSs = rule.getRightHandSides();
		assert ruleRHSs.size() > 0;
		// @BUG FIX: when rule is unconditional there is no need to generate extra antecedent
		boolean doesNotRequiresAdditionalAntecedents = rule.isComplete() || !rule.isConditional();
		IAntecedent[] antecedents = 
			(doesNotRequiresAdditionalAntecedents? new IAntecedent[ruleRHSs.size()] : 
								new IAntecedent[ruleRHSs.size()+1]);
		// may need to make an extra antecedent if rule incomplete
		List<Predicate> allConditions = 
			(doesNotRequiresAdditionalAntecedents ? null : 
								 new ArrayList<Predicate>());
		int index = 0;
		// for each right hand side make an antecedent
		for(IDeployedRuleRHS rhs : ruleRHSs){
			// get the condition
			Predicate condition = (Predicate) simpleBinder.bind(rhs.getCondition(), binding, false);
			// if rule is incomplete keep it till later as we will make negation of disj of all conditions
			if(!doesNotRequiresAdditionalAntecedents)
				allConditions.add(condition);
			// get the new subformula
			Formula<?> rhsFormula = simpleBinder.bind(rhs.getRHSFormula(), binding, true);
			// apply the rewriting at the given position
			Predicate newPred = pred.rewriteSubFormula(position, rhsFormula, factory);
			
			Predicate goal = (isGoal ? newPred : null);
			Set<Predicate> addedHyps = new HashSet<Predicate>();
			// add interesting hyps only (no T)
			if(!condition.equals(ProverUtilities.BTRUE))
				addedHyps.add(condition);
			if(!isGoal){
				if(!newPred.equals(ProverUtilities.BTRUE))
					addedHyps.add(newPred);
			}
			List<IHypAction> hypActions = new ArrayList<IHypAction>();
			if(!condition.equals(ProverUtilities.BTRUE)){
				hypActions.add(ProverFactory.makeSelectHypAction(Collections.singleton(condition)));
			}
			if(!isGoal){
				hypActions.add(ProverFactory.makeHideHypAction(Collections.singleton(pred)));
				if(!newPred.equals(ProverUtilities.BTRUE))
					hypActions.add(ProverFactory.makeSelectHypAction(Collections.singleton(newPred)));
			}
			addedHyps = addedHyps.size()>0 ? addedHyps : null;
			antecedents[index] = ProverFactory.makeAntecedent(goal, addedHyps, null, hypActions);
			index ++;
		}
		if(!doesNotRequiresAdditionalAntecedents){
			// we have one left to fill
			assert allConditions.size() >= 1;
			Predicate negOfDisj = factory.makeUnaryPredicate(Formula.NOT, 
					allConditions.size() == 1 ? 
							allConditions.get(0):
							factory.makeAssociativePredicate(Formula.LOR, allConditions, null), 
					null);
			Predicate goal = (isGoal ? pred : null);
			antecedents[index] = ProverFactory.makeAntecedent( goal, 
					Collections.singleton(negOfDisj), 
					ProverFactory.makeSelectHypAction(Collections.singleton(negOfDisj)));
		}
		return antecedents;
	}
}
