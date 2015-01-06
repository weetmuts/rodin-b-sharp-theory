/*******************************************************************************
 * Copyright (c) 2010 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.reasoning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.extensions.pm.IBinding;
import org.eventb.core.ast.extensions.pm.Matcher;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.theory.core.IGeneralRule;
import org.eventb.theory.core.IReasoningTypeElement.ReasoningType;
import org.eventb.theory.rbp.reasoners.input.InferenceInput;
import org.eventb.theory.rbp.rulebase.BaseManager;
import org.eventb.theory.rbp.rulebase.IPOContext;
import org.eventb.theory.rbp.rulebase.basis.IDeployedGiven;
import org.eventb.theory.rbp.rulebase.basis.IDeployedInferenceRule;
import org.eventb.theory.rbp.tactics.applications.InferenceTacticApplication;
import org.eventb.ui.prover.ITacticApplication;

/**
 * @author maamria, asiehsalehi
 * 
 * the case when inf rule is tyoe of ISCInferenceRule is commented; 
 * for now the (SC) inf rules defined in a theory are not applicable in proving within the theory
 * TODO: like application of theorems, in POs of each inf rule, the inf rules above that should be available
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
		// FormulaFactory factory = context.getFormulaFactory();
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
					// if goal match infer
					if (binding != null) {
						HashMap<IDeployedGiven, List<Predicate>> matches = new HashMap<IDeployedGiven, List<Predicate>>();
						IBinding cloneBinding = binding.clone();
						List<IDeployedGiven> hypGivens = ((IDeployedInferenceRule) rule)
								.getHypGivens();
						boolean matchFound = false;

						// perform the hypgivens checking in 2 steps
						// step1: fill a map of hypGiven and matched hypothesis
						if (!hypGivens.isEmpty()) {
							for (IDeployedGiven hypGiven : hypGivens) {
								matchFound = false;
								List<Predicate> hypList = new ArrayList<Predicate>();
								for (Iterator<Predicate> iterator = selectedHyp
										.iterator(); iterator.hasNext();) {
									Predicate hyp = (Predicate) iterator.next();
									IBinding hypBinding = finder.match(hyp,
											hypGiven.getGivenClause(), false);
									if (hypBinding != null
											&& cloneBinding
													.isBindingInsertable(hypBinding)) {
										hypList.add(hyp);
										matchFound = true;
									}
								}
								matches.put(hypGiven, hypList);
								if (!matchFound)
									break;
							}

							boolean found = false;
							// step2: for all of hypGivens at least one
							// hypothesis should be matched
							if (matchFound) {
								found = true;
								@SuppressWarnings("unchecked")
								HashMap<IDeployedGiven, List<Predicate>> cloneMatches = (HashMap<IDeployedGiven, List<Predicate>>) matches.clone();
								Object[] hypGivenArr = hypGivens.toArray();
								IBinding restoreBinding = null;

								// check the compatible bindings
								for (int i = 0; i < hypGivenArr.length; i++) {
									IDeployedGiven hypGiven = (IDeployedGiven) hypGivenArr[i];
									boolean inHypApplicable = false;

									if (!cloneMatches.containsKey(hypGiven)) {
										if (i == 0) {
											found = false;
											break;
										} else {
											// reset match list
											cloneMatches.put(hypGiven,
													matches.get(hypGiven));
											i = i - 2;
											continue;
										}
									}

									for (Predicate hyp : cloneMatches
											.get(hypGiven)) {
										IBinding hypBinding = finder.match(hyp,
												hypGiven.getGivenClause(),
												false);
										if (hypBinding != null
												&& cloneBinding
														.isBindingInsertable(hypBinding)) {
											restoreBinding = cloneBinding
													.clone();
											cloneBinding
													.insertBinding(hypBinding);
											cloneMatches.get(hypGiven).remove(
													hyp);
											inHypApplicable = true;
											break;
										}
									}

									if (!inHypApplicable) {
										// reset binding
										cloneBinding = restoreBinding.clone();
										i = i - 2;
										continue;
									}
								}
							}

							if (found) {
								apps.add(new InferenceTacticApplication(
										new InferenceInput(
												((IDeployedInferenceRule) rule)
														.getProjectName(),
												((IDeployedInferenceRule) rule)
														.getTheoryName(),
												((IDeployedInferenceRule) rule)
														.getRuleName(),
												((IDeployedInferenceRule) rule)
														.getDescription(),
												null, false, cloneBinding,
												context)));
							}
						} else {
							apps.add(new InferenceTacticApplication(
									new InferenceInput(
											((IDeployedInferenceRule) rule)
													.getProjectName(),
											((IDeployedInferenceRule) rule)
													.getTheoryName(),
											((IDeployedInferenceRule) rule)
													.getRuleName(),
											((IDeployedInferenceRule) rule)
													.getDescription(), null,
											false, cloneBinding, context)));
						}

					}
				}
				// else { // if (rule instanceof ISCInferenceRule) {
				// try {
				// ITypeEnvironment typeEnvironment = ProverUtilities
				// .makeTypeEnvironment(factory,
				// (ISCInferenceRule) rule);
				// IBinding binding = finder
				// .match(goal, ((ISCInferenceRule) rule)
				// .getInfers()[0].getPredicate(factory,
				// typeEnvironment), false);
				// if (binding != null) {
				// IBinding cloneBinding = binding.clone();
				// List<ISCGiven> hypGivens = new ArrayList<ISCGiven>();
				// for (ISCGiven r : Arrays.asList(((ISCInferenceRule)
				// rule).getGivens())) {
				// if (r.isHyp()) {
				// hypGivens.add(r);
				// }
				// }
				// boolean inHypApplicable = true;
				// mainloop:
				// for (ISCGiven hypGiven : hypGivens) {
				// inHypApplicable = false;
				// for (Iterator<Predicate> iterator = selectedHyp.iterator();
				// iterator.hasNext();) {
				// Predicate hyp = (Predicate) iterator.next();
				// IBinding hypBinding = finder.match(hyp,
				// hypGiven.getPredicate(factory, typeEnvironment), false);
				// if (hypBinding != null &&
				// cloneBinding.isBindingInsertable(hypBinding)) {
				// inHypApplicable = true;
				// //cloneBinding.insertBinding(hypBinding);
				// break;
				// }
				// }
				// if (!inHypApplicable) {
				// break mainloop;
				// }
				// }
				//
				// if (inHypApplicable) {
				// apps.add(new InferenceTacticApplication(
				// new InferenceInput(
				// ((ISCInferenceRule) rule).getRoot()
				// .getRodinProject()
				// .getElementName(),
				// ((ISCInferenceRule) rule).getRoot()
				// .getElementName(),
				// ((ISCInferenceRule) rule)
				// .getLabel(),
				// ((ISCInferenceRule) rule)
				// .getDescription(), null,
				// false, cloneBinding, context)));
				//
				// }
				// }
				// } catch (RodinDBException e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// return null;
				// }
				// }
			}
		}
		// forward
		else {
			List<IGeneralRule> rules = ruleBaseManager.getInferenceRules(false,
					ReasoningType.FORWARD, context);
			for (IGeneralRule rule : rules) {
				if (rule instanceof IDeployedInferenceRule) {
					List<IDeployedGiven> givens = new ArrayList<IDeployedGiven>();
					givens.addAll(((IDeployedInferenceRule) rule).getGivens());
					givens.addAll(((IDeployedInferenceRule) rule).getHypGivens());
					if (givens.size() < 1) {
						continue;
					}
					IDeployedGiven firstGiven =givens.get(0);
					Predicate givenPredicate = firstGiven.getGivenClause();
					IBinding binding = finder.match(predicate, givenPredicate,
							true);
					if (binding == null) {
						continue;
					}
					List<Predicate> otherGivens = new ArrayList<Predicate>();
					for (IDeployedGiven given : givens) {
						if (!given.equals(firstGiven)) {
							otherGivens.add(given.getGivenClause());
						}
					}
					List<Predicate> otherHyps = new ArrayList<Predicate>();
					//we can read eaither from all of the hyps or the selected hyps, to be compatible to the backward application we read from selected hyps here
					//TODO: there can be optional for the user to select the applicability of the inf rules (both backwarf or forward) to be available only 
					//by selected hyps or all of hyps
					//for (Predicate hyp : sequent.sehypIterable()) {
					for (Predicate hyp : sequent.selectedHypIterable()) {
						if (!hyp.equals(predicate)) {
							otherHyps.add(hyp);
						}
					}
					IBinding finalBinding = finder.match(otherHyps
							.toArray(new Predicate[otherHyps.size()]),
							otherGivens.toArray(new Predicate[otherGivens
									.size()]), binding);
					if (finalBinding == null) {
						continue;
					}
					apps.add(new InferenceTacticApplication(new InferenceInput(
							((IDeployedInferenceRule) rule).getProjectName(),
							((IDeployedInferenceRule) rule).getTheoryName(),
							((IDeployedInferenceRule) rule).getRuleName(),
							((IDeployedInferenceRule) rule).getDescription(),
							predicate, true, finalBinding, context)));
				} 
//				else { // if (rule instanceof ISCInferenceRule) {
//					try {
//						ITypeEnvironment typeEnvironment = ProverUtilities
//								.makeTypeEnvironment(factory,
//										(ISCInferenceRule) rule);
//						if (((ISCInferenceRule) rule).getGivens().length < 1) {
//							continue;
//						}
//						ISCGiven firstGiven = ((ISCInferenceRule) rule)
//								.getGivens()[0];
//						Predicate givenPredicate = firstGiven.getPredicate(
//								factory, typeEnvironment);
//						IBinding binding = finder.match(predicate,
//								givenPredicate, true);
//						if (binding == null) {
//							continue;
//						}
//						List<Predicate> otherGivens = new ArrayList<Predicate>();
//						for (ISCGiven given : ((ISCInferenceRule) rule)
//								.getGivens()) {
//							if (!given.equals(firstGiven)) {
//								otherGivens.add(given.getPredicate(factory,
//										typeEnvironment));
//							}
//						}
//						List<Predicate> otherHyps = new ArrayList<Predicate>();
//						for (Predicate hyp : sequent.hypIterable()) {
//							if (!hyp.equals(predicate)) {
//								otherHyps.add(hyp);
//							}
//						}
//						ACProblem<Predicate> acProblem = new ACPredicateProblem(
//								Formula.LAND,
//								otherHyps.toArray(new Predicate[otherHyps
//										.size()]),
//								otherGivens.toArray(new Predicate[otherGivens
//										.size()]), binding);
//						IBinding finalBinding = acProblem.solve(true);
//						if (finalBinding == null) {
//							continue;
//						}
//						apps.add(new InferenceTacticApplication(
//								new InferenceInput(((ISCInferenceRule) rule)
//										.getRoot().getRodinProject()
//										.getElementName(),
//										((ISCInferenceRule) rule).getRoot()
//												.getElementName(),
//										((ISCInferenceRule) rule).getLabel(),
//										((ISCInferenceRule) rule)
//												.getDescription(), predicate,
//										true, finalBinding, context)));
//					} catch (RodinDBException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//						return null;
//					}
//				}
			}

		}
		return apps;
	}

}
