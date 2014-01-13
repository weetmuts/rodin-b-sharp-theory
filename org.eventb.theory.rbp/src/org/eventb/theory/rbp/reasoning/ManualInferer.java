/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.reasoning;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.extensions.pm.IBinding;
import org.eventb.core.ast.extensions.pm.SimpleBinder;
import org.eventb.core.ast.extensions.pm.assoc.ACPredicateProblem;
import org.eventb.core.ast.extensions.pm.assoc.ACProblem;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.theory.core.IGeneralRule;
import org.eventb.theory.core.ISCGiven;
import org.eventb.theory.core.ISCInferenceRule;
import org.eventb.theory.rbp.rulebase.IPOContext;
import org.eventb.theory.rbp.rulebase.basis.IDeployedGiven;
import org.eventb.theory.rbp.rulebase.basis.IDeployedInferenceRule;
import org.eventb.theory.rbp.utils.ProverUtilities;
import org.rodinp.core.RodinDBException;

/**
 * @author maamria
 *
 */
public class ManualInferer extends AbstractRulesApplyer{
	
	private SimpleBinder binder;
	
	public ManualInferer(IPOContext context){
		super(context);
		this.binder = new SimpleBinder(context.getParentRoot().getFormulaFactory());
	}
	
	/**
	 * Returns the antecedents resulting from applying the specified rule.
	 * <p>
	 * @param pred to which the rule was applicable
	 * @param position 
	 * @param isGoal 
	 * @param theoryName
	 * @param ruleName
	 * @return the antecedents or <code>null</code> if the rule is not found or inapplicable
	 */
	public IAntecedent[] getAntecedents(IProverSequent sequent, Predicate pred, boolean forward, String projectName, String theoryName, String ruleName){
		IGeneralRule rule = manager.getInferenceRule(projectName, theoryName, ruleName, context);
		// rule not found
		if (rule == null) {
			return null;
		}
		// expected forward but the passed predicate is not a hypothesis
		if (forward && !sequent.containsHypothesis(pred)) {
			return null;
		}
		
		if (rule instanceof IDeployedInferenceRule) {
			// if expected forward application but rule is not suitable
			if (forward && !((IDeployedInferenceRule) rule).isSuitableForForwardReasoning()) {
				return null;
			}
			// if expected backward application but rule is not suitable
			if (!forward && !((IDeployedInferenceRule) rule).isSuitableForBackwardReasoning()){
				return null;
			}
			// all conditions met
			if (forward)
				return forwardReason(sequent, pred, ((IDeployedInferenceRule) rule));
			else
				return backwardReason(sequent, ((IDeployedInferenceRule) rule));
		} else { // if (rule instanceof ISCInferenceRule) {
			// if expected forward application but rule is not suitable
			try {
			if (forward && !((ISCInferenceRule) rule).isSuitableForForwardReasoning()) {
				return null;
			}
			// if expected backward application but rule is not suitable
			if (!forward && !((ISCInferenceRule) rule).isSuitableForBackwardReasoning()){
				return null;
			}
			} catch (RodinDBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
			// all conditions met
			if (forward)
				return forwardReason(sequent, pred, ((ISCInferenceRule) rule));
			else
				return backwardReason(sequent, ((ISCInferenceRule) rule));
		}
		
		
	}

	protected IAntecedent[] backwardReason(IProverSequent sequent,
			IDeployedInferenceRule rule) {
		Predicate goal = sequent.goal();
		Iterable<Predicate> selectedHyp = sequent.selectedHypIterable();
		Predicate infer = rule.getInfer().getInferClause();
		IBinding binding = finder.match(goal, infer, false);
		//add inhyp givens binding
		if (binding != null) {
			IBinding cloneBinding = binding.clone();
			List<IDeployedGiven> hypGivens = rule.getHypGivens();
			for (IDeployedGiven hypGiven : hypGivens) {
				for (Iterator<Predicate> iterator = selectedHyp.iterator(); iterator.hasNext();) {
					Predicate hyp = (Predicate) iterator.next();
					IBinding hypBinding = finder.match(hyp, hypGiven.getGivenClause(), false);
					if (hypBinding != null && cloneBinding.isBindingInsertable(hypBinding)) {
						cloneBinding.insertBinding(hypBinding);
						break;
					}
				}
			}
			cloneBinding.makeImmutable();
			List<IDeployedGiven> givens = rule.getGivens();
			Set<IAntecedent> antecedents = new LinkedHashSet<IAntecedent>();
			for (IDeployedGiven given : givens) {
				Predicate subGoal = (Predicate) binder.bind(given.getGivenClause(), cloneBinding);
				antecedents.add(ProverFactory.makeAntecedent(subGoal));
			}
			// add the well-definedness conditions where appropriate
			Map<FreeIdentifier, Expression> expressionMappings = cloneBinding.getExpressionMappings();
			for (FreeIdentifier identifier : expressionMappings.keySet()){
				Expression mappedExpression = expressionMappings.get(identifier);
				Predicate wdPredicate = mappedExpression.getWDPredicate(context.getFormulaFactory());
				if (!wdPredicate.equals(ProverUtilities.BTRUE)){
					if (!sequent.containsHypothesis(wdPredicate))
						antecedents.add(ProverFactory.makeAntecedent(wdPredicate));
				}
			}
			return antecedents.toArray(new IAntecedent[antecedents.size()]);
		}
		return null;
	}

	protected IAntecedent[] forwardReason(IProverSequent sequent, Predicate hypothesis, IDeployedInferenceRule rule){
		// rule must have at least one given clause
		if (rule.getGivens().size() < 1){
			return null;
		}
		IDeployedGiven firstGiven = rule.getGivens().get(0);
		Predicate givenPredicate = firstGiven.getGivenClause();
		IBinding binding = finder.match(hypothesis, givenPredicate, true);
		if(binding == null){
			return null;
		}
		List<Predicate> otherGivens = new ArrayList<Predicate>();
		for (IDeployedGiven given : rule.getGivens()){
			if(!given.equals(firstGiven)){
				otherGivens.add(given.getGivenClause());
			}
		}
		List<Predicate> otherHyps = new ArrayList<Predicate>();
		for (Predicate hyp : sequent.hypIterable()){
			if (!hyp.equals(hypothesis)){
				otherHyps.add(hyp);
			}
		}
		ACProblem<Predicate> acProblem = new ACPredicateProblem(
				Formula.LAND, otherHyps.toArray(new Predicate[otherHyps.size()]), 
				otherGivens.toArray(new Predicate[otherGivens.size()]), binding);
		IBinding finalBinding = acProblem.solve(true);
		if (finalBinding==null){
			return null;
		}
		finalBinding.makeImmutable();
		Predicate newHyp = (Predicate) binder.bind(rule.getInfer().getInferClause(), finalBinding);
		Set<IAntecedent> antecedents = new LinkedHashSet<IAntecedent>();
		// add the antecedent corresponding to the infer clause
		IAntecedent mainAntecedent = ProverFactory.makeAntecedent(null, Collections.singleton(newHyp), 
				ProverFactory.makeSelectHypAction(Collections.singleton(newHyp)));
		antecedents.add( mainAntecedent);
		// add the well-definedness conditions where appropriate
		Map<FreeIdentifier, Expression> mappedIdents = finalBinding.getExpressionMappings();
		for (FreeIdentifier identifier : mappedIdents.keySet()){
			Expression mappedExpression = mappedIdents.get(identifier);
			Predicate wdPredicate = mappedExpression.getWDPredicate(context.getFormulaFactory());
			if (!wdPredicate.equals(ProverUtilities.BTRUE)){
				if (!sequent.containsHypothesis(wdPredicate))
					antecedents.add(ProverFactory.makeAntecedent(wdPredicate));
			}
		}
		return antecedents.toArray(new IAntecedent[antecedents.size()]);
	}
	
	protected IAntecedent[] backwardReason(IProverSequent sequent,
			ISCInferenceRule rule) {
		try{
		FormulaFactory factory = context.getFormulaFactory();
		ITypeEnvironment typeEnvironment = ProverUtilities.makeTypeEnvironment(factory, rule);
		Predicate goal = sequent.goal();
		Iterable<Predicate> selectedHyp = sequent.selectedHypIterable();
		Predicate infer = rule.getInfers()[0].getPredicate(factory, typeEnvironment);
		IBinding binding = finder.match(goal, infer, false);
		if (binding != null) {
			List<ISCGiven> hypGivens = new ArrayList<ISCGiven>();
			for (ISCGiven r : Arrays.asList(rule.getGivens())) {
				if (r.isHyp()) {
					hypGivens.add(r);
				}
			}
			IBinding cloneBinding = binding.clone();
			for (ISCGiven hypGiven : hypGivens) {
				for (Iterator<Predicate> iterator = selectedHyp.iterator(); iterator.hasNext();) {
					Predicate hyp = (Predicate) iterator.next();
					IBinding hypBinding = finder.match(hyp, hypGiven.getPredicate(factory, typeEnvironment), false);
					if (hypBinding != null && cloneBinding.isBindingInsertable(hypBinding)) {
						cloneBinding.insertBinding(hypBinding);
						break;
					}
				}
			}
			cloneBinding.makeImmutable();
			//List<ISCGiven> givens = Arrays.asList(rule.getGivens());
			//add just non-inhyp givens
			List<ISCGiven> givens = new ArrayList<ISCGiven>();
			for (ISCGiven r : Arrays.asList(rule.getGivens())) {
				if (!r.isHyp()) {
					givens.add(r);
				}
			}
			Set<IAntecedent> antecedents = new LinkedHashSet<IAntecedent>();
			for (ISCGiven given : givens) {
				Predicate subGoal = (Predicate) binder.bind(given.getPredicate(factory, typeEnvironment), cloneBinding);
				antecedents.add(ProverFactory.makeAntecedent(subGoal));
			}
			// add the well-definedness conditions where appropriate
			Map<FreeIdentifier, Expression> expressionMappings = cloneBinding.getExpressionMappings();
			for (FreeIdentifier identifier : expressionMappings.keySet()){
				Expression mappedExpression = expressionMappings.get(identifier);
				Predicate wdPredicate = mappedExpression.getWDPredicate(context.getFormulaFactory());
				if (!wdPredicate.equals(ProverUtilities.BTRUE)){
					if (!sequent.containsHypothesis(wdPredicate))
						antecedents.add(ProverFactory.makeAntecedent(wdPredicate));
				}
			}
			return antecedents.toArray(new IAntecedent[antecedents.size()]);
		}
	} catch (RodinDBException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
		return null;
	}

	protected IAntecedent[] forwardReason(IProverSequent sequent, Predicate hypothesis, ISCInferenceRule rule){
		try{
		// rule must have at least one given clause
		if (rule.getGivens().length < 1){
			return null;
		}
		ISCGiven firstGiven = rule.getGivens()[0];
		FormulaFactory factory = context.getFormulaFactory();
		//ITypeEnvironment typeEnv = sequent.typeEnvironment();
		ITypeEnvironment typeEnv = ProverUtilities.makeTypeEnvironment(factory, rule);
		Predicate givenPredicate = firstGiven.getPredicate(factory, typeEnv);
		IBinding binding = finder.match(hypothesis, givenPredicate, true);
		if(binding == null){
			return null;
		}
		List<Predicate> otherGivens = new ArrayList<Predicate>();
		for (ISCGiven given : rule.getGivens()){
			if(!given.equals(firstGiven)){
				otherGivens.add(given.getPredicate(factory, typeEnv));
			}
		}
		List<Predicate> otherHyps = new ArrayList<Predicate>();
		for (Predicate hyp : sequent.hypIterable()){
			if (!hyp.equals(hypothesis)){
				otherHyps.add(hyp);
			}
		}
		ACProblem<Predicate> acProblem = new ACPredicateProblem(
				Formula.LAND, otherHyps.toArray(new Predicate[otherHyps.size()]), 
				otherGivens.toArray(new Predicate[otherGivens.size()]), binding);
		IBinding finalBinding = acProblem.solve(true);
		if (finalBinding==null){
			return null;
		}
		finalBinding.makeImmutable();
		Predicate newHyp = (Predicate) binder.bind(rule.getInfers()[0].getPredicate(factory, typeEnv), finalBinding);
		Set<IAntecedent> antecedents = new LinkedHashSet<IAntecedent>();
		// add the antecedent corresponding to the infer clause
		IAntecedent mainAntecedent = ProverFactory.makeAntecedent(null, Collections.singleton(newHyp), 
				ProverFactory.makeSelectHypAction(Collections.singleton(newHyp)));
		antecedents.add( mainAntecedent);
		// add the well-definedness conditions where appropriate
		Map<FreeIdentifier, Expression> mappedIdents = finalBinding.getExpressionMappings();
		for (FreeIdentifier identifier : mappedIdents.keySet()){
			Expression mappedExpression = mappedIdents.get(identifier);
			Predicate wdPredicate = mappedExpression.getWDPredicate(context.getFormulaFactory());
			if (!wdPredicate.equals(ProverUtilities.BTRUE)){
				if (!sequent.containsHypothesis(wdPredicate))
					antecedents.add(ProverFactory.makeAntecedent(wdPredicate));
			}
		}
		return antecedents.toArray(new IAntecedent[antecedents.size()]);
	} catch (RodinDBException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return null;
	}
	}

}
