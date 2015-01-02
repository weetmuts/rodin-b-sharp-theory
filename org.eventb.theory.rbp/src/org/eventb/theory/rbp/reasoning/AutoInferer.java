/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.reasoning;

import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.ast.Expression;
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
import org.eventb.theory.core.IReasoningTypeElement.ReasoningType;
import org.eventb.theory.core.ISCGiven;
import org.eventb.theory.core.ISCInferenceRule;
import org.eventb.theory.rbp.reasoning.structures.InferenceDerivationTree;
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

	public AutoInferer(IPOContext context) {
		super(context);
		this.binder = new SimpleBinder(context.getFormulaFactory());
	}

	public IAntecedent[] applyInferenceRules(IProverSequent sequent) {
		// apply forward inferences to enhance the hypotheses
		List<IGeneralRule> fRules = manager.getInferenceRules(true, ReasoningType.FORWARD, context);
		Set<Predicate> addedHyps = new LinkedHashSet<Predicate>();
		Set<Predicate> extraWDants = new LinkedHashSet<Predicate>();
		for (IGeneralRule rule : fRules) {
			applyForwardRule(sequent, rule, addedHyps, extraWDants);
		}
		// apply backward inferences to split/discharge
		List<IGeneralRule> bRules = manager.getInferenceRules(true, ReasoningType.BACKWARD, context);
		IAntecedent goalAntecedent = ProverFactory.makeAntecedent(sequent.goal(), addedHyps, null);
		InferenceDerivationTree tree = new InferenceDerivationTree(goalAntecedent, null);
		for (IGeneralRule rule : bRules) {
			applyBackwardRule(sequent, tree, rule, addedHyps);
		}
		Set<IAntecedent> resultAnts = tree.getLeafAntecedents();
		if (resultAnts == null) {
			if (!addedHyps.isEmpty()) {
				Set<IAntecedent> fAnts = new LinkedHashSet<IAntecedent>();
				fAnts.add(ProverFactory.makeAntecedent(sequent.goal(), addedHyps, null));
				for (Predicate wd : extraWDants) {
					fAnts.add(ProverFactory.makeAntecedent(wd));
				}
				return fAnts.toArray(new IAntecedent[fAnts.size()]);
			}
			return null;
		}
		for (Predicate wd : extraWDants) {
			resultAnts.add(ProverFactory.makeAntecedent(wd));
		}
		return resultAnts.toArray(new IAntecedent[resultAnts.size()]);
	}

	private void applyBackwardRule(IProverSequent sequent,
			InferenceDerivationTree tree, IGeneralRule rule,
			Set<Predicate> addedHyps) {
		if (tree.continueDeriving()) {
			if (!tree.hasBeenDerived()) {
				IAntecedent ant = tree.getAntecedent();
				Predicate goal = ant.getGoal();
				Predicate infer;
				if (rule instanceof IDeployedInferenceRule) {
					infer = ((IDeployedInferenceRule) rule).getInfer()
							.getInferClause();

					IBinding binding = finder.match(goal, infer, false);
					if (binding != null) {
						List<IDeployedGiven> givens = ((IDeployedInferenceRule) rule)
								.getGivens();
						Set<IAntecedent> ants = new LinkedHashSet<IAntecedent>();
						for (IDeployedGiven given : givens) {
							Predicate pred = (Predicate) binder.bind(
									given.getGivenClause(), binding);
							ants.add(ProverFactory.makeAntecedent(pred,
									addedHyps, null));
						}
						Map<FreeIdentifier, Expression> expressionMappings = binding
								.getExpressionMappings();
						for (FreeIdentifier identifier : expressionMappings
								.keySet()) {
							Expression mappedExpression = expressionMappings
									.get(identifier);
							Predicate wdPredicate = mappedExpression
									.getWDPredicate();
							if (!wdPredicate.equals(ProverUtilities.BTRUE)) {
								if (!sequent.containsHypothesis(wdPredicate))
									ants.add(ProverFactory.makeAntecedent(
											wdPredicate, addedHyps, null));
							}
						}
						tree.setAntecedents(ants);
						for (InferenceDerivationTree derivTree : tree
								.getInferenceTrees()) {
							applyBackwardRule(sequent, derivTree, rule,
									addedHyps);
						}
					}
				} else { // if (rule instanceof ISCInferenceRule) {
					try {
						FormulaFactory factory = context.getFormulaFactory();
						ITypeEnvironment typeEnvironment = ProverUtilities
								.makeTypeEnvironment(factory,
										(ISCInferenceRule) rule);
						infer = ((ISCInferenceRule) rule).getInfers()[0]
								.getPredicate(typeEnvironment);
						IBinding binding = finder.match(goal, infer, false);
						if (binding != null) {
							List<ISCGiven> givens = unmodifiableList(Arrays
									.asList(((ISCInferenceRule) rule)
											.getGivens()));
							Set<IAntecedent> ants = new LinkedHashSet<IAntecedent>();
							for (ISCGiven given : givens) {
								Predicate pred = (Predicate) binder.bind(
										given.getPredicate(typeEnvironment), binding);
								ants.add(ProverFactory.makeAntecedent(pred,
										addedHyps, null));
							}
							Map<FreeIdentifier, Expression> expressionMappings = binding
									.getExpressionMappings();
							for (FreeIdentifier identifier : expressionMappings
									.keySet()) {
								Expression mappedExpression = expressionMappings
										.get(identifier);
								Predicate wdPredicate = mappedExpression
										.getWDPredicate();
								if (!wdPredicate.equals(ProverUtilities.BTRUE)) {
									if (!sequent
											.containsHypothesis(wdPredicate))
										ants.add(ProverFactory.makeAntecedent(
												wdPredicate, addedHyps, null));
								}
							}
							tree.setAntecedents(ants);
							for (InferenceDerivationTree derivTree : tree
									.getInferenceTrees()) {
								applyBackwardRule(sequent, derivTree, rule,
										addedHyps);
							}
						}
					} catch (CoreException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} else {
				for (InferenceDerivationTree childDerivationTree : tree
						.getInferenceTrees()) {
					applyBackwardRule(sequent, childDerivationTree, rule,
							addedHyps);
				}
			}
		}
	}

	/**
	 * FIXME this potentially can go on forever : example rule x:NAT |- x+1:NAT which is automatic.
	 * @param sequent
	 * @param rule
	 * @param addedHyps
	 * @param extraWDants
	 */
	private void applyForwardRule(IProverSequent sequent, IGeneralRule rule, Set<Predicate> addedHyps, Set<Predicate> extraWDants) {
		try{
		Predicate[] predicates;
		FormulaFactory factory= context.getFormulaFactory();
		ITypeEnvironment typeEnv;
		if (rule instanceof IDeployedInferenceRule) {
			List<IDeployedGiven> givens = ((IDeployedInferenceRule) rule).getGivens();
			predicates = getPredicates(givens);
		}
		else { //if (rule instanceof ISCInferenceRule) {
			typeEnv = ProverUtilities.makeTypeEnvironment(factory, (ISCInferenceRule) rule);
			List<ISCGiven> givens = unmodifiableList(Arrays.asList(((ISCInferenceRule) rule).getGivens()));
			predicates = getPredicates(givens, factory, typeEnv);
		}
		ACProblem<Predicate> problem = new ACPredicateProblem(Predicate.LAND, getPredicates(sequent.hypIterable()), predicates, finder.getMatchingFactory().createBinding(true,
				context.getFormulaFactory()));
		IBinding binding = problem.solve(true);
		if (binding != null) {
			binding.makeImmutable();
			Predicate ins;
			if (rule instanceof IDeployedInferenceRule) {
				ins = (Predicate) binder.bind(((IDeployedInferenceRule) rule).getInfer().getInferClause(), binding);
			}
			else { //if (rule instanceof ISCInferenceRule) {
				typeEnv = ProverUtilities.makeTypeEnvironment(factory, (ISCInferenceRule) rule);
				ins = (Predicate) binder.bind(( (ISCInferenceRule) rule).getInfers()[0].getPredicate(typeEnv), binding);
			}
			
			if (!sequent.containsHypothesis(ins)) {
				addedHyps.add(ins);
				Map<FreeIdentifier, Expression> expressionMappings = binding.getExpressionMappings();
				for (FreeIdentifier identifier : expressionMappings.keySet()) {
					Expression mappedExpression = expressionMappings.get(identifier);
					Predicate wdPredicate = mappedExpression.getWDPredicate();
					if (!wdPredicate.equals(ProverUtilities.BTRUE)) {
						if (!sequent.containsHypothesis(wdPredicate))
							extraWDants.add(wdPredicate);
					}
				}
			}
		}
	} catch (CoreException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}

	private Predicate[] getPredicates(Iterable<Predicate> iter) {
		ArrayList<Predicate> list = new ArrayList<Predicate>();
		Iterator<Predicate> iterator = iter.iterator();
		while (iterator.hasNext()) {
			list.add(iterator.next());
		}
		return list.toArray(new Predicate[list.size()]);
	}

	private Predicate[] getPredicates(List<IDeployedGiven> givens) {
		ArrayList<Predicate> list = new ArrayList<Predicate>();
		Iterator<IDeployedGiven> iterator = givens.iterator();
		while (iterator.hasNext()) {
			list.add(iterator.next().getGivenClause());
		}
		return list.toArray(new Predicate[list.size()]);
	}
	
	private Predicate[] getPredicates(List<ISCGiven> givens, FormulaFactory factory, ITypeEnvironment typeEnv) {
		ArrayList<Predicate> list = new ArrayList<Predicate>();
		Iterator<ISCGiven> iterator = givens.iterator();
		while (iterator.hasNext()) {
			try {
				list.add(iterator.next().getPredicate(typeEnv));
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return list.toArray(new Predicate[list.size()]);
	}

}
