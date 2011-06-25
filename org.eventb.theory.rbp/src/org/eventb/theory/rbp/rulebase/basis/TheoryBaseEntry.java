/*******************************************************************************
 * Copyright (c) 2011 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.theory.rbp.rulebase.basis;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eventb.core.IEventBRoot;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.theory.core.IExtensionRulesSource;
import org.eventb.theory.core.IFormulaExtensionsSource;
import org.eventb.theory.core.IReasoningTypeElement.ReasoningType;
import org.eventb.theory.rbp.rulebase.ITheoryBaseEntry;
import org.eventb.theory.rbp.utils.ProverUtilities;

/**
 * 
 * @author maamria
 * 
 */
public class TheoryBaseEntry<R extends IEventBRoot & IFormulaExtensionsSource & IExtensionRulesSource> implements ITheoryBaseEntry<R> {

	private boolean hasChanged;
	private R theoryRoot;

	/**
	 * All rules.
	 */
	private List<IDeployedRewriteRule> rewriteRules;
	private List<IDeployedInferenceRule> inferenceRules;

	/**
	 * Mapped automatic rules by runtime class of formula.
	 */
	private Map<Class<? extends Expression>, List<IDeployedRewriteRule>> autoExpRewRules;
	private Map<Class<? extends Predicate>, List<IDeployedRewriteRule>> autoPredRewRules;

	/**
	 * Mapped interactive rules by runtime class of formula.
	 */
	private Map<Class<? extends Expression>, List<IDeployedRewriteRule>> interExpRewRules;
	private Map<Class<? extends Predicate>, List<IDeployedRewriteRule>> interPredRewRules;

	/**
	 * Mapped inference rules by reasoning type runtime class.
	 */
	private Map<ReasoningType, List<IDeployedInferenceRule>> autoTypedInferenceMap;
	private Map<ReasoningType, List<IDeployedInferenceRule>> interTypedInferenceMap;

	public TheoryBaseEntry(R theoryRoot) {
		this.theoryRoot = theoryRoot;
		this.autoExpRewRules = new LinkedHashMap<Class<? extends Expression>, List<IDeployedRewriteRule>>();
		this.autoPredRewRules = new LinkedHashMap<Class<? extends Predicate>, List<IDeployedRewriteRule>>();
		this.interExpRewRules = new LinkedHashMap<Class<? extends Expression>, List<IDeployedRewriteRule>>();
		this.interPredRewRules = new LinkedHashMap<Class<? extends Predicate>, List<IDeployedRewriteRule>>();
		this.autoTypedInferenceMap = new LinkedHashMap<ReasoningType, List<IDeployedInferenceRule>>();
		this.interTypedInferenceMap = new LinkedHashMap<ReasoningType, List<IDeployedInferenceRule>>();
		// set to true to initiate an reload
		this.hasChanged = true;
	}

	protected void reload(FormulaFactory factory) {
		IDeployedTheoryFile file = new DeployedTheoryFile<R>(theoryRoot, factory);
		rewriteRules = file.getRewriteRules();
		inferenceRules = file.getInferenceRules();
		// clear all
		autoExpRewRules.clear();
		autoPredRewRules.clear();
		interExpRewRules.clear();
		interPredRewRules.clear();
		autoTypedInferenceMap.clear();
		interTypedInferenceMap.clear();

		for (IDeployedRewriteRule rule : rewriteRules) {
			// only automatic + unconditional rewrites
			if (rule.isAutomatic() && !rule.isConditional()) {
				Formula<?> leftHandSide = rule.getLeftHandSide();
				if (leftHandSide instanceof Expression) {
					Expression exp = (Expression) leftHandSide;
					if (autoExpRewRules.get(exp.getClass()) == null) {
						List<IDeployedRewriteRule> list = new ArrayList<IDeployedRewriteRule>();
						autoExpRewRules.put(exp.getClass(), list);
					}
					autoExpRewRules.get(exp.getClass()).add(rule);
				} 
				else {
					Predicate pred = (Predicate) leftHandSide;
					if (autoPredRewRules.get(pred.getClass()) == null) {
						List<IDeployedRewriteRule> list = new ArrayList<IDeployedRewriteRule>();
						autoPredRewRules.put(pred.getClass(), list);
					}
					autoPredRewRules.get(pred.getClass()).add(rule);
				}
			} 
			// interactive rewrites
			if (rule.isInteracive()) {
				Formula<?> leftHandSide = rule.getLeftHandSide();
				if (leftHandSide instanceof Expression) {
					Expression exp = (Expression) leftHandSide;
					if (interExpRewRules.get(exp.getClass()) == null) {
						List<IDeployedRewriteRule> list = new ArrayList<IDeployedRewriteRule>();
						interExpRewRules.put(exp.getClass(), list);
					}
					interExpRewRules.get(exp.getClass()).add(rule);
				} else {
					Predicate pred = (Predicate) leftHandSide;
					if (interPredRewRules.get(pred.getClass()) == null) {
						List<IDeployedRewriteRule> list = new ArrayList<IDeployedRewriteRule>();
						interPredRewRules.put(pred.getClass(), list);
					}
					interPredRewRules.get(pred.getClass()).add(rule);
				}
			}
		}
		for (IDeployedInferenceRule rule : inferenceRules) {
			// automatic + backward inference only
			if (rule.isAutomatic() && rule.isSuitableForBackwardReasoning()) {
				ReasoningType type = rule.getReasoningType();
				if (!autoTypedInferenceMap.containsKey(type)) {
					List<IDeployedInferenceRule> list = new ArrayList<IDeployedInferenceRule>();
					autoTypedInferenceMap.put(type, list);
				}
				autoTypedInferenceMap.get(type).add(rule);
			} 
			// interactive inference
			if(rule.isInteracive()){
				ReasoningType type = rule.getReasoningType();
				if (!interTypedInferenceMap.containsKey(type)) {
					List<IDeployedInferenceRule> list = new ArrayList<IDeployedInferenceRule>();
					interTypedInferenceMap.put(type, list);
				}
				interTypedInferenceMap.get(type).add(rule);
			}
		}
	}

	protected void checkStatus(FormulaFactory factory) {
		if (hasChanged) {
			reload(factory);
			setHasChanged(false);
		}
	}

	public boolean hasChanged() {
		return hasChanged;
	}

	public void setHasChanged(boolean hasChanged) {
		this.hasChanged = hasChanged;
	}

	@Override
	public List<IDeployedRewriteRule> getExpressionRewriteRules(boolean automatic, Class<? extends Expression> clazz, FormulaFactory factory) {
		checkStatus(factory);
		if (automatic) {
			if (autoExpRewRules.containsKey(clazz))
				return getList(autoExpRewRules.get(clazz));
		} else {
			if (interExpRewRules.containsKey(clazz))
				return getList(interExpRewRules.get(clazz));
		}
		return new ArrayList<IDeployedRewriteRule>();
	}

	@Override
	public List<IDeployedRewriteRule> getPredicateRewriteRules(boolean automatic, Class<? extends Predicate> clazz, FormulaFactory factory) {
		checkStatus(factory);
		if (automatic) {
			if (autoPredRewRules.containsKey(clazz))
				return getList(autoPredRewRules.get(clazz));
		} else {
			if (interPredRewRules.containsKey(clazz))
				return getList(interPredRewRules.get(clazz));
		}
		return new ArrayList<IDeployedRewriteRule>();
	}

	@Override
	public List<IDeployedInferenceRule> getInferenceRules(boolean automatic, ReasoningType type, FormulaFactory factory) {
		checkStatus(factory);
		List<IDeployedInferenceRule> toReturn = new ArrayList<IDeployedInferenceRule>();
		if (automatic) {
			if (!autoTypedInferenceMap.containsKey(type)) {
				List<IDeployedInferenceRule> list = new ArrayList<IDeployedInferenceRule>();
				autoTypedInferenceMap.put(type, list);
			}
			List<IDeployedInferenceRule> bfRules = ProverUtilities.safeList(autoTypedInferenceMap.get(ReasoningType.BACKWARD_AND_FORWARD));
			switch (type) {
			case BACKWARD:
			case FORWARD: {
				toReturn.addAll(ProverUtilities.safeList(autoTypedInferenceMap.get(type)));
				toReturn.addAll(bfRules);
				break;
			}
			case BACKWARD_AND_FORWARD: {
				toReturn.addAll(bfRules);
			}
			}
		} else {
			if (!interTypedInferenceMap.containsKey(type)) {
				List<IDeployedInferenceRule> list = new ArrayList<IDeployedInferenceRule>();
				interTypedInferenceMap.put(type, list);
			}
			List<IDeployedInferenceRule> bfRules = ProverUtilities.safeList(interTypedInferenceMap.get(ReasoningType.BACKWARD_AND_FORWARD));
			switch (type) {
			case BACKWARD:
			case FORWARD: {
				toReturn.addAll(ProverUtilities.safeList(interTypedInferenceMap.get(type)));
				toReturn.addAll(bfRules);
				break;
			}
			case BACKWARD_AND_FORWARD: {
				if (bfRules != null)
					toReturn.addAll(bfRules);
			}
			}
		}
		return toReturn;
	}

	@Override
	public IDeployedInferenceRule getInferenceRule(String ruleName, FormulaFactory factory) {
		checkStatus(factory);
		for (IDeployedInferenceRule rule : inferenceRules) {
			if (rule.getRuleName().equals(ruleName)) {
				return rule;
			}
		}
		return null;
	}

	@Override
	public IDeployedRewriteRule getExpressionRewriteRule(String ruleName, Class<? extends Expression> clazz, FormulaFactory factory) {
		checkStatus(factory);
		if (interExpRewRules.get(clazz) == null) {
			return null;
		}
		for (IDeployedRewriteRule rule : interExpRewRules.get(clazz)) {
			if (rule.getRuleName().equals(ruleName)) {
				return rule;
			}
		}
		return null;
	}

	@Override
	public IDeployedRewriteRule getPredicateRewriteRule(String ruleName, Class<? extends Predicate> clazz, FormulaFactory factory) {
		checkStatus(factory);
		if (interPredRewRules.get(clazz) == null) {
			return null;
		}
		for (IDeployedRewriteRule rule : interPredRewRules.get(clazz)) {
			if (rule.getRuleName().equals(ruleName)) {
				return rule;
			}
		}
		return null;
	}

	/**
	 * Returns a list with same element but different reference.
	 * 
	 * @param <E>
	 *            the type of elements
	 * @param list
	 *            the original list
	 * @return same list with different reference
	 */
	private <E> List<E> getList(List<E> list) {
		return new ArrayList<E>(list);
	}

}
