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
import java.util.Iterator;
import java.util.List;

import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.extensions.pm.IBinding;
import org.eventb.core.ast.extensions.pm.Matcher;
import org.eventb.core.ast.extensions.pm.assoc.ACPredicateProblem;
import org.eventb.core.ast.extensions.pm.assoc.ACProblem;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.theory.core.IGeneralRule;
import org.eventb.theory.core.IReasoningTypeElement.ReasoningType;
import org.eventb.theory.core.ISCGiven;
import org.eventb.theory.core.ISCInferenceRule;
import org.eventb.theory.rbp.reasoners.input.InferenceInput;
import org.eventb.theory.rbp.rulebase.BaseManager;
import org.eventb.theory.rbp.rulebase.IPOContext;
import org.eventb.theory.rbp.rulebase.basis.IDeployedGiven;
import org.eventb.theory.rbp.rulebase.basis.IDeployedInferenceRule;
import org.eventb.theory.rbp.tactics.applications.InferenceTacticApplication;
import org.eventb.theory.rbp.utils.ProverUtilities;
import org.eventb.ui.prover.ITacticApplication;
import org.rodinp.core.RodinDBException;

/**
 * @author maamria
 * 
 */
public class InferenceSelector {

	protected Matcher finder;
	protected BaseManager ruleBaseManager;
	protected IPOContext context;

	public InferenceSelector(IPOContext context) {
		ruleBaseManager = BaseManager.getDefault();
		finder = new Matcher(context.getFormulaFactory());
		this.context = context;
	}

	public List<ITacticApplication> select(Predicate predicate,
			IProverSequent sequent) {
		// TODO change here to incorporate HYP
		FormulaFactory factory = context.getFormulaFactory();
		List<ITacticApplication> apps = new ArrayList<ITacticApplication>();
		if (predicate == null) {
			// backward
			Predicate goal = sequent.goal();
			Iterable<Predicate> selectedHyp = sequent.selectedHypIterable();
			List<IGeneralRule> rules = ruleBaseManager.getInferenceRules(false,
					ReasoningType.BACKWARD, context);
			for (IGeneralRule rule : rules) {
				if (rule instanceof IDeployedInferenceRule) {
					IBinding binding = finder.match(goal,
							((IDeployedInferenceRule) rule).getInfer()
									.getInferClause(), false);
					if (binding != null) {
						IBinding cloneBinding = binding.clone();
						boolean inHypApplicable = true;
						mainloop:
						for (IDeployedGiven hypGiven : ((IDeployedInferenceRule) rule).getHypGivens()) {
							inHypApplicable = false;
							for (Iterator<Predicate> iterator = selectedHyp.iterator(); iterator.hasNext();) {
								Predicate hyp = (Predicate) iterator.next();
								IBinding hypBinding = finder.match(hyp, hypGiven.getGivenClause(), false);
								if (hypBinding != null && cloneBinding.isBindingInsertable(hypBinding)) {
									inHypApplicable = true;
									cloneBinding.insertBinding(hypBinding);
									break;
								}
							}
							if (!inHypApplicable) {
								break mainloop;
							}
						}
					
						if (inHypApplicable) {
							apps.add(new InferenceTacticApplication(
								new InferenceInput(
										((IDeployedInferenceRule) rule)
												.getProjectName(),
										((IDeployedInferenceRule) rule)
												.getTheoryName(),
										((IDeployedInferenceRule) rule)
												.getRuleName(),
										((IDeployedInferenceRule) rule)
												.getDescription(), null, false,
										context)));
						}
					}
				} else { // if (rule instanceof ISCInferenceRule) {
					try {
						ITypeEnvironment typeEnvironment = ProverUtilities
								.makeTypeEnvironment(factory,
										(ISCInferenceRule) rule);
						IBinding binding = finder
								.match(goal, ((ISCInferenceRule) rule)
										.getInfers()[0].getPredicate(factory,
										typeEnvironment), false);
						if (binding != null) {
							IBinding cloneBinding = binding.clone();
							List<ISCGiven> hypGivens = new ArrayList<ISCGiven>();
							for (ISCGiven r : Arrays.asList(((ISCInferenceRule) rule).getGivens())) {
								if (r.isHyp()) {
									hypGivens.add(r);
								}
							}
							boolean inHypApplicable = true;
							mainloop:
							for (ISCGiven hypGiven : hypGivens) {
								inHypApplicable = false;
								for (Iterator<Predicate> iterator = selectedHyp.iterator(); iterator.hasNext();) {
									Predicate hyp = (Predicate) iterator.next();
									IBinding hypBinding = finder.match(hyp, hypGiven.getPredicate(factory, typeEnvironment), false);
									if (hypBinding != null && cloneBinding.isBindingInsertable(hypBinding)) {
										inHypApplicable = true;
										//cloneBinding.insertBinding(hypBinding);
										break;
									}
								}
								if (!inHypApplicable) {
									break mainloop;
								}
							}
						
							if (inHypApplicable) {
								apps.add(new InferenceTacticApplication(
									new InferenceInput(
											((ISCInferenceRule) rule).getRoot()
													.getRodinProject()
													.getElementName(),
											((ISCInferenceRule) rule).getRoot()
													.getElementName(),
											((ISCInferenceRule) rule)
													.getLabel(),
											((ISCInferenceRule) rule)
													.getDescription(), null,
											false, context)));

							}
						}
					} catch (RodinDBException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return null;
					}
				}
			}
		}
		// forward
		else {
			List<IGeneralRule> rules = ruleBaseManager.getInferenceRules(false,
					ReasoningType.FORWARD, context);
			for (IGeneralRule rule : rules) {
				if (rule instanceof IDeployedInferenceRule) {
					if (((IDeployedInferenceRule) rule).getGivens().size() < 1) {
						continue;
					}
					IDeployedGiven firstGiven = ((IDeployedInferenceRule) rule)
							.getGivens().get(0);
					Predicate givenPredicate = firstGiven.getGivenClause();
					IBinding binding = finder.match(predicate, givenPredicate,
							true);
					if (binding == null) {
						continue;
					}
					List<Predicate> otherGivens = new ArrayList<Predicate>();
					for (IDeployedGiven given : ((IDeployedInferenceRule) rule)
							.getGivens()) {
						if (!given.equals(firstGiven)) {
							otherGivens.add(given.getGivenClause());
						}
					}
					List<Predicate> otherHyps = new ArrayList<Predicate>();
					for (Predicate hyp : sequent.hypIterable()) {
						if (!hyp.equals(predicate)) {
							otherHyps.add(hyp);
						}
					}
					ACProblem<Predicate> acProblem = new ACPredicateProblem(
							Formula.LAND,
							otherHyps.toArray(new Predicate[otherHyps.size()]),
							otherGivens.toArray(new Predicate[otherGivens
									.size()]), binding);
					IBinding finalBinding = acProblem.solve(true);
					if (finalBinding == null) {
						continue;
					}
					apps.add(new InferenceTacticApplication(new InferenceInput(
							((IDeployedInferenceRule) rule).getProjectName(),
							((IDeployedInferenceRule) rule).getTheoryName(),
							((IDeployedInferenceRule) rule).getRuleName(),
							((IDeployedInferenceRule) rule).getDescription(),
							predicate, true, context)));
				} else { // if (rule instanceof ISCInferenceRule) {
					try {
						ITypeEnvironment typeEnvironment = ProverUtilities
								.makeTypeEnvironment(factory,
										(ISCInferenceRule) rule);
						if (((ISCInferenceRule) rule).getGivens().length < 1) {
							continue;
						}
						ISCGiven firstGiven = ((ISCInferenceRule) rule)
								.getGivens()[0];
						Predicate givenPredicate = firstGiven.getPredicate(
								factory, typeEnvironment);
						IBinding binding = finder.match(predicate,
								givenPredicate, true);
						if (binding == null) {
							continue;
						}
						List<Predicate> otherGivens = new ArrayList<Predicate>();
						for (ISCGiven given : ((ISCInferenceRule) rule)
								.getGivens()) {
							if (!given.equals(firstGiven)) {
								otherGivens.add(given.getPredicate(factory,
										typeEnvironment));
							}
						}
						List<Predicate> otherHyps = new ArrayList<Predicate>();
						for (Predicate hyp : sequent.hypIterable()) {
							if (!hyp.equals(predicate)) {
								otherHyps.add(hyp);
							}
						}
						ACProblem<Predicate> acProblem = new ACPredicateProblem(
								Formula.LAND,
								otherHyps.toArray(new Predicate[otherHyps
										.size()]),
								otherGivens.toArray(new Predicate[otherGivens
										.size()]), binding);
						IBinding finalBinding = acProblem.solve(true);
						if (finalBinding == null) {
							continue;
						}
						apps.add(new InferenceTacticApplication(
								new InferenceInput(((ISCInferenceRule) rule)
										.getRoot().getRodinProject()
										.getElementName(),
										((ISCInferenceRule) rule).getRoot()
												.getElementName(),
										((ISCInferenceRule) rule).getLabel(),
										((ISCInferenceRule) rule)
												.getDescription(), predicate,
										true, context)));
					} catch (RodinDBException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return null;
					}
				}
			}

		}
		return apps;
	}

}
