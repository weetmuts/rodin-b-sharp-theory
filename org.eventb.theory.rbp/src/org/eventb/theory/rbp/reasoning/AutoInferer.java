/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.reasoning;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pm.IBinding;
import org.eventb.core.pm.SimpleBinder;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.theory.core.IReasoningTypeElement.ReasoningType;
import org.eventb.theory.rbp.reasoners.AutoInferenceReasoner;
import org.eventb.theory.rbp.rulebase.IPOContext;
import org.eventb.theory.rbp.rulebase.basis.IDeployedGiven;
import org.eventb.theory.rbp.rulebase.basis.IDeployedInferenceRule;
import org.eventb.theory.rbp.utils.ProverUtilities;

/**
 * @author maamria
 *
 */
public class AutoInferer extends AbstractRulesApplyer {
	
	private SimpleBinder binder;
	
	public AutoInferer(IPOContext context){
		super(context);
		this.binder = new SimpleBinder(context.getFormulaFactory());
	}

	public IAntecedent[] applyInferenceRules(IProverSequent sequent){
		// only inference rules that are backward and automatic
		List<IDeployedInferenceRule> rules = manager.getInferenceRules(true, ReasoningType.BACKWARD, context);
		IAntecedent goalAntecedent = ProverFactory.makeAntecedent(sequent.goal());
		InferenceDerivationTree tree = new InferenceDerivationTree(goalAntecedent, null);
		for (IDeployedInferenceRule rule : rules) {
			applyRule(tree, rule);
		}
		Set<IAntecedent> resultAnts = tree.getLeafAntecedents();
		if (resultAnts == null) {
			return null;
		}
		return resultAnts.toArray(new IAntecedent[resultAnts.size()]);
	}

	protected void applyRule(InferenceDerivationTree tree, IDeployedInferenceRule rule) {
		if (tree.continueDeriving()){
			if (!tree.hasBeenDerived()) {
				IAntecedent ant = tree.getAntecedent();
				Predicate goal = ant.getGoal();
				Predicate infer = rule.getInfer().getInferClause();
				IBinding binding = finder.match(goal, infer, false);
				if (binding != null) {
					List<IDeployedGiven> givens = rule.getGivens();
					Set<IAntecedent> ants = new LinkedHashSet<IAntecedent>();
					for (IDeployedGiven given : givens) {
						Predicate pred = (Predicate) binder.bind(given.getGivenClause(), binding);
						ants.add(ProverFactory.makeAntecedent(pred));
					}
					Map<FreeIdentifier, Expression> expressionMappings = binding.getExpressionMappings();
					for (FreeIdentifier identifier : expressionMappings.keySet()){
						Expression mappedExpression = expressionMappings.get(identifier);
						Predicate wdPredicate = mappedExpression.getWDPredicate(context.getFormulaFactory());
						if (!wdPredicate.equals(ProverUtilities.BTRUE)){
							ants.add(ProverFactory.makeAntecedent(wdPredicate));
						}
					}
					tree.setAntecedents(ants);
					addUsedTheory(rule.getTheoryName());
					for (InferenceDerivationTree derivTree : tree.getInferenceTrees()) {
						applyRule(derivTree, rule);
					}
				}
			}
			else {
				for ( InferenceDerivationTree childDerivationTree : tree.getInferenceTrees()){
					applyRule(childDerivationTree, rule);
				}
			}
		}
	}

	protected void addUsedTheory(String name) {
		if (!AutoInferenceReasoner.usedTheories.contains(name))
			AutoInferenceReasoner.usedTheories.add(name);
	}

}
